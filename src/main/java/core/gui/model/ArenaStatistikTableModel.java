package core.gui.model;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.ProgressbarTableEntry;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchKurzInfo;
import core.model.misc.Economy;
import core.util.HOLogger;
import core.util.Helper;
import core.util.StringUtils;

import java.awt.Color;
import java.text.DateFormat;
import java.time.Duration;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

public class ArenaStatistikTableModel extends AbstractTableModel {
    //~ Static fields/initializers -----------------------------------------------------------------

	private static final long serialVersionUID = 7187251269604772672L;


    //~ Instance fields ----------------------------------------------------------------------------

    public String[] m_sToolTipStrings = {
			HOVerwaltung.instance().getLanguageString("Datum"),
			// Spielart
			HOVerwaltung.instance().getLanguageString("Spielart"),
			// Gast
			HOVerwaltung.instance().getLanguageString("Gast"),
			HOVerwaltung.instance().getLanguageString("ls.match.result"),
			HOVerwaltung.instance().getLanguageString("ls.match.weather"),
			HOVerwaltung.instance().getLanguageString("ls.match.id"),
			HOVerwaltung.instance().getLanguageString("Aktuell"), // Stadiongroesse
			HOVerwaltung.instance().getLanguageString("Zuschauer"), // Zuschauer
			"%", // Auslastung
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.total"), // Income Total

			HOVerwaltung.instance().getLanguageString("ls.club.arena.terraces"),
			"%", // Percentage of crowd on the terraces
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.terraces"), // Income Terraces

			HOVerwaltung.instance().getLanguageString("ls.club.arena.basicseating"),
			"%", // Percentage of crowd in the basic seats
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.basicseating"), // Income Basic

			HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsunderroof"),
			"%", // Percentage of crowd under the roof
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.seatsunderroof"), // Income Seats Under Roof

			HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsinvipboxes"),
			"%", // Percentage of crowd in the VIP seats
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.seatsinvipboxes"), // Income Seats In Vip Boxes

			HOVerwaltung.instance().getLanguageString("Fans"), // Fananzahl
			HOVerwaltung.instance().getLanguageString("Fans") + " / "
					+ HOVerwaltung.instance().getLanguageString("Wochen"),
			HOVerwaltung.instance().getLanguageString("Zuschauer") + " / "
					+ HOVerwaltung.instance().getLanguageString("Fans"),
			HOVerwaltung.instance().getLanguageString("Fans"), // Stimmung
			HOVerwaltung.instance().getLanguageString("Platzierung") // LigaPlatz
    };

    protected Object[][] m_clData;

    protected String[] m_sColumnNames = {
			HOVerwaltung.instance().getLanguageString("Datum"),
			"", // Spielart
			HOVerwaltung.instance().getLanguageString("Gast"), // Gast
			HOVerwaltung.instance().getLanguageString("ls.match.result"),
			HOVerwaltung.instance().getLanguageString("ls.match.weather"),
			HOVerwaltung.instance().getLanguageString("ls.match.id"),
			HOVerwaltung.instance().getLanguageString("Aktuell"), // Stadiongroesse
			HOVerwaltung.instance().getLanguageString("Zuschauer"), // Zuschauer
			"%", // Auslastung
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.total"), // Income Total

			HOVerwaltung.instance().getLanguageString("ls.club.arena.terraces"),
			"%", // Percentage of crowd on the terraces
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.terraces"), // Income Terraces

			HOVerwaltung.instance().getLanguageString("ls.club.arena.basicseating"),
			"%", // Percentage of crowd in the basic seats
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.basicseating"), // Income Basic

			HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsunderroof"),
			"%", // Percentage of crowd under the roof
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.seatsunderroof"), // Income Seats Under Roof

			HOVerwaltung.instance().getLanguageString("ls.club.arena.seatsinvipboxes"),
			"%", // Percentage of crowd in the VIP seats
			HOVerwaltung.instance().getLanguageString("ls.club.arena.income.seatsinvipboxes"), // Income Seats In Vip Boxes

			HOVerwaltung.instance().getLanguageString("Fans"), // Fananzahl
			HOVerwaltung.instance().getLanguageString("Fans") + " / "
					+ HOVerwaltung.instance().getLanguageString("Wochen"),
			HOVerwaltung.instance().getLanguageString("Zuschauer") + " / "
					+ HOVerwaltung.instance().getLanguageString("Fans"),
			HOVerwaltung.instance().getLanguageString("Fans"), // Stimmung
			HOVerwaltung.instance().getLanguageString("Platzierung") // LigaPlatz
    };
    private ArenaStatistikModel[] m_clMatches;
    private int m_iMaxArenaGroesse;
    private int m_iMaxFananzahl;

