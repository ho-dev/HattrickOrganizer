package core.constants.player;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.util.Helper;

public final class PlayerAbility {
	private static final String[] languageKeys = {
		"ls.player.skill.value.non-existent",
		"ls.player.skill.value.disastrous",
		"ls.player.skill.value.wretched",
		"ls.player.skill.value.poor",
		"ls.player.skill.value.weak",
		"ls.player.skill.value.inadequate",
		"ls.player.skill.value.passable",
		"ls.player.skill.value.solid",
		"ls.player.skill.value.excellent",
		"ls.player.skill.value.formidable",
		"ls.player.skill.value.outstanding",
		"ls.player.skill.value.brilliant",
		"ls.player.skill.value.magnificent",
		"ls.player.skill.value.worldclass",
		"ls.player.skill.value.supernatural",
		"ls.player.skill.value.titanic",
		"ls.player.skill.value.extra-terrestrial",
		"ls.player.skill.value.mythical",
		"ls.player.skill.value.magical",
		"ls.player.skill.value.utopian",
		"ls.player.skill.value.divine"};

	public static final int NON_EXISTENT 	= 0;
	public static final int DISASTROUS 		= 1;
	public static final int WRETCHED 		= 2;
	public static final int POOR 			= 3;
	public static final int WEAK			= 4;
	public static final int INADEQUATE 		= 5;
	public static final int PASSABLE 		= 6;
	public static final int SOLID 			= 7;
	public static final int EXCELLENT 		= 8;
	public static final int FORMIDABLE 		= 9;
	public static final int OUTSTANDING 	= 10;
	public static final int BRILLIANT 		= 11;
	public static final int MAGNIFICENT 	= 12;
	public static final int WORLD_CLASS 	= 13;
	public static final int SUPERNATURAL 	= 14;
	public static final int TITANIC 		= 15;
	public static final int EXTRA_TERRESTRIAL = 16;
	public static final int MYTHICAL 		= 17;
	public static final int MAGICAL 		= 18;
	public static final int UTOPIAN 		= 19;
	public static final int DIVINE 			= 20;

	public static final CBItem[] ITEMS = {
			new CBItem(getNameForSkill(NON_EXISTENT), NON_EXISTENT),
			new CBItem(getNameForSkill(DISASTROUS), DISASTROUS),
			new CBItem(getNameForSkill(WRETCHED), WRETCHED),
			new CBItem(getNameForSkill(POOR), POOR),
			new CBItem(getNameForSkill(WEAK), WEAK),
			new CBItem(getNameForSkill(INADEQUATE), INADEQUATE),
			new CBItem(getNameForSkill(PASSABLE), PASSABLE),
			new CBItem(getNameForSkill(SOLID), SOLID),
			new CBItem(getNameForSkill(EXCELLENT), EXCELLENT),
			new CBItem(getNameForSkill(FORMIDABLE), FORMIDABLE),
			new CBItem(getNameForSkill(OUTSTANDING), OUTSTANDING),
			new CBItem(getNameForSkill(BRILLIANT), BRILLIANT),
			new CBItem(getNameForSkill(MAGNIFICENT), MAGNIFICENT),
			new CBItem(getNameForSkill(WORLD_CLASS), WORLD_CLASS),
			new CBItem(getNameForSkill(SUPERNATURAL), SUPERNATURAL),
			new CBItem(getNameForSkill(TITANIC), TITANIC),
			new CBItem(getNameForSkill(EXTRA_TERRESTRIAL), EXTRA_TERRESTRIAL),
			new CBItem(getNameForSkill(MYTHICAL), MYTHICAL),
			new CBItem(getNameForSkill(MAGICAL), MAGICAL),
			new CBItem(getNameForSkill(UTOPIAN), UTOPIAN),
			new CBItem(getNameForSkill(DIVINE), DIVINE) };

	private PlayerAbility(){};

	public static String toString(int ability){
		if( ability >= NON_EXISTENT && ability <= DIVINE) {
			return HOVerwaltung.instance().getLanguageString(languageKeys[ability]);
		}
		else {
			String value = HOVerwaltung.instance().getLanguageString(ability>DIVINE ? languageKeys[DIVINE]: "Unbestimmt");
			if (ability > 20)
				value +=  "(+" + (ability - 20) + ")";
			
			return value;
		}
	}

	public static String getNameForSkill(float bewertungwertfloat, boolean zahlen, boolean isMatch) {
	    String bewertung = null;
	    int bewertungwert = (int) bewertungwertfloat;

	    //Für match
	    int sublevel = 0;

	    //Umrechnung für ein Spiel
	    if (isMatch) {
	        sublevel = (bewertungwert) % 4;

	        //(int)Math.floor ( ( (float)bewertungwert)/4f ) +1;
	        bewertungwert = ((bewertungwert - 1) / 4) + 1;
	    }

	    bewertung = toString(bewertungwert);

	    if (isMatch) {
	        bewertung += PlayerAbility.getName4Sublevel(sublevel);
	    }

	    if (zahlen) {
	        if (isMatch) {
	            bewertung += (" ("
	            +  Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
	    		.format(Helper.round(bewertungwert + PlayerAbility.getValue4Sublevel(sublevel), 2))
	             + ")");
	        } else {
	            bewertung += (" ("
	            		+ Helper.getNumberFormat(false, UserParameter.instance().nbDecimals)
	             		.format(Helper.round(bewertungwertfloat, UserParameter.instance().nbDecimals)) + ")");
	        }
	    }

	    return bewertung;
	}

	public static String getNameForSkill(boolean isMatch, float bewertungwert) {
	    return getNameForSkill(bewertungwert, UserParameter.instance().zahlenFuerSkill, isMatch);
	}

	public static String getNameForSkill(float bewertungwert, boolean zahlen) {
	    return getNameForSkill(bewertungwert, zahlen, false);
	}

	public static String getNameForSkill(float bewertung) {
	    return getNameForSkill(bewertung, UserParameter.instance().zahlenFuerSkill);
	}


	public static double getValue4Sublevel(int sub) {
	    if (sub == 1) {
	        return 0.125;
	    } else if (sub == 2) {
	        return 0.375;
	    } else if (sub == 3) {
	        return 0.625;
	    } else if (sub == 0) {
	        return 0.875;
	    } else {
	        return 0;
	    }
	}

	private static String getName4Sublevel(int sub) {
	    if (sub == 1) {
	        return " ("
	               + HOVerwaltung.instance().getLanguageString("verylow")
	               + ")";
	    } else if (sub == 2) {
	        return " ("
	               + HOVerwaltung.instance().getLanguageString("low")
	               + ")";
	    } else if (sub == 3) {
	        return " ("
	               + HOVerwaltung.instance().getLanguageString("high")
	               + ")";
	    } else if (sub == 0) {
	        return " ("
	               + HOVerwaltung.instance().getLanguageString("veryhigh")
	               + ")";
	    } else {
	        return "";
	    }
	}



}
