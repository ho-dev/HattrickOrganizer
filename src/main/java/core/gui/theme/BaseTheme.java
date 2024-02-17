package core.gui.theme;

import core.model.UserParameter;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static core.gui.theme.HOColorName.*;

public abstract class BaseTheme implements Theme {

   private EnumMap<HOColorName, HOColor> _colors ;

   private EnumMap<HOColorName, HOColor> getColors(){
       initColorMap();
       return _colors;
   }
   private void initColorMap() {
       if ( _colors != null ) return;

       _colors = new EnumMap<>(HOColorName.class);
      /*
       * Init default color settings
       */
      addDefaultColor(RED, Color.RED);
      addDefaultColor(PINK, Color.PINK);
      addDefaultColor(BLACK, Color.BLACK);
      addDefaultColor(WHITE, Color.WHITE);
      addDefaultColor(GRAY, Color.GRAY);
      addDefaultColor(GREEN, Color.GREEN);
      addDefaultColor(YELLOW, Color.YELLOW);
      addDefaultColor(BLUE, Color.BLUE);
      addDefaultColor(ORANGE, Color.ORANGE);
      addDefaultColor(DARK_GRAY, Color.DARK_GRAY);
      addDefaultColor(LIGHT_GRAY, Color.LIGHT_GRAY);
      addDefaultColor(LIGHTGREEN, new Color(220, 255, 220));
      addDefaultColor(LIGHTYELLOW, new Color(255, 255, 200));
      addDefaultColor(HO_GRAY1, new Color(230, 230, 230));
      addDefaultColor(FOREST_GREEN, new Color(34, 139, 34));

      addDefaultColor(URL_PANEL_BG, new Color(9, 9, 167));

      addDefaultColor(PANEL_BG, new Color(214, 217, 223));
      addDefaultColor(PANEL_BORDER, DARK_GRAY);
      addDefaultColor(PLAYER_POSITION_PANEL_BORDER, LIGHT_GRAY);
      addDefaultColor(BUTTON_BG, WHITE);
      addDefaultColor(BUTTON_ASSIST_CANCEL_BG, new Color(226, 31, 31));
      addDefaultColor(BUTTON_ASSIST_OK_BG, new Color(34, 225, 36));
      addDefaultColor(LABEL_ERROR_FG, Color.RED);
      addDefaultColor(LABEL_FG, BLACK);
      addDefaultColor(LIST_FG, BLACK);
      addDefaultColor(TABLE_SELECTION_BG, new Color(235, 235, 235));
      addDefaultColor(TABLE_SELECTION_FG, LABEL_FG);
      addDefaultColor(LIST_SELECTION_BG, new Color(220, 220, 255));

      // player
      addDefaultColor(PLAYER_SKILL_SPECIAL_BG, LIGHTGREEN);
      addDefaultColor(PLAYER_SKILL_BG, LIGHTYELLOW);
      addDefaultColor(TABLEENTRY_BG, WHITE);
      addDefaultColor(BACKGROUND_CONTAINER, Color.WHITE);
      addDefaultColor(TABLEENTRY_FG, BLACK);
      addDefaultColor(PLAYER_POS_BG, new Color(220, 220, 255));
      addDefaultColor(PLAYER_SUBPOS_BG, new Color(235, 235, 255));
      addDefaultColor(PLAYER_OLD_FG, GRAY);
      addDefaultColor(TABLEENTRY_IMPROVEMENT_FG, new Color(34, 139, 34));
      addDefaultColor(TABLEENTRY_DECLINE_FG, new Color(235, 0, 0));
      addDefaultColor(SKILLENTRY2_BG, GRAY);

      // league Table
      addDefaultColor(HOME_TEAM_FG, new Color(179, 60, 180));
      addDefaultColor(SELECTED_TEAM_FG, new Color(36, 90, 235));
      addDefaultColor(LEAGUE_TITLE_BG, HO_GRAY1);
      addDefaultColor(LEAGUE_BG, WHITE);
      addDefaultColor(LEAGUE_FG, BLACK);
      addDefaultColor(LEAGUE_PANEL_BG, Color.WHITE);

      // league history panel
      addDefaultColor(LEAGUEHISTORY_CROSS_FG, LIGHT_GRAY);

      // lineup
      addDefaultColor(SEL_OVERLAY_SELECTION_BG, new Color(10, 255, 10, 40));
      addDefaultColor(SEL_OVERLAY_BG, new Color(255, 10, 10, 40));
      addDefaultColor(SUBST_CHANGED_VALUE_BG, LIGHTGREEN);
      addDefaultColor(SWAP_COLOR, FOREST_GREEN);
      addDefaultColor(RESET_COLOR, Color.RED);
      addDefaultColor(SWAP_COLOR_PRESSED, Color.RED);
      addDefaultColor(LINEUP_PARTIAL_TRAINING, new Color(34, 255, 255));
      addDefaultColor(LINEUP_FULL_TRAINING, new Color(0, 0, 255));
      addDefaultColor(LINEUP_PLAYER_SELECTED, Color.LIGHT_GRAY);
      addDefaultColor(LINEUP_PLAYER_SUB, new Color(220, 220, 220));
      addDefaultColor(WARNING_ICON_CB_COLOR, Color.RED);
      addDefaultColor(RATING_BORDER_BELOW_LIMIT, new Color(255, 0, 0));
      addDefaultColor(RATING_BORDER_ABOVE_LIMIT, new Color(0, 0, 225));
      addDefaultColor(START_ASSISTANT, new Color(34, 139, 34));
      addDefaultColor(CLEAR_LINEUP, new Color(255, 0, 0));
      addDefaultColor(LINEUP_COLOR, new Color(0, 0, 139));
      addDefaultColor(LINEUP_HIGHLIGHT_FG, new Color(0, 0, 139));
      addDefaultColor(ORDERS_LINEUP, new Color(190, 190, 190));
      addDefaultColor(ORDERS_TICK, new Color(45, 75, 45));
      addDefaultColor(ORDERS_PEN, new Color(139, 0, 0));

      //matches module
      addDefaultColor(HOME_ACTION, new Color(110, 205, 234));
      addDefaultColor(GUEST_ACTION, new Color(209, 94, 94));
      addDefaultColor(NEUTRAL_ACTION, new Color(166, 166, 166));
      addDefaultColor(BORDER_RATING_BAR, Color.BLACK);

      // shirts
      addDefaultColor(SHIRT_KEEPER, BLACK);
      addDefaultColor(SHIRT_CENTRALDEFENCE, new Color(0, 0, 220));
      addDefaultColor(SHIRT_WINGBACK, new Color(0, 220, 0));
      addDefaultColor(SHIRT_MIDFIELD, new Color(220, 220, 0));
      addDefaultColor(SHIRT_WING, new Color(220, 140, 0));
      addDefaultColor(SHIRT_FORWARD, new Color(220, 0, 0));
      addDefaultColor(SHIRT_SUBKEEPER, new Color(200, 200, 200));
      addDefaultColor(SHIRT_SUBDEFENCE, new Color(200, 200, 255));
      addDefaultColor(SHIRT_SUBMIDFIELD, new Color(255, 255, 180));
      addDefaultColor(SHIRT_SUBWING, new Color(255, 225, 180));
      addDefaultColor(SHIRT_SUBFORWARD, new Color(255, 200, 200));
      addDefaultColor(SHIRT, HO_GRAY1);

      // Smileys
      addDefaultColor(SMILEYS_COLOR, BLACK);

      // Players specialty Colors
      addDefaultColor(PLAYER_SPECIALTY_COLOR, BLACK);

      addDefaultColor(PLAYER_STAR_COLOR, new Color(255, 215, 0));
      addDefaultColor(STAT_PANEL_BG, Color.WHITE);
      addDefaultColor(STAT_PANEL_FG, Color.DARK_GRAY);

      // matchtypes
      addDefaultColor(MATCHTYPE_BG, WHITE);
      addDefaultColor(MATCHTYPE_LEAGUE_BG, LIGHTYELLOW);
      addDefaultColor(MATCHTYPE_QUALIFIKATION_BG, new Color(255, 200, 200));
      addDefaultColor(MATCHTYPE_CUP_BG, new Color(200, 255, 200));
      addDefaultColor(MATCHTYPE_FRIENDLY_BG, Color.WHITE);
      addDefaultColor(MATCHTYPE_MASTERS_BG, new Color(255, 215, 120));
      addDefaultColor(MATCHTYPE_INTFRIENDLY_BG, WHITE);
      addDefaultColor(MATCHTYPE_NATIONAL_BG, new Color(240, 220, 255));
      addDefaultColor(MATCHTYPE_TOURNAMENT_GROUP_BG, new Color(218, 237, 247));
      addDefaultColor(MATCHTYPE_TOURNAMENT_FINALS_BG, new Color(218, 237, 247));
      addDefaultColor(MATCHTYPE_DIVISIONBATTLE_BG, new Color(200, 210, 247));

      // Transfer module
      addDefaultColor(TRANSFER_IN_COLOR, GREEN);
      addDefaultColor(TRANSFER_OUT_COLOR, RED);

      // Colours for form streak in league details
      addDefaultColor(FORM_STREAK_WIN, new Color(73, 146, 45));
      addDefaultColor(FORM_STREAK_DRAW, new Color(111, 111, 111));
      addDefaultColor(FORM_STREAK_DEFEAT, new Color(224, 51, 51));
      addDefaultColor(FORM_STREAK_UNKNOWN, new Color(170, 170, 170));

      // Colours for alternating rows in table
      addDefaultColor(TABLE_LEAGUE_EVEN, Color.WHITE);
      addDefaultColor(TABLE_LEAGUE_ODD, new Color(240, 240, 240));

      // Training
      addDefaultColor(TRAINING_BIRTHDAY_BG, new Color(255, 240, 175));
      addDefaultColor(TRAINING_FULL_BG, LIGHTGREEN);
      addDefaultColor(TRAINING_PARTIAL_BG, LIGHTYELLOW);
      addDefaultColor(TRAINING_OSMOSIS_BG, Color.LIGHT_GRAY);
      addDefaultColor(TRAINING_ICON_COLOR_1, new Color(0, 0, 0));
      addDefaultColor(TRAINING_ICON_COLOR_2, new Color(255, 255, 255));

      // Training bars
      addDefaultColor(FULL_TRAINING_DONE, new Color(37, 110, 9));
      addDefaultColor(PARTIAL_TRAINING_DONE, new Color(73, 208, 21));
      addDefaultColor(FULL_STAMINA_DONE, new Color(69, 127, 217));
      addDefaultColor(FULL_TRAINING_PLANNED, new Color(56, 56, 56));
      addDefaultColor(PARTIAL_TRAINING_PLANNED, new Color(184, 184, 184, 184));
      addDefaultColor(STAMINA_PLANNED, new Color(104, 242, 255));

      // TS Forecast
      addDefaultColor(TSFORECAST_ALT_COLOR, Color.BLUE);

      // HRF Explorer
      addDefaultColor(HRF_GREEN_BG, new Color(220, 255, 220));
      addDefaultColor(HRF_LIGHTBLUE_BG, new Color(235, 235, 255));
      addDefaultColor(HRF_DARKBLUE_BG, new Color(220, 220, 255));
      addDefaultColor(HRF_RED_BG, new Color(255, 200, 200));

      // Player State colours
      addDefaultColor(FG_INJURED, new Color(200, 0, 0));
      addDefaultColor(INJURY, new Color(255, 0, 0));
      addDefaultColor(PLASTER, new Color(247, 195, 176));

      //players details
      addDefaultColor(PLAYER_DETAILS_BAR_BORDER_COLOR, Color.BLACK);
      addDefaultColor(PLAYER_DETAILS_BAR_FILL_GREEN, Color.GREEN);
      addDefaultColor(PLAYER_DETAILS_STARS_FILL, Color.YELLOW);

      // Promotion
      addDefaultColor(FG_PROMOTION_INFO, new Color(238, 39, 39, 255));

      // palettes
      addDefaultColor(PALETTE13_0, new Color(255, 204, 0));
      addDefaultColor(PALETTE13_1, new Color(23, 111, 36));
      addDefaultColor(PALETTE13_2, new Color(255, 59, 48));
      addDefaultColor(PALETTE13_3, new Color(49, 220, 209));
      addDefaultColor(PALETTE13_4, new Color(162, 132, 94));
      addDefaultColor(PALETTE13_5, new Color(147, 250, 45));
      addDefaultColor(PALETTE13_6, new Color(0, 122, 255));
      addDefaultColor(PALETTE13_7, new Color(255, 149, 0));
      addDefaultColor(PALETTE13_8, new Color(88, 86, 214));
      addDefaultColor(PALETTE13_9, new Color(142, 142, 147));
      addDefaultColor(PALETTE13_10, new Color(175, 82, 222));
      addDefaultColor(PALETTE13_11, new Color(40, 205, 65));
      addDefaultColor(PALETTE13_12, new Color(90, 200, 250));

      // League Details
      addDefaultColor(SHOW_MATCH, new Color(23, 111, 36));
      addDefaultColor(DOWNLOAD_MATCH, new Color(238, 39, 39, 255));

      addDefaultColor(LINK_LABEL_FG, new Color(6, 69, 173));
   }

