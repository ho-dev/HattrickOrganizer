package module.youth

import core.constants.player.PlayerSkill
import core.model.HOVerwaltung
import core.model.player.Specialty
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import java.util.*

class YouthPlayerTests {
    @Test
    fun calculateRateMyAcademyScore() {
        val hoVerwaltung = mock(HOVerwaltung::class.java)
        mockStatic(HOVerwaltung::class.java).use { mockedStatic ->
            mockedStatic.`when`<HOVerwaltung> {
                HOVerwaltung.instance()
            }.thenReturn(hoVerwaltung)

            val properties = Properties()
            properties.setProperty("age", "16")
            properties.setProperty("agedays", "84")
            properties.setProperty("canbepromotedin", "96")
            properties.setProperty("defenderskill", "4")
            properties.setProperty("defenderskillmax", "8")
            properties.setProperty("passingskillmax", "4")
            properties.setProperty("scorerskillmax", "5")
            var youthPlayer = YouthPlayer(properties)
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2158)

            youthPlayer.ageDays += 1
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2148)

            youthPlayer.canBePromotedIn += 1
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2138)

            youthPlayer.specialty = Specialty.Head
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2238)

            youthPlayer.setMax(PlayerSkill.PLAYMAKING, 8)
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2830)

            youthPlayer.specialty = Specialty.Regainer
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2730)

            youthPlayer.specialty = Specialty.NoSpecialty
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2630)

            properties.setProperty("scoutcomment0text", "")
            properties.setProperty("scoutcomment0type", "6")        // overallskill type
            properties.setProperty("scoutcomment0skilltype", "7")   // solid value (never seen that in real hattrick;-)
            properties.setProperty("playmakerskillmax", "8")

            youthPlayer = YouthPlayer(properties)
            assertThat(youthPlayer.calculateRateMyAcademyScore()).isEqualTo(2928)
        }
    }
}
