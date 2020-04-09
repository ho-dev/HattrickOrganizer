package core.gui.theme;

public interface Theme {

    /**
     * Name of the theme.
     *
     * @return String – Name of the theme.
     */
    String getName();

    /**
     * Loads the theme
     * @return boolean – return true if the theme is loaded successfully, false otherwise.
     */
    boolean loadTheme();
}
