package core.db;

import core.HO;
import core.db.backup.BackupDialog;
import core.db.user.User;
import core.db.user.UserManager;
import core.file.hrf.HRF;
import core.gui.comp.table.HOTableModel;
import core.gui.model.ArenaStatistikTableModel;
import core.gui.model.PlayerMatchCBItem;
import core.model.*;
import core.model.Tournament.TournamentDetails;
import core.model.enums.DBDataSource;
import core.model.enums.MatchType;
import core.model.match.*;
import core.model.misc.Basics;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import core.util.HODateTime;
import module.matches.MatchLocation;
import module.nthrf.NtTeamDetails;
import module.youth.YouthPlayer;
import core.model.series.Liga;
import core.model.series.Paarung;
import core.training.FuturePlayerTraining;
import core.training.TrainingPerWeek;
import module.youth.YouthTrainerComment;
import core.util.HOLogger;
import core.util.ExceptionUtils;
import module.ifa.IfaMatch;
import module.lineup.substitution.model.Substitution;
import module.series.Spielplan;
import module.teamAnalyzer.vo.PlayerInfo;
import module.transfer.PlayerTransfer;
import module.transfer.scout.ScoutEintrag;
import module.youth.YouthTraining;
import org.jetbrains.annotations.Nullable;
import tool.arenasizer.Stadium;
import org.hsqldb.error.ErrorCode;
import java.io.File;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Db manager.
 */
public class DBManager {

	/** database versions */
	private static final int DBVersion = 700; // HO 7.0 version
	/**
	 * Previous db version is used by development versions to ensure that db upgrade will rerun on each
	 * new installed preliminary version
	 */
	private static final int previousDBVersion = 601;
	private static final double DBConfigVersion = 7d; // HO 7.0 version

	/** 2004-06-14 11:00:00.0 */
	public static Timestamp TSIDATE = new Timestamp(1087203600000L);

	/** singleton */
	private static @Nullable DBManager m_clInstance;

	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** DB-Adapter */
	private @Nullable JDBCAdapter m_clJDBCAdapter; // new JDBCAdapter();

	/** all Tables */
	private final Hashtable<String, AbstractTable> tables = new Hashtable<>();

