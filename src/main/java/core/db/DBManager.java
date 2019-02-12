package core.db;

import core.datatype.CBItem;
import core.db.backup.BackupDialog;
import core.file.hrf.HRF;
import core.gui.comp.table.HOTableModel;
import core.gui.model.ArenaStatistikTableModel;
import core.gui.model.SpielerMatchCBItem;
import core.model.FactorObject;
import core.model.HOModel;
import core.model.HOParameter;
import core.model.StaffMember;
import core.model.Team;
import core.model.UserParameter;
import core.model.WorldDetailLeague;
import core.model.XtraData;
import core.model.match.MatchHighlight;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineup;
import core.model.match.MatchLineupPlayer;
import core.model.match.MatchLineupTeam;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.model.match.MatchesHighlightsStat;
import core.model.match.MatchesOverviewRow;
import core.model.misc.Basics;
import core.model.misc.Finanzen;
import core.model.misc.Verein;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.model.series.Liga;
import core.model.series.Paarung;
import core.training.TrainingPerWeek;
import core.util.HOLogger;
import module.ifa.IfaMatch;
import module.lineup.Lineup;
import module.lineup.substitution.model.Substitution;
import module.series.Spielplan;
import module.teamAnalyzer.vo.PlayerInfo;
import module.transfer.PlayerTransfer;
import module.transfer.scout.ScoutEintrag;
import tool.arenasizer.Stadium;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

public class DBManager {
	// ~ Static fields/initializers
	// -----------------------------------------------------------------

	// Datum der TSI Umstellung. Alle Marktwerte der Spieler müssen vor dem
	// Datum durch 1000 geteilt werden (ohne Sprachfaktor)
	/** database version */
	private static final int DBVersion = 24;

	/** 2004-06-14 11:00:00.0 */
	public static Timestamp TSIDATE = new Timestamp(1087203600000L);

	/** singleton */
	private static DBManager m_clInstance;

	// ~ Instance fields
	// ----------------------------------------------------------------------------

	/** DB-Adapter */
	private JDBCAdapter m_clJDBCAdapter = null; // new JDBCAdapter();

	/** all Tables */
	private final Hashtable<String, AbstractTable> tables = new Hashtable<String, AbstractTable>();

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

	public static int getVersion() {
		return DBVersion;
	}

