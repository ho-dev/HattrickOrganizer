package tool.arenasizer;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoubleLabelEntries;
import core.gui.comp.entry.IHOTableEntry;
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

final class ArenaPanel extends JPanel {

	private final ArenaSizer m_clArenaSizer = new ArenaSizer();
	private final JTable m_jtArena = new JTable();
	private final ArenaSizer m_clArenaSizer = new ArenaSizer();
	private final JTable m_jtArena = new JTable();

	//Teststadium
	private Stadium m_clStadium;
	private final String[] UEBERSCHRIFT = {"", TranslationFacility.tr("Aktuell"), TranslationFacility.tr("Maximal"),
		TranslationFacility.tr("Durchschnitt"), TranslationFacility.tr("Minimal")};
	private Stadium[] m_clStadien;
	private IHOTableEntry[][] values;

	ArenaPanel() {
		setLayout(new BorderLayout());
		add(new JScrollPane(m_jtArena));
		m_jtArena.setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		m_jtArena.getTableHeader().setReorderingAllowed(false);
		initTabelle();
		reInit();
	}

	public void reInit() {
		HOModel model = HOVerwaltung.instance().getModel();
		m_clStadium = model.getStadium();
		m_clStadien = m_clArenaSizer.calcConstructionArenas(m_clStadium, model.getClub().getFans());
		reinitTable();
	}

	private void initTabelle() {
		//Tablewerte setzen
		values = new IHOTableEntry[9][5];

		String[] columnText = {"ls.club.arena.terraces", "ls.club.arena.basicseating", "ls.club.arena.seatsunderroof", "ls.club.arena.seatsinvipboxes", "Gesamt", "Einnahmen", "Unterhalt", "Gewinn", "Baukosten"};
		for (int i = 0; i < columnText.length; i++) {
			values[i][0] = new ColorLabelEntry(TranslationFacility.tr(columnText[i]),
				ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, SwingConstants.LEFT);
		}

		//Platzwerte
		for (int i = 0; i < 9; i++) {
			for (int j = 1; j < 5; j++) {
				if (i < 4)
					values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
				else if (i == 4)
					values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
				else values[i][j] = createDoppelLabelEntry(ColorLabelEntry.BG_SINGLEPLAYERVALUES);
			}

		}

		m_jtArena.setModel(new TableModel(values, UEBERSCHRIFT));

		final TableColumnModel columnModel = m_jtArena.getColumnModel();
		columnModel.getColumn(0).setMinWidth(Helper.calcCellWidth(150));
		columnModel.getColumn(1).setMinWidth(Helper.calcCellWidth(160));
		columnModel.getColumn(2).setMinWidth(Helper.calcCellWidth(160));
		columnModel.getColumn(3).setMinWidth(Helper.calcCellWidth(160));
		columnModel.getColumn(4).setMinWidth(Helper.calcCellWidth(160));
	}

	/**
	 * Create a new DoppelLabelEntry with default values
	 *
	 * @param background Color
	 * @return DoubleLabelEntries
	 */
	private DoubleLabelEntries createDoppelLabelEntry(Color background) {
		return new DoubleLabelEntries(new ColorLabelEntry("",
			ColorLabelEntry.FG_STANDARD,
			background, SwingConstants.RIGHT),
			new ColorLabelEntry("",
				ColorLabelEntry.FG_STANDARD,
				background, SwingConstants.RIGHT));
	}

	void reinitArena(Stadium currentArena, int maxSupporter, int normalSupporter, int minSupporter) {
		m_clStadium = currentArena;
		m_clStadien = m_clArenaSizer.calcConstructionArenas(currentArena, maxSupporter, normalSupporter, minSupporter);
		reinitTable();
	}

