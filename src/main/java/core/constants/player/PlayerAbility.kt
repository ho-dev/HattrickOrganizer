package core.constants.player

import core.datatype.CBItem
import core.model.HOVerwaltung
import core.model.UserParameter
import core.util.Helper

object PlayerAbility {
    private val languageKeys = arrayOf(
        "ls.player.skill.value.non-existent",
        "ls.player.skill.value.disastrous",
        "ls.player.skill.value.wretched",
        "ls.player.skill.value.poor",
        "ls.player.skill.value.weak",
        "ls.player.skill.value.inadequate",
        "ls.player.skill.value.passable",
        "ls.player.skill.value.solid",
        "ls.player.skill.value.excellent",
        "ls.player.skill.value.formidable",
        "ls.player.skill.value.outstanding",
        "ls.player.skill.value.brilliant",
        "ls.player.skill.value.magnificent",
        "ls.player.skill.value.worldclass",
        "ls.player.skill.value.supernatural",
        "ls.player.skill.value.titanic",
        "ls.player.skill.value.extra-terrestrial",
        "ls.player.skill.value.mythical",
        "ls.player.skill.value.magical",
        "ls.player.skill.value.utopian",
        "ls.player.skill.value.divine"
    )
    const val NON_EXISTENT = 0
    const val DISASTROUS = 1
    const val WRETCHED = 2
    const val POOR = 3
    const val WEAK = 4
    const val INADEQUATE = 5
    const val PASSABLE = 6
    const val SOLID = 7
    const val EXCELLENT = 8
    const val FORMIDABLE = 9
    const val OUTSTANDING = 10
    const val BRILLIANT = 11
    const val MAGNIFICENT = 12
    const val WORLD_CLASS = 13
    const val SUPERNATURAL = 14
    const val TITANIC = 15
    const val EXTRA_TERRESTRIAL = 16
    const val MYTHICAL = 17
    const val MAGICAL = 18
    const val UTOPIAN = 19
    const val DIVINE = 20

    @JvmField
	val ITEMS = arrayOf(
        CBItem(getNameForSkill(NON_EXISTENT.toDouble()), NON_EXISTENT),
        CBItem(getNameForSkill(DISASTROUS.toDouble()), DISASTROUS),
        CBItem(getNameForSkill(WRETCHED.toDouble()), WRETCHED),
        CBItem(getNameForSkill(POOR.toDouble()), POOR),
        CBItem(getNameForSkill(WEAK.toDouble()), WEAK),
        CBItem(getNameForSkill(INADEQUATE.toDouble()), INADEQUATE),
        CBItem(getNameForSkill(PASSABLE.toDouble()), PASSABLE),
        CBItem(getNameForSkill(SOLID.toDouble()), SOLID),
        CBItem(getNameForSkill(EXCELLENT.toDouble()), EXCELLENT),
        CBItem(getNameForSkill(FORMIDABLE.toDouble()), FORMIDABLE),
        CBItem(getNameForSkill(OUTSTANDING.toDouble()), OUTSTANDING),
        CBItem(getNameForSkill(BRILLIANT.toDouble()), BRILLIANT),
        CBItem(getNameForSkill(MAGNIFICENT.toDouble()), MAGNIFICENT),
        CBItem(getNameForSkill(WORLD_CLASS.toDouble()), WORLD_CLASS),
        CBItem(getNameForSkill(SUPERNATURAL.toDouble()), SUPERNATURAL),
        CBItem(getNameForSkill(TITANIC.toDouble()), TITANIC),
        CBItem(getNameForSkill(EXTRA_TERRESTRIAL.toDouble()), EXTRA_TERRESTRIAL),
        CBItem(getNameForSkill(MYTHICAL.toDouble()), MYTHICAL),
        CBItem(getNameForSkill(MAGICAL.toDouble()), MAGICAL),
        CBItem(getNameForSkill(UTOPIAN.toDouble()), UTOPIAN),
        CBItem(getNameForSkill(DIVINE.toDouble()), DIVINE)
    )

    @JvmStatic
	fun toString(ability: Int): String {
        return if (ability in NON_EXISTENT..DIVINE) {
            HOVerwaltung.instance().getLanguageString(languageKeys[ability])
        } else {
            var value = HOVerwaltung.instance()
                .getLanguageString(if (ability > DIVINE) languageKeys[DIVINE] else "Unbestimmt")
            if (ability > 20) value += "(+${ability - 20})"
            value
        }
    }

    /**
     * get string representation of rating values
     *
     * @param ratingValue double [0..20]
     * @param showNumbers true for numerical representation
     * @param isMatch true shows' sub-level representations
     * @param nbDecimal nbDecimalDisplayed
     * @return String
     */
	@JvmStatic
	fun getNameForSkill(ratingValue: Double, showNumbers: Boolean, isMatch: Boolean, nbDecimal: Int): String {
        val tmpRatingValue = ratingValue.toInt()
        var sublevel = 0
        if (isMatch) {
            sublevel = (ratingValue * 4).toInt() % 4
        }
        var rating = toString(tmpRatingValue)
        if (isMatch) {
            rating += getName4Sublevel(sublevel)
        }
        if (showNumbers) {
            rating += if (isMatch) {
                val s = (" ("
                        + Helper.getNumberFormat(false, nbDecimal)
                    .format(Helper.round(tmpRatingValue + getValue4Sublevel(sublevel), 2))
                        + ")")
                s
            } else {
                (" ("
                        + Helper.getNumberFormat(false, nbDecimal)
                    .format(Helper.round(ratingValue, nbDecimal)) + ")")
            }
        }
        return rating
    }

    /**
     * get string representation of rating values
     *
     * @param ratingValue double [0..20]
     * @param showNumbers true for numerical representation
     * @param isMatch true shows' sub-level representations
     * @return String
     */
	@JvmStatic
	fun getNameForSkill(ratingValue: Double, showNumbers: Boolean, isMatch: Boolean): String {
        return getNameForSkill(ratingValue, showNumbers, isMatch, UserParameter.instance().nbDecimals)
    }

    @JvmStatic
	fun getNameForSkill(isMatch: Boolean, ratingValue: Double): String {
        return getNameForSkill(ratingValue, UserParameter.instance().zahlenFuerSkill, isMatch)
    }

    @JvmStatic
	fun getNameForSkill(ratingValue: Double, zahlen: Boolean): String {
        return getNameForSkill(ratingValue, zahlen, false)
    }

    @JvmStatic
	fun getNameForSkill(bewertung: Double): String {
        return getNameForSkill(bewertung, UserParameter.instance().zahlenFuerSkill)
    }

    @JvmStatic
	fun getValue4Sublevel(sub: Int): Double {
        return when (sub) {
            0 -> 0.0
            1 -> 0.25
            2 -> 0.5
            3 -> 0.75
            else -> 0.0
        }
    }

    private fun getName4Sublevel(sub: Int): String {
        return when (sub) {
            0 -> " (${HOVerwaltung.instance().getLanguageString("verylow")})"
            1 -> " (${HOVerwaltung.instance().getLanguageString("low")})"
            2 -> " (${HOVerwaltung.instance().getLanguageString("high")})"
            3 -> " (${HOVerwaltung.instance().getLanguageString("veryhigh")})"
            else -> ""
        }
    }
}
