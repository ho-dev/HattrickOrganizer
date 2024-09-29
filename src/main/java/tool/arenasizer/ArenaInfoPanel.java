package tool.arenasizer;

import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.HODateTime;
import core.util.Helper;
import core.util.HumanDuration;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static core.gui.LabelWithSignedNumber.percentString;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class ArenaInfoPanel extends JPanel {

    private final CapacityPanel currentCapacityPanel;
    private final CapacityPanel expandedCapacityPanel;
    private final CapacityPanel futureCapacityPanel;

    public ArenaInfoPanel() {
        setLayout(new FlowLayout());

        currentCapacityPanel = new CapacityPanel();
        add(currentCapacityPanel);

        expandedCapacityPanel = new CapacityPanel();
        add(expandedCapacityPanel);

        futureCapacityPanel = new CapacityPanel();
        add(futureCapacityPanel);

        updateValues();

        Timer timer = new Timer(1000, actionEvent -> updateExpansionFinishedLabel());
        timer.start();
    }

    private void updateValues() {
        final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();

        final String notAvailableString = TranslationFacility.tr("ls.general_label.not_available_abbreviation");
        final var numberformat = Helper.getNumberFormat(false, 0);

        // Current Capacity
        currentCapacityPanel.labelCountTerraces.setText(numberformat.format(stadium.getStehplaetze()));
        currentCapacityPanel.labelCountBasicSeating.setText(numberformat.format(stadium.getSitzplaetze()));
        currentCapacityPanel.labelCountSeatsUnderRoof.setText(numberformat.format(stadium.getUeberdachteSitzplaetze()));
        currentCapacityPanel.labelCountVipBoxes.setText(numberformat.format(stadium.getLogen()));
        currentCapacityPanel.labelCountTotal.setText(numberformat.format(stadium.getGesamtgroesse()));

        currentCapacityPanel.labelPercentageTerraces.setText(percentString(stadium.getStehplaetze(), stadium.getGesamtgroesse()));
        currentCapacityPanel.labelPercentageBasicSeating.setText(percentString(stadium.getSitzplaetze(), stadium.getGesamtgroesse()));
        currentCapacityPanel.labelPercentageSeatsUnderRoof.setText(percentString(stadium.getUeberdachteSitzplaetze(), stadium.getGesamtgroesse()));
        currentCapacityPanel.labelPercentageVipBoxes.setText(percentString(stadium.getLogen(), stadium.getGesamtgroesse()));
        currentCapacityPanel.labelPercentageTotal.setText(percentString(stadium.getGesamtgroesse(), stadium.getGesamtgroesse()));

        currentCapacityPanel.label2.setText(Optional.ofNullable(stadium.getRebuiltDate()).map(HODateTime::toLocaleDate).orElse(TranslationFacility.tr("ArenaInfoPanel.construction_in_progress")));

        // Expanded Capacity
        expandedCapacityPanel.labelCountTerraces.setNumber(stadium.isAusbau() ? stadium.getAusbauStehplaetze() : null);
        expandedCapacityPanel.labelCountBasicSeating.setNumber(stadium.isAusbau() ? stadium.getAusbauSitzplaetze() : null);
        expandedCapacityPanel.labelCountSeatsUnderRoof.setNumber(stadium.isAusbau() ? stadium.getAusbauUeberdachteSitzplaetze() : null);
        expandedCapacityPanel.labelCountVipBoxes.setNumber(stadium.isAusbau() ? stadium.getAusbauLogen() : null);
        expandedCapacityPanel.labelCountTotal.setNumber(stadium.getAusbauGesamtgroesse().orElse(null));

        expandedCapacityPanel.labelPercentageTerraces.setPercentNumber(stadium.isAusbau() ? BigDecimal.valueOf(stadium.getAusbauStehplaetze()).divide(BigDecimal.valueOf(stadium.getStehplaetze()), 3, RoundingMode.HALF_UP) : null);
        expandedCapacityPanel.labelPercentageBasicSeating.setPercentNumber(stadium.isAusbau() ? BigDecimal.valueOf(stadium.getAusbauSitzplaetze()).divide(BigDecimal.valueOf(stadium.getSitzplaetze()), 3, RoundingMode.HALF_UP) : null);
        expandedCapacityPanel.labelPercentageSeatsUnderRoof.setPercentNumber(stadium.isAusbau() ? BigDecimal.valueOf(stadium.getAusbauUeberdachteSitzplaetze()).divide(BigDecimal.valueOf(stadium.getUeberdachteSitzplaetze()), 3, RoundingMode.HALF_UP) : null);
        expandedCapacityPanel.labelPercentageVipBoxes.setPercentNumber(stadium.isAusbau() ? BigDecimal.valueOf(stadium.getAusbauLogen()).divide(BigDecimal.valueOf(stadium.getLogen()), 3, RoundingMode.HALF_UP) : null);
        expandedCapacityPanel.labelPercentageTotal.setPercentNumber(stadium.getAusbauGesamtgroesse().map(expansionTotal -> BigDecimal.valueOf(expansionTotal).divide(BigDecimal.valueOf(stadium.getGesamtgroesse()), 3, RoundingMode.HALF_UP)).orElse(null));

        expandedCapacityPanel.label2.setText(Optional.ofNullable(stadium.getExpansionDate()).map(expansionDate -> expansionDate.toLocaleDateTime()).orElse(EMPTY));

        // Future
        futureCapacityPanel.labelCountTerraces.setText(stadium.getZukunftStehplaetze().map(numberformat::format).orElse(notAvailableString));
        futureCapacityPanel.labelCountBasicSeating.setText(stadium.getZukunftSitzplaetze().map(numberformat::format).orElse(notAvailableString));
        futureCapacityPanel.labelCountSeatsUnderRoof.setText(stadium.getZukunftUeberdachteSitzplaetze().map(numberformat::format).orElse(notAvailableString));
        futureCapacityPanel.labelCountVipBoxes.setText(stadium.getZukunftLogen().map(numberformat::format).orElse(notAvailableString));
        futureCapacityPanel.labelCountTotal.setText(stadium.getZukunftGesamtgroesse().map(numberformat::format).orElse(notAvailableString));

        futureCapacityPanel.labelPercentageTerraces.setText(stadium.getZukunftStehplaetze().map(future -> percentString(future, stadium.getZukunftGesamtgroesse().get())).orElse(notAvailableString));
        futureCapacityPanel.labelPercentageBasicSeating.setText(stadium.getZukunftSitzplaetze().map(future -> percentString(future, stadium.getZukunftGesamtgroesse().get())).orElse(notAvailableString));
        futureCapacityPanel.labelPercentageSeatsUnderRoof.setText(stadium.getZukunftUeberdachteSitzplaetze().map(future -> percentString(future, stadium.getZukunftGesamtgroesse().get())).orElse(notAvailableString));
        futureCapacityPanel.labelPercentageVipBoxes.setText(stadium.getZukunftLogen().map(future -> percentString(future, stadium.getZukunftGesamtgroesse().get())).orElse(notAvailableString));
        futureCapacityPanel.labelPercentageTotal.setText(stadium.getZukunftGesamtgroesse().map(future -> percentString(future, stadium.getZukunftGesamtgroesse().get())).orElse(notAvailableString));

        futureCapacityPanel.label2.setText(Optional.ofNullable(stadium.getExpansionDate())
                .map(HODateTime::nextLocalDay)
                .map(HODateTime::toLocaleDate)
                .orElse(EMPTY));

        setTranslation();
    }

    private void setTranslation() {
        currentCapacityPanel.setTranslation();
        currentCapacityPanel.setTitle(TranslationFacility.tr("ArenaInfoPanel.current"));
        currentCapacityPanel.label1.setText(TranslationFacility.tr("ArenaInfoPanel.last_improvement"));

        expandedCapacityPanel.setTranslation();
        expandedCapacityPanel.setTitle(TranslationFacility.tr("ArenaInfoPanel.expansion"));
        expandedCapacityPanel.labelPercentage.setText(TranslationFacility.tr("ArenaInfoPanel.change_in_percent"));
        expandedCapacityPanel.label1.setText(TranslationFacility.tr("ArenaInfoPanel.completion"));

        futureCapacityPanel.setTranslation();
        futureCapacityPanel.setTitle(TranslationFacility.tr("ArenaInfoPanel.future"));
        futureCapacityPanel.label1.setText(TranslationFacility.tr("ArenaInfoPanel.available"));
    }

    private void updateExpansionFinishedLabel() {
        final JLabel labelExpansionFinished = expandedCapacityPanel.label3;
        final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();
        Optional.ofNullable(stadium).map(Stadium::getExpansionDate).ifPresentOrElse(expansionDate -> {
                    final boolean building = expansionDate.isAfter(HODateTime.now());
                    if (building) {
                        final String text = toDurationString(expansionDate);
                        final String toolTipText = String.format(
                                TranslationFacility.tr("ArenaInfoPanel.finished_in_n_days_format"),
                                HODateTime.daysFromNow(expansionDate, 1));
                        labelExpansionFinished.setText(text);
                        labelExpansionFinished.setToolTipText(toolTipText);
                    } else {
                        labelExpansionFinished.setText(String.format(TranslationFacility.tr("ArenaInfoPanel.built")));
                        labelExpansionFinished.setToolTipText(null);
                    }
                },
                () -> {
                    labelExpansionFinished.setText(EMPTY);
                    labelExpansionFinished.setToolTipText(null);
                }
        );
    }

    private static String toDurationString(HODateTime hoDateTime) {
        return HumanDuration.of(HODateTime.between(HODateTime.now(), hoDateTime)).toHumanString();
    }
}