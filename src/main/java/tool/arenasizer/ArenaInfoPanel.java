package tool.arenasizer;

import core.gui.UrlImageLabel;
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

    private final JPanel stadiumCard;
    private final UrlImageLabel imageLoader;
	private final CapacityPanel currentCapacityPanel;
    private final JPanel renovationCard;
	private final CapacityPanel expandedCapacityPanel;
	private final CapacityPanel futureCapacityPanel;

	public ArenaInfoPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // LEFT: stadium card (image + current capacity)
        stadiumCard = new JPanel();
        stadiumCard.setLayout(new BoxLayout(stadiumCard, BoxLayout.Y_AXIS));

        imageLoader = new UrlImageLabel();
        imageLoader.setPreferredSize(new Dimension(220, 220));
        imageLoader.setMinimumSize(new Dimension(220, 220));
        imageLoader.setMaximumSize(new Dimension(220, 220));
        imageLoader.setAlignmentX(Component.LEFT_ALIGNMENT);

        currentCapacityPanel = new CapacityPanel();
        currentCapacityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        stadiumCard.add(imageLoader);
        stadiumCard.add(Box.createVerticalStrut(8));
        stadiumCard.add(currentCapacityPanel);

        // RIGHT: renovation card (expanded + future)
        renovationCard = new JPanel();
        renovationCard.setLayout(new BoxLayout(renovationCard, BoxLayout.Y_AXIS));

        expandedCapacityPanel = new CapacityPanel();
        expandedCapacityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        futureCapacityPanel = new CapacityPanel();
        futureCapacityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        renovationCard.add(expandedCapacityPanel);
        renovationCard.add(Box.createVerticalStrut(8));
        renovationCard.add(futureCapacityPanel);

        add(stadiumCard, BorderLayout.WEST);
        add(renovationCard, BorderLayout.CENTER);

        updateValues();

        Timer timer = new Timer(1000, actionEvent -> updateExpansionFinishedLabel());
		timer.start();
	}

	private BigDecimal calculatePercentExpansion(int numberOfSeats, int expansion) {
		if (numberOfSeats == 0) {
			return null; // Ideally we'd have a way to represent ∞, maybe future update.
		} else {
			return new BigDecimal(expansion).divide(new BigDecimal(numberOfSeats), 3, RoundingMode.HALF_UP);
		}
	}

	private void updateValues() {
		final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();

        stadiumCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(stadium.getStadiumName()),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        imageLoader.loadWithFallback(stadium.getArenaImage(), stadium.getArenaFallbackImage());

		final String notAvailableString = TranslationFacility.tr("ls.general_label.not_available_abbreviation");
		final var numberformat = Helper.getNumberFormat( 0);

		// Current Capacity
		currentCapacityPanel.labelCountTerraces.setText(numberformat.format(stadium.getTerraces()));
		currentCapacityPanel.labelCountBasicSeating.setText(numberformat.format(stadium.getBasicSeating()));
		currentCapacityPanel.labelCountSeatsUnderRoof.setText(numberformat.format(stadium.getUnderRoofSeating()));
		currentCapacityPanel.labelCountVipBoxes.setText(numberformat.format(stadium.getVipBox()));
		currentCapacityPanel.labelCountTotal.setText(numberformat.format(stadium.getTotalSize()));

		currentCapacityPanel.labelPercentageTerraces.setText(percentString(stadium.getTerraces(), stadium.getTotalSize()));
		currentCapacityPanel.labelPercentageBasicSeating.setText(percentString(stadium.getBasicSeating(), stadium.getTotalSize()));
		currentCapacityPanel.labelPercentageSeatsUnderRoof.setText(percentString(stadium.getUnderRoofSeating(), stadium.getTotalSize()));
		currentCapacityPanel.labelPercentageVipBoxes.setText(percentString(stadium.getVipBox(), stadium.getTotalSize()));
		currentCapacityPanel.labelPercentageTotal.setText(percentString(stadium.getTotalSize(), stadium.getTotalSize()));

		currentCapacityPanel.label2.setText(Optional.ofNullable(stadium.getRebuiltDate())
			.map(HODateTime::toLocaleDate)
			.orElse(TranslationFacility.tr("ArenaInfoPanel.construction_in_progress")));

		if (stadium.isUnderConstruction()) {
			expandedCapacityPanel.labelCountTerraces.setNumber(stadium.getTerracesUnderConstruction());
			expandedCapacityPanel.labelCountBasicSeating.setNumber(stadium.getBasicSeatingUnderConstruction());
			expandedCapacityPanel.labelCountSeatsUnderRoof.setNumber(stadium.getUnderRoofSeatingUnderConstruction());
			expandedCapacityPanel.labelCountVipBoxes.setNumber(stadium.getVipBoxUnderConstruction());
			expandedCapacityPanel.labelCountTotal.setNumber(stadium.getTotalSizeUnderConstruction().orElse(null));

			expandedCapacityPanel.labelPercentageTerraces.setPercentNumber(
				calculatePercentExpansion(stadium.getTerraces(), stadium.getTerracesUnderConstruction())
			);
			expandedCapacityPanel.labelPercentageBasicSeating.setPercentNumber(
				calculatePercentExpansion(stadium.getBasicSeating(), stadium.getBasicSeatingUnderConstruction())
			);
			expandedCapacityPanel.labelPercentageSeatsUnderRoof.setPercentNumber(
				calculatePercentExpansion(stadium.getUnderRoofSeating(), stadium.getUnderRoofSeatingUnderConstruction())
			);
			expandedCapacityPanel.labelPercentageVipBoxes.setPercentNumber(
				calculatePercentExpansion(stadium.getVipBox(), stadium.getVipBoxUnderConstruction())
			);
			expandedCapacityPanel.labelPercentageTotal.setPercentNumber(
				calculatePercentExpansion(stadium.getTotalSize(), stadium.getTotalSizeUnderConstruction().orElse(0))
			);
		}

		expandedCapacityPanel.label2.setText(Optional.ofNullable(stadium.getExpansionDate())
                                             .map(expansionDate -> expansionDate.toLocaleDateTime())
                                             .orElse(EMPTY));

		// Future
		futureCapacityPanel.labelCountTerraces.setText(stadium.getFutureTerraces().map(numberformat::format).orElse(notAvailableString));
		futureCapacityPanel.labelCountBasicSeating.setText(stadium.getFutureBasicSeating().map(numberformat::format).orElse(notAvailableString));
		futureCapacityPanel.labelCountSeatsUnderRoof.setText(stadium.getFutureUnderRoofSeating().map(numberformat::format).orElse(notAvailableString));
		futureCapacityPanel.labelCountVipBoxes.setText(stadium.getFutureVipBoxes().map(numberformat::format).orElse(notAvailableString));
		futureCapacityPanel.labelCountTotal.setText(stadium.getFutureTotalSize().map(numberformat::format).orElse(notAvailableString));

		futureCapacityPanel.labelPercentageTerraces.setText(stadium.getFutureTerraces().map(future -> percentString(future, stadium.getFutureTotalSize().get())).orElse(notAvailableString));
		futureCapacityPanel.labelPercentageBasicSeating.setText(stadium.getFutureBasicSeating().map(future -> percentString(future, stadium.getFutureTotalSize().get())).orElse(notAvailableString));
		futureCapacityPanel.labelPercentageSeatsUnderRoof.setText(stadium.getFutureUnderRoofSeating().map(future -> percentString(future, stadium.getFutureTotalSize().get())).orElse(notAvailableString));
		futureCapacityPanel.labelPercentageVipBoxes.setText(stadium.getFutureVipBoxes().map(future -> percentString(future, stadium.getFutureTotalSize().get())).orElse(notAvailableString));
		futureCapacityPanel.labelPercentageTotal.setText(stadium.getFutureTotalSize().map(future -> percentString(future, stadium.getFutureTotalSize().get())).orElse(notAvailableString));

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

        renovationCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(TranslationFacility.tr("ArenaInfoPanel.stadium_expansion")),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

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
