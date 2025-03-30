package pack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class converter {
    private static final String API_KEY = "87MHTyhjg6ryM2OtG5XYks8"; // Replace with your API key
    private static final String API_URL = "https://fcsapi.com/api-v3/forex/latest";

    public static double convert(double amount, String from, String to) {
        try {
            double rate = getExchangeRate(from, to);
            System.out.println("Exchange rate fetched successfully: " + rate);
            return amount * rate;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error fetching exchange rate: " + e.getMessage());
            return 0;
        }
    }
    private static double getExchangeRate(String from, String to) throws Exception {
        String urlStr = API_URL + "?symbol=" + from + "/" + to + "&access_key=" + API_KEY;
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        JSONObject json = new JSONObject(response.toString());
        JSONArray responseData = json.getJSONArray("response");
        JSONObject exchangeRateData = responseData.getJSONObject(0);
        double exchangeRate = exchangeRateData.getDouble("c");

        return exchangeRate;
    }
}