package core.gui.theme;

import core.db.AbstractTable;
import java.awt.*;
import java.util.*;
import java.util.List;

import static core.gui.theme.ThemeManager.getCurrentThemeName;
import static core.gui.theme.ThemeManager.getHOColor;

public class HOColor extends AbstractTable.Storable implements Cloneable {

//    /**
//     * Get color value
//     *
//     * @param name  Color name
//     * @param theme Theme name
//     * @return Color or null if no entry is found
//     */
//    public static Color getColor(HOColorName name, String theme) {
//        return getColor(name, theme, new ArrayList<>());
//    }

//    /**
//     * Get color value
//     *
//     * @param name       Color name
//     * @param theme      Theme name
//     * @param colorNames Color reference names (used to prevent recursions)
//     * @return Color or null if no entry if found
//     */
//    private static Color getColor(HOColorName name, String theme, List<String> colorNames) {
//        ThemeManager.instance().getRegisteredThemes();
//        if (!colorNames.contains(name.name())) { // break endless recursion
//            var hoColor = getHOColor(name, theme);
//            if (hoColor != null) {
//                var ref = hoColor.colorReference;
//                if (ref != null) {
//                    colorNames.add(hoColor.getName());
//                    return getColor(ref, theme, colorNames);
//                } else {
//                    return hoColor.getColor();
//                }
//            }
//        }
//        return null;
//    }

//    /**
//     * Get color settings of theme
//     *
//     * @param theme Theme name
//     * @return List of colors
//     */
//    public static List<HOColor> getColors(String theme) {
//        var ret = new ArrayList<HOColor>();
//        for (var name : HOColorName.values()) {
//            ret.add(getHOColor(name, theme));
//        }
//        return ret;
//    }

//    /**
//     * Get color setting.
//     * If no entry in specified theme is found, the default color is returned.
//     *
//     * @param name  Color name
//     * @param theme Theme name
//     * @return Color setting or null if no entry is found
//     */
//    public static HOColor getHOColor(HOColorName name, String theme) {
//        var colorMap = colors.get(name);
//        if (colorMap != null) {
//            var hoColor = colorMap.get(theme);
//            if (hoColor != null) return hoColor;
//            hoColor = colorMap.get("default");
//            return hoColor;
//        }
//        return null;
//    }
//
//    /**
//     * Add new color setting to the settings
//     *
//     * @param name  Name of the color
//     * @param color Color value
//     */
//    private static void addColor(HOColorName name, Color color) {
//        addColor(new HOColor(name, color));
//    }
//
//    /**
//     * Add new color setting to the settings
//     *
//     * @param name           Name of the color
//     * @param colorReference Color reference name
//     */
//    private static void addColor(HOColorName name, HOColorName colorReference) {
//        addColor(new HOColor(name, colorReference));
//    }
//
//    /**
//     * Add new color setting to the settings
//     *
//     * @param color Color setting
//     */
//    public static void addColor(HOColor color) {
//        var colorMap = colors.computeIfAbsent(color.name, k -> new HashMap<>());
//        colorMap.put(color.theme, color);
//    }

    /**
     * Unique color name
     */
    private HOColorName name;

    /**
     * Theme name
     */
    private String theme;

    /**
     * Color refers to another color.
     */
    private HOColorName colorReference;

    /**
     * The color value
     * If null, this color refers to another color
     */
    private Color color = new Color(0);

    /**
     * Default value (used to reset user defined colors)
     */
    private HOColor defaultValue;

    /**
     * Used by HOColorTable
     */
    public HOColor() {
        super();
    }

    /**
     * Default color setting
     *
     * @param name Color name
     * @param o    Color reference name
     */
    public HOColor(HOColorName name, HOColorName o) {
        this(name, "default", o);
    }

    /**
     * Default color setting
     *
     * @param name Color name
     * @param o    Color value
     */
    private HOColor(HOColorName name, Color o) {
        this(name, "default", o);
    }

    /**
     * Color setting
     *
     * @param name  Color name
     * @param theme Theme name
     * @param o     Color value
     */
    public HOColor(HOColorName name, String theme, Color o) {
        this.name = name;
        this.theme = theme;
        this.color = o;
    }

    /**
     * Color setting
     *
     * @param name  Color name
     * @param theme Theme name
     * @param o     Color reference name
     */
    public HOColor(HOColorName name, String theme, HOColorName o) {
//        assert null != getColor(o, theme) : "Color " + o.name() + " not found";
        this.name = name;
        this.theme = theme;
        this.colorReference = o;
    }

