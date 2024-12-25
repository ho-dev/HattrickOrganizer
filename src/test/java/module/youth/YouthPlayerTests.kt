package module.youth

import core.constants.player.PlayerSkill
import core.model.player.Specialty
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.*

class YouthPlayerTests {
    @Test
    fun test() {
        // Prepare model
        var properties = Properties()
        properties.setProperty("age", "16")
        properties.setProperty("agedays", "84")
        properties.setProperty("canbepromotedin", "96")
        properties.setProperty("defenderskill", "4")
        properties.setProperty("defenderskillmax", "8")
        properties.setProperty("passingskillmax", "4")
        properties.setProperty("scorerskillmax", "5")
        var youthPlayer = YouthPlayer(properties)
        Assertions.assertEquals(2158, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.ageDays += 1;
        Assertions.assertEquals(2148, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.canBePromotedIn += 1;
        Assertions.assertEquals(2138, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.specialty = Specialty.Head
        Assertions.assertEquals(2238, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.setMax(PlayerSkill.PLAYMAKING, 8)
        Assertions.assertEquals(2830, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.specialty = Specialty.Regainer
        Assertions.assertEquals(2730, youthPlayer.calculateRateMyAcademyScore())

        youthPlayer.specialty = Specialty.NoSpecialty
        Assertions.assertEquals(2630, youthPlayer.calculateRateMyAcademyScore())

        properties.setProperty("scoutcomment0text", "")
        properties.setProperty("scoutcomment0type", "6")        // overallskill type
        properties.setProperty("scoutcomment0skilltype", "7")   // solid value (never seen that in real hattrick;-)
        properties.setProperty("playmakerskillmax", "8")

        youthPlayer = YouthPlayer(properties)
        Assertions.assertEquals(2928, youthPlayer.calculateRateMyAcademyScore())


    }
}