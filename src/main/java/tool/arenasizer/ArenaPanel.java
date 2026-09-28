package tool.arenasizer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.util.Helper;
import tool.updater.TableModel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

final class ArenaPanel extends JPanel {

    private static final String[] ROW_TRANSLATION_KEYS = {"ls.club.arena.terraces", "ls.club.arena.basicseating", "ls.club.arena.seatsunderroof", "ls.club.arena.seatsinvipboxes", "Gesamt", "Einnahmen", "Unterhalt", "Gewinn", "Baukosten"};

    private static final int ROW_INDEX_TERRACES = 0;
    private static final int ROW_INDEX_BASIC_SEATING = 1;
    private static final int ROW_INDEX_SEATS_UNDER_ROOF = 2;
    private static final int ROW_INDEX_VIP = 3;
    private static final int ROW_INDEX_SUM = 4;
    private static final int ROW_INDEX_REVENUE = 5;
    private static final int ROW_INDEX_WEEKLY_COST = 6;
    private static final int ROW_INDEX_PROFIT = 7;
    private static final int ROW_INDEX_CONSTRUCTION_COST = 8;
    private static final int NUMBER_OF_ROWS = 9;

    private static final String[] COLUMN_TRANSLATION_KEYS_TITLES = {null, "Aktuell", "Maximal", "Durchschnitt", "Minimal"};

    private static final int COLUMN_INDEX_TITLE = 0;
    private static final int COLUMN_INDEX_ACTUAL = 1;
    private static final int COLUMN_INDEX_MAXIMUM = 2;
    private static final int COLUMN_INDEX_AVERAGE = 3;
    private static final int COLUMN_INDEX_MINIMUM = 4;
    private static final int NUMBER_OF_COLUMNS = 5;

    private final JTable jTable = new JTable();

    private Stadium currentStadium;
    private Stadium[] stadiumsMaxAvgMin;
    private IHOTableCellEntry[][] tableCellEntries;

    ArenaPanel() {
        setLayout(new BorderLayout());
        add(new JScrollPane(jTable));
        jTable.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
        jTable.getTableHeader().setReorderingAllowed(false);
        initTable();
        reInit();
    }

    public void reInit() {
        HOModel model = HOVerwaltung.instance().getModel();
        currentStadium = model.getStadium();
        stadiumsMaxAvgMin = ArenaSizer.calcConstructionArenas(currentStadium, model.getClub().getFans());
        reinitTable();
    }

    private void initTable() {
        // set table values
        tableCellEntries = new IHOTableCellEntry[NUMBER_OF_ROWS][NUMBER_OF_COLUMNS];

        for (int rowIndex = 0; rowIndex < ROW_TRANSLATION_KEYS.length; rowIndex++) {
            tableCellEntries[rowIndex][COLUMN_INDEX_TITLE] = new ColorLabelEntry(TranslationFacility.tr(ROW_TRANSLATION_KEYS[rowIndex]), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, SwingConstants.LEFT);
        }

        // Placeholders
        for (int row = ROW_INDEX_TERRACES; row < NUMBER_OF_ROWS; row++) {
            for (int column = COLUMN_INDEX_ACTUAL; column < NUMBER_OF_COLUMNS; column++) {
                if (row < COLUMN_INDEX_MINIMUM) {
                    tableCellEntries[row][column] = createDoubleLabelEntries(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
                } else if (row == COLUMN_INDEX_MINIMUM) {
                    tableCellEntries[row][column] = createDoubleLabelEntries(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
                } else {
                    tableCellEntries[row][column] = createDoubleLabelEntries(ColorLabelEntry.BG_SINGLEPLAYERVALUES);
                }
            }
        }

        jTable.setModel(new TableModel(tableCellEntries, createColumnTitles()));

        final TableColumnModel columnModel = jTable.getColumnModel();
        columnModel.getColumn(COLUMN_INDEX_TITLE).setMinWidth(Helper.calcCellWidth(150));
        columnModel.getColumn(COLUMN_INDEX_ACTUAL).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(COLUMN_INDEX_MAXIMUM).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(COLUMN_INDEX_AVERAGE).setMinWidth(Helper.calcCellWidth(160));
        columnModel.getColumn(COLUMN_INDEX_MINIMUM).setMinWidth(Helper.calcCellWidth(160));
    }

    /**
     * Create a new DoubleLabelEntries with default values
     *
     * @param background Color
     * @return DoubleLabelEntries
     */
    private static DoubleLabelEntries createDoubleLabelEntries(Color background) {
        return new DoubleLabelEntries(new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, background, SwingConstants.RIGHT), new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, background, SwingConstants.RIGHT));
    }