   private void addDefaultColor(HOColorName hoColorName, Color color) {
      addColor(hoColorName, "default", color);
   }
   private void addDefaultColor(HOColorName hoColorName, HOColorName colorReference) {
      addColor(hoColorName, "default", colorReference);
   }

   public void addColor(HOColorName hoColorName, Color color){
      addColor(hoColorName, this.getName(), color);
   }

   private void addColor(HOColorName hoColorName, String theme, Color color) {
      addColor(new HOColor(hoColorName, theme, color));
   }

   public void addColor(HOColorName hoColorName, HOColorName colorReference){
      addColor(hoColorName, this.getName(), colorReference);
   }

   private void addColor(HOColorName hoColorName, String theme, HOColorName colorReference) {
      addColor(new HOColor(hoColorName, theme, colorReference));
   }

   /**
    * Get color value
    *
    * @param name  Color name
    * @return Color or null if no entry is found
    */
   public Color getColor(HOColorName name) {
      return getColor(name, new ArrayList<>());
   }

   /**
    * Get color value
    *
    * @param name       Color name
    * @param colorNames Color reference names (used to prevent recursions)
    * @return Color or null if no entry if found
    */
   private Color getColor(HOColorName name, List<String> colorNames) {
       if (!colorNames.contains(name.name())) { // break endless recursion
           var hoColor = getColors().get(name);
           if (hoColor != null) {
               var ref = hoColor.colorReference();
               if (ref != null) {
                   colorNames.add(hoColor.getName());
                   return getColor(ref, colorNames);
               } else {
                   return hoColor.getColor();
               }
           }
       }
       return null;
   }