	/** Erster Start */
	private boolean m_bFirstStart;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new instance of DBZugriff
	 */
	private DBManager() {
		m_clJDBCAdapter = new JDBCAdapter();
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Gets version.
	 *
	 * @return the version
	 */
	public static int getVersion() {
		if (HO.isDevelopment()) return previousDBVersion;
		return DBVersion;
	}

	/**
	 * Instance db manager.
	 *
	 * @return the db manager
	 */

	// INSTANCE ===============================================
	public static synchronized DBManager instance() {
		if (m_clInstance == null) {

			String errorMsg = null;
			try {
				User current_user = UserManager.instance().getCurrentUser();
				String dbFolder = current_user.getDbFolder();

				File dbfolder = new File(dbFolder);

				if (!dbfolder.exists()) {
					File parentFolder = new File(UserManager.instance().getDbParentFolder());

					boolean dbDirectoryCreated = false;
					if (!parentFolder.exists() || parentFolder.canWrite()) {
						dbDirectoryCreated = dbfolder.mkdirs();
					} else {
						errorMsg = "Could not initialize the database folder.";
						errorMsg += "No writing rights to the following directory\n" + parentFolder.getAbsolutePath() + "\n";
						errorMsg += "You can report this error by opening a new bug ticket on GitHub";
					}
					if (!dbDirectoryCreated) {
						errorMsg = "Could not create the database folder: " + dbfolder.getAbsolutePath();
					}
				}

			} catch (Exception e) {
				errorMsg = "Error encountered during database initialization: \n" + UserManager.instance().getCurrentUser().getDbURL();
				e.printStackTrace();
			}

			if (errorMsg != null) {
				javax.swing.JOptionPane.showMessageDialog(null, errorMsg, "Fatal DB Error", javax.swing.JOptionPane.ERROR_MESSAGE);
				System.exit(-1);
			}

			// Create new instance
			m_clInstance = new DBManager();
			DBUpdater dbUpdater = new DBUpdater();
			m_clInstance.initAllTables();
			// Try connecting to the DB
			try {
				m_clInstance.connect();
//				dbUpdater.setDbManager(tempInstance);
			} catch (Exception e) {

				String msg = e.getMessage();
				boolean recover = true;

				if ((msg.contains("The database is already in use by another process"))	||
						(e instanceof SQLException &&
							(((SQLException)e).getErrorCode() == ErrorCode.LOCK_FILE_ACQUISITION_FAILURE ||
									((SQLException)e).getErrorCode() == ErrorCode.LOCK_FILE_ACQUISITION_FAILURE * -1))) {
					if ((msg.contains("Permission denied"))
							|| msg.contains("system cannot find the path")) {
						msg = "Could not write to database. Make sure you have write access to the HO directory and its sub-directories.\n"
								+ "If under Windows make sure to stay out of Program Files or similar.";
					} else {
						msg = "The database is already in use. You have another HO running\n or the database is still closing. Wait and try again.";
					}
					recover = false;
				} else {
					msg = "Fatal database error. Exiting HO!\nYou should restore the db-folder from backup or delete that folder.";
				}

				javax.swing.JOptionPane
						.showMessageDialog(null, msg, "Fatal DB Error",
								javax.swing.JOptionPane.ERROR_MESSAGE);

				if (recover) {
					BackupDialog dialog = new BackupDialog();
					dialog.setVisible(true);
					while (dialog.isVisible()) {
						// wait
					}
				}

				HOLogger.instance().error(DBManager.class, msg);

				System.exit(-1);
			}

			// Does DB already exists?
			final boolean existsDB = m_clInstance.checkIfDBExists();

			// for startup
			m_clInstance.setFirstStart(!existsDB);

			// Do we need to create the database from scratch?
			if (!existsDB) {
				try {
					m_clInstance.executeSQL("SET FILES SPACE TRUE");
					m_clInstance.createAllTables();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				UserConfigurationTable configTable = (UserConfigurationTable) m_clInstance.getTable(UserConfigurationTable.TABLENAME);
				configTable.store(UserParameter.instance());
				configTable.store(HOParameter.instance());
			}
			else {
				// Check if there are any updates on the database to be done.
				dbUpdater.updateDB(DBVersion);
			}

			// tempInstance.updateConfig();
			HOLogger.instance().info(DBManager.class, "instance " + UserManager.instance().getCurrentUser().getDbURL() + "; parent folder: " + UserManager.instance().getDbParentFolder());
		}
		return m_clInstance;
	}

	private void executeSQL(String sql) {
		if ( m_clJDBCAdapter != null ) m_clJDBCAdapter.executeUpdate(sql);
	}

	private static final HashMap<String,PreparedStatement> preparedStatements = new HashMap<>();
	protected PreparedStatement getPreparedStatement(String sql) {
		PreparedStatement ret = preparedStatements.get(sql);
		if ( ret == null){
			ret = Objects.requireNonNull(m_clJDBCAdapter).createPreparedStatement(sql);
			preparedStatements.put(sql, ret);
		}
		return ret;
	}


	public static double getDBConfigVersion() {
		return DBConfigVersion;
	}

	/**
	 This method is called
	 */
	public void updateConfig(){
		DBConfigUpdater.updateDBConfig(DBConfigVersion);
	}

	private void initAllTables() {
		var adapter = this.m_clJDBCAdapter;
		tables.put(BasicsTable.TABLENAME, new BasicsTable(adapter));
		tables.put(TeamTable.TABLENAME, new TeamTable(adapter));
		tables.put(NtTeamTable.TABLENAME, new NtTeamTable(adapter));
		tables.put(FaktorenTable.TABLENAME, new FaktorenTable(adapter));
		tables.put(HRFTable.TABLENAME, new HRFTable(adapter));
		tables.put(StadionTable.TABLENAME, new StadionTable(adapter));
		tables.put(VereinTable.TABLENAME, new VereinTable(adapter));
		tables.put(LigaTable.TABLENAME, new LigaTable(adapter));
		tables.put(SpielerTable.TABLENAME, new SpielerTable(adapter));
		tables.put(EconomyTable.TABLENAME, new EconomyTable(adapter));
		tables.put(YouthPlayerTable.TABLENAME, new YouthPlayerTable(adapter));
		tables.put(YouthScoutCommentTable.TABLENAME, new YouthScoutCommentTable(adapter));
		tables.put(YouthTrainingTable.TABLENAME, new YouthTrainingTable(adapter));
		tables.put(TeamsLogoTable.TABLENAME, new TeamsLogoTable(adapter));
		tables.put(ScoutTable.TABLENAME, new ScoutTable(adapter));
		tables.put(UserColumnsTable.TABLENAME, new UserColumnsTable(adapter));
		tables.put(SpielerNotizenTable.TABLENAME, new SpielerNotizenTable(adapter));
		tables.put(SpielplanTable.TABLENAME, new SpielplanTable(adapter));
		tables.put(PaarungTable.TABLENAME, new PaarungTable(adapter));
		tables.put(MatchLineupTeamTable.TABLENAME, new MatchLineupTeamTable(adapter));
		tables.put(MatchLineupTable.TABLENAME, new MatchLineupTable(adapter));
		tables.put(XtraDataTable.TABLENAME, new XtraDataTable(adapter));
		tables.put(MatchLineupPlayerTable.TABLENAME,new MatchLineupPlayerTable(adapter));
		tables.put(MatchesKurzInfoTable.TABLENAME, new MatchesKurzInfoTable(adapter));
		tables.put(MatchDetailsTable.TABLENAME, new MatchDetailsTable(adapter));
		tables.put(MatchHighlightsTable.TABLENAME, new MatchHighlightsTable(adapter));
		tables.put(TrainingsTable.TABLENAME, new TrainingsTable(adapter));
		tables.put(FutureTrainingTable.TABLENAME, new FutureTrainingTable(adapter));
		tables.put(UserConfigurationTable.TABLENAME,new UserConfigurationTable(adapter));
		tables.put(SpielerSkillupTable.TABLENAME, new SpielerSkillupTable(adapter));
		tables.put(StaffTable.TABLENAME,  new StaffTable(adapter));
		tables.put(MatchSubstitutionTable.TABLENAME, new MatchSubstitutionTable(adapter));
		tables.put(TransferTable.TABLENAME, new TransferTable(adapter));
		tables.put(TransferTypeTable.TABLENAME, new TransferTypeTable(adapter));
		tables.put(ModuleConfigTable.TABLENAME, new ModuleConfigTable(adapter));
		tables.put(TAFavoriteTable.TABLENAME, new TAFavoriteTable(adapter));
		tables.put(TAPlayerTable.TABLENAME, new TAPlayerTable(adapter));
		tables.put(WorldDetailsTable.TABLENAME, new WorldDetailsTable(adapter));
		tables.put(IfaMatchTable.TABLENAME, new IfaMatchTable(adapter));
//		tables.put(PenaltyTakersTable.TABLENAME, new PenaltyTakersTable(adapter));
		tables.put(TournamentDetailsTable.TABLENAME, new TournamentDetailsTable(adapter));
		tables.put(FuturePlayerTrainingTable.TABLENAME, new FuturePlayerTrainingTable((adapter)));
		tables.put(MatchTeamRatingTable.TABLENAME, new MatchTeamRatingTable(adapter));
	}

	/**
	 * Gets table.
	 *
	 * @param tableName the table name
	 * @return the table
	 */
	AbstractTable getTable(String tableName) {
		return tables.get(tableName);
	}

	/**
	 * Gets adapter.
	 *
	 * @return the adapter
	 */
// Accessor
	public JDBCAdapter getAdapter() {
		return m_clJDBCAdapter;
	}

	private void setFirstStart(boolean firststart) {
		m_bFirstStart = firststart;
	}

	/**
	 * Is first start boolean.
	 *
	 * @return the boolean
	 */
	public boolean isFirstStart() {
		return m_bFirstStart;
	}

	/**
	 * disconnect from database
	 */
	public void disconnect() {
		if ( m_clJDBCAdapter != null) {
			m_clJDBCAdapter.disconnect();
			m_clJDBCAdapter = null;
		}
		m_clInstance = null;
	}

	/**
	 * connect to the database
	 */
	private void connect() throws Exception {
		User current_user = UserManager.instance().getCurrentUser();
		if (m_clJDBCAdapter != null) {
			m_clJDBCAdapter.connect(current_user.getDbURL(), current_user.getDbUsername(), current_user.getDbPwd(), UserManager.instance().getDriver());
		}
	}

	/**
	 * check if tables in DB exists
	 * 
	 * @return boolean
	 */
	private boolean checkIfDBExists() {
		if ( m_clJDBCAdapter==null) return false;
		boolean exists;
		try {
			ResultSet rs = m_clJDBCAdapter.executeQuery("SELECT Count(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'");
			assert rs != null;
			rs.next();
			exists = rs.getInt(1) > 0;
		} catch(SQLException e) {
			HOLogger.instance().error(getClass(), ExceptionUtils.getStackTrace(e));
			exists = false;
		  }
		return exists;
	}


	/**
	 * get the date of the last level increase of given player
	 *
	 * @param skill     integer code for the skill
	 * @param spielerId player ID
	 * @return [0] = Time of change  [1] = Boolean: false=no skill change found
	 */
	public Object[] getLastLevelUp(int skill, int spielerId) {
		return ((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.getLastLevelUp(skill, spielerId);
	}

	/**
	 * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill
	 * Vector filled with Skillup Objects
	 *
	 * @param skill        the skill
	 * @param m_iSpielerID the m i spieler id
	 * @return the all level up
	 */
	public Vector<Object[]> getAllLevelUp(int skill, int m_iSpielerID) {
		return ((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.getAllLevelUp(skill, m_iSpielerID);
	}

	/**
	 * Reimport skillup.
	 */
	public void reimportSkillup() {
		((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.importFromSpieler();
	}

	/**
	 * Check skillup.
	 *
	 * @param homodel the homodel
	 */
	public void checkSkillup(HOModel homodel) {
		((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.importNewSkillup(homodel);
	}

	// ------------------------------- SpielerTable
	// -------------------------------------------------

	/**
	 * gibt alle Player zurück, auch ehemalige
	 *
	 * @return the all spieler
	 */
	public Vector<Player> getAllSpieler() {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getAllSpieler();
	}

	/**
	 * Gibt die letzte Bewertung für den Player zurück // HRF
	 *
	 * @param spielerid the spielerid
	 * @return the letzte bewertung 4 spieler
	 */
	public int getLetzteBewertung4Spieler(int spielerid) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getLetzteBewertung4Spieler(spielerid);
	}

	/**
	 * lädt die Player zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the spieler
	 */
	public List<Player> getSpieler(int hrfID) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpieler(hrfID);
	}

	/**
	 * store youth players
	 *
	 * @param hrfId  the hrf id
	 * @param youthPlayers the list of youth players
	 */
	public void storeYouthPlayers(int hrfId, List<YouthPlayer> youthPlayers) {
		((YouthPlayerTable) getTable(YouthPlayerTable.TABLENAME)).storeYouthPlayers(hrfId,youthPlayers);
	}

	/**
	 * store youth players
	 *
	 * @param hrfId  the hrf id
	 * @param youthPlayer the youth player
	 */
	public void storeYouthPlayer(int hrfId, YouthPlayer youthPlayer) {
		((YouthPlayerTable) getTable(YouthPlayerTable.TABLENAME)).storeYouthPlayer(hrfId,youthPlayer);
	}

	/**
	 * Load youth players list.
	 *
	 * @param hrfID the hrf id
	 * @return the list
	 */
	public List<YouthPlayer> loadYouthPlayers(int hrfID) {
		return ((YouthPlayerTable) getTable(YouthPlayerTable.TABLENAME))
				.loadYouthPlayers(hrfID);
	}

	/**
	 * Load youth scout comments list.
	 *
	 * @param id the id
	 * @return the list
	 */
	public List<YouthPlayer.ScoutComment> loadYouthScoutComments(int id) {
		return ((YouthScoutCommentTable) getTable(YouthScoutCommentTable.TABLENAME))
				.loadYouthScoutComments(id);
	}

	/**
	 * Load youth player of match date youth player.
	 *
	 * @param id   the id
	 * @param date the date
	 * @return the youth player
	 */
	public YouthPlayer loadYouthPlayerOfMatchDate(int id, Timestamp date) {
		return ((YouthPlayerTable) getTable(YouthPlayerTable.TABLENAME))
				.loadYouthPlayerOfMatchDate(id, date);
	}

	/**
	 * Gibt einen Player zurück mit den Daten kurz vor dem Timestamp
	 *
	 * @param spielerid the spielerid
	 * @param time      the time
	 * @return the spieler at date
	 */
	public Player getSpielerAtDate(int spielerid, Timestamp time) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpielerNearDate(spielerid, time);
	}

	/**
	 * Gibt einen Player zurück aus dem ersten HRF
	 *
	 * @param spielerid the spielerid
	 * @return the spieler first hrf
	 */
	public Player getSpielerFirstHRF(int spielerid) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpielerFirstHRF(spielerid);
	}

	/**
	 * Returns the trainer code for the specified hrf. -99 if error
	 *
	 * @param hrfID HRF for which to load TrainerType
	 * @return int trainer type
	 */
	public int getTrainerType(int hrfID) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getTrainerType(hrfID);
	}

	/**
	 * speichert die Player
	 *
	 * @param hrfId  the hrf id
	 * @param player the player
	 * @param date   the date
	 */
	public void saveSpieler(int hrfId, List<Player> player, HODateTime date) {
		((SpielerTable) getTable(SpielerTable.TABLENAME)).saveSpieler(hrfId,
				player, date.toDbTimestamp());
	}

	// ------------------------------- LigaTable
	// -------------------------------------------------

	/**
	 * Gibt alle bekannten Ligaids zurück
	 *
	 * @return the integer [ ]
	 */
	public Integer[] getAllLigaIDs() {
		return ((LigaTable) getTable(LigaTable.TABLENAME)).getAllLigaIDs();
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the liga
	 */
	public Liga getLiga(int hrfID) {
		return ((LigaTable) getTable(LigaTable.TABLENAME)).getLiga(hrfID);
	}

	/**
	 * speichert die Basdics
	 *
	 * @param hrfId the hrf id
	 * @param liga  the liga
	 */
	public void saveLiga(int hrfId, Liga liga) {
		((LigaTable) getTable(LigaTable.TABLENAME)).saveLiga(hrfId, liga);
	}

	// ------------------------------- SpielplanTable
	// -------------------------------------------------

	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in
	 * der DB gefunden wurde
	 *
	 * @param seasonid the seasonid
	 * @return the liga id 4 saison id
	 */
	public int getLigaID4SaisonID(int seasonid) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getLigaID4SaisonID(seasonid);
	}

	/**
	 * holt einen Spielplan aus der DB, -1 bei den params holt den zuletzt
	 * gesavten Spielplan
	 *
	 * @param ligaId Id der Liga
	 * @param saison die Saison
	 * @return the spielplan
	 */
	public Spielplan getSpielplan(int ligaId, int saison) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getSpielplan(ligaId, saison);
	}

	public Spielplan getLatestSpielplan() {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getLatestSpielplan();
	}

	/**
	 * speichert einen Spielplan mitsamt Paarungen
	 *
	 * @param plan the plan
	 */
	public void storeSpielplan(Spielplan plan) {
		((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.storeSpielplan(plan);
	}

	public void deleteSpielplanTabelle(int saison, int ligaId) {
		var table = (SpielplanTable)getTable(SpielplanTable.TABLENAME);
		table.executePreparedDelete(saison, ligaId);
	}

	/**
	 * lädt alle Spielpläne aus der DB
	 *
	 * @param mitPaarungen inklusive der Paarungen ja/nein
	 * @return the spielplan [ ]
	 */
	public List<Spielplan> getAllSpielplaene(boolean mitPaarungen) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getAllSpielplaene(mitPaarungen);
	}

	// ------------------------------- MatchLineupPlayerTable
	// -------------------------------------------------

	/**
	 * Returns a list of ratings the player has played on [Max, Min, Average, posid]
	 *
	 * @param spielerid the spielerid
	 * @return the alle bewertungen
	 */
	public Vector<float[]> getAlleBewertungen(int spielerid) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getAllRatings(spielerid);
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Player, sowie die Anzahl der Bewertungen zurück // Match
	 *
	 * @param spielerid the spielerid
	 * @return the float [ ]
	 */
	public float[] getBewertungen4Player(int spielerid) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getBewertungen4Player(spielerid);
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Player, sowie die Anzahl der Bewertungen zurück // Match
	 *
	 * @param spielerid Spielerid
	 * @param position  Usere positionscodierung mit taktik
	 * @return the float [ ]
	 */
	public float[] getBewertungen4PlayerUndPosition(int spielerid, byte position) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getPlayerRatingForPosition(spielerid, position);
	}

	/**
	 * Gets match lineup players.
	 *
	 * @param matchID the match id
	 * @param matchType MatchType
	 * @param teamID  the team id
	 * @return the match lineup players
	 */
	public Vector<MatchLineupPosition> getMatchLineupPlayers(int matchID,
                                                             MatchType matchType, int teamID) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getMatchLineupPlayers(matchID, matchType, teamID);
	}

	/**
	 * Get match inserts of given Player
	 *
	 * @param objectPlayerID id of the player
	 * @return stored lineup positions of the player
	 */
	public List<MatchLineupPosition> getMatchInserts(int objectPlayerID) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getMatchInserts(objectPlayerID);
	}


	// ------------------------------- BasicsTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the basics
	 */
	public Basics getBasics(int hrfID) {
		return ((BasicsTable) getTable(BasicsTable.TABLENAME)).getBasics(hrfID);
	}

	/**
	 * Returns an HRF before the matchData and after previous TrainingTime
	 *
	 * @param matchTime matchData
	 * @return hrfId hrf id same training
	 */
	public int getHrfIDSameTraining(Timestamp matchTime) {
		return ((BasicsTable) getTable(BasicsTable.TABLENAME))
				.getHrfIDSameTraining(matchTime);
	}

	/**
	 * speichert die Basdics
	 *
	 * @param hrfId  the hrf id
	 * @param basics the basics
	 */
	public void saveBasics(int hrfId, core.model.misc.Basics basics) {
		((BasicsTable) getTable(BasicsTable.TABLENAME)).saveBasics(hrfId,
				basics);
	}

	/**
	 * Sets faktoren from db.
	 *
	 * @param fo the fo
	 */
// ------------------------------- FaktorenTable
	// -------------------------------------------------
	public void setFaktorenFromDB(FactorObject fo) {
		((FaktorenTable) getTable(FaktorenTable.TABLENAME))
				.pushFactorsIntoDB(fo);
	}

	/**
	 * Gets faktoren from db.
	 */
	public void getFaktorenFromDB() {
		((FaktorenTable) getTable(FaktorenTable.TABLENAME)).getFaktorenFromDB();
	}


	/**
	 * Gets tournament details from db.
	 *
	 * @param tournamentId the tournament id
	 * @return the tournament details from db
	 */
// Tournament Details
	public TournamentDetails getTournamentDetailsFromDB(int tournamentId) {
		TournamentDetails oTournamentDetails;
		oTournamentDetails = ((TournamentDetailsTable) getTable(TournamentDetailsTable.TABLENAME)).getTournamentDetails(tournamentId);
		return oTournamentDetails;
	}

	/**
	 * Store tournament details into db.
	 *
	 * @param oTournamentDetails the o tournament details
	 */
	public void storeTournamentDetailsIntoDB(TournamentDetails oTournamentDetails) {
		((TournamentDetailsTable) getTable(TournamentDetailsTable.TABLENAME)).storeTournamentDetails(oTournamentDetails);
	}

	// ------------------------------- FinanzenTable
	// -------------------------------------------------

	/**
	 * fetch the Economy table from the DB for the specified HRF ID
	 *
	 * @param hrfID the hrf id
	 * @return the economy
	 */
	public Economy getEconomy(int hrfID) {
		return ((EconomyTable) getTable(EconomyTable.TABLENAME)).getEconomy(hrfID);
	}

	/**
	 * store the economy info in the database
	 *
	 * @param hrfId   the hrf id
	 * @param economy the economy
	 * @param date    the date
	 */
	public void saveEconomyInDB(int hrfId, Economy economy, HODateTime date) {
		((EconomyTable) getTable(EconomyTable.TABLENAME)).storeEconomyInfoIntoDB(hrfId, economy, date.toDbTimestamp());
	}

	// ------------------------------- HRFTable
	// -------------------------------------------------

	/**
	 * Get a list of all HRFs
	 *
	 * @param asc   order ascending (descending otherwise)
	 * @return all matching HRFs
	 */
	public HRF[] loadAllHRFs( boolean asc) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).loadAllHRFs(asc);
	}

