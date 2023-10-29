package core.constants.player

import core.model.HOVerwaltung

object PlayerSkill {
    private val languageKeys = arrayOf(
        "ls.player.skill.keeper",
        "ls.player.skill.defending",
        "ls.player.skill.winger",
        "ls.player.skill.playmaking",
        "ls.player.skill.scoring",
        "ls.player.skill.passing",
        "ls.player.skill.stamina",
        "ls.player.form",
        "ls.player.skill.setpieces",
        "ls.player.experience",
        "ls.player.leadership",
        "ls.player.loyalty"
    )
    const val KEEPER = 0
    const val DEFENDING = 1
    const val WINGER = 2
    const val PLAYMAKING = 3
    const val SCORING = 4
    const val PASSING = 5
    const val STAMINA = 6
    const val FORM = 7
    const val SET_PIECES = 8
    const val EXPERIENCE = 9
    const val LEADERSHIP = 10
    const val LOYALTY = 11
    @JvmStatic
    fun toString(skill: Int): String {
        return if (skill in KEEPER..LOYALTY)
            HOVerwaltung.instance().getLanguageString(languageKeys[skill])
        else HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
