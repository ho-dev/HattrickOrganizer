package module.transfer.scout;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.HomegrownEntry;
import core.gui.comp.entry.PlayerLabelEntry;
import core.model.HOVerwaltung;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;

import java.util.Objects;
import java.util.Vector;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;

/**
 * @author Volker Fischer
 * @version 0.2a    31.10.2001
 */
public class TransferTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -7723286963812074041L;

	//~ Instance fields ----------------------------------------------------------------------------

    /** Array of ToolTip Strings shown in the table header (first row of table) */
    public String[] m_sToolTipStrings =
    {
    	HOVerwaltung.instance().getLanguageString("ls.player.id"),
        //Name
	    HOVerwaltung.instance().getLanguageString("ls.player.name"),
	    //Current price
	    HOVerwaltung.instance().getLanguageString("scout_price"),
	    //Ablaufdatum
	    HOVerwaltung.instance().getLanguageString("Ablaufdatum"),
	    //Beste Position
	    HOVerwaltung.instance().getLanguageString("BestePosition"),
	    //Age
	    HOVerwaltung.instance().getLanguageString("ls.player.age"),
	    //TSI
	    HOVerwaltung.instance().getLanguageString("ls.player.tsi"),
	    // Homegrown
	    HOVerwaltung.instance().getLanguageString("ls.player.motherclub"),
        //Leadership
        HOVerwaltung.instance().getLanguageString("ls.player.leadership"),
	    //Erfahrung
	    HOVerwaltung.instance().getLanguageString("ls.player.experience"),
	    //Form
	    HOVerwaltung.instance().getLanguageString("ls.player.form"),
	    //Kondition
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"),
	    //Loyalty
	    HOVerwaltung.instance().getLanguageString("ls.player.loyalty"),
	    //Torwart
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"),
	    //Verteidigung
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"),
	    //Spielaufbau
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"),
	    //Passpiel
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"),
	    //Flügelspiel
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"),
	    //Torschuss
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"),
	    //Standards
	    HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"),
	    //Torwart
	    HOVerwaltung.instance().getLanguageString("ls.player.position.keeper"),
	    //Innenverteidiger
	    HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefender"),
	    //Innenverteidiger Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefendertowardswing"),
	    //Innenverteidiger Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefenderoffensive"),
	    //Aussenverteidiger
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingback"),
	    //Aussenverteidiger Nach Innen
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingbacktowardsmiddle"),
	    //Aussenverteidiger Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackoffensive"),
	    //Aussenverteidiger Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackdefensive"),
	    //Mittelfeld
	    HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielder"),
	    //Mittelfeld Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfieldertowardswing"),
	    //Mittelfeld Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderoffensive"),
	    //Mittelfeld Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderdefensive"),
	    //Flügel
	    HOVerwaltung.instance().getLanguageString("ls.player.position.winger"),
	    //Flügel Nach Innen
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingertowardsmiddle"),
	    //Flügel Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingeroffensive"),
	    //Flügel Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.wingerdefensive"),
	    //Sturm
	    HOVerwaltung.instance().getLanguageString("ls.player.position.forward"),
	    //Sturm Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position.forwarddefensive"),
		//Sturm Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position.forwardtowardswing"),
	    //Notes
	    HOVerwaltung.instance().getLanguageString("Notizen"),

    };

    protected Object[][] m_clData;

    /** Array of Strings shown in the table header (first row of table) */
    protected String[] m_sColumnNames =
    {
        HOVerwaltung.instance().getLanguageString("ls.player.id"),
        //Name
	    HOVerwaltung.instance().getLanguageString("ls.player.name"),
	    //Current price
	    HOVerwaltung.instance().getLanguageString("scout_price"),
	    //Ablaufdatum
	    HOVerwaltung.instance().getLanguageString("Ablaufdatum"),
	    //Beste Position
	    HOVerwaltung.instance().getLanguageString("BestePosition"),
	    //Age
	    HOVerwaltung.instance().getLanguageString("ls.player.age"),
	    //TSI
	    HOVerwaltung.instance().getLanguageString("ls.player.tsi"),
	    // Homegrown
	    HOVerwaltung.instance().getLanguageString("ls.player.short_motherclub"),
        //Leadership
        HOVerwaltung.instance().getLanguageString("ls.player.leadership"),
	    //Erfahrung
	    HOVerwaltung.instance().getLanguageString("ls.player.short_experience"),
	    //Form
	    HOVerwaltung.instance().getLanguageString("ls.player.short_form"),
	    //Kondition
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.stamina"),
	    // Loyalty
	    HOVerwaltung.instance().getLanguageString("ls.player.short_loyalty"),
	    //Torwart
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.keeper"),
	    //Verteidigung
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.defending"),
	    //Spielaufbau
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.playmaking"),
	    //Passpiel
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.passing"),
	    //Flügelspiel
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.winger"),
	    //Torschuss
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.scoring"),
	    //Standards
	    HOVerwaltung.instance().getLanguageString("ls.player.skill_short.setpieces"),
	    //Torwart
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.keeper"),
	    //Innenverteidiger
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefender"),
	    //Innenverteidiger Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefendertowardswing"),
	    //Innenverteidiger Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.centraldefenderoffensive"),
	    //Aussenverteidiger
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingback"),
	    //Aussenverteidiger Zur Mitte
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbacktowardsmiddle"),
	    //Aussenverteidiger Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackoffensive"),
	    //Aussenverteidiger Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingbackdefensive"),
	    //Mittelfeld
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielder"),
	    //Mittelfeld Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfieldertowardswing"),
	    //Mittelfeld Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderoffensive"),
	    //Mittelfeld Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.innermidfielderdefensive"),
	    //Flügel
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.winger"),
	    //Flügel Nach Innen
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingertowardsmiddle"),
	    //Flügel Offensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingeroffensive"),
	    //Flügel Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.wingerdefensive"),
	    //Sturm
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.forward"),
	    //Sturm Defensiv
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwarddefensive"),
	  //Sturm Nach Aussen
	    HOVerwaltung.instance().getLanguageString("ls.player.position_short.forwardtowardswing"),
	    //Notes
	    HOVerwaltung.instance().getLanguageString("Notizen"),

    };

    private Vector<ScoutEintrag> m_vScoutEintraege;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new TransferTableModel object.
     */
    public TransferTableModel(Vector<ScoutEintrag> scouteintraege) {
        m_vScoutEintraege = scouteintraege;
        initData();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     *  Returns false.  This is the default implementation for all cells.
     *
     *  @param  row  the row being queried
     *  @param  col the column being queried
     *  @return false
     */
    @Override
	public final boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     *  Returns the class of the table entry row=0, column=columnIndex
     *  Returns String.class if there is no such entry
     *
     *  @param columnIndex  the column being queried
     *  @return see above
     */
    @Override
	public final Class<?> getColumnClass(int columnIndex) {
        final Object obj = getValueAt(0, columnIndex);
        return Objects.requireNonNullElse(obj, "").getClass();
    }

    //-----Access methods----------------------------------------
    public final int getColumnCount() {
        return m_sColumnNames.length;
    }

    /**
     *  Returns the name for the column of columnIndex.
     *  Return null if there is no match.
     *
     *
     * @param columnIndex  the column being queried
     * @return a string containing the name of <code>column</code>
     */
    @Override
	public final String getColumnName(int columnIndex) {
        if (m_sColumnNames.length > columnIndex) {
            return m_sColumnNames[columnIndex];
        } else {
            return null;
        }
    }

    /**
     * Returns the number of data sets (without header).
     *
     * @return m_clData.length
     */
    public final int getRowCount() {
        if (m_clData != null) {
            return m_clData.length;
        } else {
            return 0;
        }
    }

    public final ScoutEintrag getScoutEintrag(int playerID) {
        for (ScoutEintrag scoutEintrag : m_vScoutEintraege) {
            if (scoutEintrag.getPlayerID() == playerID) {
                return scoutEintrag.duplicate();
            }
        }
        return null;
    }

    /**
     * Returns the list of ScoutEntries
     */
    public final java.util.Vector<ScoutEintrag> getScoutListe() {
        return m_vScoutEintraege;
    }

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

    @Override
	public final void setValueAt(Object value, int row, int column) {
        m_clData[row][column] = value;
    }

    public final Object getValueAt(int row, int column) {
        if (m_clData != null) {
            return m_clData[row][column];
        }
        return null;
    }

    /**
     * Set player again
     */
    public final void setValues(Vector<ScoutEintrag> scouteintraege) {
        m_vScoutEintraege = scouteintraege;
        initData();
    }

    /**
     * Add player to the table
     */
    public final void addScoutEintrag(ScoutEintrag scouteintraege) {
        m_vScoutEintraege.add(scouteintraege.duplicate());
        initData();
    }

    /**
     * Remove one ScoutEntry from table
     *
     * @param scouteintraege the ScoutEntry which will be removed from the table
     */
    public final void removeScoutEintrag(ScoutEintrag scouteintraege) {
        if (m_vScoutEintraege.remove(scouteintraege)) {
            initData();
        }
    }

    /**
     * Remove all players from table
     *
     */
    public final void removeScoutEntries() {
    	if (m_vScoutEintraege != null) {
    		m_vScoutEintraege.removeAllElements();
            initData();
    	}
    }

    //-----initialization-----------------------------------------

    /**
     * Return a Data[][] from the player vector
     */
    private void initData() {
        m_clData = new Object[m_vScoutEintraege.size()][m_sColumnNames.length];
        for (int i = 0; i < m_vScoutEintraege.size(); i++) {
            final ScoutEintrag aktuellerScoutEintrag = m_vScoutEintraege.get(i);
            final Player aktuellerPlayer = new Player(properties, hrfdate, hoModel.getID());
            aktuellerPlayer.setFirstName("");  //TODO: fix this
            aktuellerPlayer.setNickName(" "); //TODO: fix this
            aktuellerPlayer.setLastName(aktuellerScoutEintrag.getName());
            aktuellerPlayer.setPlayerSpecialty(aktuellerScoutEintrag.getSpeciality());
            aktuellerPlayer.setExperience(aktuellerScoutEintrag.getErfahrung());
            aktuellerPlayer.setLeadership(aktuellerScoutEintrag.getLeadership());
            aktuellerPlayer.setForm(aktuellerScoutEintrag.getForm());
            aktuellerPlayer.setStamina(aktuellerScoutEintrag.getKondition());
            aktuellerPlayer.setVerteidigung(aktuellerScoutEintrag.getVerteidigung());
            aktuellerPlayer.setTorschuss(aktuellerScoutEintrag.getTorschuss());
            aktuellerPlayer.setTorwart(aktuellerScoutEintrag.getTorwart());
            aktuellerPlayer.setFluegelspiel(aktuellerScoutEintrag.getFluegelspiel());
            aktuellerPlayer.setPasspiel(aktuellerScoutEintrag.getPasspiel());
            aktuellerPlayer.setStandards(aktuellerScoutEintrag.getStandards());
            aktuellerPlayer.setSpielaufbau(aktuellerScoutEintrag.getSpielaufbau());
            aktuellerPlayer.setLoyalty(aktuellerScoutEintrag.getLoyalty());
            aktuellerPlayer.setHomeGrown(aktuellerScoutEintrag.isHomegrown());
            //ID
            m_clData[i][0] = new ColorLabelEntry(aktuellerScoutEintrag.getPlayerID()+"",
                                                 ColorLabelEntry.FG_STANDARD,
                                                 ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            //Name
            m_clData[i][1] = new PlayerLabelEntry(aktuellerPlayer, null, 0f, false, false);
            //Price
            m_clData[i][2] = new ColorLabelEntry(aktuellerScoutEintrag.getPrice()+"",
                                                 ColorLabelEntry.FG_STANDARD,
                                                 ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            //Ablaufdatum
            m_clData[i][3] = new ColorLabelEntry(aktuellerScoutEintrag.getDeadline().getTime(),
                                                 java.text.DateFormat.getDateTimeInstance()
                                                 .format(aktuellerScoutEintrag.getDeadline()),
                                                 ColorLabelEntry.FG_STANDARD,
                                                 ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            //Beste Position
            m_clData[i][4] = new ColorLabelEntry(MatchRoleID
            		.getSortId(aktuellerPlayer.getIdealPosition(), false)
            		- (aktuellerPlayer.getIdealPositionStrength(true, null, false) / 100.0f),
            		MatchRoleID.getNameForPosition(aktuellerPlayer.getIdealPosition())
            		+ " ("
            		+ aktuellerPlayer.calcPosValue(aktuellerPlayer.getIdealPosition(), true, null, false) + ")",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            //Age
            m_clData[i][5] = new ColorLabelEntry(aktuellerScoutEintrag.getAlterWithAgeDays(),
            		aktuellerScoutEintrag.getAlterWithAgeDaysAsString(),
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            //TSI
            m_clData[i][6] = new ColorLabelEntry(aktuellerScoutEintrag.getTSI()+"",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            // Homegrown
            HomegrownEntry home = new HomegrownEntry();
            home.setPlayer(aktuellerPlayer);
            m_clData[i][7] = home;
            //Leadershio
            m_clData[i][8] = new ColorLabelEntry(aktuellerPlayer.getLeadership()+"",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Erfahrung
            m_clData[i][9] = new ColorLabelEntry(aktuellerPlayer.getExperience()+"",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Form
            m_clData[i][10] = new ColorLabelEntry(aktuellerPlayer.getForm()+"",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Kondition
            m_clData[i][11] = new ColorLabelEntry(aktuellerPlayer.getStamina()+"",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.RIGHT);
            // Loyalty
            m_clData[i][12] = new ColorLabelEntry(aktuellerPlayer.getLoyalty()+"",
            		ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Torwart
            m_clData[i][13] = new ColorLabelEntry(aktuellerPlayer.getGKskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Verteidigung
            m_clData[i][14] = new ColorLabelEntry(aktuellerPlayer.getDEFskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Spielaufbau
            m_clData[i][15] = new ColorLabelEntry(aktuellerPlayer.getPMskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Passpiel
            m_clData[i][16] = new ColorLabelEntry(aktuellerPlayer.getPSskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Flügelspiel
            m_clData[i][17] = new ColorLabelEntry(aktuellerPlayer.getWIskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Torschuss
            m_clData[i][18] = new ColorLabelEntry(aktuellerPlayer.getSCskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Standards
            m_clData[i][19] = new ColorLabelEntry(aktuellerPlayer.getSPskill()+"",
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                                                  SwingConstants.RIGHT);

            //Wert Torwart
            m_clData[i][20] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(IMatchRoleID.KEEPER,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger
            m_clData[i][21] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(IMatchRoleID.CENTRAL_DEFENDER,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger Nach Aussen
            m_clData[i][22] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.CENTRAL_DEFENDER_TOWING,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger Offensiv
            m_clData[i][23] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.CENTRAL_DEFENDER_OFF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger
            m_clData[i][24] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.BACK,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Nach Innen
            m_clData[i][25] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.BACK_TOMID,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Offensiv
            m_clData[i][26] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.BACK_OFF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Defensiv
            m_clData[i][27] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.BACK_DEF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld
            m_clData[i][28] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.MIDFIELDER,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Nach Aussen
            m_clData[i][29] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.MIDFIELDER_TOWING,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Offensiv
            m_clData[i][30] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.MIDFIELDER_OFF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Defensiv
            m_clData[i][31] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.MIDFIELDER_DEF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel
            m_clData[i][32] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.WINGER,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Nach Innen
            m_clData[i][33] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.WINGER_TOMID,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Offensiv
            m_clData[i][34] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.WINGER_OFF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Defensiv
            m_clData[i][35] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.WINGER_DEF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Sturm
            m_clData[i][36] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.FORWARD,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Wert Sturm Defensiv
            m_clData[i][37] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.FORWARD_DEF,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

          //Wert Sturm Nach Aussen
            m_clData[i][38] = new ColorLabelEntry(aktuellerPlayer.calcPosValue(MatchRoleID.FORWARD_TOWING,
                                                                                true, null, false),
                                                  ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                                                  false,
                                                  core.model.UserParameter.instance().nbDecimals);

            //Notiz
            m_clData[i][39] = new ColorLabelEntry(aktuellerScoutEintrag.getInfo(),
                                                  ColorLabelEntry.FG_STANDARD,
                                                  ColorLabelEntry.BG_STANDARD, JLabel.LEFT);

        }
    }
}