	/**
	 * get the latest imported hrf
	 * this does not have to be the latest downloaded, if the user imported hrf files in any order from files
	 * @return HRF object
	 */
	public HRF getMaxIdHrf() {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getMaxHrf();
	}

	/**
	 * get the latest downloaded hrf
	 * @return HRF object
	 */
	public HRF getLatestHRF(){
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getLatestHrf();
	}

	public HRF loadHRF(int id){
		return ((HRFTable) getTable(HRFTable.TABLENAME)).loadHRF(id);
	}

	/**
	 * save the HRF info
	 *
	 * @param hrfId hrf id
	 * @param datum date time
	 */
	public void saveHRF(int hrfId, HODateTime datum) {
		((HRFTable) getTable(HRFTable.TABLENAME)).saveHRF(hrfId, datum);
	}

	/**
	 * Gets hrfid 4 date.
	 *
	 * @param time the time
	 * @return the hrfid 4 date
	 */
	public int getHRFID4Date(Timestamp time) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getHrfIdNearDate(time);
	}

	/**
	 * is there is an HRFFile in the database with the same date?
	 *
	 * @param fetchDate the date
	 * @return The date of the file to which the file was imported or zero if no suitable file is available
	 */
	public HRF loadHRFDownloadedAt(Timestamp fetchDate) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).loadHRFDownloadedAt(fetchDate);
	}

	public HRF loadLatestHRFDownloadedBefore(Timestamp fetchDate){
		return ((HRFTable) getTable(HRFTable.TABLENAME)).loadLatestHRFDownloadedBefore(fetchDate);
	}

	// ------------------------------- SpielerNotizenTable
	// -------------------------------------------------

	public void storePlayerNotes(Player.Notes notes) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME)).store(notes);
	}

	public Player.Notes loadPlayerNotes(int playerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME)).load(playerId);
	}


	// ------------------------------- MatchLineupTable
	// -------------------------------------------------

	/**
	 * Load match lineup match lineup.
	 *
	 * @param iMatchType the source system
	 * @param matchID      the match id
	 * @return the match lineup
	 */
	public MatchLineup loadMatchLineup(int iMatchType, int matchID) {
		return ((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
				.loadMatchLineup(iMatchType, matchID);
	}

	/**
	 * Is the match already in the database?
	 *
	 * @param iMatchType the source system
	 * @param matchid      the matchid
	 * @return the boolean
	 */
	public boolean isMatchLineupInDB(MatchType iMatchType, int matchid) {
		return ((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
				.isMatchLineupInDB(iMatchType, matchid);
	}

	/**
	 * Is match ifk rating in db boolean.
	 *
	 * @param matchid the matchid
	 * @return the boolean
	 */
	public boolean isMatchIFKRatingInDB(int matchid) {
		return ((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME))
				.isMatchIFKRatingAvailable(matchid);
	}

	/**
	 * Has unsure weather forecast boolean.
	 *
	 * @param matchId the match id
	 * @return the boolean
	 */
	public boolean hasUnsureWeatherForecast(int matchId){
		return ((MatchesKurzInfoTable)getTable(MatchesKurzInfoTable.TABLENAME)).hasUnsureWeatherForecast(matchId);
	}
	// ------------------------------- MatchesKurzInfoTable
	// -------------------------------------------------

	/**
	 * Check if match is available
	 *
	 * @param matchid the matchid
	 * @param matchType type of the match
	 * @return the boolean
	 */
	public boolean isMatchInDB(int matchid, MatchType matchType) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.isMatchInDB(matchid, matchType);
	}

	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 *
	 * @param matchid The ID for the match
	 * @param matchType type of the match
	 * @return The kurz info object or null
	 */
	public MatchKurzInfo getMatchesKurzInfoByMatchID(int matchid, MatchType matchType) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfoByMatchID(matchid, matchType);
	}

	/**
	 * Get all matches for the given team from the database.
	 *
	 * @param teamId the teamid or -1 for all matches
	 * @return the match kurz info [ ]
	 */
	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId);
	}

	public List<MatchKurzInfo> getMatchesKurzInfo(String where, Object ... values) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(where, values);
	}

	/**
	 * Get last matches kurz info match kurz info.
	 *
	 * @param teamId the team id
	 * @return the match kurz info
	 */
	public MatchKurzInfo getLastMatchesKurzInfo(int teamId) {
		return  ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.loadLastMatchesKurzInfo(teamId);
	}

	public MatchKurzInfo getNextMatchesKurzInfo(int teamId) {
		return  ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.loadNextMatchesKurzInfo(teamId);
	}

	public MatchKurzInfo getLastMatchWithMatchId(int matchId) {
		return ((MatchesKurzInfoTable)getTable(MatchesKurzInfoTable.TABLENAME))
				.getLastMatchWithMatchId(matchId);

	}

	/**
	 * function that fetch info of match played related to the TrainingPerWeek instance
	 * @return MatchKurzInfo[] related to this TrainingPerWeek instance
	 */
	public List<MatchKurzInfo> loadOfficialMatchesBetween(int teamId, HODateTime firstMatchDate, HODateTime lastMatchDate) {
		return  ((MatchesKurzInfoTable)getTable(MatchesKurzInfoTable.TABLENAME)).getMatchesKurzInfo(teamId, firstMatchDate.toDbTimestamp(), lastMatchDate.toDbTimestamp(), MatchType.getOfficialMatchTypes());
	}

	/**
	 * function that fetch info of NT match played related to the TrainingPerWeek instance
	 * @return MatchKurzInfo[] related to this TrainingPerWeek instance
	 */
	public List<MatchKurzInfo> loadNTMatchesBetween(int teamId,HODateTime firstMatchDate, HODateTime lastMatchDate) {
		return  ((MatchesKurzInfoTable)getTable(MatchesKurzInfoTable.TABLENAME)).getMatchesKurzInfo(teamId, firstMatchDate.toDbTimestamp(), lastMatchDate.toDbTimestamp(), MatchType.getNTMatchType());
	}

	/**
	 * Get all matches with a certain status for the given team from the
	 * database.
	 *
	 * @param teamId      the teamid or -1 for all matches
	 * @param matchStatus the match status
	 * @return the match kurz info [ ]
	 */
	public List<MatchKurzInfo> getMatchesKurzInfo(final int teamId, final int matchStatus) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId, matchStatus);
	}


	/**
	 * Gets first upcoming match with team id.
	 *
	 * @param teamId the team id
	 * @return the first upcoming match with team id
	 */
	public MatchKurzInfo getFirstUpcomingMatchWithTeamId(final int teamId) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getFirstUpcomingMatchWithTeamId(teamId);
	}

	/**
	 * Get played match info array list (own team Only)
	 *
	 * @param iNbGames           the nb games
	 * @param bOfficialGamesOnly the b official games only
	 * @return the array list
	 */
	public List<MatchKurzInfo> getOwnPlayedMatchInfo(@Nullable Integer iNbGames, boolean bOfficialGamesOnly){
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME)).getPlayedMatchInfo(iNbGames, bOfficialGamesOnly, true);
	}

	/**
	 * Get played match info array list (own team Only)
	 *
	 * @param iNbGames           the nb games
	 * @param bOfficialGamesOnly the b official games only
	 * @return the array list
	 */
	public List<MatchKurzInfo> getPlayedMatchInfo(@Nullable Integer iNbGames, boolean bOfficialGamesOnly, boolean ownTeam){
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME)).getPlayedMatchInfo(iNbGames, bOfficialGamesOnly, ownTeam);
	}

	/**
	 * Returns an array of {@link MatchKurzInfo} for the team with ID <code>teamId</code>,
	 * and of type <code>matchtyp</code>.
	 * Important: if teamId is -1, <code>matchtype</code> must be set to
	 * <code>MatchesPanel.ALL_MATCHS</code>.
	 * @param teamId   The ID of the team, or -1 for all.
	 * @param iMatchType Type of match, as defined in {@link module.matches.MatchesPanel}
	 * @param matchLocation Home, Away, Neutral
	 *
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId, int iMatchType, MatchLocation matchLocation) {
		return getMatchesKurzInfo(teamId,iMatchType, matchLocation,  HODateTime.htStart.toDbTimestamp(), true);
	}

	/**
	 * Returns an array of {@link MatchKurzInfo} for the team with ID <code>teamId</code>,
	 * and of type <code>matchtyp</code>.
	 * Important: if teamId is -1, <code>matchtype</code> must be set to
	 * <code>MatchesPanel.ALL_MATCHS</code>.
	 *
	 * @param teamId   The ID of the team, or -1 for all.
	 * @param iMatchType Type of match, as defined in {@link module.matches.MatchesPanel}
	 * @param matchLocation Home, Away, Neutral
	 * @param from filter match schedule date
	 * @param includeUpcoming if false filter finished matches only
	 * @return MatchKurzInfo[] – Array of match info.
	 */
	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId, int iMatchType, MatchLocation matchLocation, Timestamp from, boolean includeUpcoming) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME)).getMatchesKurzInfo(teamId, iMatchType, matchLocation, from, includeUpcoming);
	}

	/**
	 * Get matches kurz info up coming match kurz info [ ].
	 *
	 * @param teamId the team id
	 * @return the match kurz info [ ]
	 */
	public List<MatchKurzInfo> getMatchesKurzInfoUpComing(int teamId) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfoUpComing(teamId);
	}

	/**
	 * Gets matches kurz info.
	 *
	 * @param teamId    the team id
	 * @param matchtyp  the matchtyp
	 * @param statistic the statistic
	 * @param home      the home
	 * @return the matches kurz info
	 */
	public MatchKurzInfo getMatchesKurzInfo(int teamId, int matchtyp,
			int statistic, boolean home) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId, matchtyp, statistic, home);
	}

	/**
	 * Gets matches kurz info statistics count.
	 *
	 * @param teamId    the team id
	 * @param matchtype the matchtype
	 * @param statistic the statistic
	 * @return the matches kurz info statistics count
	 */
	public int getMatchesKurzInfoStatisticsCount(int teamId, int matchtype,
			int statistic) {
		return MatchesOverviewQuery.getMatchesKurzInfoStatisticsCount(teamId,
				matchtype, statistic);
	}

	/**
	 * speichert die Matches
	 *
	 * @param matches the matches
	 */
	public void storeMatchKurzInfos(List<MatchKurzInfo> matches) {
		((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.storeMatchKurzInfos(matches);
	}

	// ------------------------------- ScoutTable
	// -------------------------------------------------

	/**
	 * Load player list for insertion into TransferScout
	 *
	 * @return the scout list
	 */
	public Vector<ScoutEintrag> getScoutList() {
		return ((ScoutTable) getTable(ScoutTable.TABLENAME)).getScoutList();
	}

	/**
	 * Save players from TransferScout
	 *
	 * @param list the list
	 */
	public void saveScoutList(Vector<ScoutEintrag> list) {
		((ScoutTable) getTable(ScoutTable.TABLENAME)).saveScoutList(list);
	}

	// ------------------------------- StadionTable
	// -------------------------------------------------

	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the stadion
	 */
	public Stadium getStadion(int hrfID) {
		return ((StadionTable) getTable(StadionTable.TABLENAME))
				.getStadion(hrfID);
	}

	/**
	 * speichert die Finanzen
	 *
	 * @param hrfId   the hrf id
	 * @param stadion the stadion
	 */
	public void saveStadion(int hrfId, Stadium stadion) {
		((StadionTable) getTable(StadionTable.TABLENAME)).saveStadion(hrfId,
				stadion);
	}
	
	
	// ------------------------------- StaffTable
	// -------------------------------------------------

	/**
	 * Fetch a list of staff store din a hrf
	 *
	 * @param hrfId the hrf id
	 * @return A list of StaffMembers belonging to the given hrf
	 */
	public List<StaffMember> getStaffByHrfId(int hrfId) {
		return ((StaffTable) getTable(StaffTable.TABLENAME)).getStaffByHrfId(hrfId);
	}

	/**
	 * Stores a list of StaffMembers
	 *
	 * @param hrfId The hrfId
	 * @param list  The staff objects
	 */
	public void saveStaff(int hrfId, List<StaffMember> list) {
		((StaffTable) getTable(StaffTable.TABLENAME)).storeStaff(hrfId, list);
	}
	
	
	// ------------------------------- MatchSubstitutionTable
	// -------------------------------------------------

	/**
	 * Returns an array with substitution belonging to the match-team.
	 *
	 * @param matchType  match type
	 * @param teamId       The teamId for the team in question
	 * @param matchId      The matchId for the match in question
	 * @return the match substitutions by match team
	 */
	public List<Substitution> getMatchSubstitutionsByMatchTeam(int matchId, MatchType matchType,
															   int teamId) {
		return ((MatchSubstitutionTable) getTable(MatchSubstitutionTable.TABLENAME))
				.getMatchSubstitutionsByMatchTeam(matchType.getId(), teamId, matchId);
	}

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] =
	 * Stimmung [1] = Selbstvertrauen
	 *
	 * @param hrfid the hrfid
	 * @return the string [ ]
	 */
	public String[] getStimmmungSelbstvertrauen(int hrfid) {
		return ((TeamTable) getTable(TeamTable.TABLENAME))
				.getStimmmungSelbstvertrauen(hrfid);
	}

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] =
	 * Stimmung [1] = Selbstvertrauen
	 *
	 * @param hrfid the hrfid
	 * @return the int [ ]
	 */
	public int[] getStimmmungSelbstvertrauenValues(int hrfid) {
		return ((TeamTable) getTable(TeamTable.TABLENAME))
				.getStimmmungSelbstvertrauenValues(hrfid);
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the team
	 */
	public Team getTeam(int hrfID) {
		return ((TeamTable) getTable(TeamTable.TABLENAME)).getTeam(hrfID);
	}

	/**
	 * speichert das Team
	 *
	 * @param hrfId the hrf id
	 * @param team  the team
	 */
	public void saveTeam(int hrfId, Team team) {
		((TeamTable) getTable(TeamTable.TABLENAME)).saveTeam(hrfId, team);
	}

	/**
	 * Gets the content of TrainingsTable as a vector of TrainingPerWeek objects
	 */
	public List<TrainingPerWeek> getTrainingList() {
		return ((TrainingsTable) getTable(TrainingsTable.TABLENAME))
				.getTrainingList();
	}

	public List<TrainingPerWeek> getTrainingList(Timestamp fromDate, Timestamp toDate) {
		return ((TrainingsTable) getTable(TrainingsTable.TABLENAME))
				.getTrainingList(fromDate, toDate);
	}

	public void saveTraining(TrainingPerWeek training, HODateTime lastTrainingDate, boolean force) {
		((TrainingsTable) getTable(TrainingsTable.TABLENAME)).saveTraining(training, lastTrainingDate, force);
	}

	public void saveTrainings(List<TrainingPerWeek> trainings, HODateTime lastTrainingDate, boolean force) {
		((TrainingsTable) getTable(TrainingsTable.TABLENAME)).saveTrainings(trainings, lastTrainingDate, force);
	}

	// ------------------------------- FutureTrainingTable
	// -------------------------------------------------

	/**
	 * Gets future trainings vector.
	 *
	 * @return the future trainings vector
	 */
	public List<TrainingPerWeek> getFutureTrainingsVector() {
		return ((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME)).getFutureTrainingsVector();
	}

	/**
	 * Save future training.
	 *
	 * @param training the training
	 */
	public void saveFutureTraining(TrainingPerWeek training) {
		((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME))
				.storeFutureTraining(training);
	}

	public void saveFutureTrainings(List<TrainingPerWeek> trainings) {
		((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME))
				.storeFutureTrainings(trainings);
	}

	public void clearFutureTrainingsTable(){
		((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME))
				.clearFutureTrainingsTable();
	}

	// ------------------------------- VereinTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the verein
	 */
	public Verein getVerein(int hrfID) {
		return ((VereinTable) getTable(VereinTable.TABLENAME)).getVerein(hrfID);
	}

	/**
	 * speichert das Verein
	 *
	 * @param hrfId  the hrf id
	 * @param verein the verein
	 */
	public void saveVerein(int hrfId, Verein verein) {
		((VereinTable) getTable(VereinTable.TABLENAME)).saveVerein(hrfId,
				verein);
	}

	/**
	 * Gets futur training.
	 *
	 * @param trainingDate the saison
	 * @return the futur training type
	 */
// ------------------------------- FutureTraining
	// -------------------------------------------------
	public int getFuturTraining(Timestamp trainingDate) {
		return ((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME)).loadFutureTrainings(trainingDate);
	}

	// ------------------------------- XtraDataTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 *
	 * @param hrfID the hrf id
	 * @return the xtra daten
	 */
	public XtraData getXtraDaten(int hrfID) {
		return ((XtraDataTable) getTable(XtraDataTable.TABLENAME))
				.loadXtraData(hrfID);
	}

	/**
	 * speichert das Team
	 *
	 * @param hrfId the hrf id
	 * @param xtra  the xtra
	 */
	public void saveXtraDaten(int hrfId, XtraData xtra) {
		((XtraDataTable) getTable(XtraDataTable.TABLENAME)).saveXtraDaten(
				hrfId, xtra);
	}

	// ------------------------------- UserParameterTable
	// -------------------------------------------------

	/**
	 * Lädt die UserParameter direkt in das UserParameter-SingeltonObjekt
	 */
	public void loadUserParameter() {
		UserConfigurationTable table = (UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME);
		table.load(UserParameter.instance());
		table.load(HOParameter.instance());
	}

	/**
	 * Saves the user parameters in the database.
	 */
	public void saveUserParameter() {
		UserConfigurationTable table = (UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME);
		table.store(UserParameter.instance());
		table.store(HOParameter.instance());
	}

	// ------------------------------- PaarungTable
	// -------------------------------------------------

	/**
	 * Gets the fixtures for the given <code>plan</code> from the DB, and add them to that plan.
	 *
	 * @param plan Schedule for which the fixtures are retrieved, and to which they are added.
	 */
	protected void getPaarungen(Spielplan plan) {
		((PaarungTable) getTable(PaarungTable.TABLENAME)).getPaarungen(plan);
	}

	/**
	 * Saves the fixtures to an existing game schedule ({@link Spielplan}).
	 *
	 * @param fixtures the fixtures
	 * @param ligaId   the liga id
	 * @param saison   the saison
	 */
	protected void storePaarung(List<Paarung> fixtures, int ligaId, int saison) {
		((PaarungTable) getTable(PaarungTable.TABLENAME)).storePaarung(fixtures, ligaId, saison);
	}

	public void deletePaarungTabelle(int saison, int ligaId) {
		var table = getTable(PaarungTable.TABLENAME);
		table.executePreparedDelete(saison, ligaId);
	}

	// ------------------------------- MatchDetailsTable
	// -------------------------------------------------

	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 *
	 * @param iMatchType the sourcesystem
	 * @param matchId      the match id
	 * @return the matchdetails
	 */
	public Matchdetails loadMatchDetails(int iMatchType, int matchId) {
		return ((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME))
				.loadMatchDetails(iMatchType, matchId);
	}

	/**
	 * Return match statistics (Count,Win,Draw,Loss,Goals)
	 *
	 * @param matchtype the matchtype
	 * @return matches overview row [ ]
	 */
	public MatchesOverviewRow[] getMatchesOverviewValues(int matchtype, MatchLocation matchLocation) {
		return MatchesOverviewQuery.getMatchesOverviewValues(matchtype, matchLocation);
	}

	// ------------------------------- MatchHighlightsTable
	// -------------------------------------------------

	/**
	 * Gibt die MatchHighlights zu einem Match zurück
	 *
	 * @param iMatchType the source system
	 * @param matchId      the match id
	 * @return the match highlights
	 */
	public ArrayList<MatchEvent> getMatchHighlights(int iMatchType, int matchId) {
		return ((MatchHighlightsTable) getTable(MatchHighlightsTable.TABLENAME))
				.getMatchHighlights(iMatchType, matchId);
	}

	/**
	 * Get chances stat matches highlights stat [ ].
	 *
	 * @param ownTeam   the own team
	 * @param iMatchType the matchtype
	 * @param matchLocation Home, Away, Neutral
	 * @return the matches highlights stat [ ]
	 */
	public MatchesHighlightsStat[] getGoalsByActionType(boolean ownTeam, int iMatchType, MatchLocation matchLocation) {
		return MatchesOverviewQuery.getGoalsByActionType(ownTeam, iMatchType, matchLocation);
	}

	/**
	 * Gets transfers.
	 *
	 * @param playerid     the playerid
	 * @param allTransfers the all transfers
	 * @return the transfers
	 */
// Transfer
	public List<PlayerTransfer> getTransfers(int playerid, boolean allTransfers) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
				.getTransfers(playerid, allTransfers);
	}

	/**
	 * Gets transfers.
	 *
	 * @param season the season
	 * @param bought the bought
	 * @param sold   the sold
	 * @return the transfers
	 */
	public List<PlayerTransfer> getTransfers(int season, boolean bought,
			boolean sold) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
				.getTransfers(season, bought, sold);
	}

	/**
	 * Remove transfer.
	 *
	 * @param transferId the transfer id
	 */
	public void removeTransfer(int transferId) {
		((TransferTable) getTable(TransferTable.TABLENAME))
				.removeTransfer(transferId);
	}

	/**
	 * Update player transfers.
	 *
	 * @param transfers the player id
	 */
	public void updatePlayerTransfers(List<PlayerTransfer> transfers) {
		((TransferTable) getTable(TransferTable.TABLENAME))
				.updatePlayerTransfers(transfers);
	}

	/**
	 * Update team transfers boolean.
	 *
	 * @param transfers the transfers
	 * @return the boolean
	 */
	public List<Player> updateTeamTransfers(List<PlayerTransfer> transfers) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
					.updateTeamTransfers(transfers);
	}

	/**
	 * Gets transfer type.
	 *
	 * @param playerId the player id
	 * @return the transfer type
	 */
	public int getTransferType(int playerId) {
		return ((TransferTypeTable) getTable(TransferTypeTable.TABLENAME))
				.getTransferType(playerId);
	}

	/**
	 * Sets transfer type.
	 *
	 * @param playerId the player id
	 * @param type     the type
	 */
	public void setTransferType(int playerId, int type) {
		((TransferTypeTable) getTable(TransferTypeTable.TABLENAME))
				.setTransferType(playerId, type);
	}

	/**
	 * Get all world detail leagues world detail league [ ].
	 *
	 * @return the world detail league [ ]
	 */
