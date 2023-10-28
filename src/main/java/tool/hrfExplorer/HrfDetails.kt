package tool.hrfExplorer;

import core.constants.TeamConfidence;
import core.constants.TeamSpirit;
import core.constants.TrainingType;
import core.db.DBManager;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.util.HODateTime;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.temporal.ChronoField;
import java.util.Objects;
import javax.swing.*;
import static core.gui.theme.ThemeManager.getColor;
import java.time.*;

class HrfDetails {

	private String m_Str_DatumVorher;
	private String m_Str_DatumDanach;
	private HODateTime m_Datum;
	private String m_Wochentag;
	private int m_Kw;
	private String m_TrArt;
	private int m_TrArtInt;
	private int m_TrInt;
	private int m_Saison;
	private String m_Liga;
	private int m_anzCoTrainer;
	private int m_anzTwTrainer;
	private int m_Spieltag;
	private int m_Punkte;
	private int m_ToreFuer;
	private int m_ToreGegen;
	private int m_Platz;
	private int m_anzSpieler = 0;
	private String m_Stimmung;
	private String m_Selbstvertrauen;
	private int m_Fans;
	private String m_TeamName;
	private int m_TeamID;
	private Icon m_bild;

	public HrfDetails() {
	}

	private static final DBManager.PreparedStatementBuilder maxHrfDateStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT MAX(DATUM) FROM HRF WHERE DATUM < ?"
	);
	private static final DBManager.PreparedStatementBuilder minHrfDateStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT MIN(DATUM) FROM HRF WHERE DATUM > ?"
	);
	private static final DBManager.PreparedStatementBuilder countHrfDateStatementBuilder = new DBManager.PreparedStatementBuilder(
			"SELECT count(*) FROM HRF WHERE DATUM = ?"
	);

	/*****************
	 * Berechnet das vorhergehende und das folgende Datum in der DB und pr�ft,
	 * ob das File/der Eintrag in DB angelegt ist
	 */
	void createDates() {
		ResultSet m_rs = Objects.requireNonNull(DBManager
						.instance()
						.getAdapter())
				.executePreparedQuery(maxHrfDateStatementBuilder.getStatement(), m_Datum.toDbTimestamp());
		try {
			while (true) {
				assert m_rs != null;
				if (!m_rs.next()) break;
				m_rs.getTimestamp(1);

				if (m_rs.wasNull()) {
					setStr_DatumVorher("---");
				} else {
					setStr_DatumVorher((m_rs.getTimestamp(1)).toString()
							.substring(0, 19));
				}
			}
		} catch (SQLException sexc) {
			HrfExplorer.appendText("" + sexc);
		}
		m_rs = Objects.requireNonNull(DBManager
						.instance()
						.getAdapter())
				.executePreparedQuery(minHrfDateStatementBuilder.getStatement(), m_Datum.toDbTimestamp());
		try {
			while (true) {
				assert m_rs != null;
				if (!m_rs.next()) break;
				m_rs.getTimestamp(1);

				if (m_rs.wasNull()) {
					setStr_DatumDanach("---");
				} else {
					setStr_DatumDanach((m_rs.getTimestamp(1)).toString()
							.substring(0, 19));
				}
			}
		} catch (SQLException sexc) {
			HrfExplorer.appendText("" + sexc);
		}
		m_rs = Objects.requireNonNull(DBManager
						.instance()
						.getAdapter())
				.executePreparedQuery(countHrfDateStatementBuilder.getStatement(),m_Datum.toDbTimestamp());
		try {
			while (true) {
				assert m_rs != null;
				if (!m_rs.next()) break;
				if (m_rs.getInt(1) == 0) {
					setBild(ThemeManager.getIcon(HOIconName.REMOVE));
				} else {
					setBild(ImageUtilities.getRightArrowIcon(getColor(HOColorName.SHOW_MATCH)));
				}
			}
		} catch (SQLException sexc) {
			HrfExplorer.appendText("" + sexc);
		}
	}

	/*****************
	 * Berechnet diverse Datumswerte aus einem Timestamp-String
	 *
	 */
	void calcDatum() {
		var localDate = LocalDate.ofInstant(m_Datum.instant, ZoneId.systemDefault());
		setKw(localDate.get(ChronoField.ALIGNED_WEEK_OF_YEAR));
		var value = localDate.getDayOfWeek();
		String[] tage = HrfExplorer.getTage();
		setWochentag(tage[value.getValue()-1]);
//		switch (value) {
//			case Calendar.MONDAY -> setWochentag(tage[0]);
//			case Calendar.TUESDAY -> setWochentag(tage[1]);
//			case Calendar.WEDNESDAY -> setWochentag(tage[2]);
//			case Calendar.THURSDAY -> setWochentag(tage[3]);
//			case Calendar.FRIDAY -> setWochentag(tage[4]);
//			case Calendar.SATURDAY -> setWochentag(tage[5]);
//			case Calendar.SUNDAY -> setWochentag(tage[6]);
//		}
	}

	/**
	 * @return Returns the m_anzCoTrainer.
	 */
	int getAnzCoTrainer() {
		return m_anzCoTrainer;
	}

	/**
	 * @return Returns the m_anzSpieler.
	 */
	int getAnzSpieler() {
		return m_anzSpieler;
	}

	/**
	 * @return Returns the m_anzTwTrainer.
	 */
	int getAnzTwTrainer() {
		return m_anzTwTrainer;
	}

	/**
	 * @return Returns the m_bild.
	 */
	Icon getBild() {
		return m_bild;
	}

	/**
	 * @return Returns the m_Datum.
	 */
	HODateTime getDatum() {
		return m_Datum;
	}

	/**
	 * @return Returns the m_Fans.
	 */
	int getFans() {
		return m_Fans;
	}

	/**
	 * @return Returns the m_Kw.
	 */
	int getKw() {
		return m_Kw;
	}

	/**
	 * @return Returns the m_Liga.
	 */
	String getLiga() {
		return m_Liga;
	}

	/**
	 * @return Returns the m_Platz.
	 */
	int getPlatz() {
		return m_Platz;
	}

	/**
	 * @return Returns the m_Punkte.
	 */
	int getPunkte() {
		return m_Punkte;
	}

	/**
	 * @return Returns the m_Saison.
	 */
	int getSaison() {
		return m_Saison;
	}

	/**
	 * @return Returns the m_Selbstvertrauen.
	 */
	String getSelbstvertrauen() {
		return m_Selbstvertrauen;
	}

	/**
	 * @return Returns the m_Spieltag.
	 */
	int getSpieltag() {
		return m_Spieltag;
	}

	/**
	 * @return Returns the m_Stimmung.
	 */
	String getStimmung() {
		return m_Stimmung;
	}

	/**
	 * @return Returns the m_Str_DatumDanach.
	 */
	String getStr_DatumDanach() {
		return m_Str_DatumDanach;
	}

	/**
	 * @return Returns the m_Str_DatumVorher.
	 */
	String getStr_DatumVorher() {
		return m_Str_DatumVorher;
	}

	/**
	 * @return Returns the m_TeamID.
	 */
	int getTeamID() {
		return m_TeamID;
	}

	/**
	 * @return Returns the m_TeamName.
	 */
	String getTeamName() {
		return m_TeamName;
	}

	/**
	 * @return Returns the m_ToreFuer.
	 */
	int getToreFuer() {
		return m_ToreFuer;
	}

	/**
	 * @return Returns the m_ToreGegen.
	 */
	int getToreGegen() {
		return m_ToreGegen;
	}

	/**
	 * @return Returns the m_TrArt.
	 */
	String getTrArt() {
		return m_TrArt;
	}

	/**
	 * @return Returns the m_TrArtInt.
	 */
	int getTrArtInt() {
		return m_TrArtInt;
	}

	/**
	 * @return Returns the m_TrInt.
	 */
	int getTrInt() {
		return m_TrInt;
	}

	/**
	 * @return Returns the m_Wochentag.
	 */
	String getWochentag() {
		return m_Wochentag;
	}

	/**
	 * @param coTrainer
	 *            The m_anzCoTrainer to set.
	 */
	void setAnzCoTrainer(int coTrainer) {
		m_anzCoTrainer = coTrainer;
	}

	/**
	 * @param spieler
	 *            The m_anzSpieler to set.
	 */
	void setAnzSpieler(int spieler) {
		m_anzSpieler = spieler;
	}

	/**
	 * @param twTrainer
	 *            The m_anzTwTrainer to set.
	 */
	void setAnzTwTrainer(int twTrainer) {
		m_anzTwTrainer = twTrainer;
	}

	/**
	 * @param iIcon
	 *            The ImageIcon to set.
	 */
	void setBild(Icon iIcon) {
		m_bild = iIcon;
	}


	/**
	 * @param fans
	 *            The m_Fans to set.
	 */
	void setFans(int fans) {
		m_Fans = fans;
	}

	/**
	 * @param kw
	 *            The m_Kw to set.
	 */
	void setKw(int kw) {
		m_Kw = kw;
	}

	/**
	 * @param liga
	 *            The m_Liga to set.
	 */
	void setLiga(String liga) {
		m_Liga = liga;
	}

	/**
	 * @param platz
	 *            The m_Platz to set.
	 */
	void setPlatz(int platz) {
		m_Platz = platz;
	}

	/**
	 * @param punkte
	 *            The m_Punkte to set.
	 */
	void setPunkte(int punkte) {
		m_Punkte = punkte;
	}

	/**
	 * @param saison
	 *            The m_Saison to set.
	 */
	void setSaison(int saison) {
		m_Saison = saison;
	}

	/**
	 * @param selbstvertrauen
	 *            The m_Selbstvertrauen to set.
	 */
	void setSelbstvertrauen(int selbstvertrauen) {
		m_Selbstvertrauen =TeamConfidence.toString(selbstvertrauen);
	}

	/**
	 * @param spieltag
	 *            The m_Spieltag to set.
	 */
	void setSpieltag(int spieltag) {
		m_Spieltag = spieltag;
	}

	/**
	 * @param stimmung
	 *            The m_Stimmung to set.
	 */
	void setStimmung(int stimmung) {
		m_Stimmung = TeamSpirit.toString(stimmung);
	}

	/**
	 * @param str_DatumDanach
	 *            The m_Str_DatumDanach to set.
	 */
	void setStr_DatumDanach(String str_DatumDanach) {
		m_Str_DatumDanach = str_DatumDanach;
	}

	/**
	 * @param str_DatumVorher
	 *            The m_Str_DatumVorher to set.
	 */
	void setStr_DatumVorher(String str_DatumVorher) {
		m_Str_DatumVorher = str_DatumVorher;
	}

	/**
	 * @param teamID
	 *            The m_TeamID to set.
	 */
	void setTeamID(int teamID) {
		m_TeamID = teamID;
	}

	/**
	 * @param teamName
	 *            The m_TeamName to set.
	 */
	void setTeamName(String teamName) {
		m_TeamName = teamName;
	}


	/**
	 * @param toreFuer
	 *            The m_ToreFuer to set.
	 */
	void setToreFuer(int toreFuer) {
		m_ToreFuer = toreFuer;
	}

	/**
	 * @param toreGegen
	 *            The m_ToreGegen to set.
	 */
	void setToreGegen(int toreGegen) {
		m_ToreGegen = toreGegen;
	}

	/**
	 * @param trArt
	 *            The m_TrArt to set.
	 */
	void setTrArt(int trArt) {
		m_TrArt = TrainingType.toString(trArt);
	}

	/**
	 * @param trArtInt
	 *            The m_TrArtInt to set.
	 */
	void setTrArtInt(int trArtInt) {
		m_TrArtInt = trArtInt;
	}

	/**
	 * @param trInt
	 *            The m_TrInt to set.
	 */
	void setTrInt(int trInt) {
		m_TrInt = trInt;
	}

	/**
	 * @param wochentag
	 *            The m_Wochentag to set.
	 */
	void setWochentag(String wochentag) {
		m_Wochentag = wochentag;
	}

	public void setDatum(HODateTime m_Datum) {
		this.m_Datum = m_Datum;
	}
}
