package tool.arenasizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArenaAdmissionTest {

    @Test
    void constant_TERRACES() {
        assertThat(ArenaAdmission.TERRACES.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(70));
    }

    @Test
    void constant_BASIC_SEATING() {
        assertThat(ArenaAdmission.BASIC_SEATING.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void constant_UNDER_ROOF() {
        assertThat(ArenaAdmission.UNDER_ROOF.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(190));
    }

    @Test
    void constant_VIP_BOX() {
        assertThat(ArenaAdmission.VIP_BOX.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(350));
    }

    private static Stream<Arguments> calculateIncome() {
        return Stream.of(
                arguments(1, 0, 0, 0, BigDecimal.valueOf(70)),
                arguments(-1, 0, 0, 0, BigDecimal.valueOf(-70)),
                arguments(10, 0, 0, 0, BigDecimal.valueOf(700)),
                arguments(-10, 0, 0, 0, BigDecimal.valueOf(-700)),
                arguments(0, 1, 0, 0, BigDecimal.valueOf(100)),
                arguments(0, -1, 0, 0, BigDecimal.valueOf(-100)),
                arguments(0, 10, 0, 0, BigDecimal.valueOf(1000)),
                arguments(0, -10, 0, 0, BigDecimal.valueOf(-1000)),
                arguments(0, 0, 1, 0, BigDecimal.valueOf(190)),
                arguments(0, 0, -1, 0, BigDecimal.valueOf(-190)),
                arguments(0, 0, 10, 0, BigDecimal.valueOf(1900)),
                arguments(0, 0, -10, 0, BigDecimal.valueOf(-1900)),
                arguments(0, 0, 0, 1, BigDecimal.valueOf(350)),
                arguments(0, 0, 0, -1, BigDecimal.valueOf(-350)),
                arguments(0, 0, 0, 10, BigDecimal.valueOf(3500)),
                arguments(0, 0, 0, -10, BigDecimal.valueOf(-3500)),
                arguments(1, 2, 3, 4, BigDecimal.valueOf(2240)),
                arguments(-1, -2, -3, -4, BigDecimal.valueOf(-2240)),
                arguments(0, 0, 0, 0, BigDecimal.ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateIncome(int terraces, int basicSeating, int underRoof, int vipBox, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaAdmission.calculateIncome(terraces, basicSeating, underRoof, vipBox).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }

    private static Stream<Arguments> calculateTerracesIncome() {
        return Stream.of(
                arguments(0, BigDecimal.ZERO),
                arguments(1, BigDecimal.valueOf(70)),
                arguments(-1, BigDecimal.valueOf(-70)),
                arguments(10, BigDecimal.valueOf(700)),
                arguments(-10, BigDecimal.valueOf(-700))
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateTerracesIncome(int terraces, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaAdmission.calculateTerracesIncome(terraces).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }

    private static Stream<Arguments> calculateBasicSeatingIncome() {
        return Stream.of(
                arguments(0, BigDecimal.ZERO),
                arguments(1, BigDecimal.valueOf(100)),
                arguments(-1, BigDecimal.valueOf(-100)),
                arguments(10, BigDecimal.valueOf(1000)),
                arguments(-10, BigDecimal.valueOf(-1000))
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateBasicSeatingIncome(int basicSeating, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaAdmission.calculateBasicSeatingIncome(basicSeating).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }

    private static Stream<Arguments> calculateUnderRoofIncome() {
        return Stream.of(
                arguments(0, BigDecimal.ZERO),
                arguments(1, BigDecimal.valueOf(190)),
                arguments(-1, BigDecimal.valueOf(-190)),
                arguments(10, BigDecimal.valueOf(1900)),
                arguments(-10, BigDecimal.valueOf(-1900))
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateUnderRoofIncome(int underRoof, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaAdmission.calculateUnderRoofIncome(underRoof).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }

    private static Stream<Arguments> calculateVipBoxIncome() {
        return Stream.of(
                arguments(0, BigDecimal.ZERO),
                arguments(1, BigDecimal.valueOf(350)),
                arguments(-1, BigDecimal.valueOf(-350)),
                arguments(10, BigDecimal.valueOf(3500)),
                arguments(-10, BigDecimal.valueOf(-3500))
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateVipBoxIncome(int vipBox, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaAdmission.calculateVipBoxIncome(vipBox).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }
}