	// //////////////////////////////////////////////////////////////////////////////
	// INSTANCE
	// //////////////////////////////////////////////////////////////////////////////
	public static synchronized DBManager instance() {
		if (m_clInstance == null) {
			// HOLogger.instance().log(getClass(),"TSI: " + TSIDATE);

			String errorMsg = null;
			try {
				String url = User.getCurrentUser().getUrl();
				String filePart = url.substring("jdbc:hsqldb:file:".length());
				String folder = new File(filePart).getAbsoluteFile()
						.getParent();
				File dbfolder = new File(folder);

				if (!dbfolder.exists()) {
					dbfolder.mkdirs();
				}
				
				if (!dbfolder.canRead() || !dbfolder.canWrite()) {
					errorMsg = "Could not write to database. Make sure you have write access to the HO directory and its sub-directories.\n"
							+ "If under Windows make sure to stay out of Program Files or similar.\n"
							+ dbfolder.getAbsolutePath();
				}

			} catch (Exception e) {
				errorMsg = "Can't connect to database: "
						+ User.getCurrentUser().getUrl();
				e.printStackTrace();
			}

			if (errorMsg != null) {
				javax.swing.JOptionPane
						.showMessageDialog(null, errorMsg, "Fatal DB Error",
								javax.swing.JOptionPane.ERROR_MESSAGE);
				HOLogger.instance().error(null, errorMsg);
				System.exit(-1);
			}

			// Create new instance
			DBManager tempInstance = new DBManager();
			DBUpdater dbUpdater = new DBUpdater();
			tempInstance.initAllTables(tempInstance.getAdapter());
			// Try connecting to the DB
			try {
				tempInstance.connect();
				dbUpdater.setDbZugriff(tempInstance);
			} catch (Exception e) {

				String msg = e.getMessage();
				boolean recover = User.getCurrentUser().isHSQLDB();

				if (msg.indexOf("The database is already in use by another process") > -1) {
					if ((msg.indexOf("Permission denied") > -1)
							|| msg.indexOf("system cannot find the path") > -1) {
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

				HOLogger.instance().error(null, msg);

				System.exit(-1);
			}

			// Does DB already exists?
			final boolean existsDB = tempInstance.checkIfDBExists();

			// for startup
			tempInstance.setFirstStart(!existsDB);

			// Do we need to create the database from scratch?
			if (!existsDB) {
				try {
					tempInstance.createAllTables();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
				UserConfigurationTable configTable = (UserConfigurationTable) tempInstance
						.getTable(UserConfigurationTable.TABLENAME);
				configTable.store(UserParameter.instance());
				configTable.store(HOParameter.instance());
			} else {
				// Check if there are any updates on the database to be done.
				dbUpdater.updateDB(DBVersion);
			}

			// Check if there are any config updates
			// new since 1.401 - flattermann
			dbUpdater.updateConfig();
			dbUpdater = null;
			// tempInstance.updateConfig();
			m_clInstance = tempInstance;
		}

		return m_clInstance;
	}

	private void initAllTables(JDBCAdapter adapter) {
		tables.put(BasicsTable.TABLENAME, new BasicsTable(adapter));
		tables.put(TeamTable.TABLENAME, new TeamTable(adapter));
		tables.put(FaktorenTable.TABLENAME, new FaktorenTable(adapter));
		tables.put(PositionenTable.TABLENAME, new PositionenTable(adapter));
		tables.put(AufstellungTable.TABLENAME, new AufstellungTable(adapter));
		tables.put(HRFTable.TABLENAME, new HRFTable(adapter));
		tables.put(StadionTable.TABLENAME, new StadionTable(adapter));
		tables.put(VereinTable.TABLENAME, new VereinTable(adapter));
		tables.put(LigaTable.TABLENAME, new LigaTable(adapter));
		tables.put(SpielerTable.TABLENAME, new SpielerTable(adapter));
		tables.put(FinanzenTable.TABLENAME, new FinanzenTable(adapter));
		tables.put(ScoutTable.TABLENAME, new ScoutTable(adapter));
		tables.put(UserColumnsTable.TABLENAME, new UserColumnsTable(adapter));
		tables.put(SpielerNotizenTable.TABLENAME, new SpielerNotizenTable(
				adapter));
		tables.put(SpielplanTable.TABLENAME, new SpielplanTable(adapter));
		tables.put(PaarungTable.TABLENAME, new PaarungTable(adapter));
		tables.put(MatchLineupTeamTable.TABLENAME, new MatchLineupTeamTable(
				adapter));
		tables.put(MatchLineupTable.TABLENAME, new MatchLineupTable(adapter));
		tables.put(XtraDataTable.TABLENAME, new XtraDataTable(adapter));
		tables.put(MatchLineupPlayerTable.TABLENAME,
				new MatchLineupPlayerTable(adapter));
		tables.put(MatchesKurzInfoTable.TABLENAME, new MatchesKurzInfoTable(
				adapter));
		tables.put(MatchDetailsTable.TABLENAME, new MatchDetailsTable(adapter));
		tables.put(MatchHighlightsTable.TABLENAME, new MatchHighlightsTable(
				adapter));
		tables.put(TrainingsTable.TABLENAME, new TrainingsTable(adapter));
		tables.put(FutureTrainingTable.TABLENAME, new FutureTrainingTable(
				adapter));
		tables.put(UserConfigurationTable.TABLENAME,
				new UserConfigurationTable(adapter));
		tables.put(SpielerSkillupTable.TABLENAME, new SpielerSkillupTable(
				adapter));
		tables.put(StaffTable.TABLENAME,  new StaffTable(adapter));
		tables.put(MatchSubstitutionTable.TABLENAME,
				new MatchSubstitutionTable(adapter));
		tables.put(TransferTable.TABLENAME, new TransferTable(adapter));
		tables.put(TransferTypeTable.TABLENAME, new TransferTypeTable(adapter));
		tables.put(ModuleConfigTable.TABLENAME, new ModuleConfigTable(adapter));
		tables.put(TAFavoriteTable.TABLENAME, new TAFavoriteTable(adapter));
		tables.put(TAPlayerTable.TABLENAME, new TAPlayerTable(adapter));
		tables.put(WorldDetailsTable.TABLENAME, new WorldDetailsTable(adapter));
		tables.put(IfaMatchTable.TABLENAME, new IfaMatchTable(adapter));
		tables.put(PenaltyTakersTable.TABLENAME,
				new PenaltyTakersTable(adapter));

	}

	AbstractTable getTable(String tableName) {
		return (AbstractTable) tables.get(tableName);
	}

	// Accessor
	public JDBCAdapter getAdapter() {
		return m_clJDBCAdapter;
	}

	/**
	 * Set First start flag
	 */
	private void setFirstStart(boolean firststart) {
		m_bFirstStart = firststart;
	}

	public boolean isFirstStart() {
		return m_bFirstStart;
	}

	/**
	 * trennt die Verbindung zur Datenbank
	 */
	public void disconnect() {
		m_clJDBCAdapter.disconnect();
		m_clJDBCAdapter = null;
		m_clInstance = null;
	}

	/**
	 * stellt die Verbindung zur DB her
	 */
	private void connect() throws Exception {
		User user = User.getCurrentUser();
		m_clJDBCAdapter.connect(user.getUrl(), user.getUser(), user.getPwd(),
				user.getDriver());
	}

	/**
	 * check if tables in DB exists
	 * 
	 * @return boolean
	 */
	private boolean checkIfDBExists() {
		return m_clJDBCAdapter.executeQuery("SELECT * FROM HRF WHERE 1=2") != null;
	}

	// ------------------------------- SpielerSkillupTable
	// -------------------------------------------------

	/**
	 * liefert das Datum des letzen Levelups anhand key und value
	 * 
	 * @param skill
	 *            integer code for the skill
	 * @param spielerId
	 * 			  player ID
	 * 
	 * @return [0] = Time der Änderung [1] = Boolean: false=Keine Änderung
	 *         gefunden
	 */
	public Object[] getLastLevelUp(int skill, int spielerId) {
		return ((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.getLastLevelUp(skill, spielerId);
	}

	/**
	 * liefert das Datum des letzen LevelAufstiegs für den angeforderten Skill
	 * Vector filled with Skillup Objects
	 * 
	 */
	public Vector<Object[]> getAllLevelUp(int skill, int m_iSpielerID) {
		return ((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.getAllLevelUp(skill, m_iSpielerID);
	}

	public void reimportSkillup() {
		((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.importFromSpieler();
	}

	public void checkSkillup(HOModel homodel) {
		((SpielerSkillupTable) getTable(SpielerSkillupTable.TABLENAME))
				.importNewSkillup(homodel);
	}

	// ------------------------------- SpielerTable
	// -------------------------------------------------

	/**
	 * gibt alle Spieler zurück, auch ehemalige
	 */
	public Vector<Spieler> getAllSpieler() {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getAllSpieler();
	}

	/**
	 * Gibt die letzte Bewertung für den Spieler zurück // HRF
	 */
	public int getLetzteBewertung4Spieler(int spielerid) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getLetzteBewertung4Spieler(spielerid);
	}

	/**
	 * lädt die Spieler zum angegeben HRF file ein
	 */
	public Vector<Spieler> getSpieler(int hrfID) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpieler(hrfID);
	}

	/**
	 * get a player from a specific HRF
	 * 
	 * @param hrfID
	 *            hrd id
	 * @param playerId
	 *            player id
	 * 
	 * 
	 * @return player
	 */
	public Spieler getSpielerFromHrf(int hrfID, int playerId) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpielerFromHrf(hrfID, playerId);
	}

	/**
	 * Gibt einen Spieler zurück mit den Daten kurz vor dem Timestamp
	 */
	public Spieler getSpielerAtDate(int spielerid, Timestamp time) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpielerAtDate(spielerid, time);
	}

	/**
	 * Gibt einen Spieler zurück aus dem ersten HRF
	 */
	public Spieler getSpielerFirstHRF(int spielerid) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getSpielerFirstHRF(spielerid);
	}

	/**
	 * Gibt das Datum des ersten HRFs zurück, in dem der Spieler aufgetaucht ist
	 */
	public Timestamp getTimestamp4FirstPlayerHRF(int spielerid) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getTimestamp4FirstPlayerHRF(spielerid);
	}

	/**
	 * Returns the trainer code for the specified hrf. -99 if error
	 * 
	 * @param hrfID
	 *            HRF for which to load TrainerType
	 * @return
	 */
	public int getTrainerType(int hrfID) {
		return ((SpielerTable) getTable(SpielerTable.TABLENAME))
				.getTrainerType(hrfID);
	}

	/**
	 * speichert die Spieler
	 */
	public void saveSpieler(int hrfId, Vector<Spieler> spieler, Timestamp date) {
		((SpielerTable) getTable(SpielerTable.TABLENAME)).saveSpieler(hrfId,
				spieler, date);
	}

	/**
	 * saves one player to the DB
	 * 
	 * @param hrfId
	 *            hrf id
	 * @param player
	 *            the player to be saved
	 * @param date
	 *            date to save
	 */
	public void saveSpieler(int hrfId, Spieler player, Timestamp date) {
		((SpielerTable) getTable(SpielerTable.TABLENAME)).saveSpieler(hrfId,
				player, date);
	}

	// ------------------------------- LigaTable
	// -------------------------------------------------

	/**
	 * Gibt alle bekannten Ligaids zurück
	 */
	public Integer[] getAllLigaIDs() {
		return ((LigaTable) getTable(LigaTable.TABLENAME)).getAllLigaIDs();
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	public Liga getLiga(int hrfID) {
		return ((LigaTable) getTable(LigaTable.TABLENAME)).getLiga(hrfID);
	}

	/**
	 * speichert die Basdics
	 */
	public void saveLiga(int hrfId, Liga liga) {
		((LigaTable) getTable(LigaTable.TABLENAME)).saveLiga(hrfId, liga);
	}

	// ------------------------------- SpielplanTable
	// -------------------------------------------------

	/**
	 * Gibt eine Ligaid zu einer Seasonid zurück, oder -1, wenn kein Eintrag in
	 * der DB gefunden wurde
	 */
	public int getLigaID4SaisonID(int seasonid) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getLigaID4SaisonID(seasonid);
	}

	/**
	 * holt einen Spielplan aus der DB, -1 bei den params holt den zuletzt
	 * gesavten Spielplan
	 * 
	 * @param ligaId
	 *            Id der Liga
	 * @param saison
	 *            die Saison
	 */
	public Spielplan getSpielplan(int ligaId, int saison) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getSpielplan(ligaId, saison);
	}

	/**
	 * speichert einen Spielplan mitsamt Paarungen
	 */
	public void storeSpielplan(Spielplan plan) {
		((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.storeSpielplan(plan);
	}

	public void deleteSpielplanTabelle(String[] whereSpalten,
			String[] whereValues) {
		getTable(SpielplanTable.TABLENAME).delete(whereSpalten, whereValues);
	}

	/**
	 * lädt alle Spielpläne aus der DB
	 * 
	 * @param mitPaarungen
	 *            inklusive der Paarungen ja/nein
	 */
	public Spielplan[] getAllSpielplaene(boolean mitPaarungen) {
		return ((SpielplanTable) getTable(SpielplanTable.TABLENAME))
				.getAllSpielplaene(mitPaarungen);
	}

	// ------------------------------- MatchLineupPlayerTable
	// -------------------------------------------------

	/**
	 * Gibt eine Liste an Ratings zurück, auf denen der Spieler gespielt hat: 0
	 * = Max 1 = Min 2 = Durchschnitt 3 = posid
	 */
	public Vector<float[]> getAlleBewertungen(int spielerid) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getAlleBewertungen(spielerid);
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Spieler, sowie die Anzahl der Bewertungen zurück // Match
	 */
	public float[] getBewertungen4Player(int spielerid) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getBewertungen4Player(spielerid);
	}

	/**
	 * Gibt die beste, schlechteste und durchschnittliche Bewertung für den
	 * Spieler, sowie die Anzahl der Bewertungen zurück // Match
	 *
	 * @param spielerid
	 *            Spielerid
	 * @param position
	 *            Usere positionscodierung mit taktik
	 */
	public float[] getBewertungen4PlayerUndPosition(int spielerid, byte position) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getBewertungen4PlayerUndPosition(spielerid, position);
	}

	public Vector<MatchLineupPlayer> getMatchLineupPlayers(int matchID,
			int teamID) {
		return ((MatchLineupPlayerTable) getTable(MatchLineupPlayerTable.TABLENAME))
				.getMatchLineupPlayers(matchID, teamID);
	}

	// ------------------------------- AufstellungTable
	// -------------------------------------------------

	/**
	 * lädt System Positionen
	 */
	public Lineup getAufstellung(int hrfID, String name) {
		return ((AufstellungTable) getTable(AufstellungTable.TABLENAME))
				.getAufstellung(hrfID, name);
	}

	/**
	 * gibt liste für Aufstellungen
	 * 
	 * @param hrfID
	 *            -1 für default = hrf unabhängig
	 */
	public Vector<String> getAufstellungsListe(int hrfID) {
		return ((AufstellungTable) getTable(AufstellungTable.TABLENAME))
				.getAufstellungsListe(hrfID);
	}

	/**
	 * Gibt eine Liste aller Usergespeicherten Aufstellungsnamen zurück
	 */
	public Vector<String> getUserAufstellungsListe() {
		return ((AufstellungTable) getTable(AufstellungTable.TABLENAME))
				.getUserAufstellungsListe();
	}

	/**
	 * speichert die Aufstellung und die aktuelle Aufstellung als STANDARD
	 */
	public void saveAufstellung(int hrfId, Lineup aufstellung, String name) {
		try {
			((AufstellungTable) getTable(AufstellungTable.TABLENAME))
					.saveAufstellung(hrfId, aufstellung, name);
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}

	// ------------------------------- BasicsTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	public Basics getBasics(int hrfID) {
		return ((BasicsTable) getTable(BasicsTable.TABLENAME)).getBasics(hrfID);
	}

	/**
	 * Gibt eine Vector mit HRF-CBItems zurück
	 * 
	 * @param datum
	 *            from which hrf has to be returned, used to load a subset of
	 *            hrf
	 */
	public Vector<CBItem> getCBItemHRFListe(Timestamp datum) {
		return ((BasicsTable) getTable(BasicsTable.TABLENAME))
				.getCBItemHRFListe(datum);
	}

	/**
	 * Returns an HRF before the matchData and after previous TrainingTime
	 * 
	 * @param matchTime
	 *            matchData
	 * @return hrfId
	 */
	public int getHrfIDSameTraining(Timestamp matchTime) {
		return ((BasicsTable) getTable(BasicsTable.TABLENAME))
				.getHrfIDSameTraining(matchTime);
	}

	/**
	 * speichert die Basdics
	 */
	public void saveBasics(int hrfId, core.model.misc.Basics basics) {
		((BasicsTable) getTable(BasicsTable.TABLENAME)).saveBasics(hrfId,
				basics);
	}

	// ------------------------------- FaktorenTable
	// -------------------------------------------------
	public void setFaktorenFromDB(FactorObject fo) {
		((FaktorenTable) getTable(FaktorenTable.TABLENAME))
				.setFaktorenFromDB(fo);
	}

	public void getFaktorenFromDB() {
		((FaktorenTable) getTable(FaktorenTable.TABLENAME)).getFaktorenFromDB();
	}

	// ------------------------------- FinanzenTable
	// -------------------------------------------------

	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 */
	public Finanzen getFinanzen(int hrfID) {
		return ((FinanzenTable) getTable(FinanzenTable.TABLENAME))
				.getFinanzen(hrfID);
	}

	/**
	 * speichert die Finanzen
	 */
	public void saveFinanzen(int hrfId, Finanzen finanzen, Timestamp date) {
		((FinanzenTable) getTable(FinanzenTable.TABLENAME)).saveFinanzen(hrfId,
				finanzen, date);
	}

	// ------------------------------- HRFTable
	// -------------------------------------------------

	/**
	 * Get a list of all HRFs
	 * 
	 * @param minId
	 *            minimum HRF id (<0 for all)
	 * @param maxId
	 *            maximum HRF id (<0 for all)
	 * @param asc
	 *            order ascending (descending otherwise)
	 * 
	 * @return all matching HRFs
	 */
	public HRF[] getAllHRFs(int minId, int maxId, boolean asc) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getAllHRFs(minId,
				maxId, asc);
	}

//	/**
//	 * get HRF by Id
//	 */
//	public HRF getHrf(int hrfId) {
//		return ((HRFTable) getTable(HRFTable.TABLENAME)).getHRF(hrfId);
//	}

	/**
	 * liefert die aktuelle Id des neuesten HRF-Files
	 */
	public int getMaxHrfId() {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getMaxHrf().getHrfId();
	}

	/**
	 * liefert die Maximal Vergebene Id eines HRF-Files
	 */
	public int getLatestHrfId() {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getLatestHrf()
				.getHrfId();
	}

	/**
	 * Sucht das letzte HRF zwischen dem angegebenen Datum und 6 Tagen davor
	 * Wird kein HRF gefunden wird -1 zurückgegeben
	 */
	public int getPreviousHRF(int hrfId) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getPreviousHRF(hrfId);
	}

