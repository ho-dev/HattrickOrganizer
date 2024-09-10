package module.transfer.scout;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.HomegrownEntry;
import core.gui.comp.entry.PlayerLabelEntry;
import core.model.HOVerwaltung;
import core.model.TranslationFacility;
import core.model.UserParameter;
import core.model.player.IMatchRoleID;
import core.model.player.MatchRoleID;
import core.model.player.Player;
import core.util.Helper;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.io.Serial;
import java.util.Objects;
import java.util.Vector;

import static core.model.player.IMatchRoleID.*;

/**
 * @author Volker Fischer
 * @version 0.2a    31.10.2001
 */
public class TransferTableModel extends AbstractTableModel {

    @Serial
    private static final long serialVersionUID = -7723286963812074041L;

    //~ Instance fields ----------------------------------------------------------------------------

    /**
     * Array of ToolTip Strings shown in the table header (first row of table)
     */
    public String[] m_sToolTipStrings =
            {
                    TranslationFacility.tr("ls.player.id"),
                    //Name
                    TranslationFacility.tr("ls.player.name"),
                    //Current price
                    TranslationFacility.tr("scout_price"),
                    //Ablaufdatum
                    TranslationFacility.tr("Ablaufdatum"),
                    //Beste Position
                    TranslationFacility.tr("BestePosition"),
                    //Age
                    TranslationFacility.tr("ls.player.age"),
                    //TSI
                    TranslationFacility.tr("ls.player.tsi"),
                    // Homegrown
                    TranslationFacility.tr("ls.player.motherclub"),
                    //Leadership
                    TranslationFacility.tr("ls.player.leadership"),
                    //Erfahrung
                    TranslationFacility.tr("ls.player.experience"),
                    //Form
                    TranslationFacility.tr("ls.player.form"),
                    //Kondition
                    TranslationFacility.tr("ls.player.skill.stamina"),
                    //Loyalty
                    TranslationFacility.tr("ls.player.loyalty"),
                    //Torwart
                    TranslationFacility.tr("ls.player.skill.keeper"),
                    //Verteidigung
                    TranslationFacility.tr("ls.player.skill.defending"),
                    //Spielaufbau
                    TranslationFacility.tr("ls.player.skill.playmaking"),
                    //Passpiel
                    TranslationFacility.tr("ls.player.skill.passing"),
                    //Flügelspiel
                    TranslationFacility.tr("ls.player.skill.winger"),
                    //Torschuss
                    TranslationFacility.tr("ls.player.skill.scoring"),
                    //Standards
                    TranslationFacility.tr("ls.player.skill.setpieces"),
                    //Torwart
                    TranslationFacility.tr("ls.player.position.keeper"),
                    //Innenverteidiger
                    TranslationFacility.tr("ls.player.position.centraldefender"),
                    //Innenverteidiger Nach Aussen
                    TranslationFacility.tr("ls.player.position.centraldefendertowardswing"),
                    //Innenverteidiger Offensiv
                    TranslationFacility.tr("ls.player.position.centraldefenderoffensive"),
                    //Aussenverteidiger
                    TranslationFacility.tr("ls.player.position.wingback"),
                    //Aussenverteidiger Nach Innen
                    TranslationFacility.tr("ls.player.position.wingbacktowardsmiddle"),
                    //Aussenverteidiger Offensiv
                    TranslationFacility.tr("ls.player.position.wingbackoffensive"),
                    //Aussenverteidiger Defensiv
                    TranslationFacility.tr("ls.player.position.wingbackdefensive"),
                    //Mittelfeld
                    TranslationFacility.tr("ls.player.position.innermidfielder"),
                    //Mittelfeld Nach Aussen
                    TranslationFacility.tr("ls.player.position.innermidfieldertowardswing"),
                    //Mittelfeld Offensiv
                    TranslationFacility.tr("ls.player.position.innermidfielderoffensive"),
                    //Mittelfeld Defensiv
                    TranslationFacility.tr("ls.player.position.innermidfielderdefensive"),
                    //Flügel
                    TranslationFacility.tr("ls.player.position.winger"),
                    //Flügel Nach Innen
                    TranslationFacility.tr("ls.player.position.wingertowardsmiddle"),
                    //Flügel Offensiv
                    TranslationFacility.tr("ls.player.position.wingeroffensive"),
                    //Flügel Defensiv
                    TranslationFacility.tr("ls.player.position.wingerdefensive"),
                    //Sturm
                    TranslationFacility.tr("ls.player.position.forward"),
                    //Sturm Defensiv
                    TranslationFacility.tr("ls.player.position.forwarddefensive"),
                    //Sturm Nach Aussen
                    TranslationFacility.tr("ls.player.position.forwardtowardswing"),
                    //Notes
                    TranslationFacility.tr("Notizen"),
                    //Notes
                    TranslationFacility.tr("ls.player.wage")
            };

