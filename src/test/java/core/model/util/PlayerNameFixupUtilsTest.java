package core.model.util;

import core.model.util.PlayerNameFixupUtils.PlayerName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static core.model.util.PlayerNameFixupUtils.splitName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PlayerNameFixupUtilsTest {

    private static Stream<Arguments> playerNames() {
        return Stream.of(
            // Names with nicknames
            arguments("Cristiano 'CR7' Ronaldo", new PlayerName("Cristiano", "CR7", "Ronaldo")),
            arguments("Jean Claude 'JC' Van Damme", new PlayerName("Jean Claude", "JC", "Van Damme")),
            arguments("John 'Johnny' Smith", new PlayerName("John", "Johnny", "Smith")),
            arguments("John 'The Rocket' Smith", new PlayerName("John", "The Rocket", "Smith")),
            arguments("John 'Johnny' O'Connor", new PlayerName("John", "Johnny", "O'Connor")),

            // Names without nicknames
            arguments("Cristiano Ronaldo", new PlayerName("Cristiano", "", "Ronaldo")),
            arguments("John Smith", new PlayerName("John", "", "Smith")),
            arguments("Jean-Claude Van-Damme", new PlayerName("Jean-Claude", "", "Van-Damme")),
            arguments("John O'Connor", new PlayerName("John", "", "O'Connor")),

            // Names that cannot be split reliably
            arguments("Ronaldo", new PlayerName("", "", "Ronaldo")),
            arguments("Jean Claude Van Damme", new PlayerName("", "", "Jean Claude Van Damme")),
            arguments("John Ronald Reuel Tolkien", new PlayerName("", "", "John Ronald Reuel Tolkien")
            ),

            // Whitespace handling
            arguments("  Cristiano Ronaldo  ", new PlayerName("Cristiano", "", "Ronaldo")),
            arguments("Cristiano    Ronaldo", new PlayerName("Cristiano", "", "Ronaldo")),
            arguments("Cristiano\tRonaldo", new PlayerName("Cristiano", "", "Ronaldo")),
            arguments("  Cristiano  'CR7'  Ronaldo  ", new PlayerName("Cristiano", "CR7", "Ronaldo")),

            // Invalid or incomplete nickname syntax
            arguments("Cristiano 'CR7 Ronaldo", new PlayerName("", "", "Cristiano 'CR7 Ronaldo")),
            arguments("Cristiano '' Ronaldo", new PlayerName("", "", "Cristiano '' Ronaldo")),

            // Empty input
            arguments("", new PlayerName("", "", "")),
            arguments("   ", new PlayerName("", "", "")),
            arguments(null, new PlayerName("", "", "")
            )
        );
    }

    @ParameterizedTest(name = "[{index}] splitName(\"{0}\")")
    @MethodSource("playerNames")
    void testSplitName(String fullName, PlayerName expected) {
        assertEquals(expected, splitName(fullName));
    }
}
