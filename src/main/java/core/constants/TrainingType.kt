package core.constants;

import core.datatype.CBItem;
import core.model.HOVerwaltung;
import core.util.Helper;

public final class TrainingType {
	public static final int SET_PIECES 			= 2;
	public static final int DEFENDING 			= 3;
	public static final int SCORING 			= 4;
	public static final int CROSSING_WINGER 	= 5;
	public static final int SHOOTING 			= 6;
	public static final int SHORT_PASSES 		= 7;
	public static final int PLAYMAKING 			= 8;
	public static final int GOALKEEPING 		= 9;
	public static final int THROUGH_PASSES 		= 10;
	public static final int DEF_POSITIONS 		= 11;
    public static final int WING_ATTACKS 		= 12;

    public static CBItem[] ITEMS = {
		new CBItem(Helper.getTranslation("ls.team.trainingtype.setpieces"), SET_PIECES),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.defending"), DEFENDING),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.scoring"), SCORING),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.crossing"), CROSSING_WINGER),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.shooting"), SHOOTING),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.shortpasses"), SHORT_PASSES),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.playmaking"), PLAYMAKING),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.goalkeeping"), GOALKEEPING),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.throughpasses"), THROUGH_PASSES),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.defensivepositions"), DEF_POSITIONS),
		new CBItem(Helper.getTranslation("ls.team.trainingtype.wingattacks"), WING_ATTACKS)
	};

    private TrainingType(){};

    public static String toString(int trainingType){
    	if(trainingType >= SET_PIECES && trainingType <= WING_ATTACKS)
    		return ITEMS[trainingType-SET_PIECES].getText();
    	else
    		return Helper.getTranslation("Unbestimmt");
    }
}