    /**
     * Clone the color setting
     *
     * @return Color setting
     */
    @Override
    public HOColor clone() {
        HOColor ret;
        try {
            ret = (HOColor) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
        if (this.color != null) {
            ret.color = new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.color.getAlpha());
        }
        if (this.defaultValue != null) {
            ret.defaultValue = this.defaultValue.clone();
        }
        return ret;
    }

    /**
     * Get the color value
     * This is null if a color reference is set.
     *
     * @return Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color value
     *
     * @param color Color
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get color setting name
     *
     * @return String
     */
    public String getName() {
        return name.name();
    }

    /**
     * Get color setting name
     *
     * @return HOColorName
     */
    public HOColorName getHOColorName() {
        return name;
    }

    /**
     * Set the color setting name
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = HOColorName.valueOf(name);
    }

    /**
     * Get the color reference name
     *
     * @return HOColorName
     */
    public HOColorName colorReference() {
        return colorReference;
    }

    /**
     * Get the color reference name
     *
     * @return String
     */
    public String getColorReference() {
        if (colorReference != null) return colorReference.name();
        return null;
    }

    /**
     * Set the color reference name
     *
     * @param colorReference String
     */
    public void setColorReference(String colorReference) {
        if (colorReference != null && !colorReference.isEmpty()) {
            this.colorReference = HOColorName.valueOf(colorReference);
        } else {
            this.colorReference = null;
        }
    }

    /**
     * Get color value
     * (used by the database table)
     *
     * @return Integer
     */
    public Integer getValue() {
        if (color != null) {
            return color.getRGB();
        }
        return null;
    }

    /**
     * Set color value
     * (used by the database table)
     *
     * @param v Integer
     */
    public void setValue(Integer v) {
        if (v != null) {
            this.color = new Color(v, true);
        } else {
            this.color = null;
        }
    }

    /**
     * Get theme name
     *
     * @return String
     */
    public String getTheme() {
        return theme;
    }

    /**
     * Set theme name
     *
     * @param v String
     */
    public void setTheme(String v) {
        this.theme = v;
    }

    /**
     * Set the default value
     *
     * @param o Color setting
     */
    public void setDefaultValue(HOColor o) {
        this.defaultValue = o;
    }

    /**
     * Get the default value
     *
     * @return Default color setting
     */
    public HOColor getDefaultValue() {
        return this.defaultValue;
    }

//    /**
//     * Initialize the default value with the current setting
//     * if default is not already set
//     */
//    public void initDefaultValue() {
//        if (this.defaultValue == null) {
//            if (this.colorReference != null) {
//                this.defaultValue = HOColor.getHOColor(this.colorReference, this.theme);
//            } else {
//                this.defaultValue = new HOColor();
//                this.defaultValue.name = this.name;
//                this.defaultValue.color = this.color;
//                this.defaultValue.colorReference = null;
//                this.defaultValue.theme = this.theme;
//            }
//        }
//    }


    /**
     * Compare two color settings
     *
     * @param c1 Color setting, may be null
     * @param c2 Color setting, may  be null
     * @return True, if the settings are different
     */
    public static boolean areDifferentColors(HOColor c1, HOColor c2) {
        if (c1 == null || c2 == null) return c1 != c2;
        return areDifferent(c1.getColor(), c2.getColor()) || areDifferent(c1.colorReference(), c2.colorReference());
    }

    /**
     * Compare two objects
     *
     * @param o1 Object, may  be null
     * @param o2 Object, may  be null
     * @return True, if the objects are different
     */
    private static boolean areDifferent(Object o1, Object o2) {
        if (o1 == o2) return false;
        if (o1 == null) return true;
        return !o1.equals(o2);
    }

    /**
     * Get the color value of the color setting
     * If no value is set, the corresponding reference is searched in argument setting list.
     *
     * @param colors Color settings, used to find referenced color setting
     * @return Color value
     */
    public Color getColor(List<HOColor> colors) {
        if (this.color == null) {
            if (this.colorReference != null) {
                var hoColor = colors.stream().filter(i -> i.getName().equals(this.getName())).findFirst();
                if (hoColor.isPresent()) {
                    return hoColor.get().getColor(colors);
                }
            }
        }
        return this.color;
    }
}