	/**
	 * Sucht das letzte HRF zwischen dem angegebenen Datum und 6 Tagen davor
	 * Wird kein HRF gefunden wird -1 zurückgegeben
	 */
	public int getFollowingHRF(int hrfId) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getFollowingHRF(hrfId);
	}

	/**
	 * speichert das Verein
	 */
	public void saveHRF(int hrfId, String name, Timestamp datum) {
		((HRFTable) getTable(HRFTable.TABLENAME)).saveHRF(hrfId, name, datum);
	}

	public int getHRFID4Date(Timestamp time) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getHrfId4Date(time);
	}

	/**
	 * gibt es ein HRFFile in der Datenbank mit dem gleichen Dateimodifieddatum
	 * schon?
	 * 
	 * @param date
	 *            der letzten Dateiänderung der zu vergleichenden Datei
	 * 
	 * @return Das Datum der Datei, an den die Datei importiert wurde oder null,
	 *         wenn keine passende Datei vorhanden ist
	 */
	public String getHRFName4Date(Timestamp date) {
		return ((HRFTable) getTable(HRFTable.TABLENAME)).getHrfName4Date(date);
	}

	// ------------------------------- SpielerNotizenTable
	// -------------------------------------------------

	public String getManuellerSmilie(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getManuellerSmilie(spielerId);
	}

	public String getTeamInfoSmilie(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getTeamInfoSmilie(spielerId);
	}

	public String getSpielerNotiz(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getSpielerNotiz(spielerId);
	}

	public boolean getSpielerSpielberechtigt(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getSpielerSpielberechtigt(spielerId);
	}

	public byte getSpielerUserPosFlag(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getSpielerUserPosFlag(spielerId);
	}

	public boolean getIsSpielerFired(int spielerId) {
		return ((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.getIsSpielerFired(spielerId);
	}

	public void saveManuellerSmilie(int spielerId, String smilie) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveManuellerSmilie(spielerId, smilie);
	}

	public void saveSpielerNotiz(int spielerId, String notiz) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveSpielerNotiz(spielerId, notiz);
	}

	public void saveSpielerSpielberechtigt(int spielerId,
			boolean spielberechtigt) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveSpielerSpielberechtigt(spielerId, spielberechtigt);
	}

	public void saveSpielerUserPosFlag(int spielerId, byte flag) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveSpielerUserPosFlag(spielerId, flag);
	}

	public void saveTeamInfoSmilie(int spielerId, String smilie) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveTeamInfoSmilie(spielerId, smilie);
	}

	public void saveIsSpielerFired(int spielerId, boolean isFired) {
		((SpielerNotizenTable) getTable(SpielerNotizenTable.TABLENAME))
				.saveIsSpielerFired(spielerId, isFired);
	}

	// ------------------------------- MatchLineupTable
	// -------------------------------------------------

	public MatchLineup getMatchLineup(int matchID) {
		return ((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
				.getMatchLineup(matchID);
	}

	/**
	 * Ist das Match schon in der Datenbank vorhanden?
	 */
	public boolean isMatchLineupVorhanden(int matchid) {
		return ((MatchLineupTable) getTable(MatchLineupTable.TABLENAME))
				.isMatchLineupVorhanden(matchid);
	}

	// ------------------------------- MatchesKurzInfoTable
	// -------------------------------------------------

	/**
	 * Check if match is available
	 */
	public boolean isMatchVorhanden(int matchid) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.isMatchVorhanden(matchid);
	}

	/**
	 * Returns the MatchKurzInfo for the match. Returns null if not found.
	 * 
	 * @param matchid
	 *            The ID for the match
	 * @return The kurz info object or null
	 */
	public MatchKurzInfo getMatchesKurzInfoByMatchID(int matchid) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfoByMatchID(matchid);
	}

	/**
	 * Get all matches for the given team from the database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 */
	public MatchKurzInfo[] getMatchesKurzInfo(int teamId) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId);
	}

	/**
	 * Get all matches for the given sql where claus.
	 * 
	 * @param where
	 *            The string containing sql where claus
	 */
	public MatchKurzInfo[] getMatchesKurzInfo(String where) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(where);
	}

	/**
	 * Get all matches with a certain status for the given team from the
	 * database.
	 * 
	 * @param teamId
	 *            the teamid or -1 for all matches
	 */
	public MatchKurzInfo[] getMatchesKurzInfo(final int teamId,
			final int matchStatus) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId, matchStatus);
	}

	/**
	 * Wichtig: Wenn die Teamid = -1 ist muss der Matchtyp ALLE_SPIELE sein!
	 * 
	 * @param teamId
	 *            Die Teamid oder -1 für alle
	 * @param matchtyp
	 *            Welche Matches? Konstanten im SpielePanel!
	 * @param asc
	 */
	public MatchKurzInfo[] getMatchesKurzInfo(int teamId, int matchtyp,
			boolean asc) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId, matchtyp, asc);
	}
	

	public MatchKurzInfo getMatchesKurzInfo(int teamId, int matchtyp,
			int statistic, boolean home) {
		return ((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.getMatchesKurzInfo(teamId, matchtyp, statistic, home);
	}

	public int getMatchesKurzInfoStatisticsCount(int teamId, int matchtype,
			int statistic) {
		return MatchesOverviewQuery.getMatchesKurzInfoStatisticsCount(teamId,
				matchtype, statistic);
	}

	/**
	 * speichert die Matches
	 */
	public void storeMatchKurzInfos(MatchKurzInfo[] matches) {
		((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.storeMatchKurzInfos(matches);
	}

	// ------------------------------- ScoutTable
	// -------------------------------------------------

	/**
	 * Load player list for insertion into TransferScout
	 */
	public Vector<ScoutEintrag> getScoutList() {
		return ((ScoutTable) getTable(ScoutTable.TABLENAME)).getScoutList();
	}

	/**
	 * Save players from TransferScout
	 */
	public void saveScoutList(Vector<ScoutEintrag> list) {
		((ScoutTable) getTable(ScoutTable.TABLENAME)).saveScoutList(list);
	}

	// ------------------------------- StadionTable
	// -------------------------------------------------

	/**
	 * lädt die Finanzen zum angegeben HRF file ein
	 */
	public Stadium getStadion(int hrfID) {
		return ((StadionTable) getTable(StadionTable.TABLENAME))
				.getStadion(hrfID);
	}

	/**
	 * speichert die Finanzen
	 */
	public void saveStadion(int hrfId, tool.arenasizer.Stadium stadion) {
		((StadionTable) getTable(StadionTable.TABLENAME)).saveStadion(hrfId,
				stadion);
	}
	
	
	// ------------------------------- StaffTable
	// -------------------------------------------------

	/**
	 * Fetch a list of staff store din a hrf
	 * @param hrfId
	 * @return A list of StaffMembers belonging to the given hrf
	 */
	public List<StaffMember> getStaffByHrfId(int hrfId) {
		return ((StaffTable) getTable(StaffTable.TABLENAME)).getStaffByHrfId(hrfId);
	}
	
	/**
	 * Stores a list of StaffMembers
	 * 
	 * @param hrfId The hrfId
	 * @param list The staff objects
	 */
	public void saveStaff(int hrfId, List<StaffMember> list) {
		((StaffTable) getTable(StaffTable.TABLENAME)).storeStaff(hrfId, list);
	}
	
	
	// ------------------------------- MatchSubstitutionTable
	// -------------------------------------------------

	/**
	 * Returns an array with substitution belonging to the match-team.
	 * 
	 * @param teamId
	 *            The teamId for the team in question
	 * @param matchId
	 *            The matchId for the match in question
	 * 
	 */
	public List<Substitution> getMatchSubstitutionsByMatchTeam(int teamId,
			int matchId) {
		return ((MatchSubstitutionTable) getTable(MatchSubstitutionTable.TABLENAME))
				.getMatchSubstitutionsByMatchTeam(teamId, matchId);
	}

	/**
	 * Returns an array with substitution belonging to given hrfId and name
	 * 
	 * @param hrfId
	 *            The teamId for the team in question
	 * @param lineupName
	 *            The name of the lineup
	 * 
	 */
	public List<Substitution> getMatchSubstitutionsByHrf(int hrfId,
			String lineupName) {
		return ((MatchSubstitutionTable) getTable(MatchSubstitutionTable.TABLENAME))
				.getMatchSubstitutionsByHrf(hrfId, lineupName);
	}

	List<SpielerPosition> getPenaltyTakers(String lineupName) {
		try {
			return ((PenaltyTakersTable) getTable(PenaltyTakersTable.TABLENAME))
					.getPenaltyTakers(lineupName);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// ------------------------------- TeamTable
	// -------------------------------------------------

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] =
	 * Stimmung [1] = Selbstvertrauen
	 */
	public String[] getStimmmungSelbstvertrauen(int hrfid) {
		return ((TeamTable) getTable(TeamTable.TABLENAME))
				.getStimmmungSelbstvertrauen(hrfid);
	}

	/**
	 * Gibt die Teamstimmung und das Selbstvertrauen für ein HRFID zurück [0] =
	 * Stimmung [1] = Selbstvertrauen
	 */
	public int[] getStimmmungSelbstvertrauenValues(int hrfid) {
		return ((TeamTable) getTable(TeamTable.TABLENAME))
				.getStimmmungSelbstvertrauenValues(hrfid);
	}

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	public Team getTeam(int hrfID) {
		return ((TeamTable) getTable(TeamTable.TABLENAME)).getTeam(hrfID);
	}

	/**
	 * speichert das Team
	 */
	public void saveTeam(int hrfId, Team team) {
		((TeamTable) getTable(TeamTable.TABLENAME)).saveTeam(hrfId, team);
	}

	// ------------------------------- PositionenTable
	// -------------------------------------------------

	/**
	 * lädt System Positionen
	 */
	public Vector<ISpielerPosition> getSystemPositionen(int hrfID,
			String sysName) {
		return ((PositionenTable) getTable(PositionenTable.TABLENAME))
				.getSystemPositionen(hrfID, sysName);
	}

	/**
	 * speichert System Positionen
	 */
	public void saveSystemPositionen(int hrfId,
			Vector<ISpielerPosition> positionen, String sysName) {
		((PositionenTable) getTable(PositionenTable.TABLENAME))
				.saveSystemPositionen(hrfId, positionen, sysName);
	}

	/**
	 * delete das System
	 */
	public void deleteSystem(int hrfId, String sysName) {
		final String[] whereS = { "Aufstellungsname", "HRF_ID" };
		final String[] whereV = { "'" + sysName + "'", "" + hrfId };

		// erst vorhandene einträge für diesen Posnamen entfernen
		getTable(PositionenTable.TABLENAME).delete(whereS, whereV);
	}

	// ------------------------------- TrainingsTable
	// -------------------------------------------------

	public List<TrainingPerWeek> getTrainingOverrides() {
		return ((TrainingsTable) getTable(TrainingsTable.TABLENAME))
				.getTrainingList();
	}

	public void saveTraining(core.training.TrainingPerWeek training) {
		((TrainingsTable) getTable(TrainingsTable.TABLENAME))
				.saveTraining(training);
	}

	// ------------------------------- FutureTrainingTable
	// -------------------------------------------------

	public List<TrainingPerWeek> getFutureTrainingsVector() {
		return ((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME))
				.getFutureTrainingsVector();
	}

	public void saveFutureTraining(TrainingPerWeek training) {
		((FutureTrainingTable) getTable(FutureTrainingTable.TABLENAME))
				.saveFutureTraining(training);
	}

	// ------------------------------- VereinTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	public Verein getVerein(int hrfID) {
		return ((VereinTable) getTable(VereinTable.TABLENAME)).getVerein(hrfID);
	}

	/**
	 * speichert das Verein
	 */
	public void saveVerein(int hrfId, Verein verein) {
		((VereinTable) getTable(VereinTable.TABLENAME)).saveVerein(hrfId,
				verein);
	}

	// ------------------------------- XtraDataTable
	// -------------------------------------------------

	/**
	 * lädt die Basics zum angegeben HRF file ein
	 */
	public XtraData getXtraDaten(int hrfID) {
		return ((XtraDataTable) getTable(XtraDataTable.TABLENAME))
				.getXtraDaten(hrfID);
	}

	/**
	 * speichert das Team
	 */
	public void saveXtraDaten(int hrfId, XtraData xtra) {
		((XtraDataTable) getTable(XtraDataTable.TABLENAME)).saveXtraDaten(
				hrfId, xtra);
	}

	// ------------------------------- MatchLineupTeamTable
	// -------------------------------------------------
	public MatchLineupTeam getMatchLineupTeam(int matchID, int teamID) {
		return ((MatchLineupTeamTable) getTable(MatchLineupTeamTable.TABLENAME))
				.getMatchLineupTeam(matchID, teamID);
	}

	// ------------------------------- UserParameterTable
	// -------------------------------------------------

	/**
	 * Speichert die Spaltenreihenfolge der Tabellen private void
	 * saveTabellenSpaltenReihenfolge( int[][] spieleruebersicht, int[][]
	 * aufstellung ) {}
	 */
	/**
	 * Lädt die UserParameter direkt in das UserParameter-SingeltonObjekt
	 */
	public void loadUserParameter() {
		UserConfigurationTable table = (UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME);
		table.load(UserParameter.instance());
		table.load(HOParameter.instance());
	}

	/**
	 * Speichert die UserParameter in der Datenbank
	 * 
	 */
	public void saveUserParameter() {

		UserConfigurationTable table = (UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME);
		table.store(UserParameter.instance());
		table.store(HOParameter.instance());
	}

	// ------------------------------- PaarungTable
	// -------------------------------------------------

	/**
	 * holt die Paarungen zum Plan aus der DB und added sie
	 */
	protected void getPaarungen(Spielplan plan) {
		((PaarungTable) getTable(PaarungTable.TABLENAME)).getPaarungen(plan);
	}

	/**
	 * speichert die Paarungen zu einem Spielplan
	 */
	protected void storePaarung(Vector<Paarung> paarungen, int ligaId,
			int saison) {
		((PaarungTable) getTable(PaarungTable.TABLENAME)).storePaarung(
				paarungen, ligaId, saison);
	}

	public void deletePaarungTabelle(String[] whereSpalten, String[] whereValues) {
		getTable(PaarungTable.TABLENAME).delete(whereSpalten, whereValues);
	}

	// ------------------------------- MatchDetailsTable
	// -------------------------------------------------

	/**
	 * Gibt die MatchDetails zu einem Match zurück
	 */
	public Matchdetails getMatchDetails(int matchId) {
		return ((MatchDetailsTable) getTable(MatchDetailsTable.TABLENAME))
				.getMatchDetails(matchId);
	}

	/**
	 * Return match statistics (Count,Win,Draw,Loss,Goals)
	 * 
	 * @param matchtype
	 * @return
	 */
	public MatchesOverviewRow[] getMatchesOverviewValues(int matchtype) {
		return MatchesOverviewQuery.getMatchesOverviewValues(matchtype);
	}

	// ------------------------------- MatchHighlightsTable
	// -------------------------------------------------

	/**
	 * Gibt die MatchHighlights zu einem Match zurück
	 */
	public Vector<MatchHighlight> getMatchHighlights(int matchId) {
		return ((MatchHighlightsTable) getTable(MatchHighlightsTable.TABLENAME))
				.getMatchHighlights(matchId);
	}

	public MatchesHighlightsStat[] getChancesStat(boolean ownTeam, int matchtype) {
		return MatchesOverviewQuery.getChancesStat(ownTeam, matchtype);

	}

	public Vector<MatchHighlight> getMatchHighlightsByTypIdAndPlayerId(
			int type, int playerId) {
		return ((MatchHighlightsTable) getTable(MatchHighlightsTable.TABLENAME))
				.getMatchHighlightsByTypIdAndPlayerId(type, playerId);
	}

	// Transfer
	public List<PlayerTransfer> getTransfers(int playerid, boolean allTransfers) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
				.getTransfers(playerid, allTransfers);
	}

	public List<PlayerTransfer> getTransfers(int season, boolean bought,
			boolean sold) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
				.getTransfers(season, bought, sold);
	}

    public void removeTransfer(int transferId) {
		((TransferTable) getTable(TransferTable.TABLENAME))
				.removeTransfer(transferId);
	}

	public void updatePlayerTransfers(int playerId) {
		((TransferTable) getTable(TransferTable.TABLENAME))
				.updatePlayerTransfers(playerId);
	}

	public boolean updateTeamTransfers(int teamid) {
		return ((TransferTable) getTable(TransferTable.TABLENAME))
					.updateTeamTransfers(teamid);
	}

	public int getTransferType(int playerId) {
		return ((TransferTypeTable) getTable(TransferTypeTable.TABLENAME))
				.getTransferType(playerId);
	}

	public void setTransferType(int playerId, int type) {
		((TransferTypeTable) getTable(TransferTypeTable.TABLENAME))
				.setTransferType(playerId, type);
	}

	// WorldDetail
	public WorldDetailLeague[] getAllWorldDetailLeagues() {
		return ((WorldDetailsTable) getTable(WorldDetailsTable.TABLENAME))
				.getAllWorldDetailLeagues();
	}

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

	public double[][] getSpielerDaten4Statistik(int spielerId, int anzahlHRF) {
		return StatisticQuery.getSpielerDaten4Statistik(spielerId, anzahlHRF);
	}

	public double[][] getFinanzen4Statistik(int anzahlHRF) {
		return StatisticQuery.getFinanzen4Statistik(anzahlHRF);
	}

//	public double[][] getSpielerFinanzDaten4Statistik(int spielerId,
//			int anzahlHRF) {
//		return StatisticQuery.getSpielerFinanzDaten4Statistik(spielerId,
//				anzahlHRF);
//	}

	public ArenaStatistikTableModel getArenaStatistikModel(int matchtyp) {
		return StatisticQuery.getArenaStatistikModel(matchtyp);
	}

	public double[][] getDurchschnittlicheSpielerDaten4Statistik(int anzahlHRF,
			String group) {
		return StatisticQuery.getDurchschnittlicheSpielerDaten4Statistik(
				anzahlHRF, group);
	}

//	/**
//	 * Sucht das letzte HRF zwischen dem angegebenen Datum und 6 Tagen davor
//	 * Wird kein HRF gefunden wird -1 zurückgegeben
//	 */
//	public Timestamp getPreviousTrainingDate(int hrfId) {
//		String sql = "select trainingdate from HRF, XTRADATA where HRF.hrf_id=XTRADATA.hrf_id and trainingdate < (select trainingdate from XTRADATA where hrf_id="
//				+ hrfId + ") order by datum desc limit 1";
//		final ResultSet rs = m_clJDBCAdapter.executeQuery(sql);
//
//		try {
//			if (rs != null) {
//				if (rs.first()) {
//					return rs.getTimestamp("trainingdate");
//				}
//			}
//		} catch (Exception e) {
//			HOLogger.instance().log(getClass(),
//					"DBZugriff.getPreviousTrainingDate: " + e.toString());
//		}
//		return null;
//	}

	public int getCountOfPlayedMatches(int playerId, boolean official) {
		String sqlStmt = "select count(MATCHESKURZINFO.matchid) as MatchNumber FROM MATCHLINEUPPLAYER INNER JOIN MATCHESKURZINFO ON MATCHESKURZINFO.matchid = MATCHLINEUPPLAYER.matchid ";
		sqlStmt = sqlStmt + "where spielerId = " + playerId
				+ " and FIELDPOS>-1 ";

		if (official) {
			sqlStmt = sqlStmt + "and matchtyp <8";
		} else {
			sqlStmt = sqlStmt + "and matchtyp >7";
		}

		final ResultSet rs = getAdapter().executeQuery(sqlStmt.toString());

		if (rs == null) {
			return 0;
		}

		int count = 0;

		try {
			while (rs.next()) {
				count = rs.getInt("MatchNumber");
			}
		} catch (SQLException e) {
		}

		return count;
	}

	/**
	 * Gibt eine Liste mit SpielerMatchCBItems zu den einzelnen Matches zurück
	 */
	public Vector<SpielerMatchCBItem> getSpieler4Matches(int spielerid) {
		final Vector<SpielerMatchCBItem> spielerMatchCBItems = new Vector<SpielerMatchCBItem>();

		// Liste aller Matchplayer mit der spielerid holen
		try {
			final Vector<SpielerMatchCBItem> tempSpielerMatchCBItems = new Vector<SpielerMatchCBItem>();

			final String sql = "SELECT DISTINCT MATCHLINEUPPLAYER.MatchID, MATCHLINEUPPLAYER.MatchID, MATCHLINEUPPLAYER.Rating, MATCHLINEUP.MatchDate, MATCHLINEUP.HeimName, MATCHLINEUP.HeimID, MATCHLINEUP.GastName, MATCHLINEUP.GastID, MATCHLINEUPPLAYER.HoPosCode, MATCHLINEUP.MatchTyp FROM MATCHLINEUPPLAYER, MATCHLINEUP WHERE MATCHLINEUPPLAYER.SpielerID="
					+ spielerid
					+ " AND MATCHLINEUPPLAYER.Rating>-1 AND MATCHLINEUPPLAYER.MatchID=MATCHLINEUP.MatchID ORDER BY MATCHLINEUP.MatchDate DESC";
			final ResultSet rs = m_clJDBCAdapter.executeQuery(sql);
			rs.beforeFirst();

			// Alle Daten zu dem Spieler holen
			while (rs.next()) {
				final SpielerMatchCBItem temp = new SpielerMatchCBItem(null,
						rs.getInt("MatchID"), rs.getFloat("Rating") * 2,
						rs.getInt("HoPosCode"), rs.getString("MatchDate"),
						DBManager.deleteEscapeSequences(rs
								.getString("HeimName")), rs.getInt("HeimID"),
						DBManager.deleteEscapeSequences(rs
								.getString("GastName")), rs.getInt("GastID"),
						MatchType.getById(rs.getInt("MatchTyp")), null, "", "");
				tempSpielerMatchCBItems.add(temp);
			}

			final java.text.SimpleDateFormat simpleFormat = new java.text.SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
			final java.text.SimpleDateFormat simpleFormat2 = new java.text.SimpleDateFormat(
					"yyyy-MM-dd", Locale.GERMANY);
			Timestamp filter = null;
			Date datum = null;

			// Die Spielerdaten zu den Matches holen
			for (int i = 0; i < tempSpielerMatchCBItems.size(); i++) {
				final SpielerMatchCBItem item = tempSpielerMatchCBItems.get(i);
				try {
					datum = simpleFormat.parse(item.getMatchdate());
				} catch (Exception e1) {
				}

				if (datum == null) {
					datum = simpleFormat2.parse(item.getMatchdate());
				}

				if (datum != null) {
					filter = new Timestamp(datum.getTime());
				}

				// Spieler
				final core.model.player.Spieler player = getSpielerAtDate(
						spielerid, filter);

				// Matchdetails
				final core.model.match.Matchdetails details = getMatchDetails(item
						.getMatchID());

				// Stimmung und Selbstvertrauen
				final String[] stimmungSelbstvertrauen = getStimmmungSelbstvertrauen(getHRFID4Date(filter));

				// Nur wenn Spielerdaten gefunden wurden diese in den
				// RückgabeVector übergeben
				if ((player != null) && (details != null)
						&& (stimmungSelbstvertrauen != null)) {
					item.setSpieler(player);
					item.setMatchdetails(details);
					item.setStimmung(stimmungSelbstvertrauen[0]);
					item.setSelbstvertrauen(stimmungSelbstvertrauen[1]);
					spielerMatchCBItems.add(item);
				}
			}
		} catch (Exception e) {
			HOLogger.instance().log(getClass(),
					"DatenbankZugriff.getSpieler4Matches : " + e);
		}

		return spielerMatchCBItems;
	}

	// ------------------------------------- Delete
	// -------------------------------------------------------
	public void deleteHRF(int hrfid) {
		final String[] where = { "HRF_ID" };
		final String[] value = { hrfid + "" };

		getTable(StadionTable.TABLENAME).delete(where, value);
		getTable(HRFTable.TABLENAME).delete(where, value);
		getTable(LigaTable.TABLENAME).delete(where, value);
		getTable(VereinTable.TABLENAME).delete(where, value);
		getTable(AufstellungTable.TABLENAME).delete(where, value);
		((MatchSubstitutionTable) getTable(MatchSubstitutionTable.TABLENAME))
				.deleteAllMatchSubstitutionsByHrfId(hrfid);

		getTable(PositionenTable.TABLENAME).delete(where, value);
		getTable(TeamTable.TABLENAME).delete(where, value);
		getTable(FinanzenTable.TABLENAME).delete(where, value);
		getTable(BasicsTable.TABLENAME).delete(where, value);
		getTable(SpielerTable.TABLENAME).delete(where, value);
		getTable(SpielerSkillupTable.TABLENAME).delete(where, value);
		getTable(XtraDataTable.TABLENAME).delete(where, value);
		((StaffTable) getTable(StaffTable.TABLENAME)).deleteAllStaffByHrfId(hrfid);
		
		
	}

	/**
	 * Deletes all data for the given match
	 * 
	 * @param matchid
	 *            The matchid. Must be larger than 0.
	 */
	public void deleteMatch(int matchid) {
		final String[] whereSpalten = { "MatchID" };
		final String[] whereValues = { "" + matchid };
		getTable(MatchDetailsTable.TABLENAME).delete(whereSpalten, whereValues);
		getTable(MatchHighlightsTable.TABLENAME).delete(whereSpalten,
				whereValues);
		getTable(MatchLineupTable.TABLENAME).delete(whereSpalten, whereValues);
		getTable(MatchLineupTeamTable.TABLENAME).delete(whereSpalten,
				whereValues);
		getTable(MatchLineupPlayerTable.TABLENAME).delete(whereSpalten,
				whereValues);
		getTable(MatchesKurzInfoTable.TABLENAME).delete(whereSpalten,
				whereValues);
		((MatchSubstitutionTable) getTable(MatchSubstitutionTable.TABLENAME))
				.deleteAllMatchSubstitutionsByMatchId(matchid);
	}

	/**
	 * Stores the given match info. If info is missing, or the info are not for
	 * the same match, nothing is stored and false is returned. If the store is
	 * successful, true is returned.
	 * 
	 * If status of the info is not FINISHED, nothing is stored, and false is
	 * also returned.
	 * 
	 * @param info
	 *            The MatchKurzInfo for the match
	 * @param details
	 *            The MatchDetails for the match
	 * @param lineup
	 *            The MatchLineup for the match
	 * @return true if the match is stored. False if not
	 */
	public boolean storeMatch(MatchKurzInfo info, Matchdetails details,
			MatchLineup lineup) {

		if ((info == null) || (details == null) || (lineup == null)) {
			return false;
		}

		if ((info.getMatchID() == details.getMatchID())
				&& (info.getMatchID() == lineup.getMatchID())
				&& (info.getMatchStatus() == MatchKurzInfo.FINISHED)) {

			deleteMatch(info.getMatchID());

			MatchKurzInfo[] matches = new MatchKurzInfo[1];
			matches[0] = info;
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
	 * @param match
	 *            the match to update.
	 */
	public void updateMatchKurzInfo(MatchKurzInfo match) {
		((MatchesKurzInfoTable) getTable(MatchesKurzInfoTable.TABLENAME))
				.update(match);
	}

	/**
	 * delete eine Aufstellung + Positionen
	 */
	public void deleteAufstellung(int hrfId, String name) {
		String[] whereS = { "HRF_ID", "Aufstellungsname" };
		String[] whereV = { "" + hrfId, "'" + name + "'" };

		// erst Vorhandene Aufstellung löschen
		getTable(AufstellungTable.TABLENAME).delete(whereS, whereV);

		// Standard sys resetten
		getTable(PositionenTable.TABLENAME).delete(whereS, whereV);

		whereS = new String[] { "LineupName" };
		whereV = new String[] { "'" + name + "'" };
		getTable(MatchSubstitutionTable.TABLENAME).delete(whereS, whereV);
		getTable(PenaltyTakersTable.TABLENAME).delete(whereS, whereV);
	}

	private void createAllTables() throws SQLException {
		Object[] allTables = tables.values().toArray();
		for (int i = 0; i < allTables.length; i++) {
			AbstractTable table = (AbstractTable) allTables[i];
			table.createTable();
			String[] statements = table.getCreateIndizeStatements();
			for (int j = 0; j < statements.length; j++) {
				m_clJDBCAdapter.executeUpdate(statements[j]);
			}
		}
	}

	public Map<String, Object> loadModuleConfigs() {
		return ((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.findAll();
	}

	public void saveModuleConfigs(Map<String, Object> values) {
		((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.saveConfig(values);
	}

	public void deleteModuleConfigsKey(String key) {
		((ModuleConfigTable) getTable(ModuleConfigTable.TABLENAME))
				.deleteConfig(key);
	}

	/**
	 * Set a single UserParameter in the DB
	 * 
	 * @param fieldName
	 *            the name of the parameter to set
	 * @param value
	 *            the target value
	 */
	void saveUserParameter(String fieldName, int value) {
		saveUserParameter(fieldName, "" + value);
	}

	/**
	 * Set a single UserParameter in the DB
	 * 
	 * @param fieldName
	 *            the name of the parameter to set
	 * @param value
	 *            the target value
	 */
	void saveUserParameter(String fieldName, double value) {
		saveUserParameter(fieldName, "" + value);
	}

	/**
	 * Set a single UserParameter in the DB
	 * 
	 * @param fieldName
	 *            the name of the parameter to set
	 * @param value
	 *            the target value
	 */
	void saveUserParameter(String fieldName, String value) {
		((UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME))
				.update(fieldName, value);
	}

	/**
	 * Remove a single UserParameter from the DB
	 * 
	 * @param fieldName
	 *            the name of the parameter to remove
	 */
	void removeUserParameter(String fieldName) {
		((UserConfigurationTable) getTable(UserConfigurationTable.TABLENAME))
				.remove(fieldName);
	}

	public void saveHOColumnModel(HOTableModel model) {
		((UserColumnsTable) getTable(UserColumnsTable.TABLENAME))
				.saveModel(model);
	}

	public void loadHOColumModel(HOTableModel model) {
		((UserColumnsTable) getTable(UserColumnsTable.TABLENAME))
				.loadModel(model);
	}

	public void removeTAFavoriteTeam(int teamId) {
		((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME))
				.removeTeam(teamId);
	}

	public void addTAFavoriteTeam(module.teamAnalyzer.vo.Team team) {
		((TAFavoriteTable) getTable(TAFavoriteTable.TABLENAME)).addTeam(team);
	}

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

	public PlayerInfo getTAPlayerInfo(int playerId, int week, int season) {
		return ((TAPlayerTable) getTable(TAPlayerTable.TABLENAME))
				.getPlayerInfo(playerId, week, season);
	}

	public PlayerInfo getTALatestPlayerInfo(int playerId) {
		return ((TAPlayerTable) getTable(TAPlayerTable.TABLENAME))
				.getLatestPlayerInfo(playerId);
	}

	public void addTAPlayerInfo(PlayerInfo info) {
		((TAPlayerTable) getTable(TAPlayerTable.TABLENAME)).addPlayer(info);
	}

	public void updateTAPlayerInfo(PlayerInfo info) {
		((TAPlayerTable) getTable(TAPlayerTable.TABLENAME)).updatePlayer(info);
	}

//	public void deleteTAOldPlayerInfos() {
//		((TAPlayerTable) getTable(TAPlayerTable.TABLENAME)).deleteOldPlayers();
//	}

	/**
	 * IFA
	 */
//
//	public boolean isIFALeagueIDinDB(int leagueID, boolean homeAway) {
//		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
//				.isLeagueIDinDB(leagueID, homeAway);
//	}

	public boolean isIFAMatchinDB(int matchId) {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.isMatchinDB(matchId);
	}

	public String getLastIFAMatchDate(String defaultValue) {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.getLastMatchDate(defaultValue);
	}

	public IfaMatch[] getIFAMatches(boolean home) {
		return ((IfaMatchTable) getTable(IfaMatchTable.TABLENAME))
				.getMatches(home);
	}

	public void insertIFAMatch(IfaMatch match) {
		((IfaMatchTable) getTable(IfaMatchTable.TABLENAME)).insertMatch(match);
	}

	/**
	 * Deletes all the content of the IFA match table.
	 */
	public void deleteIFAMatches() {
		((IfaMatchTable) getTable(IfaMatchTable.TABLENAME)).deleteAllMatches();
	}

	/**
	 * Alle \ entfernen
	 */
	public static String deleteEscapeSequences(String text) {
		if (text == null) {
			return "";
		}

		final StringBuffer buffer = new StringBuffer();
		final char[] chars = text.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == '§') {
				buffer.append("\\");
			} else if (chars[i] != '#') {
				buffer.append(chars[i]);
			} else {
				buffer.append("'");
			}
		}

		return buffer.toString();
	}

	/**
	 * ' " und ´ codieren durch \
	 */
	public static String insertEscapeSequences(String text) {
		if (text == null) {
			return "";
		}
		final StringBuffer buffer = new StringBuffer();
		final char[] chars = text.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			int code = (int) chars[i];
			if ((chars[i] == '"') || (chars[i] == '\'') || (chars[i] == '´')) {
				buffer.append("#");
			} else if (code == 92) {
				buffer.append("§");
			} else {
				buffer.append(chars[i]);
			}
		}

		return buffer.toString();
	}
}
