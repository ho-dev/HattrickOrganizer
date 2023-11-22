package core.constants.player;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.model.match.Weather;
import java.util.LinkedHashMap;

public final class PlayerSpeciality {

	public static final int NO_SPECIALITY = 0;
	public static final int TECHNICAL = 1;
	public static final int QUICK = 2;
	public static final int POWERFUL = 3;
	public static final int UNPREDICTABLE = 4;
	public static final int HEAD = 5;
	public static final int REGAINER = 6;
	public static final int SUPPORT = 8;

	public static final int NoWeatherEffect = 0;
	public static final int PositiveWeatherEffect = 1;
	public static final int NegativeWeatherEffect = -1;

	public static final float ImpactWeatherEffect = 0.05f;

	public static final CBItem[] ITEMS = {
			new CBItem("", NO_SPECIALITY),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"), TECHNICAL),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"), QUICK),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"), POWERFUL),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"), UNPREDICTABLE),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"), HEAD),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"), REGAINER),
			new CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.support"), SUPPORT)
	};


	private static final LinkedHashMap<Integer, String> ITEMS2;

	static {
		ITEMS2 = new LinkedHashMap<>();
		ITEMS2.put(NO_SPECIALITY, "");
		ITEMS2.put(TECHNICAL, HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"));
		ITEMS2.put(QUICK, HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"));
		ITEMS2.put(POWERFUL, HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"));
		ITEMS2.put(UNPREDICTABLE, HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"));
		ITEMS2.put(HEAD, HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"));
		ITEMS2.put(REGAINER, HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"));
		ITEMS2.put(SUPPORT, HOVerwaltung.instance().getLanguageString("ls.player.speciality.support"));
	}


	private PlayerSpeciality() {
	}

	public static String toString(Integer speciality) {
		if ( speciality != null) {
			return ITEMS2.getOrDefault(speciality, HOVerwaltung.instance().getLanguageString("Unbestimmt"));
		}
		return HOVerwaltung.instance().getLanguageString("Unbestimmt");
	}


	public static int getWeatherEffect(Weather weather, int playerSpecialty) {
		switch (weather) {
			case SUNNY:

				if (playerSpecialty == TECHNICAL) {
					return PositiveWeatherEffect; //Technical players gain 5% on all their skills in the sun
				} else if (playerSpecialty == POWERFUL) {
					return NegativeWeatherEffect;   //Powerfull players loose 5% on all their skills in the rain
				} else if (playerSpecialty == QUICK) {
					return NegativeWeatherEffect;  // Quick players lose 5% in the rain and in the sun.
				}

				break;

			case RAINY:
				if (playerSpecialty == TECHNICAL) {
					return NegativeWeatherEffect;  //Technical players loose 5% on all their skills in the sun
				} else if (playerSpecialty == POWERFUL) {
					return PositiveWeatherEffect;  //Powerfull players gain 5% on all their skills in the rain
				} else if (playerSpecialty == QUICK) {
					return NegativeWeatherEffect;  // Quick players lose 5% in the rain and in the sun.
				}
				break;

			default:
				break;
		}

		return NoWeatherEffect;
	}
}
