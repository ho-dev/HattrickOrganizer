package core.db;

import core.util.HODateTime;
import module.youth.YouthTrainerComment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class YouthTrainerCommentTableTest {

    private static final int PLAYER_ID = 910_000_004;

    private static YouthTrainerComment report(String date, int type) {
        final var report = new YouthTrainerComment();
        report.setYouthPlayerId(PLAYER_ID);
        report.setReportDate(HODateTime.fromHT(date));
        report.setReportType(type);
        report.setSkillType(4);
        report.setSkillLevel(5);
        report.setText("report " + type);
        return report;
    }

    @Test
    void storingAgainAddsOnlyTheNewReports() {
        DBManager.instance().storeYouthTrainerComments(List.of(report("2026-11-07 21:00:00", 3)));
        DBManager.instance().storeYouthTrainerComments(List.of(
                report("2026-11-14 21:00:00", 18), report("2026-11-07 21:00:00", 3)));

        final var stored = DBManager.instance().loadYouthTrainerComments(PLAYER_ID);

        assertThat(stored).extracting(YouthTrainerComment::getReportType).containsExactly(18, 3);
        assertThat(stored.get(0).getText()).isEqualTo("report 18");
        assertThat(stored.get(0).getSkillLevel()).isEqualTo(5);
    }
}
