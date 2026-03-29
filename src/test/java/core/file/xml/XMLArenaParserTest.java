package core.file.xml;

import core.util.HODateTime;
import core.util.ResourceUtils;
import hattrickdata.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class XMLArenaParserTest {

    private static final String FILENAME = "arenadetails.xml";
    private static final String VERSION = "1.7";

    @Test
    void parseArenaFromString_noExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_noExpansion.xml");

        final var expected = Pair.of(
            HattrickDataInfo.builder()
                .fileName(FILENAME)
                .version(VERSION)
                .userId(1234567)
                .fetchedDate(HODateTime.fromHT("2024-08-21 01:13:12"))
                .build(),
            Arena.builder()
                .id(2345678)
                .name("ArenaName")
                .arenaImage("//res.hattrick.org/arenas/22/216/2155/2345678/custom-220-100.jpg")
                .arenaFallbackImage("//res.hattrick.org/arenas/default/28000/custom-220-100.jpg")
                .team(Team.builder().id(3456789).name("TeamName").build())
                .league(League.builder().id(3).name("LeagueName").build())
                .region(Region.builder().id(227).name("RegionName").build())
                .currentCapacity(CurrentCapacity.builder()
                    .rebuildDate(HODateTime.fromHT("2024-08-13 00:05:26"))
                    .terraces(8000)
                    .basic(3000)
                    .roof(1792)
                    .vip(282)
                    .total(13074)
                    .build())
                .expandedCapacity(null)
                .build());

        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @Test
    void parseArenaFromString_withExpansion() throws IOException {
        // given
        final var content = ResourceUtils.getResourceFileAsString("arenaDetails_withExpansion.xml");

        final var expected = Pair.of(
            HattrickDataInfo.builder()
                .fileName(FILENAME)
                .version(VERSION)
                .userId(1234567)
                .fetchedDate(HODateTime.fromHT("2024-08-21 11:08:22"))
                .build(),
            Arena.builder()
                .id(2345678)
                .name("ArenaName")
                .arenaImage("//res.hattrick.org/arenas/22/216/2155/2345678/custom-220-100.jpg")
                .arenaFallbackImage("//res.hattrick.org/arenas/default/28000/custom-220-100.jpg")
                .team(Team.builder().id(3456789).name("TeamName").build())
                .league(League.builder().id(3).name("LeagueName").build())
                .region(Region.builder().id(227).name("RegionName").build())
                .currentCapacity(CurrentCapacity.builder()
                    .rebuildDate(null)
                    .terraces(8000)
                    .basic(3000)
                    .roof(1792)
                    .vip(282)
                    .total(13074)
                    .build())
                .expandedCapacity(ExpandedCapacity.builder()
                    .expansionDate(HODateTime.fromHT("2024-08-30 11:07:17"))
                    .terraces(226)
                    .basic(234)
                    .roof(1138)
                    .vip(78)
                    .total(1676)
                    .build())
                .build());
        // when
        final var result = XMLArenaParser.parseArenaFromString(content);

        // then
        assertThat(result).isEqualTo(expected);
    }
}
