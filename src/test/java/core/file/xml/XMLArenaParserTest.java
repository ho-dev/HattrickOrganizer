package core.file.xml;

import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class XMLArenaParserTest {

    @Test
    void parseArenaFromString_noExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_noExpansion.xml");

        // FileName, Version and UserID are currently not read
        final var expected = Map.ofEntries(
                Map.entry("FetchedDate", "2024-08-21 01:13:12"),
                Map.entry("ArenaID", "2345678"),
                Map.entry("ArenaName", "ArenaName"),
                Map.entry("TeamID", "3456789"),
                Map.entry("TeamName", "TeamName"),
                Map.entry("LeagueID", "3"),
                Map.entry("LeagueName", "LeagueName"),
                Map.entry("RegionID", "227"),
                Map.entry("RegionName", "RegionName"),
                Map.entry("RebuiltDate", "2024-08-13 00:05:26"),
                Map.entry("Terraces", "8000"),
                Map.entry("Basic", "3000"),
                Map.entry("Roof", "1792"),
                Map.entry("VIP", "282"),
                Map.entry("Total", "13074"),
                Map.entry("isExpanding", "0"),
                Map.entry("ExpansionDate", "0"),
                Map.entry("ExTerraces", "0"),
                Map.entry("ExBasic", "0"),
                Map.entry("ExRoof", "0"),
                Map.entry("ExVIP", "0"),
                Map.entry("ExTotal", "0"));

        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void parseArenaFromString_withExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_withExpansion.xml");

        // FileName, Version and UserID are currently not read
        final var expected = Map.ofEntries(
                Map.entry("FetchedDate", "2024-08-21 11:08:22"),
                Map.entry("ArenaID", "2345678"),
                Map.entry("ArenaName", "ArenaName"),
                Map.entry("TeamID", "3456789"),
                Map.entry("TeamName", "TeamName"),
                Map.entry("LeagueID", "3"),
                Map.entry("LeagueName", "LeagueName"),
                Map.entry("RegionID", "227"),
                Map.entry("RegionName", "RegionName"),
                Map.entry("Terraces", "8000"),
                Map.entry("Basic", "3000"),
                Map.entry("Roof", "1792"),
                Map.entry("VIP", "282"),
                Map.entry("Total", "13074"),
                Map.entry("isExpanding", "1"),
                Map.entry("ExpansionDate", "2024-08-30 11:07:17"),
                Map.entry("ExTerraces", "226"),
                Map.entry("ExBasic", "234"),
                Map.entry("ExRoof", "1138"),
                Map.entry("ExVIP", "78"),
                Map.entry("ExTotal", "1676"));

        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected).doesNotContainKey("RebuiltDate");
    }
}