    protected Object[][] m_clData;

    /**
     * Array of Strings shown in the table header (first row of table)
     */
    protected String[] m_sColumnNames =
            {
                    TranslationFacility.tr("ls.player.id"),
                    //Name
                    TranslationFacility.tr("ls.player.name"),
                    //Current price
                    TranslationFacility.tr("scout_price"),
                    //Ablaufdatum
                    TranslationFacility.tr("Ablaufdatum"),
                    //Beste Position
                    TranslationFacility.tr("BestePosition"),
                    //Age
                    TranslationFacility.tr("ls.player.age"),
                    //TSI
                    TranslationFacility.tr("ls.player.tsi"),
                    // Homegrown
                    TranslationFacility.tr("ls.player.short_motherclub"),
                    //Leadership
                    TranslationFacility.tr("ls.player.leadership"),
                    //Erfahrung
                    TranslationFacility.tr("ls.player.short_experience"),
                    //Form
                    TranslationFacility.tr("ls.player.short_form"),
                    //Kondition
                    TranslationFacility.tr("ls.player.skill_short.stamina"),
                    // Loyalty
                    TranslationFacility.tr("ls.player.short_loyalty"),
                    //Torwart
                    TranslationFacility.tr("ls.player.skill_short.keeper"),
                    //Verteidigung
                    TranslationFacility.tr("ls.player.skill_short.defending"),
                    //Spielaufbau
                    TranslationFacility.tr("ls.player.skill_short.playmaking"),
                    //Passpiel
                    TranslationFacility.tr("ls.player.skill_short.passing"),
                    //Flügelspiel
                    TranslationFacility.tr("ls.player.skill_short.winger"),
                    //Torschuss
                    TranslationFacility.tr("ls.player.skill_short.scoring"),
                    //Standards
                    TranslationFacility.tr("ls.player.skill_short.setpieces"),
                    //Torwart
                    TranslationFacility.tr("ls.player.position_short.keeper"),
                    //Innenverteidiger
                    TranslationFacility.tr("ls.player.position_short.centraldefender"),
                    //Innenverteidiger Nach Aussen
                    TranslationFacility.tr("ls.player.position_short.centraldefendertowardswing"),
                    //Innenverteidiger Offensiv
                    TranslationFacility.tr("ls.player.position_short.centraldefenderoffensive"),
                    //Aussenverteidiger
                    TranslationFacility.tr("ls.player.position_short.wingback"),
                    //Aussenverteidiger Zur Mitte
                    TranslationFacility.tr("ls.player.position_short.wingbacktowardsmiddle"),
                    //Aussenverteidiger Offensiv
                    TranslationFacility.tr("ls.player.position_short.wingbackoffensive"),
                    //Aussenverteidiger Defensiv
                    TranslationFacility.tr("ls.player.position_short.wingbackdefensive"),
                    //Mittelfeld
                    TranslationFacility.tr("ls.player.position_short.innermidfielder"),
                    //Mittelfeld Nach Aussen
                    TranslationFacility.tr("ls.player.position_short.innermidfieldertowardswing"),
                    //Mittelfeld Offensiv
                    TranslationFacility.tr("ls.player.position_short.innermidfielderoffensive"),
                    //Mittelfeld Defensiv
                    TranslationFacility.tr("ls.player.position_short.innermidfielderdefensive"),
                    //Flügel
                    TranslationFacility.tr("ls.player.position_short.winger"),
                    //Flügel Nach Innen
                    TranslationFacility.tr("ls.player.position_short.wingertowardsmiddle"),
                    //Flügel Offensiv
                    TranslationFacility.tr("ls.player.position_short.wingeroffensive"),
                    //Flügel Defensiv
                    TranslationFacility.tr("ls.player.position_short.wingerdefensive"),
                    //Sturm
                    TranslationFacility.tr("ls.player.position_short.forward"),
                    //Sturm Defensiv
                    TranslationFacility.tr("ls.player.position_short.forwarddefensive"),

                    //Sturm Nach Aussen
                    TranslationFacility.tr("ls.player.position_short.forwardtowardswing"),
                    //Notes
                    TranslationFacility.tr("Notizen"),
                    //Notes
                    TranslationFacility.tr("ls.player.wage")
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
     * Returns false.  This is the default implementation for all cells.
     *
     * @param row the row being queried
     * @param col the column being queried
     * @return false
     */
    @Override
    public final boolean isCellEditable(int row, int col) {
        return false;
    }

    /**
     * Returns the class of the table entry row=0, column=columnIndex
     * Returns String.class if there is no such entry
     *
     * @param columnIndex the column being queried
     * @return see above
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
     * Returns the name for the column of columnIndex.
     * Return null if there is no match.
     *
     * @param columnIndex the column being queried
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
            final Player aktuellerPlayer = new Player();
            aktuellerPlayer.setFirstName("");  //TODO: fix this
            aktuellerPlayer.setNickName(" "); //TODO: fix this
            aktuellerPlayer.setLastName(aktuellerScoutEintrag.getName());
            aktuellerPlayer.setSpecialty(aktuellerScoutEintrag.getSpeciality());
            aktuellerPlayer.setExperience(aktuellerScoutEintrag.getErfahrung());
            aktuellerPlayer.setLeadership(aktuellerScoutEintrag.getLeadership());
            aktuellerPlayer.setForm(aktuellerScoutEintrag.getForm());
            aktuellerPlayer.setStamina(aktuellerScoutEintrag.getKondition());
            aktuellerPlayer.setDefendingSkill(aktuellerScoutEintrag.getVerteidigung());
            aktuellerPlayer.setScoringSkill(aktuellerScoutEintrag.getTorschuss());
            aktuellerPlayer.setGoalkeeperSkill(aktuellerScoutEintrag.getTorwart());
            aktuellerPlayer.setWingerSkill(aktuellerScoutEintrag.getFluegelspiel());
            aktuellerPlayer.setPassingSkill(aktuellerScoutEintrag.getPasspiel());
            aktuellerPlayer.setSetPiecesSkill(aktuellerScoutEintrag.getStandards());
            aktuellerPlayer.setPlaymakingSkill(aktuellerScoutEintrag.getSpielaufbau());
            aktuellerPlayer.setLoyalty(aktuellerScoutEintrag.getLoyalty());
            aktuellerPlayer.setHomeGrown(aktuellerScoutEintrag.isHomegrown());

            var ratingPredictionModel = HOVerwaltung.instance().getModel().getRatingPredictionModel();
            //ID
            m_clData[i][0] = new ColorLabelEntry(aktuellerScoutEintrag.getPlayerID() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            //Name
            m_clData[i][1] = new PlayerLabelEntry(aktuellerPlayer, null, 0f, false, false);
            //Price
            m_clData[i][2] = new ColorLabelEntry(Helper.formatCurrency(aktuellerScoutEintrag.getPrice() / UserParameter.instance().FXrate),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            //Ablaufdatum
            m_clData[i][3] = new ColorLabelEntry(aktuellerScoutEintrag.getDeadline().getTime(),
                    java.text.DateFormat.getDateTimeInstance()
                            .format(aktuellerScoutEintrag.getDeadline()),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            //Beste Position
            m_clData[i][4] = new ColorLabelEntry(
                    MatchRoleID.getSortId(aktuellerPlayer.getIdealPosition(), false) - (aktuellerPlayer.getIdealPositionRating() / 100.0f),
                    MatchRoleID.getNameForPosition(aktuellerPlayer.getIdealPosition())
                            + " ("
                            + aktuellerPlayer.getIdealPositionRating() + ")",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
            //Age
            m_clData[i][5] = new ColorLabelEntry(aktuellerScoutEintrag.getAlterWithAgeDays(),
                    aktuellerScoutEintrag.getAlterWithAgeDaysAsString(),
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.CENTER);
            //TSI
            m_clData[i][6] = new ColorLabelEntry(aktuellerScoutEintrag.getTSI() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
            // Homegrown
            HomegrownEntry home = new HomegrownEntry();
            home.setPlayer(aktuellerPlayer);
            m_clData[i][7] = home;
            //Leadershio
            m_clData[i][8] = new ColorLabelEntry(aktuellerPlayer.getLeadership() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Erfahrung
            m_clData[i][9] = new ColorLabelEntry(aktuellerPlayer.getExperience() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Form
            m_clData[i][10] = new ColorLabelEntry(aktuellerPlayer.getForm() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_PLAYERSPECIALVALUES, SwingConstants.RIGHT);
            //Kondition
            m_clData[i][11] = new ColorLabelEntry(aktuellerPlayer.getStamina() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES, SwingConstants.RIGHT);
            // Loyalty
            m_clData[i][12] = new ColorLabelEntry(aktuellerPlayer.getLoyalty() + "",
                    ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Torwart
            m_clData[i][13] = new ColorLabelEntry(aktuellerPlayer.getGoalkeeperSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Verteidigung
            m_clData[i][14] = new ColorLabelEntry(aktuellerPlayer.getDefendingSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Spielaufbau
            m_clData[i][15] = new ColorLabelEntry(aktuellerPlayer.getPlaymakingSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Passpiel
            m_clData[i][16] = new ColorLabelEntry(aktuellerPlayer.getPassingSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Flügelspiel
            m_clData[i][17] = new ColorLabelEntry(aktuellerPlayer.getWingerSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Torschuss
            m_clData[i][18] = new ColorLabelEntry(aktuellerPlayer.getScoringSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Standards
            m_clData[i][19] = new ColorLabelEntry(aktuellerPlayer.getSetPiecesSkill() + "",
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_SINGLEPLAYERVALUES,
                    SwingConstants.RIGHT);

            //Wert Torwart
            m_clData[i][20] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.keeper, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger
            m_clData[i][21] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger Nach Aussen
            m_clData[i][22] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, TOWARDS_WING),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Innnenverteidiger Offensiv
            m_clData[i][23] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, IMatchRoleID.leftCentralDefender, OFFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger
            m_clData[i][24] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Nach Innen
            m_clData[i][25] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, TOWARDS_MIDDLE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Offensiv
            m_clData[i][26] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, OFFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Aussenverteidiger Defensiv
            m_clData[i][27] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftBack, DEFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld
            m_clData[i][28] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Nach Aussen
            m_clData[i][29] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, TOWARDS_WING),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Offensiv
            m_clData[i][30] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, OFFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Mittelfeld Defensiv
            m_clData[i][31] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftInnerMidfield, DEFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel
            m_clData[i][32] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Nach Innen
            m_clData[i][33] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, TOWARDS_MIDDLE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Offensiv
            m_clData[i][34] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, OFFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Flügel Defensiv
            m_clData[i][35] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftWinger, DEFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Sturm
            m_clData[i][36] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, NORMAL),
                    ColorLabelEntry.BG_PLAYERSPOSITIONVALUES, false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Sturm Defensiv
            m_clData[i][37] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, DEFENSIVE),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Wert Sturm Nach Aussen
            m_clData[i][38] = new ColorLabelEntry(
                    ratingPredictionModel.getPlayerMatchAverageRating(aktuellerPlayer, leftForward, TOWARDS_WING),
                    ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES,
                    false,
                    core.model.UserParameter.instance().nbDecimals);

            //Notiz
            m_clData[i][39] = new ColorLabelEntry(aktuellerScoutEintrag.getInfo(),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, JLabel.LEFT);

            m_clData[i][40] = new ColorLabelEntry(Helper.formatCurrency(aktuellerScoutEintrag.getbaseWage() / UserParameter.instance().FXrate),
                    ColorLabelEntry.FG_STANDARD,
                    ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);

        }
    }
}