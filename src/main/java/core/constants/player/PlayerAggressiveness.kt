package core.constants.player

import core.model.HOVerwaltung

object PlayerAggressiveness {
    private val languageKeys = arrayOf(
        "ls.player.aggressiveness.tranquil",
        "ls.player.aggressiveness.calm",
        "ls.player.aggressiveness.balanced",
        "ls.player.aggressiveness.temperamental",
        "ls.player.aggressiveness.fiery",
        "ls.player.aggressiveness.unstable"
    )
    const val TRANQUIL = 0
    const val CALM = 1
    const val BALANCED = 2
    const val TEMPERAMENTAL = 3
    const val FIERY = 4
    const val UNSTABLE = 5
    @JvmStatic
    fun toString(aggressiveness: Int): String {
        return if (aggressiveness in TRANQUIL..UNSTABLE)
            HOVerwaltung.instance().getLanguageString(languageKeys[aggressiveness])
        else HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
