package core.net;

import core.db.DBManager;
import core.util.HODateTime;
import module.youth.YouthPlayer;
import module.youth.YouthTrainerComment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class OnlineWorkerTest {

    /** A download in the days after the 2026-11-21 match. */
    private static final HODateTime NOW = HODateTime.fromHT("2026-11-22 10:00:00");

    private static YouthPlayer player(int id, String lastMatch) {
        final var player = new YouthPlayer();
        player.setId(id);
        player.setYouthMatchDate(lastMatch == null ? null : HODateTime.fromHT(lastMatch));
        return player;
    }

    @Test
    void aPlayerWithoutMatchNeedsNoTrainingReports() {
        assertThat(OnlineWorker.needsTrainingReports(player(910_000_001, null), NOW)).isFalse();
    }

    @Test
    void aPlayerWhoPlayedButHasNoReportNeedsOne() {
        assertThat(OnlineWorker.needsTrainingReports(player(910_000_002, "2026-11-21 19:15:00"), NOW)).isTrue();
    }

    @Test
    void onlyAMatchAfterTheNewestReportAsksAgain() {
        final var report = new YouthTrainerComment();
        report.setYouthPlayerId(910_000_003);
        report.setReportDate(HODateTime.fromHT("2026-11-21 21:00:00"));
        report.setReportType(18);
        DBManager.instance().storeYouthTrainerComments(List.of(report));

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
}
