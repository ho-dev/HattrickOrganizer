package tool.arenasizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArenaMaintenanceTest {

    @Test
    void constant_TERRACES() {
        assertThat(ArenaMaintenance.TERRACES.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    void constant_BASIC_SEATING() {
        assertThat(ArenaMaintenance.BASIC_SEATING.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(7));
    }

    @Test
    void constant_UNDER_ROOF() {
        assertThat(ArenaMaintenance.UNDER_ROOF.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void constant_VIP_BOX() {
        assertThat(ArenaMaintenance.VIP_BOX.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(25));
    }

    private static Stream<Arguments> calculateCosts() {
        return Stream.of(
                arguments(1, 0, 0, 0, BigDecimal.valueOf(5)),
                arguments(-1, 0, 0, 0, BigDecimal.valueOf(-5)),
                arguments(10, 0, 0, 0, BigDecimal.valueOf(50)),
                arguments(-10, 0, 0, 0, BigDecimal.valueOf(-50)),
                arguments(0, 1, 0, 0, BigDecimal.valueOf(7)),
                arguments(0, -1, 0, 0, BigDecimal.valueOf(-7)),
                arguments(0, 10, 0, 0, BigDecimal.valueOf(70)),
                arguments(0, -10, 0, 0, BigDecimal.valueOf(-70)),
                arguments(0, 0, 1, 0, BigDecimal.valueOf(10)),
                arguments(0, 0, -1, 0, BigDecimal.valueOf(-10)),
                arguments(0, 0, 10, 0, BigDecimal.valueOf(100)),
                arguments(0, 0, -10, 0, BigDecimal.valueOf(-100)),
                arguments(0, 0, 0, 1, BigDecimal.valueOf(25)),
                arguments(0, 0, 0, -1, BigDecimal.valueOf(-25)),
                arguments(0, 0, 0, 10, BigDecimal.valueOf(250)),
                arguments(0, 0, 0, -10, BigDecimal.valueOf(-250)),
                arguments(1, 2, 3, 4, BigDecimal.valueOf(149)),
                arguments(-1, -2, -3, -4, BigDecimal.valueOf(-149)),
                arguments(0, 0, 0, 0, BigDecimal.ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateCosts(int terraces, int basicSeating, int underRoof, int vipBox, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaMaintenance.calculateCosts(terraces, basicSeating, underRoof, vipBox).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }
}