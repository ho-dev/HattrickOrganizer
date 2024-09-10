package core.constants.player;

import core.model.TranslationFacility;

public final class PlayerAgreeability {
	private static final String[] languageKeys = {
		"ls.player.agreeability.nastyfellow",
		"ls.player.agreeability.controversialperson",
		"ls.player.agreeability.pleasantguy",
		"ls.player.agreeability.sympatheticguy",
		"ls.player.agreeability.popularguy",
		"ls.player.agreeability.belovedteammember"};
	public static final int NASTY_FELLOW 			= 0;
	public static final int CONTROVERSIAL_PERSON 	= 1;
	public static final int PLEASANT_GUY 			= 2;
	public static final int SYMPATHETIC_GUY 		= 3;
	public static final int POPULAR_GUY 			= 4;
	public static final int BELOVED_TEAM_MEMBER 	= 5;

	private PlayerAgreeability(){}


	public static String toString(Integer agreeability){
		if(agreeability!=null && agreeability >= NASTY_FELLOW && agreeability <= BELOVED_TEAM_MEMBER)
			return TranslationFacility.tr(languageKeys[agreeability]);
		return TranslationFacility.tr("Unbestimmt");
	}


}
