// %1876740819:de.hattrickorganizer.gui.utils%
package core.gui;

import core.util.HOLogger;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.plaf.FontUIResource;


/**
 * Shows an info frame centered in the middle of the screen.
 *
 * @author Volker Fischer
 * @version 0.2a 28.08.01
 */
public final class SplashFrame extends JFrame {
	private static final long serialVersionUID = -4948885175460734368L;
	private Image background;
	private String m_sInfotext = "";
	private String m_sVersionText = HOMainFrame.getVersionString();
	private int step;
	private int subStep = 0;
	private int maxStep = 9;
	private FontUIResource fontText = new FontUIResource("SansSerif", Font.PLAIN, 10);
	private FontUIResource fontVersion = new FontUIResource("SansSerif", Font.PLAIN, 16);
	private Color progressColor = new Color(8,115,10);

    /**
     * Creates a new InterruptionWindow object.
     */
	public SplashFrame() {
		final MediaTracker tracker = new MediaTracker(this);

		try {
			final URL resource = getClass().getClassLoader().getResource("gui/bilder/intro.png");
			background = ImageIO.read(resource);
			tracker.addImage(background, 1);

			try {
				tracker.waitForAll();
			} catch (InterruptedException ie) {
			}

			setSize(background.getWidth(null), background.getHeight(null));
			setLocation((getToolkit().getScreenSize().width / 2) - (getSize().width / 2), //
					(getToolkit().getScreenSize().height / 2) - (getSize().height / 2));

			setUndecorated(true);
			setVisible(true);
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "InterruptionWindow.<init> : " + e);
			HOLogger.instance().log(getClass(), e);
		}
    }

    /**
     * Set text info (e.g. progress).
     */
	public final void setInfoText(int step, String text) {
        m_sInfotext = text;
        if(step > maxStep)
        	step = maxStep;

        if(this.step == step)
        	subStep = subStep  + 2;
        else
        	subStep = 0;
        this.step = step;

        repaint();
    }

    /**
     * Manually implemented paint() method.
     */
    @Override
	public final void paint(Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;
        //enable antialiasing
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        //draw background image
        g2d.drawImage(background, 0, 0, null);
		// border
        g2d.drawRect(0, 0, getSize().width - 1, getSize().height - 1);
        g2d.setColor(Color.gray);
        g2d.drawRect(1, 1, getSize().width - 3, getSize().height - 3);
        g2d.setColor(Color.lightGray);
        g2d.drawRect(2, 2, getSize().width - 5, getSize().height - 5);



        //GradientPaint pat= new GradientPaint(5, 136, Color.LIGHT_GRAY, getSize().width - 10, 14, Color.WHITE);
        //g2d.setPaint(pat);
        g2d.setColor(progressColor);
        g2d.fillRect(10, 150, Math.min((step * (getSize().width/(maxStep)))+subStep,getSize().width ) , 5);

        //infotext / progress
        g2d.setColor(Color.black);
        g2d.setFont(fontText);
        g2d.drawString(m_sInfotext, 10,147);
        g2d.setFont(fontVersion);
        g2d.drawString(m_sVersionText, 125, 30);
    }

}
