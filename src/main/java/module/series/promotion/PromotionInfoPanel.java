package module.series.promotion;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.misc.Basics;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static core.gui.theme.ThemeManager.getColor;

/**
 * Panel displaying the main details about Promotion / Demotion status.
 */
public class PromotionInfoPanel extends ImagePanel {

    private final LeaguePromotionHandler promotionHandler;
    private final HOVerwaltung verwaltung = HOVerwaltung.instance();

    // Force font when loading panel, otherwise chooses a different from default.
    private final Font defaultFont = new Font("SansSerif", Font.BOLD, UserParameter.instance().fontSize +1);
    private final Color fgColor = ThemeManager.getColor(HOColorName.FG_PROMOTION_INFO);

    public PromotionInfoPanel(LeaguePromotionHandler promotionHandler) {
        this.promotionHandler = promotionHandler;
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(new EmptyBorder(0, 10, 0, 0));

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
                            } else if (promotionHandler.getLeagueStatus() == LeagueStatus.BEING_PROCESSED) {
                                createBeingProcessedLabel();
                                promotionHandler.pollPromotionStatus();
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
        final JButton downloadLeagueButton = new JButton(ImageUtilities.getDownloadIcon(getColor(HOColorName.DOWNLOAD_MATCH), 14, 14));
        this.add(downloadLeagueButton);
        final JLabel downloadLabel = new JLabel(TranslationFacility.tr("pd_status.download.unavailable.data"));
        downloadLabel.setFont(defaultFont);
        downloadLabel.setForeground(fgColor);
        this.add(downloadLabel);

        downloadLeagueButton.setToolTipText(TranslationFacility.tr("pd_status.download.data"));
        downloadLeagueButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(HOMainFrame.instance(),
                    TranslationFacility.tr("pd_status.download.warning.message"),
                    TranslationFacility.tr("pd_status.download.warning.title"),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.OK_OPTION) {
                promotionHandler.downloadLeagueData(basics.getLiga());
                downloadLeagueButton.setEnabled(false);
                downloadLeagueButton.setIcon(ThemeManager.getIcon(HOIconName.SPINNER));
                downloadLabel.setText(TranslationFacility.tr("pd_status.download.unavailable.loading"));
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
        infoLeagueData.setForeground(fgColor);
        this.add(infoLeagueData);

        this.revalidate();
        this.repaint();
    }

    private void createBeingProcessedLabel() {
        this.removeAll();

        final Basics basics = DBManager.instance().getBasics(HOVerwaltung.instance().getId());
        JLabel processingLabel = new JLabel(TranslationFacility.tr(
                "pd_status.download.pending",
                basics.getLiga()));
        processingLabel.setFont(defaultFont);
        processingLabel.setForeground(fgColor);
        this.add(processingLabel);

        this.revalidate();
        this.repaint();
    }

    private String createPromotionStatusDisplayString(LeaguePromotionInfo leaguePromotionInfo) {
        List<String> leagueDetails = Collections.EMPTY_LIST;
        List<Map<String, String>> teamDetails = Collections.EMPTY_LIST;
        if (!leaguePromotionInfo.teams.isEmpty() && !leaguePromotionInfo.teams.contains(-1)) {
            final DownloadCountryDetails downloadCountryDetails = new DownloadCountryDetails();

            teamDetails = leaguePromotionInfo.teams
                    .stream()
                    .map(downloadCountryDetails::getTeamSeries)
                    .collect(Collectors.toList());
            leagueDetails = teamDetails
                    .stream()
                    .map(teamInfo -> teamInfo.get("LeagueLevelUnitName"))
                    .collect(Collectors.toList());
        }

        if (leagueDetails.isEmpty()) {
            return TranslationFacility.tr("pd_status." + leaguePromotionInfo.status.name());
        } else {
            return TranslationFacility.tr("pd_status." + leaguePromotionInfo.status.name(),
                    String.join(", ", leagueDetails),
                    String.join(", ", teamDetails.stream()
                            .map(stringStringMap -> stringStringMap.get("TeamName"))
                            .collect(Collectors.toList())),
                    String.join(", ", teamDetails.stream()
                            .map(stringStringMap -> stringStringMap.get("TeamID"))
                            .collect(Collectors.toList()))
                    );
        }
    }
}
