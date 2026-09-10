package core.model.player

import core.HOModelBuilder
import core.model.HOVerwaltung
import core.util.HODateTime
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.Arguments.of
import org.junit.jupiter.params.provider.MethodSource
import java.time.temporal.ChronoUnit
import java.util.stream.Stream

class InjuryTest {

    private fun getPlayerById(playerId: Int): Player =
        HOVerwaltung.instance()
            .model
            .currentPlayers
            .single { it.playerId == playerId }

    companion object {
        private const val REFERENCE_DATE_STR = "2026-06-30 14:00:00"
        private const val HRF_MOCK_ID = 43
        private const val HRF_43_DATE_OFFSET = 4
        private val referenceDate = HODateTime.fromHT(REFERENCE_DATE_STR).plus(HRF_43_DATE_OFFSET, ChronoUnit.DAYS)

        @JvmStatic
        private fun testData(): Stream<Arguments> = Stream.of(
            of(
                1,
                false,
                referenceDate.plus(1, ChronoUnit.DAYS),
                null,
                TypeOfRecoveryEstimation.OPTIMISTIC_ESTIMATE,
            ),
            of(
                2,
                false,
                referenceDate.plus(5 + 7, ChronoUnit.DAYS),
                referenceDate.plus(5, ChronoUnit.DAYS),
                TypeOfRecoveryEstimation.REALISTIC_ESTIMATE,
            ),
            of(
                3,
                false,
                referenceDate.plus(3 + 14 * 7, ChronoUnit.DAYS),
                referenceDate.plus(1 + 9 * 7, ChronoUnit.DAYS),
                TypeOfRecoveryEstimation.PESSIMISTIC_ESTIMATE,
            ),
            of(
                4,
                true,
                null,
                null,
                TypeOfRecoveryEstimation.PESSIMISTIC_ESTIMATE,
            ),
        )
    }

    @BeforeEach
    fun setup() {
        val hoAdmin = HOVerwaltung.instance()
        val model = HOModelBuilder().hrfId(HRF_MOCK_ID).build()
        hoAdmin.model = model
    }

    @ParameterizedTest
    @MethodSource("testData")
    fun testInjury(
        playerId: Int,
        expectedIsSportsInvalid: Boolean,
        expectedWhenHealthy: HODateTime?,
        expectedWhenSlightlyInjured: HODateTime?,
        expectedTypeOfEstimate: TypeOfRecoveryEstimation
    ) {
        val player = getPlayerById(playerId)
        val injury = Injury(player)
        assertThat(injury.isSportsInvalid).isEqualTo(expectedIsSportsInvalid)
        assertThat(injury.whenHealthy).isEqualTo(expectedWhenHealthy)
        assertThat(injury.whenSlightlyInjured).isEqualTo(expectedWhenSlightlyInjured)
        assertThat(injury.typeOfEstimate).isEqualTo(expectedTypeOfEstimate)
    }
}
