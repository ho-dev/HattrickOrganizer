package core.file.xml;

import core.constants.player.PlayerSkill;
import core.util.HODateTime;
import core.util.ResourceUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/** youthplayerdetails 1.4 TrainingReports (fixtures built from the CHPP docs, see their headers). */
class XMLYouthPlayerDetailsParserTest {

    private static String details() throws IOException {
        return Objects.requireNonNull(ResourceUtils.getResourceFileAsString("chpp/youth/youthplayerdetails_1.4.xml"));
    }

    @Test
    void trainingReportsNewestFirst() throws IOException {
        final var result = XMLYouthPlayerDetailsParser.parseTrainingReports(details());

        assertThat(result).isNotNull();
        assertThat(result.youthPlayerId()).isEqualTo(364643130);
        assertThat(result.supporterTier()).isEqualTo("gold");
        assertThat(result.reportsAvailable()).isTrue();
        assertThat(result.reports()).hasSize(2);
        final var newest = result.reports().get(0);
        assertThat(newest.getYouthPlayerId()).isEqualTo(364643130);
        assertThat(newest.getReportDate()).isEqualTo(HODateTime.fromHT("2026-11-14 21:00:00"));
        assertThat(newest.getReportType()).isEqualTo(18);
        assertThat(newest.getSkillType()).isEqualTo(4);
        assertThat(newest.getSkill()).isEqualTo(PlayerSkill.PLAYMAKING);
        assertThat(newest.getSkillLevel()).isEqualTo(5);
        assertThat(newest.getText()).startsWith("Claudio hat im Spielaufbau");
        final var older = result.reports().get(1);
        assertThat(older.getSkill()).isNull();
        assertThat(older.getSkillLevel()).isZero();
    }

    @Test
    void noReportsBelowGold() throws IOException {
        final var xml = details()
                .replace("<UserSupporterTier>gold</UserSupporterTier>", "<UserSupporterTier>silver</UserSupporterTier>")
                .replaceAll("(?s)<TrainingReports>.*</TrainingReports>", "<TrainingReports />");

        final var result = XMLYouthPlayerDetailsParser.parseTrainingReports(xml);

        assertThat(result).isNotNull();
        assertThat(result.reports()).isEmpty();
        assertThat(result.reportsAvailable()).isFalse();
    }

    @Test
    void withoutTierTheReportsCountAsAvailable() throws IOException {
        final var xml = details().replace("<UserSupporterTier>gold</UserSupporterTier>", "");

        final var result = XMLYouthPlayerDetailsParser.parseTrainingReports(xml);

        assertThat(result).isNotNull();
        assertThat(result.supporterTier()).isNull();
        assertThat(result.reportsAvailable()).isTrue();
    }

    @Test
    void aPromotedPlayerGivesNothing() throws IOException {
        assertThat(XMLYouthPlayerDetailsParser.parseTrainingReports(
                ResourceUtils.getResourceFileAsString("chpp/youth/chpperror_62.xml"))).isNull();
        assertThat(XMLYouthPlayerDetailsParser.parseTrainingReports("")).isNull();
    }
}
