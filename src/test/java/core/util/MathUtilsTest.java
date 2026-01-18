package core.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MathUtilsTest {

    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final int SCALE = 2;

    @SuppressWarnings("DataFlowIssue")
    @Test
    void average_withNullForList_throws_NPE() {
        assertThatThrownBy(() -> MathUtils.average(null, ROUNDING_MODE, SCALE))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void average_withListIncludingNull_throws_NPE() {
        final List<BigDecimal> bigDecimals = new ArrayList<>();
        bigDecimals.add(BigDecimal.TEN);
        bigDecimals.add(null);

        assertThatThrownBy(() -> MathUtils.average(bigDecimals, ROUNDING_MODE, SCALE))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    void average_withEmptyList_return_optionalEmpty() {
        assertThat(MathUtils.average(List.of(), ROUNDING_MODE, SCALE)).isNotPresent();
    }

    @Test
    void average() {
        // given
        final var bigDecimals = List.of(BigDecimal.valueOf(12.255), BigDecimal.valueOf(13.355), BigDecimal.valueOf(14.455));

        // when
        final var average = MathUtils.average(bigDecimals, ROUNDING_MODE, SCALE);

        // then
        assertThat(average).isEqualTo(Optional.of(BigDecimal.valueOf(13.36)));
    }
}
