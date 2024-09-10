package core.constants;

import core.datatype.CBItem;
import core.model.TranslationFacility;

public class TeamConfidence {

	public static final int NON_EXISTENT 			= 0;
	public static final int DISASTROUS 				= 1;
	public static final int WRETCHED 				= 2;
	public static final int POOR 					= 3;
	public static final int DECENT 					= 4;
	public static final int STRONG 					= 5;
	public static final int WONDERFUL 				= 6;
	public static final int SLIGHTLY_EXAGGERATED 	= 7;
	public static final int EXAGGERATED 			= 8;
	public static final int COMPLETELY_EXAGGERATED 	= 9;

	public static CBItem[] ITEMS = {
		new CBItem(TranslationFacility.tr("ls.team.confidence.non-existent"), NON_EXISTENT),
		new CBItem(TranslationFacility.tr("ls.team.confidence.disastrous"), DISASTROUS),
		new CBItem(TranslationFacility.tr("ls.team.confidence.wretched"), WRETCHED),
		new CBItem(TranslationFacility.tr("ls.team.confidence.poor"), POOR),
		new CBItem(TranslationFacility.tr("ls.team.confidence.decent"), DECENT),
		new CBItem(TranslationFacility.tr("ls.team.confidence.strong"), STRONG),
		new CBItem(TranslationFacility.tr("ls.team.confidence.wonderful"), WONDERFUL),
		new CBItem(TranslationFacility.tr("ls.team.confidence.slightlyexaggerated"), SLIGHTLY_EXAGGERATED),
		new CBItem(TranslationFacility.tr("ls.team.confidence.exaggerated"), EXAGGERATED),
		new CBItem(TranslationFacility.tr("ls.team.confidence.completelyexaggerated"), COMPLETELY_EXAGGERATED)
	};


	public static String toString(int teamConfidence){
		if(teamConfidence >= NON_EXISTENT && teamConfidence <= COMPLETELY_EXAGGERATED)
			return ITEMS[teamConfidence].getText();
		else
			return TranslationFacility.tr("Unbestimmt");
	}

}
