package core.file.hrf;

import core.model.HOModel;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.Team;
import core.model.XtraData;
import core.model.misc.Basics;
import core.model.misc.Finanzen;
import core.model.misc.Verein;
import core.model.player.Player;
import core.model.series.Liga;
import core.util.HOLogger;
import core.util.IOUtils;
import module.lineup.Lineup;
import tool.arenasizer.Stadium;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class HRFStringParser {

	private static final String ENTITY = "Entity";
	private static final String BASICS = "[basics]";
	private static final String LEAGUE = "[league]";
	private static final String CLUB = "[club]";
	private static final String TEAM = "[team]";
	private static final String LINEUP = "[lineup]";
	private static final String ECONOMY = "[economy]";
	private static final String ARENA = "[arena]";
	private static final String PLAYER = "[player]";
	private static final String XTRA = "[xtra]";
	private static final String LASTLINEUP = "[lastlineup]";
	private static final String STAFF = "[staff]";

	private HRFStringParser() {
	}

	public static HOModel parse(String hrf) {
		HOModel modelReturn = null;
		Timestamp hrfdate = null;

		if (hrf == null || hrf.length() == 0) {
			HOLogger.instance().log(HRFStringParser.class, "HRF string is empty");
			return null;
		}

		try {
			final List<Properties> propertiesList = new ArrayList<Properties>();
			Properties properties = null;

			// Load hrf string into a stream
			final ByteArrayInputStream bis = new ByteArrayInputStream(hrf.getBytes("UTF-8"));
			final InputStreamReader isr = new InputStreamReader(bis, "UTF-8");
			BufferedReader hrfReader = new BufferedReader(isr);
			String lineString = "";
			Object entity = null;
			String datestring = "";
			int indexEqualsSign = -1;
			// While there is still data to process
			while (hrfReader.ready()) {
				// Read a line
				lineString = hrfReader.readLine();

				// Ignore empty lines
				if ((lineString == null) || lineString.trim().equals("")) {
					continue;
				}

				// New Properties
				if (lineString.startsWith("[")) {
					// Old Property found, add to the Vector
					if (properties != null) {
						// HRF date
						entity = properties.get(ENTITY);
						if (entity != null && entity.toString().equalsIgnoreCase(BASICS)) {
							datestring = properties.getProperty("date");
							hrfdate = getDateFromString(datestring);
						}
						propertiesList.add(properties);
					}

					// Create a new Property
					properties = new Properties();
					// Player?
					if (lineString.startsWith("[player")) {
						properties.setProperty(ENTITY, PLAYER);
						properties.setProperty("id",
								lineString.substring(7, lineString.lastIndexOf(']')));
					} else {
						properties.setProperty(ENTITY, lineString);
					}
				}
				// Fill actual Properties
				else {
					indexEqualsSign = lineString.indexOf('=');
					if (indexEqualsSign > 0) {
						if (properties == null) {
							properties = new Properties();
						}
						properties.setProperty(lineString.substring(0, indexEqualsSign)
								.toLowerCase(java.util.Locale.ENGLISH), lineString
								.substring(indexEqualsSign + 1));
					}
				}
			}

			// Add the last property
			if (properties != null) {
				propertiesList.add(properties);
			}

			// Close the reader
			IOUtils.closeQuietly(hrfReader);

			// Create HOModel
			modelReturn = createHOModel(propertiesList, hrfdate);
		} catch (Exception e) {
			HOLogger.instance().error(HRFStringParser.class, e);
		}
		return modelReturn;
	}

	private static Timestamp getDateFromString(String date) {
		SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				java.util.Locale.GERMANY);
		try {
			// Hattrick
			return new Timestamp(simpleFormat.parse(date).getTime());
		} catch (Exception e) {
			try {
				// Hattrick
				simpleFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.GERMANY);
				return new Timestamp(simpleFormat.parse(date).getTime());
			} catch (Exception expc) {
				HOLogger.instance().log(HRFStringParser.class, e);
				return new Timestamp(System.currentTimeMillis());
			}
		}
	}

	/**
	 * Erzeugt aus dem Vector mit Properties ein HOModel
	 */
	private static HOModel createHOModel(List<Properties> propertiesList, Timestamp hrfdate)
			throws Exception {

		final HOModel hoModel = new HOModel();
		int trainerID = -1;

		for (Properties properties : propertiesList) {

			Object entity = properties.get(ENTITY);

			if (entity != null) {
				// basics
				if (entity.toString().equalsIgnoreCase(BASICS)) {
					hoModel.setBasics(new Basics(properties));
				}
				// league
				else if (entity.toString().equalsIgnoreCase(LEAGUE)) {
					hoModel.setLiga(new Liga(properties));
				}
				// club
				else if (entity.toString().equalsIgnoreCase(CLUB)) {
					hoModel.setVerein(new Verein(properties));
				}
				// team
				else if (entity.toString().equalsIgnoreCase(TEAM)) {
					hoModel.setTeam(new Team(properties));
				}
				// lineup
				else if (entity.toString().equalsIgnoreCase(LINEUP)) {
					hoModel.setAufstellung(new Lineup(properties));
				}
				// economy
				else if (entity.toString().equalsIgnoreCase(ECONOMY)) {
					hoModel.setFinanzen(new Finanzen(properties));
				}
				// arena
				else if (entity.toString().equalsIgnoreCase(ARENA)) {
					hoModel.setStadium(new Stadium(properties));
				}
				// player
				else if (entity.toString().equalsIgnoreCase(PLAYER)) {
					hoModel.addSpieler(new Player(properties, hrfdate));
				}
				// Xtra
				else if (entity.toString().equalsIgnoreCase(XTRA)) {
					hoModel.setXtraDaten(new XtraData(properties));
					trainerID = Integer.parseInt(properties.getProperty("trainerid", "-1")
							.toString());
				} else if (entity.toString().equalsIgnoreCase(LASTLINEUP)) {
					hoModel.setLastAufstellung(new Lineup(properties));
				} else if (entity.toString().equalsIgnoreCase(STAFF)) {
					hoModel.setStaff(parseStaff(properties));
				}
				// Unbekannt
				else {
					// Ignorieren!
					HOLogger.instance().log(HRFStringParser.class,
							"Unbekannte Entity: " + entity.toString());
				}
			} else {
				HOLogger.instance().log(HRFStringParser.class,
						"Fehlerhafte Datei / Keine Entity gefunden");
				return null;
			}
		}

		// Only keep trainerinformation for player equal to trainerID, rest is
		// resetted . So later trainer could be found by searching for player
		// having trainerType != -1
		if (trainerID > -1) {
			List<Player> players = hoModel.getAllSpieler();
			for (Player player : players) {
				if (player.isTrainer() && player.getSpielerID() != trainerID) {
					player.setTrainer(-1);
					player.setTrainerTyp(-1);
				}
			}
		}

		return hoModel;
	}
	
	
	private static List<StaffMember> parseStaff(Properties props) {
		
		try {
			ArrayList<StaffMember> list = new ArrayList<StaffMember>();
			
			int i = 0;
			while (true) {
				if (!props.containsKey("staff" + i + "name")) {
					break;
				}
		
				StaffMember member = new StaffMember();
				member.setName(props.getProperty("staff" + i + "name"));
				member.setId(Integer.parseInt(props.getProperty("staff" + i + "staffid")));
				member.setStaffType(StaffType.getById(Integer.parseInt(props.getProperty("staff" + i + "stafftype"))));
				member.setLevel(Integer.parseInt(props.getProperty("staff" + i + "stafflevel")));
				member.setCost(Integer.parseInt(props.getProperty("staff" + i + "cost")));
						
				i++;
				list.add(member);
			}
			
			// because it is handy...
			Collections.sort(list);
			
			return list;

		} catch (Exception e) {
			HOLogger.instance().error(null, "HRFStringParser: Failed to parse staff members");
			return new ArrayList<StaffMember>();
		}
	}
}
