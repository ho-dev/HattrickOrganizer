// %556564099:de.hattrickorganizer.gui.templates%
package core.gui.comp.panel;

import core.gui.theme.HOBooleanName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;

import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/**
 * JPanel mit HintergrundGrafik für Fenster
 *
 * @author Volker Fischer
 * @version 0.2.1a 28.02.02
 */
public class ImagePanel extends JPanel {
    //~ Static fields/initializers -----------------------------------------------------------------

    /**
	 * 
	 */
	private static final long serialVersionUID = 8960221838823071903L;

    public static BufferedImage background;

    //~ Instance fields ----------------------------------------------------------------------------

    private boolean m_bPrint;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new ImagePanel object.
     */
    public ImagePanel() {
        super();
        init(false);
    }

    /**
     * Creates a new ImagePanel object.
     */
    public ImagePanel(java.awt.LayoutManager layout) {
        super(layout);
        init(false);
    }

    /**
     * Creates a new ImagePanel object.
     */
    public ImagePanel(boolean forprint) {
        super();
        init(forprint);
    }

    /**
     * Creates a new ImagePanel object.
     */
    public ImagePanel(java.awt.LayoutManager layout, boolean forprint) {
        super(layout);
        init(forprint);
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	public final void paint(java.awt.Graphics g) {
    	if(!ThemeManager.instance().isSet(HOBooleanName.IMAGEPANEL_BG_PAINTED)){
    		super.paint(g);
    	}else {
    		final java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;

    		paintComponent(g2d);

    		if (!m_bPrint) {
                Rectangle2D tr = new Rectangle2D.Double(0, 0, background.getWidth(), background.getHeight());
                TexturePaint tp = new TexturePaint(background, tr);
                g2d.setPaint(tp);
                g2d.fill(g2d.getClip());
    		}

    		paintChildren(g2d);
    		paintBorder(g2d);
    	}
    }

    private void init(boolean printing) {
        m_bPrint = printing;

        if (background == null) {
        	 background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND).getImage());
        }
    }
}
