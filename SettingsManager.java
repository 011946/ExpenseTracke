package expensetracker;

import java.awt.Color;

/**
 * Singleton class for managing application settings.
 * 
 * Abstraction Function: Represents global application settings with a currency symbol and theme.
 * AF(s) = settings with currency symbol s.currencySymbol and theme s.theme
 * 
 * Representation Invariant:
 * - currencySymbol != null && !currencySymbol.isEmpty()
 * - theme != null && theme is either "Light" or "Dark"
 */
public class SettingsManager {
    private static final SettingsManager INSTANCE = new SettingsManager();
    private String currencySymbol = "$";
    private String theme = "Light";

    private SettingsManager() {
        checkRep();
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the SettingsManager instance
     * @ensures result != null
     */
    public static SettingsManager getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the currency symbol.
     * 
     * @return the currency symbol
     * @ensures result != null && !result.isEmpty()
     */
    public String getCurrencySymbol() { 
        return currencySymbol; 
    }

    /**
     * Sets the currency symbol.
     * 
     * @param symbol the new currency symbol
     * @requires symbol != null && !symbol.isEmpty()
     * @ensures this.currencySymbol == symbol
     */
    public void setCurrencySymbol(String symbol) { 
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Currency symbol cannot be null or empty");
        }
        this.currencySymbol = symbol; 
        checkRep();
    }

    /**
     * Returns the current theme.
     * 
     * @return the theme
     * @ensures result != null && result is "Light" or "Dark"
     */
    public String getTheme() { 
        return theme; 
    }

    /**
     * Sets the theme.
     * 
     * @param theme the new theme
     * @requires theme != null && theme is "Light" or "Dark"
     * @ensures this.theme == theme
     */
    public void setTheme(String theme) { 
        if (theme == null || (!theme.equals("Light") && !theme.equals("Dark"))) {
            throw new IllegalArgumentException("Theme must be 'Light' or 'Dark'");
        }
        this.theme = theme; 
        checkRep();
    }

    /**
     * Gets the background color based on the current theme.
     * 
     * @return the background color
     * @ensures result != null
     */
    public Color getBackgroundColor() {
        return theme.equals("Dark") ? new Color(30, 30, 30) : Color.WHITE;
    }

    /**
     * Gets the foreground color based on the current theme.
     * 
     * @return the foreground color
     * @ensures result != null
     */
    public Color getForegroundColor() {
        return theme.equals("Dark") ? Color.WHITE : Color.BLACK;
    }

    /**
     * Checks the representation invariant.
     */
    private void checkRep() {
        assert currencySymbol != null && !currencySymbol.isEmpty() : "Currency symbol cannot be null or empty";
        assert theme != null && (theme.equals("Light") || theme.equals("Dark")) : "Theme must be 'Light' or 'Dark'";
    }
}