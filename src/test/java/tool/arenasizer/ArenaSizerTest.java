package tool.arenasizer;

import core.util.AmountOfMoney;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ArenaSizerTest {

    private static Stream<Arguments> calcConstructionArenasGeneral() {
        return Stream.of(
                arguments(
                        createStadium(0, 0, 0, 0),
                        1000,
                        createStadium(15000, 5875, 3500, 625, 15000, 5875, 3500, 625, new AmountOfMoney(16281250)),
                        createStadium(12000, 4700, 2800, 500, 12000, 4700, 2800, 500, new AmountOfMoney(13045000)),
                        createStadium(9000, 3525, 2100, 375, 9000, 3525, 2100, 375, new AmountOfMoney(9808750))
                ),
                arguments(
                        createStadium(1000, 1000, 1000, 1000),
                        1000,
                        createStadium(15000, 5875, 3500, 625, 14000, 4875, 2500, -375, new AmountOfMoney(12328750)),
                        createStadium(12000, 4700, 2800, 500, 11000, 3700, 1800, -500, new AmountOfMoney(9475000)),
                        createStadium(9000, 3525, 2100, 375, 8000, 2525, 1100, -625, new AmountOfMoney(6621250))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("calcConstructionArenasGeneral")
    void calcConstructionArenas_general(Stadium currentArena, int supporter, Stadium expectedMaxArena, Stadium expectedNormalArena, Stadium expectedMinArena) {
        // when
        final var stadiums = ArenaSizer.calcConstructionArenas(currentArena, supporter);

        final var maxArena = stadiums[0];
        final var normalArena = stadiums[1];
        final var minArena = stadiums[2];

        assertThat(maxArena).isEqualTo(expectedMaxArena);
        assertThat(normalArena).isEqualTo(expectedNormalArena);
        assertThat(minArena).isEqualTo(expectedMinArena);
    }

    private static Stream<Arguments> calcConstructionArenasIndividual() {
        return Stream.of(
                arguments(
                        createStadium(0, 0, 0, 0),
                        24000, 19000, 14000,
                        createStadium(14400, 5640, 3360, 600, 14400, 5640, 3360, 600, new AmountOfMoney(15634000)),
                        createStadium(11400, 4465, 2660, 475, 11400, 4465, 2660, 475, new AmountOfMoney(12397750)),
                        createStadium(8400, 3290, 1960, 350, 8400, 3290, 1960, 350, new AmountOfMoney(9161500))
                ),
                arguments(
                        createStadium(1000, 1000, 1000, 1000),
                        24000, 19000, 14000,
                        createStadium(14400, 5640, 3360, 600, 13400, 4640, 2360, -400, new AmountOfMoney(11758000)),
                        createStadium(11400, 4465, 2660, 475, 10400, 3465, 1660, -525, new AmountOfMoney(8904250)),
                        createStadium(8400, 3290, 1960, 350, 7400, 2290, 960, -650, new AmountOfMoney(6050500))
                )
        );
    }

    @ParameterizedTest
    @MethodSource("calcConstructionArenasIndividual")
    void calcConstructionArenas_individual(Stadium currentArena, int maxSize, int normalSize, int minSize, Stadium expectedMaxArena, Stadium expectedNormalArena, Stadium expectedMinArena) {
        // when
        final var stadiums = ArenaSizer.calcConstructionArenas(currentArena, maxSize, normalSize, minSize);

        final var maxArena = stadiums[0];
        final var normalArena = stadiums[1];
        final var minArena = stadiums[2];

        assertThat(maxArena).isEqualTo(expectedMaxArena);
        assertThat(normalArena).isEqualTo(expectedNormalArena);
        assertThat(minArena).isEqualTo(expectedMinArena);
    }

    private static Stadium createStadium(int terraces,
                                         int basicSeating,
                                         int underRoof,
                                         int vipBox,
                                         int terracesUnderConstruction,
                                         int basicSeatingUnderConstruction,
                                         int underRoofUnderConstruction,
                                         int vipBoxUnderConstruction,
                                         AmountOfMoney expansionCost) {
        Stadium stadium = createStadium(terraces, basicSeating, underRoof, vipBox);
        stadium.setTerracesUnderConstruction(terracesUnderConstruction);
        stadium.setBasicSeatingUnderConstruction(basicSeatingUnderConstruction);
        stadium.setUnderRoofSeatingUnderConstruction(underRoofUnderConstruction);
        stadium.setVipBoxUnderConstruction(vipBoxUnderConstruction);
        stadium.setExpansionCosts(expansionCost);
        return stadium;
    }

    private static Stadium createStadium(int terraces, int basicSeating, int underRoof, int vipBox) {
        Stadium stadium = new Stadium();
        stadium.setTerraces(terraces);
        stadium.setBasicSeating(basicSeating);
        stadium.setUnderRoofSeating(underRoof);
        stadium.setVipBox(vipBox);
        return stadium;
    }
}