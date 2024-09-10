package core.constants;

import core.datatype.CBItem;
import core.model.TranslationFacility;

public final class TeamSpirit {

	public static final int LIKE_THE_COLD_WAR 	= 0;
	public static final int MURDEROUS 			= 1;
	public static final int FURIOUS 			= 2;
	public static final int IRRITATED 			= 3;
	public static final int COMPOSED 			= 4;
	public static final int CALM 				= 5;
	public static final int CONTENT 			= 6;
	public static final int SATISFIED 			= 7;
	public static final int DELIRIOUS 			= 8;
	public static final int WALKING_ON_CLOUDS 	= 9;
	public static final int PARADISE_ON_EARTH 	= 10;

	public static CBItem[] ITEMS = {
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.likethecoldwar"),	LIKE_THE_COLD_WAR),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.murderous"),	MURDEROUS),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.furious"),	FURIOUS),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.irritated"),	IRRITATED),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.composed"), COMPOSED),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.calm"), CALM),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.content"), CONTENT),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.satisfied"),	SATISFIED),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.delirious"),	DELIRIOUS),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.walkingonclouds"),	WALKING_ON_CLOUDS),
			new CBItem(TranslationFacility.tr("ls.team.teamspirit.paradiseonearth"),	PARADISE_ON_EARTH) };

	private TeamSpirit() {
	}


    public static String toString(int teamSpirit){
		if(teamSpirit >= LIKE_THE_COLD_WAR && teamSpirit <= PARADISE_ON_EARTH)
			return ITEMS[teamSpirit].getText();
		else
			return TranslationFacility.tr("Unbestimmt");
	}
}
