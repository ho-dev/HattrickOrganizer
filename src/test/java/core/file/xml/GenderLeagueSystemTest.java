package core.file.xml;

import core.model.enums.Gender;
import core.model.misc.Basics;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * #2429: GenderID and LeagueSystemID of Hattrick's women's league system (1 male, 2 female).
 */
class GenderLeagueSystemTest {

    // WorldDetailLeague is left out: its static HOConfigurationStringParameter opens HO's database.

    @Test
    void basicsReadGenderAndLeagueSystemFromHrf() {
        var properties = new Properties();
        properties.setProperty("date", "2026-11-14 12:00:00");
        properties.setProperty("genderid", "2");
        properties.setProperty("leaguesystemid", "2");

        var basics = new Basics(properties);

        assertThat(basics.getGender()).isEqualTo(Gender.FEMALE);
        assertThat(basics.getLeagueSystem()).isEqualTo(Gender.FEMALE);
    }

    @Test
    void basicsWithoutGenderStayUnknown() {
        var properties = new Properties();
        properties.setProperty("date", "2026-11-14 12:00:00");

        var basics = new Basics(properties);

        assertThat(basics.getGender()).isNull();
        assertThat(basics.getLeagueSystem()).isNull();
    }

    @Test
    void unknownIdsStayUnknown() {
        assertThat(Gender.fromId(1)).isEqualTo(Gender.MALE);
        assertThat(Gender.fromId(3)).isNull();
        assertThat(Gender.toId(Gender.FEMALE)).isEqualTo(2);
    }
}
