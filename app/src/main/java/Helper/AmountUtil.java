package Helper;

import java.text.DecimalFormat;

public class AmountUtil {

    /**
     * Parses a formatted amount string (e.g. "1,234.56") into a double.
     *
     * @param input the formatted string
     * @return the double value
     */
    public static double ParseFormattedStringToDouble(String input) {
        if (input == null || input.isEmpty()) {
            return 0.0; // or throw exception, up to you
        }
        // Remove commas and trim whitespace
        String cleaned = input.replace(",", "").trim();
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0; // or handle how you like
        }
    }
    
    /**
     * Formats a double amount to a string with commas and two decimal places.
     *
     * Example: 1234.56 => "1,234.56"
     *
     * @param amount the double amount
     * @return the formatted string
     */
    public static String FormatAmount(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");
        return formatter.format(amount);
    }
}
