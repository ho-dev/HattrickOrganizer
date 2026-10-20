package core.playername;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static core.playername.PlayerNameFormatter.NameFormat.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PlayerNameFormatterTest {

    private record TestPlayerName(int playerId, String firstName, String nickName, String lastName)
        implements PlayerName {

        @Override
        public int getPlayerId() {
            return playerId;
        }

        @Override
        public String getFirstName() {
            return firstName;
        }

        @Override
        public String getNickName() {
            return nickName;
        }

        @Override
        public String getLastName() {
            return lastName;
        }
    }

    private static TestPlayerName player(int id, String firstName, String nickName, String lastName) {
        return new TestPlayerName(id, firstName, nickName, lastName);
    }

    @ParameterizedTest(name = "{0} -> {1}")
    @MethodSource("standardFormats")
    void formatsCompleteName(NameFormat format, String expected) {
        var player = player(1, "Cristiano", "CR7", "Ronaldo");
        var formatter = new PlayerNameFormatter(List.of(player));

        assertThat(formatter.format(1, format)).isEqualTo(expected);
        assertThat(formatter.format(player, format)).isEqualTo(expected);
    }

    private static Stream<Arguments> standardFormats() {
        return Stream.of(
            arguments(NameFormat.HATTRICK, "Cristiano 'CR7' Ronaldo"),
            arguments(FULL_FIRST_LAST, "Cristiano 'CR7' Ronaldo"),
            arguments(FULL_LAST_FIRST, "Ronaldo, Cristiano 'CR7'"),
            arguments(SMART_FIRST_LAST, "'CR7'"),
            arguments(SMART_LAST_FIRST, "'CR7'")
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("incompleteNames")
    void handlesMissingAndWhitespaceNameParts(
        String description,
        TestPlayerName player,
        String fullFirstLast,
        String fullLastFirst,
        String smart) {
        var formatter = new PlayerNameFormatter(List.of(player));
        int id = player.getPlayerId();

        assertThat(formatter.format(id, HATTRICK)).isEqualTo(fullFirstLast);
        assertThat(formatter.format(id, FULL_FIRST_LAST)).isEqualTo(fullFirstLast);
        assertThat(formatter.format(id, FULL_LAST_FIRST)).isEqualTo(fullLastFirst);
        assertThat(formatter.format(id, SMART_FIRST_LAST)).isEqualTo(smart);
        assertThat(formatter.format(id, SMART_LAST_FIRST)).isEqualTo(smart);
    }

    private static Stream<Arguments> incompleteNames() {
        return Stream.of(
            arguments("all null", player(1, null, null, null), "(1)", "(1)", "(1)"),
            arguments("all empty", player(2, "", "", ""), "(2)", "(2)", "(2)"),
            arguments("all whitespace", player(3, " \t ", "  ", "  "), "(3)", "(3)", "(3)"),
            arguments("trim fields", player(4, "  Anna  ", " Ace ", " Müller "), "Anna 'Ace' Müller", "Müller, Anna 'Ace'", "'Ace'"),
            arguments("first and last", player(5, "Anna", null, "Müller"), "Anna Müller", "Müller, Anna", "Müller"),
            arguments("nickname and last", player(6, null, "Ace", "Müller"), "'Ace' Müller", "Müller 'Ace'", "'Ace'"),
            arguments("first and nickname", player(7, "Anna", "Ace", null), "Anna 'Ace'", "Anna 'Ace'", "'Ace'"),
            arguments("nickname only", player(8, null, "Ace", null), "'Ace'", "'Ace'", "'Ace'"),
            arguments("last only", player(9, null, null, "Müller"), "Müller", "Müller", "Müller"),
            arguments("first only", player(10, "Anna", null, null), "Anna", "Anna", "Anna (10)"),
            arguments("trim without nickname", player(11, "  Anna  ", "  ", " Müller "), "Anna Müller", "Müller, Anna", "Müller"),
            arguments("unusual last name data is not repaired", player(12, "Robert", null, "Robert Enke"), "Robert Robert Enke", "Robert Enke, Robert", "Robert Enke")
        );
    }

    @Test
    void usesLastNameOnlyWhenItIsUnique() {
        var first = player(1, "Thomas", null, "Müller");
        var second = player(2, "Tobias", null, "Schmidt");
        var formatter = new PlayerNameFormatter(List.of(first, second));

        assertThat(formatter.format(1, SMART_FIRST_LAST)).isEqualTo("Müller");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Schmidt");
    }

    @Test
    void usesShortestUniqueFirstNamePrefixForEqualLastNames() {
        var thomas = player(1, "Thomas", null, "Müller");
        var tobias = player(2, "Tobias", null, "Müller");
        var tim = player(3, "Tim", null, "Müller");
        var formatter = new PlayerNameFormatter(List.of(thomas, tobias, tim));

        assertThat(formatter.format(1, SMART_FIRST_LAST)).isEqualTo("Th. Müller");
        assertThat(formatter.format(2, SMART_FIRST_LAST)).isEqualTo("To. Müller");
        assertThat(formatter.format(3, SMART_FIRST_LAST)).isEqualTo("Ti. Müller");
        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Müller, Th.");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Müller, To.");
        assertThat(formatter.format(3, SMART_LAST_FIRST)).isEqualTo("Müller, Ti.");
    }

    @Test
    void distinguishesBrothersWithSameLastName() {
        var volker = player(1, "Volker", null, "Dobias");
        var laszlo = player(2, "László", null, "Dobias");
        var formatter = new PlayerNameFormatter(List.of(volker, laszlo));

        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Dobias, V.");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Dobias, L.");
    }

    @Test
    void doesNotAppendDotWhenFullFirstNameIsNeeded() {
        var al = player(1, "Al", null, "Smith");
        var alex = player(2, "Alex", null, "Smith");
        var formatter = new PlayerNameFormatter(List.of(al, alex));

        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Smith, Al");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Smith, Ale.");
    }

    @Test
    void appendsPlayerIdsWhenFirstAndLastNamesAreIdentical() {
        var first = player(1, "Thomas", null, "Müller");
        var second = player(2, "Thomas", null, "Müller");
        var formatter = new PlayerNameFormatter(List.of(first, second));

        assertThat(formatter.format(1, SMART_FIRST_LAST)).isEqualTo("Thomas Müller (1)");
        assertThat(formatter.format(2, SMART_FIRST_LAST)).isEqualTo("Thomas Müller (2)");
        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Müller, Thomas (1)");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Müller, Thomas (2)");
    }

    @Test
    void prefersUniqueNicknameEvenWhenLastNameIsShared() {
        var first = player(1, "Anna", "Kocka", "Müller");
        var second = player(2, "Tobias", null, "Müller");
        var formatter = new PlayerNameFormatter(List.of(first, second));

        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("'Kocka'");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Müller, T.");
    }

    @Test
    void doesNotUseNicknameWhenItIsShared() {
        var first = player(1, "Anna", "Star", "Müller");
        var second = player(2, "Ben", "Star", "Schmidt");
        var formatter = new PlayerNameFormatter(List.of(first, second));

        assertThat(formatter.format(1, SMART_FIRST_LAST)).isEqualTo("Müller");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Schmidt");
    }

    @Test
    void includesIdWhenSharedLastNameHasNoFirstName() {
        var unnamed = player(1, null, null, "Müller");
        var named = player(2, "Thomas", null, "Müller");
        var formatter = new PlayerNameFormatter(List.of(unnamed, named));

        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Müller (1)");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Müller, T.");
    }

    @Test
    void includesIdWhenOnlyFirstNameIsAvailable() {
        var first = player(1, "Anna", null, null);
        var second = player(2, "Anna", null, null);
        var formatter = new PlayerNameFormatter(List.of(first, second));

        assertThat(formatter.format(1, SMART_FIRST_LAST)).isEqualTo("Anna (1)");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Anna (2)");
    }

    @Test
    void acceptsIdenticalDuplicatesOfTheSamePlayerId() {
        var firstCopy = player(1, "Volker", null, "Dobias");
        var secondCopy = player(1, "Volker", null, "Dobias");
        var other = player(2, "László", null, "Dobias");
        var formatter = new PlayerNameFormatter(List.of(firstCopy, secondCopy, firstCopy, other, other));

        assertThat(formatter.format(1, SMART_LAST_FIRST)).isEqualTo("Dobias, V.");
        assertThat(formatter.format(2, SMART_LAST_FIRST)).isEqualTo("Dobias, L.");
    }

    @ParameterizedTest(name = "conflicting {0}")
    @MethodSource("conflictingNameParts")
    void rejectsDifferentNamesForSamePlayerId(String description, TestPlayerName conflicting) {
        var original = player(7, "Anna", "Ace", "Müller");

        assertThatThrownBy(() -> new PlayerNameFormatter(List.of(original, conflicting)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Conflicting name data for player ID 7");
    }

    private static Stream<Arguments> conflictingNameParts() {
        return Stream.of(
            arguments("first name", player(7, "Anne", "Ace", "Müller")),
            arguments("last name", player(7, "Anna", "Ace", "Schmidt")),
            arguments("nickname", player(7, "Anna", "Star", "Müller")),
            arguments("nickname replaced by empty", player(7, "Anna", "", "Müller")),
            arguments("trailing whitespace", player(7, "Anna ", "Ace", "Müller"))
        );
    }

    @Test
    void usesKnownNameForIdBasedFormatting() {
        var known = player(1, "Thomas", null, "Müller");
        var historical = player(1, "Captain", null, "Chaos");
        var formatter = new PlayerNameFormatter(List.of(known));

        assertThat(formatter.format(1, FULL_FIRST_LAST)).isEqualTo("Thomas Müller");
        assertThatThrownBy(() -> formatter.format(historical, FULL_FIRST_LAST))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("not equal to known one");
    }

    @Test
    void acceptsEquivalentButDifferentPlayerNameInstance() {
        var known = player(1, "Thomas", null, "Müller");
        var equivalent = player(1, "Thomas", null, "Müller");
        var formatter = new PlayerNameFormatter(List.of(known));

        assertThat(formatter.format(equivalent, FULL_FIRST_LAST)).isEqualTo("Thomas Müller");
    }

    @Test
    void rejectsUnknownPlayerIdForBothFormatMethods() {
        var formatter = new PlayerNameFormatter(List.of(player(1, "Anna", null, "Müller")));

        assertThatThrownBy(() -> formatter.format(2, SMART_LAST_FIRST))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("unknown in formatter context");
        assertThatThrownBy(() -> formatter.format(player(2, "Ben", null, "Schmidt"), SMART_LAST_FIRST))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("unknown in formatter context");
    }

    @Test
    void handlesEmptyContext() {
        var formatter = new PlayerNameFormatter(List.of());

        assertThatThrownBy(() -> formatter.format(1, FULL_FIRST_LAST)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void smartNamesAreUniqueWithinRepresentativeContext() {
        var players = List.of(
            player(1, "Thomas", null, "Müller"),
            player(2, "Tobias", null, "Müller"),
            player(3, "Thomas", null, "Müller"),
            player(4, "Volker", "Star", "Dobias"),
            player(5, "László", "Star", "Dobias"),
            player(6, "Robert", null, "Robert Enke"),
            player(7, "Anna", null, null),
            player(8, null, null, null)
        );
        var formatter = new PlayerNameFormatter(players);

        for (var format : Arrays.asList(SMART_FIRST_LAST, SMART_LAST_FIRST)) {
            var displayedNames = players.stream()
                .map(player -> formatter.format(player.getPlayerId(), format))
                .toList();
            assertThat(displayedNames).doesNotHaveDuplicates();
        }
    }
}
