// %1542412551:hoplugins.trainingExperience.ui.bar%
package module.training.ui.comp;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * ColorBar: A color bar
 */
public final class ColorBar extends JComponent {

	private static final long serialVersionUID = -4167591212962253628L;
	static final int RED = 0x1000000;
	static final int GREEN = 0x0010000;
	static final int BLUE = 0x0000100;
	final float xfrac;
	/* length in x direction */
	final int length;
	int backgroundColor = 0xff0000;
	/* thickness of the bar */
	final int thickness;
	private float end;
	private final int colormask;

	/**
	 * ColorBar: initialize a color bar
	 */
	public ColorBar(float value, int len, int thick) {
		this.length = len;

		int col = GREEN;
		this.end = (int) (len * value);
		this.thickness = thick;
		this.colormask = col - (col / 256);
		this.backgroundColor = ((backgroundColor & (0xffffff ^ colormask)) ^ ((int) (value * colormask) & colormask));
		this.xfrac = (col / (float) Math.abs(length));
		paintImmediately(getBounds());
	}

	public void setLevel(float value) {
		this.end = (int) (value * length);
		paintImmediately(getBounds());
	}

	/* draw a color bar */
	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, length, thickness);

		int x = 0;
		int y = 0;
		int basecolor = (backgroundColor & (0xffffff ^ colormask));
		float amount = length - xfrac;

		for (; x < end; x += 1, amount -= xfrac) {
			int q = basecolor ^ (((int) amount) & colormask);
			g.setColor(new Color(q));
			g.drawLine(x, y, x, y + thickness);
		}
	}
}
