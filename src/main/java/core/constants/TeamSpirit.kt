package core.constants

import core.datatype.CBItem
import core.model.HOVerwaltung

object TeamSpirit {
    const val LIKE_THE_COLD_WAR = 0
    const val MURDEROUS = 1
    const val FURIOUS = 2
    const val IRRITATED = 3
    const val COMPOSED = 4
    const val CALM = 5
    const val CONTENT = 6
    const val SATISFIED = 7
    const val DELIRIOUS = 8
    const val WALKING_ON_CLOUDS = 9
    const val PARADISE_ON_EARTH = 10

    @JvmField
	var ITEMS = arrayOf(
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.likethecoldwar"), LIKE_THE_COLD_WAR),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.murderous"), MURDEROUS),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.furious"), FURIOUS),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.irritated"), IRRITATED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.composed"), COMPOSED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.calm"), CALM),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.content"), CONTENT),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.satisfied"), SATISFIED),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.delirious"), DELIRIOUS),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.walkingonclouds"), WALKING_ON_CLOUDS),
            CBItem(HOVerwaltung.instance().getLanguageString("ls.team.teamspirit.paradiseonearth"), PARADISE_ON_EARTH))

    @JvmStatic
	fun toString(teamSpirit: Int): String {
        return if (teamSpirit in LIKE_THE_COLD_WAR..PARADISE_ON_EARTH) ITEMS[teamSpirit].text
        else HOVerwaltung.instance().getLanguageString("Unbestimmt")
    }
}
