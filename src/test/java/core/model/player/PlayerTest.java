package core.model.player;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {
    @Test
    void noLastMatchReturnZero() {
        Player player = new Player();
        player.setLastMatchRating(0);
        assertEquals(0, player.getSubStamina());
    }
}