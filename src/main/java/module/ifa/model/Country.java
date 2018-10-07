package module.ifa.model;

import core.gui.theme.ImageUtilities;
import core.model.WorldDetailsManager;

import javax.swing.ImageIcon;

public class Country {

	private int countryId;

	public Country(int countryId) {
		this.countryId = countryId;
	}

	public int getCountryId() {
		return this.countryId;
	}

	public String getName() {
		return WorldDetailsManager.instance().getNameByCountryId(this.countryId);
	}

	public ImageIcon getCountryFlag() {
		return ImageUtilities.getFlagIcon(this.countryId);
	}

}
