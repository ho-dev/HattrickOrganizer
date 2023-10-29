package core.constants.player

import core.model.HOVerwaltung

object PlayerHonesty {
    private val languageKeys = arrayOf(
        "ls.player.honesty.infamous",
        "ls.player.honesty.dishonest",
        "ls.player.honesty.honest",
        "ls.player.honesty.upright",
        "ls.player.honesty.righteous",
        "ls.player.honesty.saintly"
    )
    const val INFAMOUS = 0
    const val DISHONEST = 1
    const val HONEST = 2
    const val UPRIGHT = 3
    const val RIGHTEOUS = 4
    const val SAINTLY = 5
    @JvmStatic
    fun toString(honesty: Int): String {
        return if (honesty in INFAMOUS..SAINTLY)
            HOVerwaltung.instance().getLanguageString(languageKeys[honesty])
        else
            HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
