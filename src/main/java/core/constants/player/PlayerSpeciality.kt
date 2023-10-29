package core.constants.player

import core.datatype.CBItem
import core.model.HOVerwaltung
import core.model.match.Weather

object PlayerSpeciality {
    const val NO_SPECIALITY = 0
    const val TECHNICAL = 1
    const val QUICK = 2
    const val POWERFUL = 3
    const val UNPREDICTABLE = 4
    const val HEAD = 5
    const val REGAINER = 6
    const val SUPPORT = 8
    const val NoWeatherEffect = 0
    const val PositiveWeatherEffect = 1
    const val NegativeWeatherEffect = -1
    const val ImpactWeatherEffect = 0.05f
    @JvmField
    val ITEMS = arrayOf(
        CBItem("", NO_SPECIALITY),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"), TECHNICAL),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"), QUICK),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"), POWERFUL),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"), UNPREDICTABLE),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"), HEAD),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"), REGAINER),
        CBItem(HOVerwaltung.instance().getLanguageString("ls.player.speciality.support"), SUPPORT)
    )
    private var ITEMS2: LinkedHashMap<Int, String>? = null

    init {
        ITEMS2 = linkedMapOf(
            NO_SPECIALITY to "",
            TECHNICAL to HOVerwaltung.instance().getLanguageString("ls.player.speciality.technical"),
            QUICK to HOVerwaltung.instance().getLanguageString("ls.player.speciality.quick"),
            POWERFUL to HOVerwaltung.instance().getLanguageString("ls.player.speciality.powerful"),
            UNPREDICTABLE to HOVerwaltung.instance().getLanguageString("ls.player.speciality.unpredictable"),
            HEAD to HOVerwaltung.instance().getLanguageString("ls.player.speciality.head"),
            REGAINER to HOVerwaltung.instance().getLanguageString("ls.player.speciality.regainer"),
            SUPPORT to HOVerwaltung.instance().getLanguageString("ls.player.speciality.support")
        )
    }

    @JvmStatic
    fun toString(speciality: Int): String {
        return ITEMS2!!.getOrDefault(speciality, HOVerwaltung.instance().getLanguageString("Unbestimmt"))
    }

    @JvmStatic
    fun getWeatherEffect(weather: Weather?, playerSpecialty: Int): Int {
        when (weather) {
            Weather.SUNNY -> when (playerSpecialty) {
                TECHNICAL -> {
                    return PositiveWeatherEffect //Technical players gain 5% on all their skills in the sun
                }
                POWERFUL -> {
                    return NegativeWeatherEffect //Powerful players loose 5% on all their skills in the rain
                }
                QUICK -> {
                    return NegativeWeatherEffect // Quick players lose 5% in the rain and in the sun.
                }
            }

            Weather.RAINY -> when (playerSpecialty) {
                TECHNICAL -> {
                    return NegativeWeatherEffect //Technical players loose 5% on all their skills in the sun
                }
                POWERFUL -> {
                    return PositiveWeatherEffect //Powerful players gain 5% on all their skills in the rain
                }
                QUICK -> {
                    return NegativeWeatherEffect // Quick players lose 5% in the rain and in the sun.
                }
            }

            else -> {}
        }
        return NoWeatherEffect
    }

    fun getImpactWeatherEffect(weather: Weather?, playerSpecialty: Int): Float {
        val impact = getWeatherEffect(weather, playerSpecialty)
        return 1 + impact * ImpactWeatherEffect
    }
}