    void reinitArena(Stadium currentArena, int maxSupporter, int normalSupporter, int minSupporter) {
        currentStadium = currentArena;
        stadiumsMaxAvgMin = ArenaSizer.calcConstructionArenas(currentArena, maxSupporter, normalSupporter, minSupporter);
        reinitTable();
    }

    private void reinitTable() {
        final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();
        if (currentStadium != null) {
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_TERRACES][COLUMN_INDEX_ACTUAL]).getLeft().setText(formatNumber(currentStadium.getTerraces()));
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_TERRACES][COLUMN_INDEX_ACTUAL]).getRight().setSpecialNumber(currentStadium.getTerraces() - stadium.getTerraces(), false);
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_BASIC_SEATING][COLUMN_INDEX_ACTUAL]).getLeft().setText(formatNumber(currentStadium.getBasicSeating()));
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_BASIC_SEATING][COLUMN_INDEX_ACTUAL]).getRight().setSpecialNumber(currentStadium.getBasicSeating() - stadium.getBasicSeating(), false);
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SEATS_UNDER_ROOF][COLUMN_INDEX_ACTUAL]).getLeft().setText(formatNumber(currentStadium.getUnderRoofSeating()));
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SEATS_UNDER_ROOF][COLUMN_INDEX_ACTUAL]).getRight().setSpecialNumber(currentStadium.getUnderRoofSeating() - stadium.getUnderRoofSeating(), false);
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_VIP][COLUMN_INDEX_ACTUAL]).getLeft().setText(formatNumber(currentStadium.getVipBox()));
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_VIP][COLUMN_INDEX_ACTUAL]).getRight().setSpecialNumber(currentStadium.getVipBox() - stadium.getVipBox(), false);
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SUM][COLUMN_INDEX_ACTUAL]).getLeft().setText(formatNumber(currentStadium.getTotalSize()));
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SUM][COLUMN_INDEX_ACTUAL]).getRight().setSpecialNumber(currentStadium.getTotalSize() - stadium.getTotalSize(), false);
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_REVENUE][COLUMN_INDEX_ACTUAL]).getLeft().setText(ArenaAdmission.calculateIncome(currentStadium).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_REVENUE][COLUMN_INDEX_ACTUAL]).getRight().setText(ArenaAdmission.calculateIncome(currentStadium).minus(ArenaAdmission.calculateIncome(stadium)).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_WEEKLY_COST][COLUMN_INDEX_ACTUAL]).getLeft().setText(ArenaMaintenance.calculateCosts(currentStadium).times(BigDecimal.valueOf(-1)).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_WEEKLY_COST][COLUMN_INDEX_ACTUAL]).getRight().setText(ArenaMaintenance.calculateCosts(currentStadium).minus(ArenaMaintenance.calculateCosts(stadium)).times(BigDecimal.valueOf(-1)).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_PROFIT][COLUMN_INDEX_ACTUAL]).getLeft().setText(ArenaAdmission.calculateIncome(currentStadium).minus(ArenaMaintenance.calculateCosts(currentStadium)).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_PROFIT][COLUMN_INDEX_ACTUAL]).getRight().setText(ArenaAdmission.calculateIncome(currentStadium).minus(ArenaMaintenance.calculateCosts(currentStadium)).minus(ArenaAdmission.calculateIncome(stadium)).minus(ArenaMaintenance.calculateCosts(stadium)).toLocaleString());
            final int newTerraces = currentStadium.getTerraces() - stadium.getTerraces();
            final int newBasicSeating = currentStadium.getBasicSeating() - stadium.getBasicSeating();
            final int newUnderRoofSeating = currentStadium.getUnderRoofSeating() - stadium.getUnderRoofSeating();
            final int newVipBox = currentStadium.getVipBox() - stadium.getVipBox();
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_CONSTRUCTION_COST][COLUMN_INDEX_ACTUAL]).getLeft().setText(ArenaRebuild.calculateCosts(newTerraces, newBasicSeating, newUnderRoofSeating, newVipBox).toLocaleString());
            ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_CONSTRUCTION_COST][COLUMN_INDEX_ACTUAL]).getRight().setText("");

            final int startColumn = COLUMN_INDEX_MAXIMUM;
            for (int column = startColumn; column < NUMBER_OF_COLUMNS; column++) {
                final int stadiumTypeIndex = column - startColumn;
                final Stadium stadiumType = stadiumsMaxAvgMin[stadiumTypeIndex];
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_TERRACES][column]).getLeft().setText(formatNumber(stadiumType.getTerraces()));
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_TERRACES][column]).getRight().setSpecialNumber(stadiumType.getTerraces() - currentStadium.getTerraces(), false);
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_BASIC_SEATING][column]).getLeft().setText(formatNumber(stadiumType.getBasicSeating()));
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_BASIC_SEATING][column]).getRight().setSpecialNumber(stadiumType.getBasicSeating() - currentStadium.getBasicSeating(), false);
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SEATS_UNDER_ROOF][column]).getLeft().setText(formatNumber(stadiumType.getUnderRoofSeating()));
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SEATS_UNDER_ROOF][column]).getRight().setSpecialNumber(stadiumType.getUnderRoofSeating() - currentStadium.getUnderRoofSeating(), false);
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_VIP][column]).getLeft().setText(formatNumber(stadiumType.getVipBox()));
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_VIP][column]).getRight().setSpecialNumber(stadiumType.getVipBox() - currentStadium.getVipBox(), false);
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SUM][column]).getLeft().setText(formatNumber(stadiumType.getTotalSize()));
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_SUM][column]).getRight().setSpecialNumber(stadiumType.getTotalSize() - currentStadium.getTotalSize(), false);
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_REVENUE][column]).getLeft().setText(ArenaAdmission.calculateIncome(stadiumType).toLocaleString());
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_REVENUE][column]).getRight().setText(ArenaAdmission.calculateIncome(stadiumType).minus(ArenaAdmission.calculateIncome(currentStadium)).toLocaleString());
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_WEEKLY_COST][column]).getLeft().setText(ArenaMaintenance.calculateCosts(stadiumType).times(BigDecimal.valueOf(-1)).toLocaleString());
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_WEEKLY_COST][column]).getRight().setText(ArenaMaintenance.calculateCosts(stadiumType).minus(ArenaMaintenance.calculateCosts(currentStadium)).times(BigDecimal.valueOf(-1)).toLocaleString());
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_PROFIT][column]).getLeft().setText(ArenaAdmission.calculateIncome(stadiumType).minus(ArenaMaintenance.calculateCosts(stadiumType)).toLocaleString());
                ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_PROFIT][column]).getRight().setText(ArenaAdmission.calculateIncome(stadiumType).minus(ArenaMaintenance.calculateCosts(stadiumType)).minus(ArenaAdmission.calculateIncome(currentStadium)).minus(ArenaMaintenance.calculateCosts(currentStadium)).toLocaleString());
                var expansionCosts = stadiumType.getExpansionCosts();
                if (expansionCosts != null) {
                    ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_CONSTRUCTION_COST][column]).getLeft().setText(expansionCosts.times(BigDecimal.valueOf(-1)).toLocaleString());
                } else {
                    ((DoubleLabelEntries) tableCellEntries[ROW_INDEX_CONSTRUCTION_COST][column]).getLeft().setText("");
                }
            }

            jTable.setModel(new TableModel(tableCellEntries, createColumnTitles()));
            jTable.getColumnModel().getColumn(COLUMN_INDEX_TITLE).setMinWidth(Helper.calcCellWidth(150));
            jTable.getColumnModel().getColumn(COLUMN_INDEX_ACTUAL).setMinWidth(Helper.calcCellWidth(160));
            jTable.getColumnModel().getColumn(COLUMN_INDEX_MAXIMUM).setMinWidth(Helper.calcCellWidth(160));
            jTable.getColumnModel().getColumn(COLUMN_INDEX_AVERAGE).setMinWidth(Helper.calcCellWidth(160));
            jTable.getColumnModel().getColumn(COLUMN_INDEX_MINIMUM).setMinWidth(Helper.calcCellWidth(160));
        }
    }

    private static Object[] createColumnTitles() {
        return Stream.of(COLUMN_TRANSLATION_KEYS_TITLES).map(key -> Optional.ofNullable(key).map(TranslationFacility::tr).orElse("")).toArray();
    }

    private static String formatNumber(int number) {
        final var numberformat = Helper.getNumberFormat(0);
        return numberformat.format(number);
    }
}
