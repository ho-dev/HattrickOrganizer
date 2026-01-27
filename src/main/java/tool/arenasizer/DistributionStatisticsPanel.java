package tool.arenasizer;

import core.db.DBManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.model.ArenaStatistikModel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.TranslationFacility;
import core.util.Helper;
import module.matches.MatchesPanel;
import tool.updater.TableModel;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;


class DistributionStatisticsPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	public DistributionStatisticsPanel() {
		initialize();
	}

	private void initialize() {
		setLayout(new BorderLayout());
		add(createTable(), BorderLayout.CENTER);

	}

	protected JScrollPane createTable() {
		JTable table = new JTable(getModel());
		//table.setDefaultRenderer(Object.class, new UpdaterCellRenderer());
		table.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		table.getTableHeader().setReorderingAllowed(false);

		final TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(50));
        columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(50));

		JScrollPane scroll = new JScrollPane(table);
		return scroll;
	}

	protected TableModel getModel() {
		// DATUM SpielerRenderer
		String[] columnNames = {TranslationFacility.tr("ls.match.id"), TranslationFacility.tr("ls.match.weather"), TranslationFacility.tr("Zuschauer"), TranslationFacility.tr("ls.club.arena.terraces")+" ( %)",
					TranslationFacility.tr("ls.club.arena.basicseating")+" ( %)", TranslationFacility.tr("ls.club.arena.seatsunderroof")+" ( %)", TranslationFacility.tr("ls.club.arena.seatsinvipboxes")+" ( %)",
					TranslationFacility.tr("Fans")+" ( )"};

		ArenaStatistikModel[] matches=  DBManager.instance().getArenaStatistikModel(MatchesPanel.OWN_LEAGUE_GAMES).getMatches();
		IHOTableCellEntry[][] value = new IHOTableCellEntry[matches.length][columnNames.length];
        for (int i = 0; i < matches.length; i++) {
        	 value[i][0] = new ColorLabelEntry(matches[i].getMatchID()+"",
                     ColorLabelEntry.FG_STANDARD,  ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);

            value[i][1] = new ColorLabelEntry(ThemeManager.getIcon(HOIconName.WEATHER[matches[i].getWetter()]),0,ColorLabelEntry.FG_STANDARD,  ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            value[i][2] = new ColorLabelEntry(matches[i].getSpectators()+"",
                    ColorLabelEntry.FG_STANDARD,  ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            BigDecimal tmp = new BigDecimal(matches[i].getSpectators()).setScale(1);

            value[i][3] = createDoppelLabelEntry(matches[i].getSoldTerraces(), new BigDecimal(matches[i].getSoldTerraces()*100).setScale(1).divide(tmp, RoundingMode.HALF_DOWN).toString());
            value[i][4] = createDoppelLabelEntry(matches[i].getSoldBasics(),new BigDecimal(matches[i].getSoldBasics()*100).setScale(1).divide(tmp, RoundingMode.HALF_DOWN).toString());
            value[i][5] = createDoppelLabelEntry(matches[i].getSoldRoof(),new BigDecimal(matches[i].getSoldRoof()*100).setScale(1).divide(tmp, RoundingMode.HALF_DOWN).toString());
            value[i][6] = createDoppelLabelEntry(matches[i].getSoldVip(),new BigDecimal(matches[i].getSoldVip()*100).setScale(1).divide(tmp, RoundingMode.HALF_DOWN).toString());
            value[i][7] = createFansDoppelLabelEntry(matches[i].getFans(),tmp.divide(new BigDecimal(matches[i].getFans()), RoundingMode.HALF_DOWN).setScale(1).toString());
        }

        TableModel model = new TableModel(value, columnNames);

        return model;
    }

	private DoubleLabelEntries createDoppelLabelEntry(int leftValue, String rightValue){
    	return new DoubleLabelEntries(new ColorLabelEntry(leftValue+"",
                						ColorLabelEntry.FG_STANDARD,
                						ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT),
                	               new ColorLabelEntry(rightValue+" %",
                	            		   ThemeManager.getColor(HOColorName.PLAYER_OLD_FG),
                	            		   ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT));
    }

	private DoubleLabelEntries createFansDoppelLabelEntry(int leftValue, String rightValue){
    	return new DoubleLabelEntries(new ColorLabelEntry(leftValue+"",
                						ColorLabelEntry.FG_STANDARD,
                						ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT),
                	               new ColorLabelEntry(rightValue+"",
                	            		   ThemeManager.getColor(HOColorName.PLAYER_OLD_FG),
                	            		   ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT));
    }

}
