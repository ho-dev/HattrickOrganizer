package module.series.promotion;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.misc.Basics;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Panel displaying the main details about Promotion / Demotion status.
 */
public class PromotionInfoPanel extends JPanel {

    private final LeaguePromotionHandler promotionHandler;
    private final HOVerwaltung verwaltung = HOVerwaltung.instance();

    // Force font when loading panel, otherwise chooses a different from default.
    private final Font defaultFont = new Font("SansSerif", Font.PLAIN, UserParameter.instance().schriftGroesse);

    public PromotionInfoPanel(LeaguePromotionHandler promotionHandler) {
        this.promotionHandler = promotionHandler;
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(new EmptyBorder(0, 10, 0, 0));
        setPreferredSize(new Dimension(500, 40));

        SwingUtilities.invokeLater(() -> {

            final Basics basics = DBManager.instance().getBasics(HOVerwaltung.instance().getId());
            int teamId = basics.getTeamId();
            int seriesId = basics.getLiga();

            boolean active = promotionHandler.isActive(seriesId);

            if (active) {
                if (promotionHandler.getLeagueStatus() == LeagueStatus.NOT_AVAILABLE) {
                    initComponents();
                    promotionHandler.addChangeListener(e -> {
                        final Object source = e.getSource();
                        if (source == promotionHandler) {
                            if (promotionHandler.getLeagueStatus() == LeagueStatus.AVAILABLE) {
                                final LeaguePromotionInfo promotionStatus = promotionHandler.getPromotionStatus(seriesId, teamId);
                                createPromotionInfoLabel(promotionStatus);
                            }
                        }
                    });
                } else {
                    final LeaguePromotionInfo promotionStatus = promotionHandler.getPromotionStatus(seriesId, teamId);
                    createPromotionInfoLabel(promotionStatus);
                }
            }
        });
    }

    private void initComponents() {
        final Basics basics = DBManager.instance().getBasics(HOVerwaltung.instance().getId());
        final JButton downloadLeagueButton = new JButton(ThemeManager.getIcon(HOIconName.DOWNLOAD_MATCH));
        this.add(downloadLeagueButton);
        final JLabel downloadLabel = new JLabel(verwaltung.getLanguageString("pd_status.download.unavailable.data"));
        downloadLabel.setFont(defaultFont);
        this.add(downloadLabel);

        downloadLeagueButton.setToolTipText(verwaltung.getLanguageString("pd_status.download.data"));
        downloadLeagueButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                    verwaltung.getLanguageString("pd_status.download.warning.message"),
                    verwaltung.getLanguageString("pd_status.download.warning.title"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.OK_OPTION) {
                promotionHandler.downloadLeagueData(basics.getLiga());
                downloadLeagueButton.setEnabled(false);
                downloadLeagueButton.setIcon(ThemeManager.getIcon(HOIconName.SPINNER));
                downloadLabel.setText(verwaltung.getLanguageString("pd_status.download.unavailable.loading"));
                this.revalidate();
                this.repaint();
            }
        });

        this.revalidate();
        this.repaint();
    }

    private void createPromotionInfoLabel(final LeaguePromotionInfo promotionStatus) {
        this.removeAll();
        JLabel infoLeagueData = new JLabel(createPromotionStatusDisplayString(promotionStatus));
        infoLeagueData.setFont(defaultFont);
        this.add(infoLeagueData);

        this.revalidate();
        this.repaint();
    }

    private String createPromotionStatusDisplayString(LeaguePromotionInfo leaguePromotionInfo) {
        List<String> leagueDetails = Collections.EMPTY_LIST;
        if (!leaguePromotionInfo.teams.isEmpty() && !leaguePromotionInfo.teams.contains(-1)) {
            final DownloadCountryDetails downloadCountryDetails = new DownloadCountryDetails();

            leagueDetails = leaguePromotionInfo.teams
                    .stream()
                    .map(teamId -> {
                        Map<String, String> teamInfo = downloadCountryDetails.getTeamSeries(teamId);
                        return teamInfo.get("LeagueLevelUnitName");
                    })
                    .collect(Collectors.toList());
        }

        if (leagueDetails.isEmpty()) {
            return HOVerwaltung.instance().getLanguageString("pd_status." + leaguePromotionInfo.status.name());
        } else {
            return HOVerwaltung.instance().getLanguageString("pd_status." + leaguePromotionInfo.status.name(),
                    String.join(", ", leagueDetails));
        }
    }
}
