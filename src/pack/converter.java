package pack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class converter {

    private static final String API_KEY = "87MHTyhjg6ryM2OtG5XYks8";
    private static final String API_URL = "https://fcsapi.com/api-v3/forex/latest";
    private static final int TIMEOUT_MS = 5000; // 5 seconds timeout

    /*
     * Converts an amount from one currency to another using an external API.
     *
     * @param amount The amount to convert (must be non-negative).
     * @param from   The 3-letter currency code to convert from.
     * @param to     The 3-letter currency code to convert to.
     * @return The converted amount.
     * @throws IOException              If there's a network error or API returns an HTTP error.
     * @throws JSONException            If the API response cannot be parsed correctly.
     * @throws IllegalArgumentException If the input amount is negative or currencies are invalid.
     */
    public static double convert(double amount, String from, String to)
            throws IOException, JSONException, IllegalArgumentException {

        // --- Input Validation ---
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Currency codes cannot be empty or null.");
        }
        if (from.equalsIgnoreCase(to)) { // Case-insensitive check if converting to same currency
            return amount;
        }

        // --- Fetch rate and calculate ---
        // Let exceptions from getExchangeRate propagate up
        double rate = getExchangeRate(from, to);
        // System.out.println("Fetched rate: " + rate + " for " + from + "/" + to); // Optional Debugging
        return amount * rate;
    }

    /**
     * Fetches the exchange rate from the API.
     */
    private static double getExchangeRate(String from, String to)
            throws IOException, JSONException { // Declare specific checked exceptions

        // Use uppercase for currency codes as APIs often expect this
        String urlStr = API_URL + "?symbol=" + from.toUpperCase() + "/" + to.toUpperCase() + "&access_key=" + API_KEY;
        URL url;
        try {
        // Create URI first for better validation, then convert to URL
        URI uri = new URI(urlStr);
        url = uri.toURL();
    } catch (URISyntaxException e) {
        // Handle invalid URI syntax (e.g., illegal characters)
        throw new IOException("Internal error: Invalid API URI syntax: " + urlStr, e);
    } catch (MalformedURLException e) {
        // Handle issues converting a valid URI to a URL (less common here)
        throw new IOException("Internal error: Cannot convert URI to URL: " + urlStr, e);
    } catch (IllegalArgumentException e) {
         // Handle other potential issues from URI or toURL()
         throw new IOException("Internal error: Invalid argument for URI/URL: " + urlStr, e);
    }

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(TIMEOUT_MS); // Added: Connection timeout
            conn.setReadTimeout(TIMEOUT_MS);    // Added: Read timeout

            // --- Check HTTP Status Code ---
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) { // 200 OK?
                String errorDetails = readStream(conn.getErrorStream()); // Try to get error message from API
                throw new IOException("API request failed: HTTP " + responseCode + ". " + errorDetails);
            }

            // --- Read Response using try-with-resources ---
            String responseBody = readStream(conn.getInputStream());
            if (responseBody.isEmpty()) {
                 throw new IOException("API returned empty response.");
            }

            // --- Parse JSON Robustly ---
            JSONObject json = new JSONObject(responseBody);
            // Check if 'response' key exists and is an array
            if (!json.has("response") || !(json.get("response") instanceof JSONArray)) {
                throw new JSONException("API response missing 'response' array field.");
            }
            JSONArray responseData = json.getJSONArray("response");
            if (responseData.length() == 0) {
                // Provide more context in the error message
                throw new JSONException("API response 'response' array is empty for " + from + "/" + to + ". Check currency codes.");
            }
            // Check if first element is an object and has the 'c' key
            JSONObject exchangeRateData = responseData.getJSONObject(0); // Get first element
            if (!exchangeRateData.has("c")) {
                throw new JSONException("API response missing 'c' (rate) field in first element.");
            }

            // Use getDouble safely after has() check
            double exchangeRate = exchangeRateData.getDouble("c");
            return exchangeRate;

        } finally {
            // --- Ensure connection is disconnected ---
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * Helper method to read an InputStream into a String.
     * Uses try-with-resources to ensure the reader is closed. Returns empty string if stream is null.
     */
    private static String readStream(java.io.InputStream stream) throws IOException {
        if (stream == null) return ""; // Handle cases where there might be no error stream
        StringBuilder content = new StringBuilder();
        // Use try-with-resources for automatic closing of the reader
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } // reader is automatically closed here
        return content.toString();
    }
}