    // ~ Constructors
	// -------------------------------------------------------------------------------

    /**
     * Creates a new ArenaStatistikTableModel object.
     */
    public ArenaStatistikTableModel() {
        //Nix
    }

    /**
     * Creates a new ArenaStatistikTableModel object.
     */
    public ArenaStatistikTableModel(ArenaStatistikModel[] matches,
    		int maxAreanGroesse, int maxFananzahl) {
        m_clMatches = matches;
        m_iMaxArenaGroesse = maxAreanGroesse;
        m_iMaxFananzahl = maxFananzahl;
        initData();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Check, if cell is editable.
     */
    @Override
	public final boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     * Get the class for a column.
     */
    @Override
	public final Class<?> getColumnClass(int columnIndex) {
        final Object obj = getValueAt(0, columnIndex);

        if (obj != null) {
            return obj.getClass();
        }

        return "".getClass();
    }

    /**
     * Get amount of columns.
     */
    @Override
	public final int getColumnCount() {
        return m_sColumnNames.length;
    }

    /**
     * Get the column name.
     */
    @Override
	public final String getColumnName(int columnIndex) {
        if ((m_sColumnNames != null) && (m_sColumnNames.length > columnIndex)) {
            return m_sColumnNames[columnIndex];
        } else {
            return null;
        }
    }

    /**
     * Get the ArenaStatistikModel for a certain match.
     */
    public final ArenaStatistikModel getMatch(int id) {
        if (id > 0) {
			for (ArenaStatistikModel m_clMatch : m_clMatches) {
				if (m_clMatch.getMatchID() == id) {
					return m_clMatch;
				}
			}
        }
        return null;
    }

    /**
     * Get amount of rows.
     */
    @Override
	public final int getRowCount() {
        return (m_clData != null) ? m_clData.length : 0;
    }

    /**
     * Get the value at a certain row and column.
     */
    public final Object getValue(int row, String columnName) {
        if ((m_sColumnNames != null) && (m_clData != null)) {
            int i = 0;

            while ((i < m_sColumnNames.length) && !m_sColumnNames[i].equals(columnName)) {
                i++;
            }

            return m_clData[row][i];
        } else {
            return null;
        }
    }

    /**
     * Set the value at a certain row and column.
     */
    @Override
	public final void setValueAt(Object value, int row, int column) {
        m_clData[row][column] = value;
    }

    /**
     * Get the value at a certain row and column.
     */
    @Override
	public final Object getValueAt(int row, int column) {
        if (m_clData != null) {
            return m_clData[row][column];
        }
        return null;
    }

    /**
     * Matches neu setzen
     */
    public final void setValues(ArenaStatistikModel[] matches, int maxAreanGroesse, int maxFananzahl) {
        m_clMatches = matches;
        m_iMaxArenaGroesse = maxAreanGroesse;
        m_iMaxFananzahl = maxFananzahl;
        initData();
    }





    //-----initialisierung-----------------------------------------

    /**
     * Erzeugt einen Data[][] aus dem Spielervector
     */
    private void initData() {
    	try {
			m_clData = new Object[m_clMatches.length][m_sColumnNames.length];

			for (int i = 0; i < m_clMatches.length; i++) {
			    final ArenaStatistikModel match = m_clMatches[i];
			    final Color background = MatchesColumnModel.getColor4Matchtyp(match.getMatchTyp());
				int colIndex = 0;

			    //Datum
				var matchDate = match.getMatchDate();
			    m_clData[i][colIndex] = new ColorLabelEntry(matchDate.toDbTimestamp().getTime(),
			    		matchDate.toLocaleDateTime(),
			    		ColorLabelEntry.FG_STANDARD, background, SwingConstants.LEFT);
				colIndex++;
			    //Spielart
			    m_clData[i][1] = new ColorLabelEntry(
			    					ThemeManager.getIcon(HOIconName.MATCHICONS[match.getMatchTyp().getIconArrayIndex()]),
			    		match.getMatchTyp().getMatchTypeId(), ColorLabelEntry.FG_STANDARD, background, SwingConstants.CENTER);
				colIndex++;
			    //Gast
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getGastName(), ColorLabelEntry.FG_STANDARD,
			    		background, SwingConstants.LEFT);
				colIndex++;

			    //Ergebnis
			    m_clData[i][colIndex] = new ColorLabelEntry(StringUtils.getResultString(match.getHeimTore(), match.getGastTore(), ""),
			    		ColorLabelEntry.FG_STANDARD, background, SwingConstants.CENTER);
			    //Sterne für Sieger!
			    if (match.getMatchStatus() != MatchKurzInfo.FINISHED) {
			        ((ColorLabelEntry) m_clData[i][colIndex]).setIcon(ImageUtilities.NOIMAGEICON);
			    } else if (match.getHeimTore() > match.getGastTore()) {
			        ((ColorLabelEntry) m_clData[i][colIndex]).setIcon(ImageUtilities.getStarIcon());
			    } else if (match.getHeimTore() < match.getGastTore()) {
			        ((ColorLabelEntry) m_clData[i][colIndex]).setIcon(ImageUtilities.NOIMAGEICON);
			    } else {
			        ((ColorLabelEntry) m_clData[i][colIndex]).setIcon(ImageUtilities.getStarIcon());
			    }
				colIndex++;

			    //Wetter
			    m_clData[i][colIndex] = new ColorLabelEntry(ThemeManager.getIcon(HOIconName.WEATHER[match.getWetter()]),
			                                         match.getWetter(), ColorLabelEntry.FG_STANDARD,
			                                         background, SwingConstants.RIGHT);
				colIndex++;

			    //Matchid
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getMatchID(), match.getMatchID() + "",
			                                         ColorLabelEntry.FG_STANDARD, background,
			                                         SwingConstants.RIGHT);
				colIndex++;

			    //Stadiongroesse
			    m_clData[i][colIndex] = new ProgressbarTableEntry(match.getArenaGroesse(), 0,
			    		m_iMaxArenaGroesse, 0, 1, background, new Color(0, 0, 120), "");
				colIndex++;

			    //Zuschauer
			    m_clData[i][colIndex] = new ProgressbarTableEntry(match.getZuschaueranzahl(), 0,
			    		m_iMaxArenaGroesse, 0, 1, background, new Color(0, 120, 0), "");
				colIndex++;

			    //Verhältnis Auslastung
			    m_clData[i][colIndex] = new ProgressbarTableEntry(
			    		(int) ((float) match.getZuschaueranzahl() / (float) match.getArenaGroesse() * 1000),
			    		0, 1000, 1, 0.1, background, new Color(0, 120, 120), " %");
				colIndex++;

			    //Total Income
				m_clData[i][colIndex] = new ColorLabelEntry(match.getTotalIncome(), Helper.getNumberFormat(true, 0).format(match.getTotalIncome()),
						ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.LEFT);
				colIndex++;

			    //Terraces
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getSoldTerraces() + " / " + match.getMaxTerraces() + "",
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.CENTER);
				colIndex++;

			    m_clData[i][colIndex] = new ProgressbarTableEntry((int) ((float) match.getSoldTerraces() / (float) match.getMaxTerraces() * 1000), 0, 1000, 1, 0.1, background, new Color(0, 120, 0), " %");
				colIndex++;

				//Terrace Income
				m_clData[i][colIndex] = new ColorLabelEntry(match.getTerracesIncome(), Helper.getNumberFormat(true, 0).format(match.getTerracesIncome()),
						ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.LEFT);
				colIndex++;

			    //Basic seats
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getSoldBasics() + " / " + match.getMaxBasic() + "",
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.CENTER);
				colIndex++;

