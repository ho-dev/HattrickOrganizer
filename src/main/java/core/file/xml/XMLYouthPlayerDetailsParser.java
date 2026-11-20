package core.file.xml;

import core.util.HODateTime;
import core.util.HOLogger;
import core.util.XMLUtils;
import module.youth.YouthTrainerComment;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * youthplayerdetails 1.4, read for the coach's {@code TrainingReports}. Everything else the file
 * carries already comes with youthplayerlist, which HO downloads for the whole team.
 */
public final class XMLYouthPlayerDetailsParser {

    /** Supporter tiers Hattrick sends the training reports for. */
    private static final Set<String> TIERS_WITH_REPORTS = Set.of("gold", "platinum", "diamond");

    private XMLYouthPlayerDetailsParser() {
    }

    /**
     * The coach's training reports of one youth player.
     *
     * @param youthPlayerId the player
     * @param supporterTier the requesting user's {@code UserSupporterTier}, null when not sent
     * @param reports       newest first, as CHPP sends them; empty when there are none
     */
    public record TrainingReports(int youthPlayerId, @Nullable String supporterTier, List<YouthTrainerComment> reports) {

        /**
         * False when the user's tier is below Gold: Hattrick then sends no reports for any player,
         * so asking for the others is a waste of requests. True when the tier is not sent.
         */
        public boolean reportsAvailable() {
            return supporterTier == null || TIERS_WITH_REPORTS.contains(supporterTier.toLowerCase(Locale.ROOT));
        }
    }

    /**
     * @return the reports, or null when the response is empty or a CHPP error (e.g. 62: the youth
     * player has been promoted)
     */
    public static @Nullable TrainingReports parseTrainingReports(String xml) {
        final var doc = XMLManager.parseString(xml);
        if (doc == null) {
            return null;
        }
        try {
            final var root = doc.getDocumentElement();
            final var player = XMLUtils.findChildElement(root, "YouthPlayer");
            if (player.isEmpty()) {
                return null;
            }
            final int youthPlayerId = XMLUtils.getIntValue(player.get(), "YouthPlayerID");
            final var tier = XMLUtils.getStringValue(root, "UserSupporterTier", null);
            final var reports = new ArrayList<YouthTrainerComment>();
            final var container = XMLUtils.findChildElement(player.get(), "TrainingReports");
            if (container.isPresent()) {
                for (var element : XMLUtils.getChildElements(container.get(), "TrainingReport")) {
                    final var report = new YouthTrainerComment();
                    report.setYouthPlayerId(youthPlayerId);
                    report.setText(XMLUtils.getStringValue(element, "ReportText", ""));
                    report.setReportDate(HODateTime.fromHT(XMLUtils.getStringValue(element, "ReportDate")));
                    report.setReportType(XMLUtils.getIntValue(element, "ReportType"));
                    report.setSkillType(XMLUtils.getIntValue(element, "ReportSkillType", 0));
                    report.setSkillLevel(XMLUtils.getIntValue(element, "ReportSkillLevel", 0));
                    reports.add(report);
                }
            }
            return new TrainingReports(youthPlayerId, tier, List.copyOf(reports));
        } catch (Exception e) {
            HOLogger.instance().log(XMLYouthPlayerDetailsParser.class, e);
            return null;
        }
    }
}
