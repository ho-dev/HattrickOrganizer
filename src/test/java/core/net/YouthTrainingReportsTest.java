package core.net;

import core.db.DBManager;
import core.util.HODateTime;
import module.youth.YouthPlayer;
import module.youth.YouthTrainerComment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/** When a download asks for a youth player's training reports, and how they are kept. */
class YouthTrainingReportsTest {

    private static YouthTrainerComment report(int playerId, String date, int type) {
        final var report = new YouthTrainerComment();
        report.setYouthPlayerId(playerId);
        report.setReportDate(HODateTime.fromHT(date));
        report.setReportType(type);
        report.setSkillType(4);
        report.setSkillLevel(5);
        report.setText("report " + type);
        return report;
    }

    private static YouthPlayer player(int id, String lastMatch) {
        final var player = new YouthPlayer();
        player.setId(id);
        player.setYouthMatchDate(lastMatch == null ? null : HODateTime.fromHT(lastMatch));
        return player;
    }

    /** A download in the days after the 2026-11-21 match. */
    private static final HODateTime NOW = HODateTime.fromHT("2026-11-22 10:00:00");

    @Test
    void aPlayerWithoutMatchNeedsNoRequest() {
        assertThat(OnlineWorker.needsTrainingReports(player(910_000_001, null), NOW)).isFalse();
    }

    @Test
    void aPlayerWhoPlayedButHasNoReportNeedsOne() {
        assertThat(OnlineWorker.needsTrainingReports(player(910_000_002, "2026-11-21 19:15:00"), NOW)).isTrue();
    }

    @Test
    void onlyAMatchAfterTheNewestReportAsksAgain() {
        DBManager.instance().storeYouthTrainerComments(List.of(report(910_000_003, "2026-11-21 21:00:00", 18)));

        assertThat(OnlineWorker.needsTrainingReports(player(910_000_003, "2026-11-21 19:15:00"), NOW)).isFalse();
        assertThat(OnlineWorker.needsTrainingReports(player(910_000_003, "2026-11-28 19:15:00"),
                HODateTime.fromHT("2026-11-29 10:00:00"))).isTrue();
    }

    @Test
    void aMatchWithoutReportIsAskedForOnlyWithinTheWindow() {
        final var player = player(910_000_005, "2026-11-21 19:15:00");

        assertThat(OnlineWorker.needsTrainingReports(player, HODateTime.fromHT("2026-11-28 19:00:00"))).isTrue();
        assertThat(OnlineWorker.needsTrainingReports(player, HODateTime.fromHT("2026-11-28 19:30:00"))).isFalse();
    }

    @Test
    void aRedownloadAddsOnlyTheNewReports() {
        final int id = 910_000_004;
        DBManager.instance().storeYouthTrainerComments(List.of(report(id, "2026-11-07 21:00:00", 3)));
        DBManager.instance().storeYouthTrainerComments(List.of(
                report(id, "2026-11-14 21:00:00", 18), report(id, "2026-11-07 21:00:00", 3)));

        final var stored = DBManager.instance().loadYouthTrainerComments(id);

        assertThat(stored).extracting(YouthTrainerComment::getReportType).containsExactly(18, 3);
        assertThat(stored.get(0).getText()).isEqualTo("report 18");
        assertThat(stored.get(0).getSkillLevel()).isEqualTo(5);
    }
}
