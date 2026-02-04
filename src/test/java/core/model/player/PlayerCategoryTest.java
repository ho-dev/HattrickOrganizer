package core.model.player;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

class PlayerCategoryTest {

    @Test
    void category0IsDefinedAsNoCategorySet() {
        final var potentialNoCategorySet = Stream.of(PlayerCategory.values())
            .filter(playerCategory -> playerCategory.getId() == 0)
            .findFirst().orElseThrow(AssertionError::new);
        assertThat(potentialNoCategorySet).isEqualTo(PlayerCategory.NO_CATEGORY_SET);
    }

    @Test
    void categoryIdFrom1To13IsDefined() {
        final var allIds = Stream.of(PlayerCategory.values()).map(PlayerCategory::getId).collect(Collectors.toSet());
        IntStream.range(1, 13).forEach(i -> assertThat(allIds.contains(i)).isTrue());
    }

    @Test
    void notCategoryIdIsDefinedMoreThanOnce() {
        final var mapIdToFrequency = Stream.of(PlayerCategory.values())
            .collect(Collectors.groupingBy(PlayerCategory::getId, Collectors.counting()));
        mapIdToFrequency.forEach((id, frequency) -> assertThat(frequency == 1).isTrue());
    }

    private static Stream<Arguments> valueOf() {
        return Stream.of(
            of(-1, null),
            of(0, PlayerCategory.NO_CATEGORY_SET),
            of(1, PlayerCategory.KEEPER),
            of(2, PlayerCategory.WING_BACK),
            of(3, PlayerCategory.CENTRAL_DEFENDER),
            of(4, PlayerCategory.WINGER),
            of(5, PlayerCategory.INNER_MIDFIELD),
            of(6, PlayerCategory.FORWARD),
            of(7, PlayerCategory.SUBSTITUTE),
            of(8, PlayerCategory.RESERVE),
            of(9, PlayerCategory.EXTRA_1),
            of(10, PlayerCategory.EXTRA_2),
            of(11, PlayerCategory.TRAINEE_1),
            of(12, PlayerCategory.TRAINEE_2),
            of(13, PlayerCategory.COACH_PROSPECT),
            of(14, null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void valueOf(int id, PlayerCategory expected) {
        assertThat(PlayerCategory.fromId(id)).isEqualTo(expected);
    }


    private static Stream<Arguments> idOf() {
        return Stream.concat(Stream.of(PlayerCategory.values()), Stream.of((PlayerCategory) null)).map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource
    void idOf(PlayerCategory playerCategory) {
        final var expected = Optional.ofNullable(playerCategory)
            .map(PlayerCategory::getId)
            .orElse(PlayerCategory.NO_CATEGORY_SET.getId());
        assertThat(PlayerCategory.idOf(playerCategory)).isEqualTo(expected);
    }
}
