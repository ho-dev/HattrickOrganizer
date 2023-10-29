package core.constants.player

import core.model.HOVerwaltung

object PlayerAgreeability {
    private val languageKeys = arrayOf(
        "ls.player.agreeability.nastyfellow",
        "ls.player.agreeability.controversialperson",
        "ls.player.agreeability.pleasantguy",
        "ls.player.agreeability.sympatheticguy",
        "ls.player.agreeability.popularguy",
        "ls.player.agreeability.belovedteammember"
    )
    const val NASTY_FELLOW = 0
    const val CONTROVERSIAL_PERSON = 1
    const val PLEASANT_GUY = 2
    const val SYMPATHETIC_GUY = 3
    const val POPULAR_GUY = 4
    const val BELOVED_TEAM_MEMBER = 5
    @JvmStatic
    fun toString(agreeability: Int): String {
        return if (agreeability in NASTY_FELLOW..BELOVED_TEAM_MEMBER)
            HOVerwaltung.instance().getLanguageString(languageKeys[agreeability])
        else HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
