package core.gui.comp.entry;

import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.match.MatchType;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LastMatchLabelEntry extends AbstractHOTableEntry {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static ImageIcon FULL10STARIMAGEICON;
    private static ImageIcon FULLGREY10STARIMAGEICON;
    private static ImageIcon FULL5STARIMAGEICON;
    private static ImageIcon FULLGREY5STARIMAGEICON;
    private static ImageIcon FULL50STARIMAGEICON;
    private static ImageIcon FULLGREY50STARIMAGEICON;
    private static ImageIcon FULLSTARIMAGEICON;
    private static ImageIcon HALFSTARIMAGEICON;
    private static ImageIcon FULLGREYSTARIMAGEICON;
    private static ImageIcon HALFGREYSTARIMAGEICON;
    private static ImageIcon BALLIMAGEICON;

    //~ Instance fields ----------------------------------------------------------------------------

    private JComponent m_clComponent = new JPanel();
    private JLabel matchLink = new JLabel("");
    private String m_sTooltip = "";
    private boolean m_bYellowStar=true;
    private float m_fRating;
    private Date m_lastMatchDate;
    private boolean isOpaque = true;
    private Color bgColor = ColorLabelEntry.BG_STANDARD;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new LastMatchLabelEntry object.
     */
    public LastMatchLabelEntry() {
        initStarsIcons();
        m_fRating = 0.0F;
        createComponent();
    }

    /**
     * stefa
     * Creates a new LastMatchLabelEntry object.
     * @param f - rating
     */
    public LastMatchLabelEntry(float f) {
        initStarsIcons();
        m_fRating =f;
        createComponent();
    }

    /**
     * stefa
     * Creates a new LastMatchLabelEntry object.
     * @param f - rating
     * @param t - date of last match
     * @param matchType - match type
     */
    public LastMatchLabelEntry( float f, String t, MatchType matchType) {
        initStarsIcons();
        m_fRating =f;
        createComponent();
        setMatchInfo(t,matchType);
    }

    //~ Methods ------------------------------------------------------------------------------------

	public final JComponent getComponent(boolean isSelected) {
        m_clComponent.setBackground((isSelected)?HODefaultTableCellRenderer.SELECTION_BG:bgColor);
        m_clComponent.setOpaque(isOpaque);
        
        return m_clComponent;
    }

    public final float getRating() {
        return m_fRating;
    }

    public final Date getDate() {
        return m_lastMatchDate;
    }

    public final void setToolTipText(String text) {
        m_sTooltip = text;
        updateComponent();
    }

    /**
     * Create match link
     * steffano
     * @param t
     * @param matchType
     */
    public final void setMatchInfo(String t, MatchType matchType) {

        try {
            //2020-05-06 09:30:00
            m_lastMatchDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(t);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            matchLink.setText("  ("+dateFormat.format(m_lastMatchDate)+")");
            BALLIMAGEICON = ThemeManager.getIcon(HOIconName.MATCHICONS[matchType.getIconArrayIndex()]);
            matchLink.setIcon(BALLIMAGEICON);
        } catch (ParseException e) {
        }
        updateComponent();
    }


	public final void clear() {
        m_clComponent.removeAll();
        //Platzhalter
        JLabel jlabel;
        jlabel = new JLabel(ImageUtilities.NOIMAGEICON);
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        m_clComponent.add(jlabel);
    }

	public final int compareTo(IHOTableEntry obj) {
        if (obj instanceof LastMatchLabelEntry) {
            final LastMatchLabelEntry entry = (LastMatchLabelEntry) obj;
            if (entry.getDate()==null || (entry.getDate()!=null && m_lastMatchDate!=null && m_lastMatchDate.after(entry.getDate()))) {
                return -1;
            } else if (m_lastMatchDate==null || (entry.getDate()!=null && m_lastMatchDate!=null && m_lastMatchDate.before(entry.getDate()))) {
                return 1;
            } else {
                if (getRating() > entry.getRating()) {
                    return -1;
                } else if (getRating() < entry.getRating()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }

        return 0;
    }

	public final void createComponent() {
        float f = m_fRating / 2;
        JPanel renderer = new JPanel();
        renderer.setLayout(new BoxLayout(renderer, 0));
        renderer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));


        renderer.add(matchLink);
        renderer.add(new Label(" ")); // space
        setStars(renderer,f);
        renderer.setToolTipText(m_sTooltip);

        m_clComponent = renderer;
    }

    /**
     * 
     * @param panel
     * @param yellowImage
     * @param grayImage
     */
    private void addLabel(JComponent panel,ImageIcon yellowImage, ImageIcon grayImage){
    	final JLabel jlabel = new JLabel((m_bYellowStar)?yellowImage:grayImage);
        jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.add(jlabel);
    }
    
	public final void updateComponent() {

        m_clComponent.repaint();
    }
    
    private void setStars(JComponent panel, float f) {
		if (f == 0) {
			JLabel jlabel;
			jlabel = new JLabel(ImageUtilities.NOIMAGEICON);
			jlabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			panel.add(jlabel);
		}

		while (f >= 50) {
			addLabel(panel, FULL50STARIMAGEICON, FULLGREY50STARIMAGEICON);
			f -= 50;
		}

		while (f >= 10) {
			addLabel(panel, FULL10STARIMAGEICON, FULLGREY10STARIMAGEICON);
			f -= 10;
		}

		while (f >= 5) {
			addLabel(panel, FULL5STARIMAGEICON, FULLGREY5STARIMAGEICON);
			f -= 5;
		}

		while (f >= 1) {
			addLabel(panel, FULLSTARIMAGEICON, FULLGREYSTARIMAGEICON);
			f -= 1;
		}

		if (f == 0.5) {
			addLabel(panel, HALFSTARIMAGEICON, HALFGREYSTARIMAGEICON);
		}
	}
    
    private void initStarsIcons(){
    	if ((FULLSTARIMAGEICON == null) || (HALFSTARIMAGEICON == null)) {
            FULL10STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_10.png"),
                                                                                  210, 210, 185,
                                                                                  255, 255, 255));
            FULLGREY10STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_10_grey.png"),
                                                                                      215, 215,
                                                                                      215, 255,
                                                                                      255, 255));
            FULL5STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_5.png"),
                                                                                 210, 210, 185,
                                                                                 255, 255, 255));
            FULLGREY5STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_5_grey.png"),
                                                                                     215, 215, 215,
                                                                                     255, 255, 255));
            FULL50STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_50.png"),
                                                                                  210, 210, 185,
                                                                                  255, 255, 255));
            FULLGREY50STARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_50_grey.png"),
                                                                                      215, 215,
                                                                                      215, 255,
                                                                                      255, 255));
            FULLSTARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star.gif"),
                                                                                210, 210, 185, 255,
                                                                                255, 255));
            HALFSTARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_half.gif"),
                                                                                210, 210, 185, 255,
                                                                                255, 255));
            FULLGREYSTARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_grey.png"),
                                                                                    215, 215, 215,
                                                                                    255, 255, 255));
            HALFGREYSTARIMAGEICON = new ImageIcon(ImageUtilities.makeColorTransparent(ThemeManager.loadImage("gui/bilder/star_grey_half.png"),
                                                                                    215, 215, 215,
                                                                                    255, 255, 255));
        }	
    }



}
