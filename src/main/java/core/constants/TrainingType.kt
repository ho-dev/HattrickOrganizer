package core.constants

import core.datatype.CBItem
import core.util.Helper

object TrainingType {
    const val SET_PIECES = 2
    const val DEFENDING = 3
    const val SCORING = 4
    const val CROSSING_WINGER = 5
    const val SHOOTING = 6
    const val SHORT_PASSES = 7
    const val PLAYMAKING = 8
    const val GOALKEEPING = 9
    const val THROUGH_PASSES = 10
    const val DEF_POSITIONS = 11
    const val WING_ATTACKS = 12

    @JvmField
	var ITEMS = arrayOf(
            CBItem(Helper.getTranslation("ls.team.trainingtype.setpieces"), SET_PIECES),
            CBItem(Helper.getTranslation("ls.team.trainingtype.defending"), DEFENDING),
            CBItem(Helper.getTranslation("ls.team.trainingtype.scoring"), SCORING),
            CBItem(Helper.getTranslation("ls.team.trainingtype.crossing"), CROSSING_WINGER),
            CBItem(Helper.getTranslation("ls.team.trainingtype.shooting"), SHOOTING),
            CBItem(Helper.getTranslation("ls.team.trainingtype.shortpasses"), SHORT_PASSES),
            CBItem(Helper.getTranslation("ls.team.trainingtype.playmaking"), PLAYMAKING),
            CBItem(Helper.getTranslation("ls.team.trainingtype.goalkeeping"), GOALKEEPING),
            CBItem(Helper.getTranslation("ls.team.trainingtype.throughpasses"), THROUGH_PASSES),
            CBItem(Helper.getTranslation("ls.team.trainingtype.defensivepositions"), DEF_POSITIONS),
            CBItem(Helper.getTranslation("ls.team.trainingtype.wingattacks"), WING_ATTACKS)
    )

    @JvmStatic
	fun toString(trainingType: Int): String {
        return if (trainingType in SET_PIECES..WING_ATTACKS) ITEMS[trainingType - SET_PIECES].text
        else Helper.getTranslation("Unbestimmt")
    }
}
