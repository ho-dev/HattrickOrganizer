package tool.arenasizer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArenaRebuildTest {

    @Test
    void constant_TERRACES() {
        assertThat(ArenaRebuild.TERRACES.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(450));
    }

    @Test
    void constant_BASIC_SEATING() {
        assertThat(ArenaRebuild.BASIC_SEATING.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(750));
    }

    @Test
    void constant_UNDER_ROOF() {
        assertThat(ArenaRebuild.UNDER_ROOF.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(900));
    }

    @Test
    void constant_VIP_BOX() {
        assertThat(ArenaRebuild.VIP_BOX.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(3000));
    }

    @Test
    void constant_DEMOLITION() {
        assertThat(ArenaRebuild.DEMOLITION.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(60));
    }

    @Test
    void constant_FIXED_COSTS() {
        assertThat(ArenaRebuild.FIXED_COSTS.getSwedishKrona()).isEqualTo(BigDecimal.valueOf(100000));
    }

    private static Stream<Arguments> calculateCosts() {
        return Stream.of(
                arguments(1, 0, 0, 0, BigDecimal.valueOf(100450)),
                arguments(-1, 0, 0, 0, BigDecimal.valueOf(100060)),
                arguments(10, 0, 0, 0, BigDecimal.valueOf(104500)),
                arguments(-10, 0, 0, 0, BigDecimal.valueOf(100600)),
                arguments(0, 1, 0, 0, BigDecimal.valueOf(100750)),
                arguments(0, -1, 0, 0, BigDecimal.valueOf(100060)),
                arguments(0, 10, 0, 0, BigDecimal.valueOf(107500)),
                arguments(0, -10, 0, 0, BigDecimal.valueOf(100600)),
                arguments(0, 0, 1, 0, BigDecimal.valueOf(100900)),
                arguments(0, 0, -1, 0, BigDecimal.valueOf(100060)),
                arguments(0, 0, 10, 0, BigDecimal.valueOf(109000)),
                arguments(0, 0, -10, 0, BigDecimal.valueOf(100600)),
                arguments(0, 0, 0, 1, BigDecimal.valueOf(103000)),
                arguments(0, 0, 0, -1, BigDecimal.valueOf(100060)),
                arguments(0, 0, 0, 10, BigDecimal.valueOf(130000)),
                arguments(0, 0, 0, -10, BigDecimal.valueOf(100600)),
                arguments(1, 2, 3, 4, BigDecimal.valueOf(116650)),
                arguments(-1, -2, -3, -4, BigDecimal.valueOf(100600)),
                arguments(0, 0, 0, 0, BigDecimal.ZERO)
        );
    }

    @ParameterizedTest
    @MethodSource
    void calculateCosts(int terraces, int basicSeating, int underRoof, int vipBox, BigDecimal expectedSwedishKrona) {
        assertThat(ArenaRebuild.calculateCosts(terraces, basicSeating, underRoof, vipBox).getSwedishKrona()).isEqualTo(expectedSwedishKrona);
    }
}