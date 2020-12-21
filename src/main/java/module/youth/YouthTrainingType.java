package module.youth;

import core.model.match.SourceSystem;

import java.util.Arrays;

import static core.constants.TrainingType.*;

public enum YouthTrainingType {

/*
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
    public static final int WING_ATTACKS 		= 12;*/

    SingleTraining(1),
    SetPieces(SET_PIECES),
    Defending(DEFENDING),
    Scoring(SCORING),
    Winger(CROSSING_WINGER),
    Shooting(SHOOTING),
    Passing(SHORT_PASSES),
    Playmaking(PLAYMAKING),
    Goalkeeping(GOALKEEPING),
    ThroughPassing(THROUGH_PASSES),
    DefencePositions(DEF_POSITIONS),
    WingAttacking(WING_ATTACKS);

    private final int value;

    YouthTrainingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static YouthTrainingType valueOf(Integer id) {
        if ( id != null) {
            for (YouthTrainingType youthTrainingType : YouthTrainingType.values()) {
                if (youthTrainingType.getValue() == id) {
                    return youthTrainingType;
                }
            }
        }
        return null;
    }

}
