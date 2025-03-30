package pack;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

public class interfacee {

    private JFrame frame;
    private JTextField amountField;
    private JComboBox<String> fromCurrency;
    private JComboBox<String> toCurrency;
    private JLabel resultLabel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    interfacee window = new interfacee();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public interfacee() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame("Currency Converter");
        frame.getContentPane().setBackground(Color.BLACK);
        frame.getContentPane().setForeground(Color.GREEN);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 500);
        frame.getContentPane().setLayout(null); // Set layout to null for absolute positioning

        // Create components
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setForeground(Color.GREEN);
        amountLabel.setBounds(63, 132, 80, 25); // x, y, width, height
        frame.getContentPane().add(amountLabel);

        amountField = new JTextField(20);
        amountField.setBounds(198, 132, 165, 25);
        frame.getContentPane().add(amountField);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setForeground(Color.GREEN);
        fromLabel.setBounds(63, 193, 80, 25);
        frame.getContentPane().add(fromLabel);

        fromCurrency = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD", "INR"});
        fromCurrency.setBackground(Color.LIGHT_GRAY);
        fromCurrency.setBounds(198, 193, 165, 25);
        frame.getContentPane().add(fromCurrency);

        JLabel toLabel = new JLabel("To:");
        toLabel.setForeground(Color.GREEN);
        toLabel.setBounds(63, 246, 80, 25);
        frame.getContentPane().add(toLabel);

        toCurrency = new JComboBox<>(new String[]{"USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "HKD", "INR"});
        toCurrency.setBackground(Color.LIGHT_GRAY);
        toCurrency.setBounds(198, 246, 165, 25);
        frame.getContentPane().add(toCurrency);

        JButton convertButton = new JButton("Convert");
        convertButton.setBackground(Color.LIGHT_GRAY);
        convertButton.setBounds(198, 298, 165, 25);
        frame.getContentPane().add(convertButton);

        resultLabel = new JLabel("Result:");
        resultLabel.setForeground(Color.GREEN);
        resultLabel.setBounds(63, 335, 300, 25);
        frame.getContentPane().add(resultLabel);
        
        JLabel lblWelcome = new JLabel("WELCOME ^^");
        lblWelcome.setForeground(Color.GREEN);
        lblWelcome.setBounds(63, 49, 80, 25);
        frame.getContentPane().add(lblWelcome);

        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });
    }

    private void convertCurrency() {
        try {
            double amount = Double.parseDouble(amountField.getText());
            String from = (String) fromCurrency.getSelectedItem();
            String to = (String) toCurrency.getSelectedItem();
            double result = converter.convert(amount, from, to);
            resultLabel.setText("Result: " + result);
        } catch (NumberFormatException e) {
            resultLabel.setText("Invalid amount entered.");
        } catch (Exception e) {
            resultLabel.setText("Error fetching exchange rate.");
        }
    }
}