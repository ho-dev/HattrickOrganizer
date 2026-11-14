package core.file.xml;

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

        assertThat(basics.getGenderId()).isEqualTo(2);
        assertThat(basics.getLeagueSystemId()).isEqualTo(2);
    }

    @Test
    void basicsWithoutGenderStayUnknown() {
        var properties = new Properties();
        properties.setProperty("date", "2026-11-14 12:00:00");

        var basics = new Basics(properties);

        assertThat(basics.getGenderId()).isNull();
        assertThat(basics.getLeagueSystemId()).isNull();
    }
}