                m_clData[i][colIndex] = new ProgressbarTableEntry((int) ((float) match.getSoldBasics() / (float) match.getMaxBasic() * 1000), 0, 1000, 1, 0.1, background, new Color(0, 120, 0), " %");
				colIndex++;

				//Basic Seat Income
				m_clData[i][colIndex] = new ColorLabelEntry(match.getBasicSeatIncome(), Helper.getNumberFormat(true, 0).format(match.getBasicSeatIncome()),
						ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.LEFT);
				colIndex++;

			    //Seats under the roof
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getSoldRoof() + " / " + match.getMaxRoof() + "",
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.CENTER);
				colIndex++;

                m_clData[i][colIndex] = new ProgressbarTableEntry((int) ((float) match.getSoldRoof() / (float) match.getMaxRoof() * 1000), 0, 1000, 1, 0.1, background, new Color(0, 120, 0), " %");
				colIndex++;

				//Seats under the roof Income
				m_clData[i][colIndex] = new ColorLabelEntry(match.getSeatRoofIncome(), Helper.getNumberFormat(true, 0).format(match.getSeatRoofIncome()),
						ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.LEFT);
				colIndex++;

			    //VIP seats
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getSoldVip() + " / " + match.getMaxVip() + "",
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.CENTER);
				colIndex++;

                m_clData[i][colIndex] = new ProgressbarTableEntry((int) ((float) match.getSoldVip() / (float) match.getMaxVip() * 1000), 0, 1000, 1, 0.1, background, new Color(0, 120, 0), " %");
				colIndex++;

				//SVIP seats Income
				m_clData[i][colIndex] = new ColorLabelEntry(match.getVipIncome(), Helper.getNumberFormat(true, 0).format(match.getVipIncome()),
						ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.LEFT);
				colIndex++;

			    // fan count
			    m_clData[i][colIndex] = new ProgressbarTableEntry(match.getFans(), 0, m_iMaxFananzahl, 0, 1, background, new Color(80, 80, 80), "");
				colIndex++;

			    // fan count increment per week
			    float fanzuwachs = 0;

			    if ((i + 1) < m_clMatches.length) {
					var diff = Duration.between(m_clMatches[i+1].getMatchDate().instant,match.getMatchDate().instant).toMillis();
			        fanzuwachs = ((match.getFans() - m_clMatches[i + 1].getFans()) * 604800000f) / diff;
			    }

			    m_clData[i][colIndex] = new ColorLabelEntry(fanzuwachs, background, false,false,0);
				colIndex++;

			    //Quotione  Zuschauer/Fans
			    m_clData[i][colIndex] = new ColorLabelEntry(Helper.round((float) match.getZuschaueranzahl()
						/ (float) match.getFans(), 2) + "", ColorLabelEntry.FG_STANDARD, background,
						SwingConstants.RIGHT);
				colIndex++;

			    // Fanstimmung
			    m_clData[i][colIndex] = new ColorLabelEntry(Economy.getNameForLevelFans(match.getFanZufriedenheit(), match.getMatchDate()),
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.LEFT);
				colIndex++;

			    //Ligaplatz
			    m_clData[i][colIndex] = new ColorLabelEntry(match.getLigaPlatz() + ".",
			                                          ColorLabelEntry.FG_STANDARD, background,
			                                          SwingConstants.CENTER);
				colIndex++;

			}
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), e);
		}
    }


	public ArenaStatistikModel[] getMatches() {
		return m_clMatches;
	}

}
