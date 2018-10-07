package core.gui.theme.ho;

import core.gui.theme.FontUtil;
import core.model.UserParameter;
import core.util.HOLogger;

import java.awt.Font;
import java.util.Enumeration;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;


/**
 * Theme configuring HO colors, fonts and sizes.
 */
public class HOTheme extends DefaultMetalTheme {
    private static final ColorUIResource primary1 = new ColorUIResource(106, 104, 100);
    private static final ColorUIResource primary2 = new ColorUIResource(159, 156, 150);
    private static final ColorUIResource primary3 = new ColorUIResource(212, 208, 200);
    private static final ColorUIResource secondary1 = new ColorUIResource(106, 104, 100);
    private static final ColorUIResource secondary2 = new ColorUIResource(159, 156, 150);
    private static final ColorUIResource secondary3 = new ColorUIResource(212, 208, 200);
    private static FontUIResource TEXTFONT;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new HOTheme object.
     *
     * @param fontSize font size (e.g. from user parameters)
     */
    public HOTheme(int fontSize) {
        super();
       	final String fontName = FontUtil.getFontName(UserParameter.instance().sprachDatei);
		HOLogger.instance().log(getClass(), "Use Font: " + fontName + " [lang:" + UserParameter.instance().sprachDatei + "]");
		TEXTFONT = new FontUIResource((fontName != null ? fontName : "SansSerif"), Font.PLAIN, fontSize);
		setUIFont(TEXTFONT);
        //Ausnahme TitledBorderfont nicht Bold
        UIManager.put("TitledBorder.font", TEXTFONT);
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public final FontUIResource getControlTextFont() {
        return TEXTFONT;
    }

    @Override
	public final FontUIResource getMenuTextFont() {
        return TEXTFONT;
    }

    @Override
	public final FontUIResource getSubTextFont() {
        return TEXTFONT;
    }

    @Override
	public final FontUIResource getSystemTextFont() {
        return TEXTFONT;
    }

    @Override
	public final FontUIResource getUserTextFont() {
        return TEXTFONT;
    }

    @Override
	public final FontUIResource getWindowTitleFont() {
        return TEXTFONT;
    }

    // these are blue in Metal Default Theme
    @Override
	protected final ColorUIResource getPrimary1() {
        return primary1;
    }

    @Override
	protected final ColorUIResource getPrimary2() {
        return primary2;
    }

    @Override
	protected final ColorUIResource getPrimary3() {
        return primary3;
    }

    // these are gray in Metal Default Theme
    @Override
	protected final ColorUIResource getSecondary1() {
        return secondary1;
    }

    @Override
	protected final ColorUIResource getSecondary2() {
        return secondary2;
    }

    @Override
	protected final ColorUIResource getSecondary3() {
        return secondary3;
    }

    public static FontUIResource getDefaultFont() {
    	return TEXTFONT;
    }

    /**
     * Globally configure the font (size).
     */
    public static void setUIFont(FontUIResource f) {
    	try {
    		TEXTFONT = f;
			Enumeration<Object> keys = UIManager.getDefaults().keys();
			while (keys.hasMoreElements()) {
				Object key = keys.nextElement();
				Object value = UIManager.get(key);
				if (value instanceof FontUIResource) {
					UIManager.put(key, f);
				}
			}
			UIManager.put("Frame.font", f);
			UIManager.put("InternalFrame.titleFont", f);
			UIManager.put("TitledBorder.font", f);
		} catch (Exception e) {
			//HOLogger.instance().log(HO.class, "Error(setUIFont): " + e);
		}
    }
}
