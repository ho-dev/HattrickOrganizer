package core.gui.comp.panel;

import core.gui.theme.HOBooleanName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import java.awt.TexturePaint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;



public class ImagePanel extends JPanel {


    public static BufferedImage background;


    private boolean m_bPrint;

    public ImagePanel() {
        super();
        init(false);
    }


    public ImagePanel(java.awt.LayoutManager layout) {
        super(layout);
        init(false);
    }


    public ImagePanel(boolean bPrint) {
        super();
        init(bPrint);
    }

    /**
     * Creates a new ImagePanel object.
     */
    public ImagePanel(java.awt.LayoutManager layout, boolean bPrint) {
        super(layout);
        init(bPrint);
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
            background = ImageUtilities.toBufferedImage(ThemeManager.getIcon(HOIconName.IMAGEPANEL_BACKGROUND));
        }
    }
}
