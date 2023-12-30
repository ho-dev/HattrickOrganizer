package core.gui.theme;

import core.db.AbstractTable;

import java.awt.*;

public class HOColor extends AbstractTable.Storable {

    public enum Name {
        RED,
        BLACK;

    }

    /**
     * Unique color name
     */
    private Name name;

    /**
     * Theme name
     */
    private String theme;

    /**
     * Color refers to another color.
     */
    private Name colorReference;

    /**
     * The color value
     * If null, this color refers to another color
     */
    private Color color = new Color(0);

    /**
     * Default value (used to reset user defined colors)
     */
    private HOColor defaultValue;

    HOColor(){
        super();
    }

    public HOColor(Name name, Name o) {
        this.name = name;
        this.colorReference = o;
    }

    public HOColor(Name name, Color o) {
        this.name = name;
        this.color = o;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getName() {
        return name.name();
    }

    public void setName(String name) {
        this.name = Name.valueOf(name);
    }

    public String getColorReference() {
        return colorReference.name();
    }

    public void setColorReference(String colorReference) {
        this.colorReference = Name.valueOf(colorReference);
    }

    public Integer getValue() {
        if (color != null) {
            return color.getRGB();
        }
        return null;
    }

    public void setValue(Integer v) {
        if (v != null) {
            this.color = new Color(v, true);
        } else {
            this.color = null;
        }
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String v) {
        this.theme = v;
    }

    public void setDefaultValue(Object o) {
        if ( o instanceof String){
            this.defaultValue = new HOColor(this.name, Name.valueOf((String)o));
        }
        else if (o instanceof Color){
            this.defaultValue = new HOColor(this.name, (Color)o);
        }
    }
}
