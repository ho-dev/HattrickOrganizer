package core.constants.player

import core.HOModelBuilder
import core.model.HOVerwaltung
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PlayerAbilityTest {

    @BeforeEach
    fun setup() {
        val hoAdmin = HOVerwaltung.instance()
        val hoModel = HOModelBuilder()
            .hrfId(42)
            .build()
        hoAdmin.model = hoModel
        hoAdmin.setLanguageBundle(null)
    }

    @Test
    fun getValue4Sublevel() {
        val expectedValues = mapOf(0 to 0.0, 1 to 0.25, 2 to 0.5, 3 to 0.75)
        expectedValues.forEach {
            assertEquals(it.value, PlayerAbility.getValue4Sublevel(it.key))
        }
        assertEquals(0.0, PlayerAbility.getValue4Sublevel(42))
    }

    @Test
    fun getNameForSkill() {
        assertEquals(
            "!ls.player.skill.value.outstanding! (!verylow!) (10.0)",
            PlayerAbility.getNameForSkill(10.0, true, true, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding! (!verylow!)",
            PlayerAbility.getNameForSkill(10.0, false, true, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding!",
            PlayerAbility.getNameForSkill(10.0, false, false, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding! (10.0)",
            PlayerAbility.getNameForSkill(10.0, true, false, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding! (10.00)",
            PlayerAbility.getNameForSkill(10.0, true, false, 2)
        )
        assertEquals(
            "!ls.player.skill.value.divine!(+22) (!verylow!)",
            PlayerAbility.getNameForSkill(42.0, false, true, 1)
        )
    }

    @Test
    fun getName4Sublevel() {
        assertEquals(
            "!ls.player.skill.value.outstanding! (!low!) (10.2)",
            PlayerAbility.getNameForSkill(10.3, true, true, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding! (!high!) (10.5)",
            PlayerAbility.getNameForSkill(10.5, true, true, 1)
        )
        assertEquals(
            "!ls.player.skill.value.outstanding! (!veryhigh!) (10.8)",
            PlayerAbility.getNameForSkill(10.8, true, true, 1)
        )
    }
}