// WorldDetail
	public List<WorldDetailLeague> getAllWorldDetailLeagues() {
		return ((WorldDetailsTable) getTable(WorldDetailsTable.TABLENAME))
				.getAllWorldDetailLeagues();
	}

	/**
	 * Save world detail leagues.
	 *
	 * @param leagues the leagues
	 */
	public void saveWorldDetailLeagues(List<WorldDetailLeague> leagues) {
		WorldDetailsTable table = (WorldDetailsTable) getTable(WorldDetailsTable.TABLENAME);
		table.truncateTable();
		for (WorldDetailLeague league : leagues) {
			table.insertWorldDetailsLeague(league);
		}
	}

	// --------------------------------------------------------------------------------
	// -------------------------------- Statistik Part
	// --------------------------------
	// --------------------------------------------------------------------------------

	/**
	 * Get spieler daten 4 statistik double [ ] [ ].
	 *
	 * @param spielerId the spieler id
	 * @param anzahlHRF the anzahl hrf
	 * @return the double [ ] [ ]
	 */
	public double[][] getSpielerDaten4Statistik(int spielerId, int anzahlHRF) {
		return StatisticQuery.getSpielerDaten4Statistik(spielerId, anzahlHRF);
	}

	/**
	 * Get data for club statistics panel double [ ] [ ].
	 *
	 * @param nbHRFs the nb hr fs
	 * @return the double [ ] [ ]
	 */
	public double[][] getDataForClubStatisticsPanel(int nbHRFs) {
		return StatisticQuery.getDataForClubStatisticsPanel(nbHRFs);
	}

	/**
	 * Get data for finances statistics panel double [ ] [ ].
	 *
	 * @param nbHRF the nb hrf
	 * @return the double [ ] [ ]
	 */
	public double[][] getDataForFinancesStatisticsPanel(int nbHRF) {
		return StatisticQuery.getDataForFinancesStatisticsPanel(nbHRF);
	}

	/**
	 * Gets arena statistik model.
	 *
	 * @param matchtyp the matchtyp
	 * @return the arena statistik model
	 */
	public ArenaStatistikTableModel getArenaStatistikModel(int matchtyp) {
		return StatisticQuery.getArenaStatisticsModel(matchtyp);
	}

	/**
	 * Get data for team statistics panel double [ ] [ ].
	 *
	 * @param anzahlHRF the anzahl hrf
	 * @param group     the group
	 * @return the double [ ] [ ]
	 */
	public double[][] getDataForTeamStatisticsPanel(int anzahlHRF,
			String group) {
		return StatisticQuery.getDataForTeamStatisticsPanel(
				anzahlHRF, group);
	}

	/**
	 * Gets count of played matches.
	 *
	 * @param playerId the player id
	 * @param official the official
	 * @return the count of played matches
	 */
	public int getCountOfPlayedMatches(int playerId, boolean official) {
		var officialWhere = official ? "<8" : ">7";
		String sqlStmt = "select count(MATCHESKURZINFO.matchid) as MatchNumber FROM MATCHLINEUPPLAYER " +
				"INNER JOIN MATCHESKURZINFO ON MATCHESKURZINFO.matchid = MATCHLINEUPPLAYER.matchid " +
				"where spielerId = "+ playerId +
				" and FIELDPOS>-1  and matchtyp " + officialWhere;
		final ResultSet rs = getAdapter().executeQuery(sqlStmt);
		if (rs == null) {
			return 0;
		}
		int count = 0;
		try {
			while (rs.next()) {
				count = rs.getInt("MatchNumber");
			}
		} catch (SQLException ignored) {
		}
		return count;
	}

	/**
	 * Returns a list of PlayerMatchCBItems for given playerID
	 *
	 * @param playerID the player ID
	 */
	public Vector<PlayerMatchCBItem> getPlayerMatchCBItems(int playerID) {
		return getPlayerMatchCBItems(playerID, false);
	}

	/**
	 * Returns a list of PlayerMatchCBItems for given playerID
	 *
	 * @param playerID the player ID
	 * @param officialOnly whether or not to select official game only
	 */
	public Vector<PlayerMatchCBItem> getPlayerMatchCBItems(int playerID, boolean officialOnly) {
		if(playerID == -1) return new Vector<>();
		final Vector<PlayerMatchCBItem> spielerMatchCBItems = new Vector<>();
		String sql = """
				SELECT DISTINCT MatchID, MatchDate, Rating, SpielDatum, HeimName, HeimID, GastName, GastID, HoPosCode, MatchTyp
				FROM MATCHLINEUPPLAYER
				INNER JOIN MATCHLINEUP ON (MATCHLINEUPPLAYER.MatchID=MATCHLINEUP.MatchID AND MATCHLINEUPPLAYER.MATCHTYP=MATCHLINEUP.MATCHTYP)
				INNER JOIN MATCHDETAILS ON (MATCHDETAILS.MatchID=MATCHLINEUP.MatchID AND MATCHDETAILS.MATCHTYP=MATCHLINEUP.MATCHTYP)
				INNER JOIN MATCHESKURZINFO ON (MATCHESKURZINFO.MATCHID=MATCHLINEUP.MatchID AND MATCHESKURZINFO.MATCHTYP=MATCHLINEUP.MATCHTYP)
				WHERE MATCHLINEUPPLAYER.SpielerID=? AND MATCHLINEUPPLAYER.Rating>0""";

		if(officialOnly){
			var lMatchTypes =  MatchType.fromSourceSystem(SourceSystem.valueOf(SourceSystem.HATTRICK.getValue()));
			var inValues = lMatchTypes.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.joining(","));
			sql += " AND MATCHTYP IN (" + inValues + ")";
		}
		sql += " ORDER BY MATCHDETAILS.SpielDatum DESC";

		// Get all data on the player
		try {
			final Vector<PlayerMatchCBItem> playerMatchCBItems = new Vector<>();
			final ResultSet rs = m_clJDBCAdapter.executePreparedQuery(DBManager.instance().getPreparedStatement(sql), playerID);
			PlayerMatchCBItem playerMatchCBItem;
			assert rs != null;
			// Get all data on the player
			while (rs.next()) {
				playerMatchCBItem = new PlayerMatchCBItem(null,
						rs.getInt("MatchID"),
						(int)(rs.getFloat("Rating") * 2),
						rs.getInt("HoPosCode"),
						HODateTime.fromDbTimestamp(rs.getTimestamp("MatchDate")),
						rs.getString("HeimName"),
						rs.getInt("HeimID"),
						rs.getString("GastName"),
						rs.getInt("GastID"),
						MatchType.getById(rs.getInt("MatchTyp")),
						null,
						"",
						"");
				playerMatchCBItems.add(playerMatchCBItem);
			}

			Timestamp filter;
			// Get the player data for the matches
			for (final PlayerMatchCBItem item : playerMatchCBItems) {
				filter = item.getMatchdate().toDbTimestamp();
				// Player
				final Player player = getSpielerAtDate(playerID, filter);
				// Matchdetails
				final Matchdetails details = loadMatchDetails(item.getMatchType().getMatchTypeId(), item.getMatchID());
				// Stimmung und Selbstvertrauen
				final String[] sTSandConfidences = getStimmmungSelbstvertrauen(getHRFID4Date(filter));
				//Only if player data has been found, pass it into the return vector
				if ((player != null) && (details != null)
						&& (sTSandConfidences != null)) {
					item.setSpieler(player);
					item.setMatchdetails(details);
					item.setTeamSpirit(sTSandConfidences[0]);
					item.setConfidence(sTSandConfidences[1]);
					spielerMatchCBItems.add(item);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.getSpieler4Matches : " + e);
		}
		return spielerMatchCBItems;
	}


	/**
	 * Delete hrf.
	 *
	 * @param hrfid the hrfid
	 */
	public void deleteHRF(int hrfid) {
		getTable(StadionTable.TABLENAME).executePreparedDelete(hrfid);
		getTable(HRFTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(LigaTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(VereinTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(TeamTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(EconomyTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(BasicsTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(SpielerTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(SpielerSkillupTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(XtraDataTable.TABLENAME).executePreparedDelete( hrfid);
		getTable(StaffTable.TABLENAME).executePreparedDelete( hrfid);
	}

	/**
	 * Deletes all data for the given match
	 *
	 * @param matchid The matchid. Must be larger than 0.
	 */
	public void deleteMatch(int matchid, int matchType) {
		getTable(MatchDetailsTable.TABLENAME).executePreparedDelete(  matchType, matchid);
		getTable(MatchHighlightsTable.TABLENAME).executePreparedDelete(  matchType, matchid);
		getTable(MatchLineupTable.TABLENAME).executePreparedDelete(  matchType, matchid);
		getTable(MatchLineupTeamTable.TABLENAME).executePreparedDelete(  matchType, matchid);
		getTable(MatchLineupPlayerTable.TABLENAME).executePreparedDelete( matchType, matchid);
		getTable(MatchesKurzInfoTable.TABLENAME).executePreparedDelete( matchType, matchid);
		getTable(MatchSubstitutionTable.TABLENAME).executePreparedDelete(  matchType, matchid);
	}

	/**
	 * Stores the given match info. If info is missing, or the info are not for
	 * the same match, nothing is stored and false is returned. If the store is
	 * successful, true is returned.
	 * <p>
	 * If status of the info is not FINISHED, nothing is stored, and false is
	 * also returned.
	 *
	 * @param info    The MatchKurzInfo for the match
	 * @param details The MatchDetails for the match
	 * @param lineup  The MatchLineup for the match
	 * @return true if the match is stored. False if not
	 */
	public boolean storeMatch(MatchKurzInfo info, Matchdetails details,	MatchLineup lineup) {

		if ((info == null) || (details == null) || (lineup == null)) {
			return false;
		}

		if ((info.getMatchID() == details.getMatchID())
				&& (info.getMatchID() == lineup.getMatchID())
				&& (info.getMatchStatus() == MatchKurzInfo.FINISHED)) {

			deleteMatch( info.getMatchID(), info.getMatchType().getId());

			var matches = new ArrayList<MatchKurzInfo>();
			matches.add(info);
			((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
					.storeMatchKurzInfos(matches);

			((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME))
					.storeMatchDetails(details);
			((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
					.storeMatchLineup(lineup);

			return true;
		}
		return false;
	}

	/**
	 * Updates the given match in the database.
	 *
	 * @param match the match to update.
	 */
	public void updateMatchKurzInfo(MatchKurzInfo match) {
		((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.update(match);
	}
	
	private void createAllTables() throws SQLException {
		Object[] allTables = tables.values().toArray();
		for (Object allTable : allTables) {
			AbstractTable table = (AbstractTable) allTable;
			table.createTable();
			String[] statements = table.getCreateIndexStatement();
			for (String statement : statements) {
				m_clJDBCAdapter.executeUpdate(statement);
			}
		}
	}

	/**
	 * Load module configs map.
	 *
	 * @return the map
	 */
	public Map<String, Object> loadModuleConfigs() {
		return ((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.findAll();
	}

	/**
	 * Save module configs.
	 *
	 * @param values the values
	 */
	public void saveModuleConfigs(Map<String, Object> values) {
		((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.saveConfig(values);
	}

	/**
	 * Delete module configs key.
	 *
	 * @param key the key
	 */
	public void deleteModuleConfigsKey(String key) {
		((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.deleteConfig(key);
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	void saveUserParameter(String fieldName, int value) {
		saveUserParameter(fieldName, "" + value);
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	void saveUserParameter(String fieldName, double value) {
		saveUserParameter(fieldName, "" + value);
	}

	/**
	 * Set a single UserParameter in the DB
	 *
	 * @param fieldName the name of the parameter to set
	 * @param value     the target value
	 */
	void saveUserParameter(String fieldName, String value) {
		((UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME))
				.update(fieldName, value);
	}

	/**
	 * Save ho column model.
	 *
	 * @param model the model
	 */
	public void saveHOColumnModel(HOTableModel model) {
		((UserColumnsTable) getTable(UserColumnsTable.TABLENAME))
				.saveModel(model);
	}

	/**
	 * Load ho colum model.
	 *
	 * @param model the model
	 */
	public void loadHOColumModel(HOTableModel model) {
		((UserColumnsTable) getTable(UserColumnsTable.TABLENAME))
				.loadModel(model);
	}

	/**
	 * Remove ta favorite team.
	 *
	 * @param teamId the team id
	 */
	public void removeTAFavoriteTeam(int teamId) {
		((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME))
				.removeTeam(teamId);
	}

	/**
	 * Add ta favorite team.
	 *
	 * @param team the team
	 */
	public void addTAFavoriteTeam(module.teamAnalyzer.vo.Team team) {
		((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME)).addTeam(team);
	}

	/**
	 * Is ta favourite boolean.
	 *
	 * @param teamId the team id
	 * @return the boolean
	 */
	public boolean isTAFavourite(int teamId) {
		return ((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME))
				.isTAFavourite(teamId);
	}

	/**
	 * Returns all favourite teams
	 *
	 * @return List of Teams Object
	 */
	public List<module.teamAnalyzer.vo.Team> getTAFavoriteTeams() {
		return ((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME))
				.getTAFavoriteTeams();
	}

	/**
	 * Gets ta player info.
	 *
	 * @param playerId the player id
	 * @param week     the week
	 * @param season   the season
	 * @return the ta player info
	 */
	public PlayerInfo getTAPlayerInfo(int playerId, int week, int season) {
		return ((TAPlayerTable) getTable(TAPlayerTable.TABLENAME))
				.getPlayerInfo(playerId, week, season);
	}

	/**
	 * Gets ta latest player info.
	 *
	 * @param playerId the player id
	 * @return the ta latest player info
	 */
	public PlayerInfo getTALatestPlayerInfo(int playerId) {
		return ((TAPlayerTable) getTable(TAPlayerTable.TABLENAME))
				.getLatestPlayerInfo(playerId);
	}

	/**
	 * Add ta player info.
	 *
	 * @param info the info
	 */
	public void addTAPlayerInfo(PlayerInfo info) {
		((TAPlayerTable) getTable(TAPlayerTable.TABLENAME)).addPlayer(info);
	}

	/**
	 * Update ta player info.
	 *
	 * @param info the info
	 */
	public void updateTAPlayerInfo(PlayerInfo info) {
		((TAPlayerTable) getTable(TAPlayerTable.TABLENAME)).updatePlayer(info);
	}

	/**
	 * Is ifa matchin db boolean.
	 *
	 * @param matchId the match id
	 * @return the boolean
	 */
	public boolean isIFAMatchinDB(int matchId, int matchType) {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.isMatchInDB(matchId, matchType);
	}

	/**
	 * Gets last ifa match date.
	 *
	 * @return the last ifa match date
	 */
	public Timestamp getLastIFAMatchDate() {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.getLastMatchDate();
	}

	/**
	 * Get ifa matches ifa match [ ].
	 *
	 * @param home the home
	 * @return the ifa match [ ]
	 */
	public IfaMatch[] getIFAMatches(boolean home) {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.getMatches(home);
	}

	/**
	 * Insert ifa match.
	 *
	 * @param match the match
	 */
	public void insertIFAMatch(IfaMatch match) {
		((IfaMatchTable) getTable(IfaMatchTable.TABLENAME)).insertMatch(match);
	}

	/**
	 * Deletes all the content of the IFA match table.
	 */
	public void deleteIFAMatches() {
		((IfaMatchTable) getTable(IfaMatchTable.TABLENAME)).deleteAllMatches();
	}

	public static Timestamp getTimestamp(ResultSet rs, String columnLabel){
		try {
			var ret = rs.getTimestamp(columnLabel);
			if (!rs.wasNull()) return ret;
		} catch (Exception ignored) {
		}
		return null;
	}

	public static String getString(ResultSet rs, String columnLabel){
		try {
			var ret = rs.getString(columnLabel);
			if (!rs.wasNull()) return ret;
		} catch (Exception ignored) {
		}
		return "";
	}

	/**
	 * Gets integer.
	 *
	 * @param rs          the rs
	 * @param columnLabel the column label
	 * @return the integer
	 */
	public static Integer getInteger(ResultSet rs, String columnLabel) {
		try {
			var ret = rs.getInt(columnLabel);
			if (rs.wasNull()) return null;
			return ret;
		} catch (Exception ignored) {
		}
		return null;
	}

	/**
	 * Gets boolean.
	 *
	 * @param rs          the rs
	 * @param columnLabel the column label
	 * @return the boolean
	 */
	public static Boolean getBoolean(ResultSet rs, String columnLabel) {
		try {
			var ret = rs.getBoolean(columnLabel);
			if (!rs.wasNull()) return ret;
		} catch (Exception ignored) {
		}
		return null;
	}

	public static boolean getBoolean(ResultSet rs, String columnLabel, boolean defaultValue) {
		var ret = getBoolean(rs,columnLabel);
		if ( ret != null) return ret;
		return defaultValue;
	}

	/**
	 * Gets double.
	 *
	 * @param rs          the rs
	 * @param columnLabel the column label
	 * @return the double
	 */
	public static Double getDouble(ResultSet rs, String columnLabel) {
		try {
			return rs.getDouble(columnLabel);
		}
		catch(Exception ignored)
		{}
		return null;
	}

	/**
	 * Gets future player trainings.
	 *
	 * @param playerId the player id
	 * @return the future player trainings
	 */
	public List<FuturePlayerTraining> getFuturePlayerTrainings(int playerId) {
		return ((FuturePlayerTrainingTable) getTable(FuturePlayerTrainingTable.TABLENAME))
				.getFuturePlayerTrainingPlan(playerId);
	}

	/**
	 * Store future player trainings.
	 *
	 * @param spielerID             the spieler id
	 * @param futurePlayerTrainings the future player trainings
	 */
	public void storeFuturePlayerTrainings(int spielerID, List<FuturePlayerTraining> futurePlayerTrainings) {
		((FuturePlayerTrainingTable) getTable(FuturePlayerTrainingTable.TABLENAME))
				.storeFuturePlayerTrainings(spielerID, futurePlayerTrainings);

	}

	/**
	 * Gets last youth match date.
	 *
	 * @return the last youth match date
	 */
	public Timestamp getLastYouthMatchDate() {
		return ((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME))
				.getLastYouthMatchDate();
	}

	/**
	 * Get min scouting date timestamp.
	 *
	 * @return the timestamp
	 */
	public Timestamp getMinScoutingDate(){
		return ((YouthPlayerTable) getTable(YouthPlayerTable.TABLENAME))
				.loadMinScoutingDate();
	}

	/**
	 * Store match lineup.
	 *
	 * @param lineup the lineup
	 * @param teamId the team id
	 */
	public void storeMatchLineup(MatchLineup lineup, Integer teamId) {
		((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
				.storeMatchLineup(lineup, teamId);
	}

	/**
	 * Load youth trainer comments list.
	 *
	 * @param id the id
	 * @return the list
	 */
	public List<YouthTrainerComment> loadYouthTrainerComments(int id) {
		return ((YouthTrainerCommentTable) getTable(YouthTrainerCommentTable.TABLENAME)).loadYouthTrainerComments(id);
	}

	public List<MatchLineup> getYouthMatchLineups() {
		return ((MatchLineupTable)getTable(MatchLineupTable.TABLENAME)).loadYouthMatchLineups();
	}

	/**
	 * Delete youth match data.
	 *
	 * @param before       the before
	 */
	public void deleteYouthMatchDataBefore(Timestamp before){
		if ( before != null) {
			((MatchHighlightsTable) getTable(MatchHighlightsTable.TABLENAME)).deleteYouthMatchHighlightsBefore(before);
			((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME)).deleteYouthMatchDetailsBefore(before);
			((MatchLineupTable) getTable(MatchLineupTable.TABLENAME)).deleteYouthMatchLineupsBefore(before);
		}
	}

	/**
	 * Load youth trainings list.
	 *
	 * @return the list
	 */
	public List<YouthTraining> loadYouthTrainings() {
		return ((YouthTrainingTable)getTable(YouthTrainingTable.TABLENAME)).loadYouthTrainings();
	}

	/**
	 * Store youth training.
	 *
	 * @param youthTraining the youth training
	 */
	public void storeYouthTraining(YouthTraining youthTraining) {
		((YouthTrainingTable)getTable(YouthTrainingTable.TABLENAME)).storeYouthTraining(youthTraining);
    }

	/**
	 * Store match details.
	 *
	 * @param details the details
	 */
	public void storeMatchDetails(Matchdetails details) {
		((MatchDetailsTable)getTable(MatchDetailsTable.TABLENAME)).storeMatchDetails(details);
	}


	/**
	 * Gets team logo file name BUT it will triggers download of the logo from internet if it is not yet available.
	 * It will also update LAST_ACCESS field
	 *
	 * @param teamID the team id
	 * @param teamLogoFolderPath the team logo root folder path
	 * @return the team logo file name
	 */
	public String getTeamLogoFileName(Path teamLogoFolderPath, int teamID) {
		return ((TeamsLogoTable)getTable(TeamsLogoTable.TABLENAME)).getTeamLogoFileName(teamLogoFolderPath, teamID);
	}

	/**
	 * Store team logo info.
	 *
	 * @param teamID     the team id
	 * @param logoURL    the logo url
	 * @param lastAccess the last access
	 */
	public void storeTeamLogoInfo(int teamID, String logoURL, Timestamp lastAccess){
		((TeamsLogoTable)getTable(TeamsLogoTable.TABLENAME)).storeTeamLogoInfo(teamID, logoURL, lastAccess);
	}

	public List<HRF> getHRFsSince(Timestamp since) {
		return ((HRFTable)getTable(HRFTable.TABLENAME)).getHRFsSince(since);
	}

	public List<Integer> loadHrfIdPerWeekList(int nWeeks) {
		return ((HRFTable)getTable(HRFTable.TABLENAME)).getHrfIdPerWeekList(nWeeks);
	}

	public void storeTeamRatings(MatchTeamRating teamrating) {
		((MatchTeamRatingTable)getTable(MatchTeamRatingTable.TABLENAME)).store(teamrating);
	}

    public List<MatchTeamRating> loadMatchTeamRating( int matchtype, int matchId) {
		return ((MatchTeamRatingTable) getTable(MatchTeamRatingTable.TABLENAME)).load(matchId, matchtype);
	}

	// ------------------------------- MatchLineupTeamTable
	// -------------------------------------------------
	public MatchLineupTeam loadMatchLineupTeam(int iMatchType, int matchID, int teamID) {
		return ((MatchLineupTeamTable) getTable(MatchLineupTeamTable.TABLENAME))
				.getMatchLineupTeam(iMatchType, matchID, teamID);
	}

	public MatchLineupTeam loadPreviousMatchLineup(int teamID) { return loadLineup(getLastMatchesKurzInfo(teamID), teamID);}
	public MatchLineupTeam loadNextMatchLineup(int teamID) { return loadLineup(getNextMatchesKurzInfo(teamID), teamID);}

	private MatchLineupTeam loadLineup(MatchKurzInfo match, int teamID) {
		if (match != null) {
			return loadMatchLineupTeam(match.getMatchType().getId(), match.getMatchID(), teamID);
		}
		return null;
	}

	public void storeMatchLineupTeam(MatchLineupTeam matchLineupTeam) {
		((MatchLineupTeamTable)getTable(MatchLineupTeamTable.TABLENAME)).storeMatchLineupTeam(matchLineupTeam);
	}

	public void deleteMatchLineupTeam(MatchLineupTeam matchLineupTeam) {
		((MatchLineupTeamTable)getTable(MatchLineupTeamTable.TABLENAME)).deleteMatchLineupTeam(matchLineupTeam);
	}

	public ArrayList<MatchLineupTeam> loadTemplateMatchLineupTeams() {
		return ((MatchLineupTeamTable)getTable(MatchLineupTeamTable.TABLENAME)).getTemplateMatchLineupTeams();
	}

	public int getTemplateMatchLineupTeamNextNumber() {
		return ((MatchLineupTeamTable)getTable(MatchLineupTeamTable.TABLENAME)).getTemplateMatchLineupTeamNextNumber();
	}

	public NtTeamDetails loadNtTeamDetails(int teamId, Timestamp matchDate) {
		return ((NtTeamTable)getTable(NtTeamTable.TABLENAME)).load(teamId, matchDate);
	}

	public List<NtTeamDetails> loadAllNtTeamDetails() {
		return ((NtTeamTable)getTable(NtTeamTable.TABLENAME)).load(getLatestHRF().getHrfId());
	}

	public void storeNtTeamDetails(NtTeamDetails details) {
		((NtTeamTable)getTable(NtTeamTable.TABLENAME)).store(details);
	}

	private final String sql = "SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER, TRAINER" +
			" FROM XTRADATA INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID" +
			" INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID" +
			" INNER JOIN SPIELER on XTRADATA.HRF_ID = SPIELER.HRF_ID AND SPIELER.TRAINER > 0" +
			" INNER JOIN (SELECT TRAININGDATE, %s(HRF_ID) J_HRF_ID FROM XTRADATA GROUP BY TRAININGDATE) IJ1 ON XTRADATA.HRF_ID = IJ1.J_HRF_ID" +
			" WHERE XTRADATA.TRAININGDATE >= ?";

	private final PreparedStatementBuilder loadTrainingPerWeekMaxStatement = new PreparedStatementBuilder(String.format(sql, "max"));
	private final PreparedStatementBuilder loadTrainingPerWeekMinStatement = new PreparedStatementBuilder(String.format(sql, "min"));

	public List<TrainingPerWeek> loadTrainingPerWeek(Timestamp startDate, boolean all) {

		var ret = new ArrayList<TrainingPerWeek>();
		try {
			final ResultSet rs = m_clJDBCAdapter.executePreparedQuery(
					all?loadTrainingPerWeekMaxStatement.getStatement():loadTrainingPerWeekMinStatement.getStatement(),
					startDate);
			if ( rs != null ) {
				while (rs.next()) {
					int trainType = rs.getInt("TRAININGSART");
					int trainIntensity = rs.getInt("TRAININGSINTENSITAET");
					int trainStaminaPart = rs.getInt("STAMINATRAININGPART");
					// subtract one week from next training date to get the past week training date
					var trainingDate = HODateTime.fromDbTimestamp(rs.getTimestamp("TRAININGDATE")).minus(7, ChronoUnit.DAYS);
					int coachLevel = rs.getInt("TRAINER");
					int trainingAssistantLevel = rs.getInt("COTRAINER");
					TrainingPerWeek tpw = new TrainingPerWeek(trainingDate,
							trainType,
							trainIntensity,
							trainStaminaPart,
							trainingAssistantLevel,
							coachLevel,
							DBDataSource.HRF);
					ret.add( tpw);
				}
			}
			return ret;
		}
		catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error while performing loadTrainingPerWeek():  " + e);
		}
		return null;
	}

	public List<MatchKurzInfo> getMatchesKurzInfo(int teamId, int status, Timestamp from, List<Integer> matchTypes) {
		return ((MatchesKurzInfoTable)getTable(MatchesKurzInfoTable.TABLENAME)).getMatchesKurzInfo(teamId, status, from, matchTypes);
	}

	public List<Player> loadPlayerHistory(int spielerId) {
		return ((SpielerTable)getTable(SpielerTable.TABLENAME)).loadPlayerHistory(spielerId);
	}

	private static final DBManager.PreparedStatementBuilder preStatementBuilder = new DBManager.PreparedStatementBuilder(
			"select marktwert from SPIELER where spielerid=? and verletzt=-1 order by DATUM desc");
	private static final DBManager.PreparedStatementBuilder postStatementBuilder = new DBManager.PreparedStatementBuilder(
			"select marktwert from SPIELER where spielerid=? and verletzt>-1 order by DATUM desc");

	public String loadLatestTSINotInjured(int m_iSpielerID) {
		return loadLatestTSI(preStatementBuilder, m_iSpielerID);
	}
	public String loadLatestTSIInjured(int m_iSpielerID) {
		return loadLatestTSI(postStatementBuilder, m_iSpielerID);
	}

	private String loadLatestTSI(DBManager.PreparedStatementBuilder preparedStatementBuilder, int m_iSpielerID) {
		try {
			ResultSet rs = Objects.requireNonNull(this.getAdapter()).executePreparedQuery(preparedStatementBuilder.getStatement(), m_iSpielerID);
			if (rs.next()) {
				return  rs.getString("marktwert");
			}
		} catch (Exception e1) {
		}
		return "";
	}


	public static String getPlaceholders(int count){
		return String.join(",", Collections.nCopies(count, "?"));
	}

	public static class PreparedStatementBuilder{
		private final String sql;
		public PreparedStatementBuilder(String sql){
			this.sql=sql;
		}
		private PreparedStatement statement;
		public PreparedStatement getStatement() {
			if (statement == null) {
				statement = Objects.requireNonNull(DBManager.instance().getAdapter()).createPreparedStatement(sql);
			}
			return statement;
		}
	}
}
