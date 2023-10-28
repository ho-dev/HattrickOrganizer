package module.ifa;


import java.awt.Graphics;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;

import javax.swing.GrayFilter;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class FlagLabel extends JLabel {

	private static final long serialVersionUID = 2988734870943038292L;
	private String countryName;
	private int countryId;
	private boolean homeCountry;
	private FlagDisplayModel flagDisplayModel;

	public FlagLabel(FlagDisplayModel flagDisplayModel) {
		this.flagDisplayModel = flagDisplayModel;
	}

	public int getCountryId() {
		return this.countryId;
	}

	public void setCountryId(int countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return this.countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public boolean isHomeCountry() {
		return this.homeCountry;
	}

	public void setHomeCountry(boolean homeCountry) {
		this.homeCountry = homeCountry;
	}

	@Override
	public void setEnabled(boolean enabled) {
		ImageIcon src = (ImageIcon) getIcon();
		ImageFilter filter = null;
		if (this.flagDisplayModel.isGrey()) {
			filter = new GrayFilter(true, 100 - this.flagDisplayModel.getBrightness());
		} else {
			filter = new TransparentFilter(this.flagDisplayModel.getBrightness());
		}
		ImageProducer imageprod = new FilteredImageSource(src.getImage().getSource(), filter);
		ImageIcon img = new ImageIcon(createImage(imageprod));
		setDisabledIcon(img);
		super.setEnabled(enabled);
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (this.flagDisplayModel.isRoundFlag() && !this.homeCountry) {
			RoundRectangle2D.Double oval = new RoundRectangle2D.Double(0.0D, 0.0D, getSize()
					.getWidth(), getSize().getHeight(), 9.0D, 9.0D);
			g.setClip(oval);
		}
		super.paintComponent(g);
	}

	private class TransparentFilter extends RGBImageFilter {
		private int percent;

		public TransparentFilter(int percent) {
			this.percent = percent;
		}

		@Override
		public int filterRGB(int x, int y, int rgb) {
			int result = 255 * this.percent / 100;
			result <<= 24;
			result ^= 16777215;
			return rgb & result;
		}
	}
}