	private void reinitTable() {
		final Stadium stadium = HOVerwaltung.instance().getModel().getStadium();
		if (m_clStadium != null) {
			((DoubleLabelEntries) values[0][1]).getLeft().setText(m_clStadium.getTerraces() + "");
			((DoubleLabelEntries) values[0][1]).getRight().setSpecialNumber(m_clStadium.getTerraces() - stadium.getTerraces(), false);
			((DoubleLabelEntries) values[1][1]).getLeft().setText(m_clStadium.getBasicSeating() + "");
			((DoubleLabelEntries) values[1][1]).getRight().setSpecialNumber(m_clStadium.getBasicSeating() - stadium.getBasicSeating(), false);
			((DoubleLabelEntries) values[2][1]).getLeft().setText(m_clStadium.getUnderRoofSeating() + "");
			((DoubleLabelEntries) values[2][1]).getRight().setSpecialNumber(m_clStadium.getUnderRoofSeating() - stadium.getUnderRoofSeating(), false);
			((DoubleLabelEntries) values[3][1]).getLeft().setText(m_clStadium.getVipBox() + "");
			((DoubleLabelEntries) values[3][1]).getRight().setSpecialNumber(m_clStadium.getVipBox() - stadium.getVipBox(), false);
			((DoubleLabelEntries) values[4][1]).getLeft().setText(m_clStadium.getTotalSize() + "");
			((DoubleLabelEntries) values[4][1]).getRight().setSpecialNumber(m_clStadium.getTotalSize() - stadium.getTotalSize(), false);
			((DoubleLabelEntries) values[5][1]).getLeft().setText(m_clArenaSizer.calcMaxIncome(m_clStadium).toLocaleString());
			((DoubleLabelEntries) values[5][1]).getRight().setText(m_clArenaSizer.calcMaxIncome(m_clStadium).minus(m_clArenaSizer.calcMaxIncome(stadium)).toLocaleString());
			((DoubleLabelEntries) values[6][1]).getLeft().setText(m_clArenaSizer.calcMaintenance(m_clStadium).times(BigDecimal.valueOf(-1)).toLocaleString());
			((DoubleLabelEntries) values[6][1]).getRight().setText(m_clArenaSizer.calcMaintenance(m_clStadium).minus(m_clArenaSizer.calcMaintenance(stadium)).times(BigDecimal.valueOf(-1)).toLocaleString());
			((DoubleLabelEntries) values[7][1]).getLeft().setText(m_clArenaSizer.calcMaxIncome(m_clStadium).minus(m_clArenaSizer.calcMaintenance(m_clStadium)).toLocaleString());
			((DoubleLabelEntries) values[7][1]).getRight().setText(
					m_clArenaSizer.calcMaxIncome(m_clStadium)
							.minus(m_clArenaSizer.calcMaintenance(m_clStadium))
							.minus(m_clArenaSizer.calcMaxIncome(stadium))
							.minus(m_clArenaSizer.calcMaintenance(stadium))
							.toLocaleString());
			((DoubleLabelEntries) values[8][1]).getLeft().setText(
					m_clArenaSizer.calcConstructionCosts(m_clStadium.getTerraces() - stadium.getTerraces(),
				m_clStadium.getBasicSeating() - stadium.getBasicSeating(),
				m_clStadium.getUnderRoofSeating() - stadium.getUnderRoofSeating(),
				m_clStadium.getVipBox() - stadium.getVipBox()).toLocaleString());
			((DoubleLabelEntries) values[8][1]).getRight().setText("");

			for (int i = 2; i < 5; i++) {
                ((DoubleLabelEntries) values[0][i]).getLeft().setText(m_clStadien[i - 2].getTerraces() + "");
                ((DoubleLabelEntries) values[0][i]).getRight().setSpecialNumber(m_clStadien[i - 2].getTerraces() - m_clStadium.getTerraces(), false);
                ((DoubleLabelEntries) values[1][i]).getLeft().setText(m_clStadien[i - 2].getBasicSeating() + "");
                ((DoubleLabelEntries) values[1][i]).getRight().setSpecialNumber(m_clStadien[i - 2].getBasicSeating() - m_clStadium.getBasicSeating(), false);
                ((DoubleLabelEntries) values[2][i]).getLeft().setText(m_clStadien[i - 2].getUnderRoofSeating() + "");
                ((DoubleLabelEntries) values[2][i]).getRight().setSpecialNumber(m_clStadien[i - 2].getUnderRoofSeating() - m_clStadium.getUnderRoofSeating(), false);
                ((DoubleLabelEntries) values[3][i]).getLeft().setText(m_clStadien[i - 2].getVipBox() + "");
                ((DoubleLabelEntries) values[3][i]).getRight().setSpecialNumber(m_clStadien[i - 2].getVipBox() - m_clStadium.getVipBox(), false);
                ((DoubleLabelEntries) values[4][i]).getLeft().setText(m_clStadien[i - 2].getTotalSize() + "");
                ((DoubleLabelEntries) values[4][i]).getRight().setSpecialNumber(m_clStadien[i - 2].getTotalSize() - m_clStadium.getTotalSize(), false);
                ((DoubleLabelEntries) values[5][i]).getLeft().setText(m_clArenaSizer.calcMaxIncome(m_clStadien[i - 2]).toLocaleString());
                ((DoubleLabelEntries) values[5][i]).getRight().setText(m_clArenaSizer.calcMaxIncome(m_clStadien[i - 2]).minus(m_clArenaSizer.calcMaxIncome(m_clStadium)).toLocaleString());
                ((DoubleLabelEntries) values[6][i]).getLeft().setText(m_clArenaSizer.calcMaintenance(m_clStadien[i - 2]).times(BigDecimal.valueOf(-1)).toLocaleString());
                ((DoubleLabelEntries) values[6][i]).getRight().setText(m_clArenaSizer.calcMaintenance(m_clStadien[i - 2]).minus(m_clArenaSizer.calcMaintenance(m_clStadium)).times(BigDecimal.valueOf(-1)).toLocaleString());
                ((DoubleLabelEntries) values[7][i]).getLeft().setText(m_clArenaSizer.calcMaxIncome(m_clStadien[i - 2]).minus(m_clArenaSizer.calcMaintenance(m_clStadien[i - 2])).toLocaleString());
                ((DoubleLabelEntries) values[7][i]).getRight().setText(m_clArenaSizer.calcMaxIncome(m_clStadien[i - 2])
                        .minus(m_clArenaSizer.calcMaintenance(m_clStadien[i - 2]))
                        .minus(m_clArenaSizer.calcMaxIncome(m_clStadium))
                        .minus(m_clArenaSizer.calcMaintenance(m_clStadium)).toLocaleString());
                var expansionCosts = m_clStadien[i - 2].getExpansionCosts();
                if (expansionCosts != null) {
                    ((DoubleLabelEntries) values[8][i]).getLeft().setText(expansionCosts.times(BigDecimal.valueOf(-1)).toLocaleString());
                } else {
                    ((DoubleLabelEntries) values[8][i]).getLeft().setText("");
                }
            }

			m_jtArena.setModel(new TableModel(values, UEBERSCHRIFT));
			m_jtArena.getColumnModel().getColumn(0).setMinWidth(Helper.calcCellWidth(150));
			m_jtArena.getColumnModel().getColumn(1).setMinWidth(Helper.calcCellWidth(160));
			m_jtArena.getColumnModel().getColumn(2).setMinWidth(Helper.calcCellWidth(160));
			m_jtArena.getColumnModel().getColumn(3).setMinWidth(Helper.calcCellWidth(160));
			m_jtArena.getColumnModel().getColumn(4).setMinWidth(Helper.calcCellWidth(160));
		}
	}
}