package core.constants

import core.datatype.CBItem
import core.model.HOVerwaltung

object TeamConfidence {
    const val NON_EXISTENT = 0
    const val DISASTROUS = 1
    const val WRETCHED = 2
    const val POOR = 3
    const val DECENT = 4
    const val STRONG = 5
    const val WONDERFUL = 6
    const val SLIGHTLY_EXAGGERATED = 7
    const val EXAGGERATED = 8
    const val COMPLETELY_EXAGGERATED = 9

    @JvmField
    var ITEMS = arrayOf(
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.non-existent"), NON_EXISTENT),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.disastrous"), DISASTROUS),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.wretched"), WRETCHED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.poor"), POOR),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.decent"), DECENT),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.strong"), STRONG),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.wonderful"), WONDERFUL),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.slightlyexaggerated"), SLIGHTLY_EXAGGERATED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.exaggerated"), EXAGGERATED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.confidence.completelyexaggerated"), COMPLETELY_EXAGGERATED)
    )

    @JvmStatic
    fun toString(teamConfidence: Int): String {
        return if (teamConfidence in NON_EXISTENT..COMPLETELY_EXAGGERATED) ITEMS[teamConfidence].text
        else HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