   /**
    * Get color setting.
    * If no entry in specified theme is found, the default color is returned.
    *
    * @param name  Color name
    * @return Color setting or null if no entry is found
    */
   public HOColor getHOColor(HOColorName name) {
      return getColors().get(name);
   }
   public List<HOColor> getHOColors() { return getColors().values().stream().toList(); }

   /**
    * Add new color setting to the settings
    *
    * @param color Color setting
    */
   public void addColor(HOColor color) {
      getColors().put(color.getHOColorName(), color);
   }

    /**
     * Initialize the default value with the current setting
     * if default is not already set
     */
    public void initDefaultValue(HOColor color) {
        if (color.getDefaultValue() == null) {
            if (color.colorReference() != null) {
                color.setDefaultValue(getHOColor(color.colorReference()));
            } else {
                var defaultValue = new HOColor();
                defaultValue.setName(color.getName());
                defaultValue.setColor(color.getColor());
                defaultValue.setColorReference(null);
                defaultValue.setTheme(color.getTheme());
                color.setDefaultValue(defaultValue);
            }
        }
    }




    protected void setFont(int fontSize) {
        UIDefaults uid = UIManager.getLookAndFeelDefaults();

        UIManager.put("Table.rowHeight", fontSize * 1.5);

        final String fontName = FontUtil.getFontName(UserParameter.instance().sprachDatei);
        final Font userFont = new Font((fontName != null ? fontName : "SansSerif"), Font.PLAIN, fontSize);
        final Font boldFont = new Font((fontName != null ? fontName : "SansSerif"), Font.BOLD, fontSize);
        uid.put("defaultFont", userFont);
        uid.put("DesktopIcon.font", userFont);
        uid.put("FileChooser.font", userFont);
        uid.put("RootPane.font", userFont);
        uid.put("TextPane.font", userFont);
        uid.put("FormattedTextField.font", userFont);
        uid.put("Spinner.font", userFont);
        uid.put("PopupMenuSeparator.font", userFont);
        uid.put("Table.font", userFont);
        uid.put("TextArea.font", userFont);
        uid.put("Slider.font", userFont);
        uid.put("InternalFrameTitlePane.font", userFont);
        uid.put("ColorChooser.font", userFont);
        uid.put("DesktopPane.font", userFont);
        uid.put("Menu.font", userFont);
        uid.put("PasswordField.font", userFont);
        uid.put("InternalFrame.font", userFont);
        uid.put("InternalFrame.titleFont", boldFont);
        uid.put("Button.font", userFont);
        uid.put("Panel.font", userFont);
        uid.put("MenuBar.font", userFont);
        uid.put("ComboBox.font", userFont);
        uid.put("Tree.font", userFont);
        uid.put("EditorPane.font", userFont);
        uid.put("CheckBox.font", userFont);
        uid.put("ToggleButton.font", userFont);
        uid.put("TabbedPane.font", userFont);
        uid.put("TableHeader.font", userFont);
        uid.put("List.font", userFont);
        uid.put("PopupMenu.font", userFont);
        uid.put("ToolTip.font", userFont);
        uid.put("Separator.font", userFont);
        uid.put("RadioButtonMenuItem.font", userFont);
        uid.put("RadioButton.font", userFont);
        uid.put("ToolBar.font", userFont);
        uid.put("ScrollPane.font", userFont);
        uid.put("CheckBoxMenuItem.font", userFont);
        uid.put("Viewport.font", userFont);
        uid.put("TextField.font", userFont);
        uid.put("SplitPane.font", userFont);
        uid.put("MenuItem.font", userFont);
        uid.put("OptionPane.font", userFont);
        uid.put("ArrowButton.font", userFont);
        uid.put("Label.font", userFont);
        uid.put("ProgressBar.font", userFont);
        uid.put("ScrollBar.font", userFont);
        uid.put("ScrollBarThumb.font", userFont);
        uid.put("ScrollBarTrack.font", userFont);
        uid.put("SliderThumb.font", userFont);
        uid.put("SliderTrack.font", userFont);
        uid.put("TitledBorder.font", boldFont);
    }
}
