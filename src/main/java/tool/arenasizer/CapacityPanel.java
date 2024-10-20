package tool.arenasizer;

import core.gui.LabelWithSignedNumber;
import core.model.TranslationFacility;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.EMPTY;

public class CapacityPanel extends JPanel {

    private static final int HORIZONTAL_ALIGNMENT_PLACE_TYPE = SwingConstants.RIGHT;
    private static final int HORIZONTAL_ALIGNMENT_PLACES = SwingConstants.RIGHT;
    private static final int HORIZONTAL_ALIGNMENT_PERCENTAGE = SwingConstants.RIGHT;

    private static final boolean LONG_MODE = true;

    final JLabel labelPlaceType;
    final JLabel labelPlaces;
    final JLabel labelPercentage;

    final JLabel labelTerraces;
    final JLabel labelBasicSeating;
    final JLabel labelSeatsUnderRoof;
    final JLabel labelVipBoxes;
    final JLabel labelTotal;

    final LabelWithSignedNumber labelCountTerraces;
    final LabelWithSignedNumber labelCountBasicSeating;
    final LabelWithSignedNumber labelCountSeatsUnderRoof;
    final LabelWithSignedNumber labelCountVipBoxes;
    final LabelWithSignedNumber labelCountTotal;

    final LabelWithSignedNumber labelPercentageTerraces;
    final LabelWithSignedNumber labelPercentageBasicSeating;
    final LabelWithSignedNumber labelPercentageSeatsUnderRoof;
    final LabelWithSignedNumber labelPercentageVipBoxes;
    final LabelWithSignedNumber labelPercentageTotal;

    final JLabel label1;
    final JLabel label2;
    final JLabel label3;

    public CapacityPanel() {
        super(new GridLayout(7, 3, 3, 3));
        setTitle(EMPTY);

        // Title
        labelPlaceType = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        setFontToBold(labelPlaceType);
        add(labelPlaceType);
        labelPlaces = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        setFontToBold(labelPlaces);
        add(labelPlaces);
        labelPercentage = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        setFontToBold(labelPercentage);
        add(labelPercentage);

        // Terraces
        labelTerraces = new JLabel();
        loadImageIcon("gui/bilder/arena/terraces.png").ifPresent(labelTerraces::setIcon);
        labelTerraces.setHorizontalTextPosition(SwingConstants.LEADING);
        labelTerraces.setHorizontalAlignment(HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        add(labelTerraces);
        labelCountTerraces = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        add(labelCountTerraces);
        labelPercentageTerraces = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        add(labelPercentageTerraces);

        // Basic Seating
        labelBasicSeating = new JLabel();
        loadImageIcon("gui/bilder/arena/basicseating.png").ifPresent(labelBasicSeating::setIcon);
        labelBasicSeating.setHorizontalTextPosition(SwingConstants.LEADING);
        labelBasicSeating.setHorizontalAlignment(HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        add(labelBasicSeating);
        labelCountBasicSeating = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        add(labelCountBasicSeating);
        labelPercentageBasicSeating = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        add(labelPercentageBasicSeating);

        // Seats Under Roof
        labelSeatsUnderRoof = new JLabel();
        loadImageIcon("gui/bilder/arena/seatsunderroof.png").ifPresent(labelSeatsUnderRoof::setIcon);
        labelSeatsUnderRoof.setHorizontalTextPosition(SwingConstants.LEADING);
        labelSeatsUnderRoof.setHorizontalAlignment(HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        add(labelSeatsUnderRoof);
        labelCountSeatsUnderRoof = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        add(labelCountSeatsUnderRoof);
        labelPercentageSeatsUnderRoof = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        add(labelPercentageSeatsUnderRoof);

        // VIP
        labelVipBoxes = new JLabel();
        loadImageIcon("gui/bilder/arena/vipboxes.png").ifPresent(labelVipBoxes::setIcon);
        labelVipBoxes.setHorizontalTextPosition(SwingConstants.LEADING);
        labelVipBoxes.setHorizontalAlignment(HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        add(labelVipBoxes);
        labelCountVipBoxes = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        add(labelCountVipBoxes);
        labelPercentageVipBoxes = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        add(labelPercentageVipBoxes);

        // Total
        labelTotal = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        setFontToBold(labelTotal);
        add(labelTotal);
        labelCountTotal = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        setFontToBold(labelCountTotal);
        add(labelCountTotal);
        labelPercentageTotal = new LabelWithSignedNumber(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        setFontToBold(labelPercentageTotal);
        add(labelPercentageTotal);

        // Misc
        label1 = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACE_TYPE);
        setFontToBold(label1);
        add(label1);
        label2 = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PLACES);
        setFontToBold(label2);
        add(label2);
        label3 = new JLabel(EMPTY, HORIZONTAL_ALIGNMENT_PERCENTAGE);
        setFontToBold(label3);
        add(label3);

        setTranslation();
    }

    public void setTitle(String titel) {
        setBorder(BorderFactory.createTitledBorder(titel));
    }

    public void setTranslation() {
        labelPlaceType.setText(TranslationFacility.tr("ArenaInfoPanel.place_type"));
        labelPlaces.setText(TranslationFacility.tr("ArenaInfoPanel.places"));
        labelPercentage.setText(TranslationFacility.tr("ArenaInfoPanel.rate_in_percent"));

        labelTerraces.setToolTipText(TranslationFacility.tr("ls.club.arena.terraces"));
        if (LONG_MODE) {
            labelTerraces.setText(labelTerraces.getToolTipText());
        }

        labelBasicSeating.setToolTipText(TranslationFacility.tr("ls.club.arena.basicseating"));
        if (LONG_MODE) {
            labelBasicSeating.setText(labelBasicSeating.getToolTipText());
        }

        labelSeatsUnderRoof.setToolTipText(TranslationFacility.tr("ls.club.arena.seatsunderroof"));
        if (LONG_MODE) {
            labelSeatsUnderRoof.setText(labelSeatsUnderRoof.getToolTipText());
        }

        labelVipBoxes.setToolTipText(TranslationFacility.tr("ls.club.arena.seatsinvipboxes"));
        if (LONG_MODE) {
            labelVipBoxes.setText(labelVipBoxes.getToolTipText());
        }

        labelTotal.setToolTipText(TranslationFacility.tr("Gesamtgroesse"));
        if (LONG_MODE) {
            labelTotal.setText(TranslationFacility.tr("ArenaInfoPanel.total_seats_and_sum_sign"));
        } else {
            labelTotal.setText(TranslationFacility.tr("ArenaInfoPanel.sum_sign"));
        }
    }

    private static Optional<ImageIcon> loadImageIcon(String name) {
        try {
            return Optional.of(new ImageIcon(ImageIO.read(ClassLoader.getSystemResource(name))));
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    private static void setFontToBold(JComponent component) {
        component.setFont(component.getFont().deriveFont(Font.BOLD));
    }
}
