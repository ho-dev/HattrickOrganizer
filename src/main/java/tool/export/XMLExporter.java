// %1127327738353:hoplugins%
package tool.export;

import core.constants.player.PlayerSkill;
import core.db.DBManager;
import core.db.backup.HOZip;
import core.file.ExampleFileFilter;
import core.file.xml.ExportMatchData;
import core.file.xml.MatchExporter;
import core.file.xml.XMLManager;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.Team;
import core.model.match.MatchLineupPosition;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import core.model.player.IMatchRoleID;
import core.model.player.Player;
import core.net.MyConnector;
import core.rating.RatingPredictionManager;
import core.util.HODateTime;
import core.util.HOLogger;
import module.lineup.Lineup;

import java.awt.BorderLayout;
import java.sql.Timestamp;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

//implement IPlugin for integration into HO
//Refreshable to get informed by data updates
//Actionlistner for Button interaction
public class XMLExporter  {
    //~ Static fields/initializers -----------------------------------------------------------------

    private static String m_sUserRegionID = "-1";

    //~ Instance fields ----------------------------------------------------------------------------

    private SpinnerDateModel m_clSpinnerModel = new SpinnerDateModel();
   

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of DummyPlugIn
     */
    public XMLExporter() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Do the export.
     */
    public void doExport() {
        javax.swing.JWindow waitDialog = null;
		JSpinner m_jsSpinner = new JSpinner(m_clSpinnerModel);
        try {
            // Date           
            m_clSpinnerModel.setCalendarField(java.util.Calendar.MONTH);
            ((JSpinner.DateEditor) m_jsSpinner.getEditor()).getFormat().applyPattern("dd.MM.yyyy");
            m_clSpinnerModel.setValue(RatingPredictionManager.LAST_CHANGE);
	
            JFrame owner = HOMainFrame.instance();
            final JDialog dialog = new JDialog(owner, HOVerwaltung.instance().getLanguageString("xmlexport.startdate"));
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(m_jsSpinner, BorderLayout.CENTER);

            JButton button = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.ok"));
            button.addActionListener(e -> {
				dialog.setVisible(false);
				dialog.dispose();
			});
            dialog.getContentPane().add(button, BorderLayout.SOUTH);
            dialog.pack();
            dialog.setLocation((owner.getLocation().x + (owner.getWidth() / 2))
                               - (dialog.getWidth() / 2),
                               (owner.getLocation().y + (owner.getHeight() / 2))
                               - (dialog.getHeight() / 2));
            dialog.setModal(true);
            dialog.setVisible(true);
            
            // File
            java.io.File file = new java.io.File(HOVerwaltung.instance().getModel().getBasics().getTeamName() + ".zip");

            javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle(HOVerwaltung.instance().getLanguageString("windowtitle.xml-export"));

            ExampleFileFilter filter = new ExampleFileFilter();
            filter.addExtension("zip");
            filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.zip"));
            fileChooser.setFileFilter(filter);
            fileChooser.setSelectedFile(file);

            int returnVal = fileChooser.showSaveDialog(HOMainFrame.instance());

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
				HOMainFrame.instance().setWaitInformation(0);
				var date = new HODateTime(m_clSpinnerModel.getDate().toInstant());
				saveXML(file.getAbsolutePath(),date);
            }
        } catch (Exception ex) {
            HOLogger.instance().log(getClass(),ex);
        }

        HOMainFrame.instance().resetInformation();
    }


	////////////////////////////////XML schreiben/////////////////////////////
	///
	// *XML Format
	// <MatchList>
	// *<Match>
	// *  <Datum>
	// *  <MatchID>
	// *  <Derby>
	// *  <MatchTyp> //Cup usw...
	// *  <Heim/Ausw>
	// *  <Team>  
	// *      <TeamID>
	// *      <System>
	// *      <EingespieltHeit>
	// *      <Einstellung>
	// *      <Spezialtaktik>
	// *      <Stimmung>
	// *      <Selbstvertrauen>
	// *      <Ratings>...
	// *      <lineup>
	// *          <Player>
	// *              <SpielerID>
	// *              <Position>
	// *              <Taktik>
	// *              <ResultingPosition>
	// *              <SpielerDaten>....
	// *          </Player>
	// *          ...
	// *      </Lineup>
	// *  </Team>
	// *</Match>
	// </MatchList>
	// */
    /**


	/**
	 * Save XMP file.
	 */
	@SuppressWarnings("deprecation")
	public void saveXML(String filename, HODateTime startingDate) {
				
		//Alle Matches holen			
		List<ExportMatchData> matches = MatchExporter.getDataUsefullMatches(startingDate);
		
		//XML schreiben
		try {
			Document doc;
			Element ele;
			Element tmpEle;
			Element root;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			doc = builder.newDocument();
			root = doc.createElement("MatchList");
			doc.appendChild(root);

			///Team  Info + ManagerName adden
			tmpEle = doc.createElement("TeamName");
			root.appendChild(tmpEle);
			tmpEle.appendChild(doc.createTextNode("" + HOVerwaltung.instance().getModel().getBasics().getTeamName()));
			tmpEle = doc.createElement("ManagerName");
			root.appendChild(tmpEle);
			tmpEle.appendChild(doc.createTextNode("" + HOVerwaltung.instance().getModel().getBasics().getManager()));

			//Exporter Version adden
			tmpEle = doc.createElement("XMLExporterVersion");
			root.appendChild(tmpEle);
			tmpEle.appendChild(doc.createTextNode("1.05"));

			for (ExportMatchData matchData : matches) {
				//Matchdaten
				tmpEle = doc.createElement("Match");
				root.appendChild(tmpEle);
				ele = doc.createElement("MatchID");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + matchData.getInfo().getMatchID()));
				ele = doc.createElement("Datum");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode(matchData.getInfo().getMatchSchedule().toHT()));
				ele = doc.createElement("Derby");
				tmpEle.appendChild(ele);

				if (getRegionID4Team(matchData.getInfo().getGuestTeamID()).equals(getRegionID4Team(matchData.getInfo().getHomeTeamID()))) {
					ele.appendChild(doc.createTextNode("1"));
				} else {
					ele.appendChild(doc.createTextNode("0"));
				}

				ele = doc.createElement("MatchType");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + matchData.getInfo().getMatchType()));
				ele = doc.createElement("Heimspiel");
				tmpEle.appendChild(ele);

				boolean heimspiel;
				if (matchData.getInfo().getHomeTeamID() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
					ele.appendChild(doc.createTextNode("1"));
					heimspiel = true;
				} else {
					ele.appendChild(doc.createTextNode("0"));
					heimspiel = false;
				}

				//Teamdaten
				ele = doc.createElement("Team");
				tmpEle.appendChild(ele);

				//tmpRoot wechseln
				tmpEle = ele;

				//Details holen
				//details = (IMatchDetails) usefulMatches.get(new Integer(matchID));
				int hrfID = DBManager.instance().getHrfIDSameTraining(matchData.getInfo().getMatchSchedule().toDbTimestamp());

				//HRF ID vermerken
				ele = doc.createElement("HRFID");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + hrfID));

				MatchLineupTeam lineupTeam;
				Matchdetails details = matchData.getDetails();
				if (details.getHomeTeamId() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
					lineupTeam = DBManager.instance().loadMatchLineup(details.getMatchType().getId(), details.getMatchID()).getHomeTeam();
				} else {
					lineupTeam = DBManager.instance().loadMatchLineup(details.getMatchType().getId(), details.getMatchID()).getGuestTeam();
				}

				Team team = DBManager.instance().getTeam(hrfID);

				//Daten schreiben
				ele = doc.createElement("TeamID");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + HOVerwaltung.instance().getModel().getBasics().getTeamId()));
				ele = doc.createElement("System");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + lineupTeam.determineSystem()));
				ele = doc.createElement("Eingespieltheit");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + getTeamErfahrung(team, lineupTeam.determineSystem())));
				ele = doc.createElement("TrainerType");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + DBManager.instance().getTrainerType(hrfID)));
				ele.appendChild(doc.createComment(" 0=Defense, 2= Normal, 1=Offense Trainer, -99 NOT Found "));

				if (heimspiel) {
					ele = doc.createElement("Einstellung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeEinstellung()));
					ele = doc.createElement("Spezialtaktik");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeTacticType()));
					ele = doc.createElement("SpezialtaktikSkill");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeTacticSkill()));
					ele = doc.createElement("LeftAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeLeftAtt()));
					ele = doc.createElement("LeftDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeLeftDef()));
					ele = doc.createElement("MidAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeMidAtt()));
					ele = doc.createElement("MidDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeMidDef()));
					ele = doc.createElement("Midfield");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeMidfield()));
					ele = doc.createElement("RightAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeRightAtt()));
					ele = doc.createElement("RightDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getHomeRightDef()));
				} else {
					ele = doc.createElement("Einstellung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestEinstellung()));
					ele = doc.createElement("Spezialtaktik");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestTacticType()));
					ele = doc.createElement("SpezialtaktikSkill");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestTacticSkill()));
					ele = doc.createElement("LeftAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestLeftAtt()));
					ele = doc.createElement("LeftDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestLeftDef()));
					ele = doc.createElement("MidAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestMidAtt()));
					ele = doc.createElement("MidDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestMidDef()));
					ele = doc.createElement("Midfield");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestMidfield()));
					ele = doc.createElement("RightAtt");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestRightAtt()));
					ele = doc.createElement("RightDef");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + details.getGuestRightDef()));
				}

				ele = doc.createElement("Stimmung");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + getTeamStimmung(team)));
				ele = doc.createElement("Selbstvertrauen");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + getTeamSelbstvertrauen(team)));
				ele = doc.createElement("Erfahrung");
				tmpEle.appendChild(ele);
				ele.appendChild(doc.createTextNode("" + lineupTeam.getExperience()));

				//lineup
				Element lineupEle = doc.createElement("Lineup");
				tmpEle.appendChild(lineupEle);

				//Player schreiben
				for (var p : lineupTeam.getLineup().getAllPositions()) {
					Player playerData = matchData.getPlayers().get(((MatchLineupPosition) p).getPlayerId());

					//Bank + verletzte überspringen
					if (((MatchLineupPosition) p).getRoleId() >= IMatchRoleID.startReserves) {
						continue;
					}

					ele = doc.createElement("SpielerDaten");
					lineupEle.appendChild(ele);

					//tmpRoot wechseln
					tmpEle = ele;

					ele = doc.createElement("SpielerID");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getPlayerId()));
					ele = doc.createElement("Spezialitaet");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getPlayerSpecialty()));
					ele = doc.createElement("RoleID");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getRoleId()));
					ele = doc.createElement("Tactic");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getBehaviour()));
					ele = doc.createElement("HOPosition");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getPosition()));
					ele = doc.createElement("HTPositionCode");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getRoleId()));
					ele = doc.createElement("Bewertung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getRating()));
					ele = doc.createElement("Name");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + ((MatchLineupPosition) p).getSpielerName()));
					ele = doc.createElement("Alter");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getAlter()));
					ele = doc.createElement("AgeDays");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getAgeDays()));
					ele = doc.createElement("TSI");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getTSI()));
					ele = doc.createElement("Form");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getForm()));
					ele = doc.createElement("Kondition");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getStamina()));
					ele = doc.createElement("Erfahrung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getExperience()));
					ele = doc.createElement("Torwart");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getGKskill()));
					ele = doc.createElement("Verteidigung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getDEFskill()));
					ele = doc.createElement("Passspiel");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getPSskill()));
					ele = doc.createElement("Fluegel");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getWIskill()));
					ele = doc.createElement("Torschuss");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSCskill()));
					ele = doc.createElement("Standards");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSPskill()));
					ele = doc.createElement("Spielaufbau");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getPMskill()));
					ele = doc.createElement("SubTorwart");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.KEEPER)));
					ele = doc.createElement("SubTorwartHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.KEEPER, playerData, details.getMatchDate())));
					ele = doc.createElement("SubVerteidigung");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.DEFENDING)));
					ele = doc.createElement("SubVerteidigungHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.DEFENDING, playerData, details.getMatchDate())));
					ele = doc.createElement("SubPassspiel");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.PASSING)));
					ele = doc.createElement("SubPassspielHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.PASSING, playerData, details.getMatchDate())));
					ele = doc.createElement("SubFluegel");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.WINGER)));
					ele = doc.createElement("SubFluegelHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.WINGER, playerData, details.getMatchDate())));
					ele = doc.createElement("SubTorschuss");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.SCORING)));
					ele = doc.createElement("SubTorschussHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.SCORING, playerData, details.getMatchDate())));
					ele = doc.createElement("SubStandards");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.SET_PIECES)));
					ele = doc.createElement("SubStandardsHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.SET_PIECES, playerData, details.getMatchDate())));
					ele = doc.createElement("SubSpielaufbau");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + playerData.getSub4SkillAccurate(PlayerSkill.PLAYMAKING)));
					ele = doc.createElement("SubSpielaufbauHadLevelUp");
					tmpEle.appendChild(ele);
					ele.appendChild(doc.createTextNode("" + hadSkillup(PlayerSkill.PLAYMAKING, playerData, details.getMatchDate())));
				}
			}

			//Fertig -> saven
			String xml = XMLManager.getXML(doc);
			HOZip zip = new HOZip(filename);
			String xmlfile = HOVerwaltung.instance().getModel().getBasics().getTeamName() + ".xml";
			zip.addStringEntry(xmlfile,xml);
			zip.closeArchive();
		} catch (Exception e) {
			HOLogger.instance().log(getClass(), "pickupData.writeXML: " + e);
			HOLogger.instance().log(getClass(), e);
		}

		//        HOMiniModel.instance().getGUI ().getInfoPanel ().clearAll ();   
		JOptionPane.showMessageDialog(
			HOMainFrame.instance(), HOVerwaltung.instance().getLanguageString("xmlexport.information", matches.size()),
					HOVerwaltung.instance().getLanguageString("windowtitle.exportsuccessful"),
			javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Check for skillup.
	 */
	private String hadSkillup(int skill, Player player, HODateTime matchdate) {
		Object[] value = player.getLastLevelUp(skill);

		if ((value != null) && ((value[0] != null) && (value[1] != null))) {
			if (((Boolean) value[1]) && ((HODateTime) value[0]).isBefore(matchdate)) {
				return "1";
			}
		}

		return "0";
	}
	
	// -------------------------------- Helper -----------------------------------------------------------

	private String getRegionID4Team(int teamID) {
		if (teamID == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
			if (m_sUserRegionID.equals("-1")) {
				if (HOVerwaltung.instance().getModel().getBasics().getRegionId() > 0) {
					// Since HO 1.401, the regionId exists in Basics
					m_sUserRegionID = "" + HOVerwaltung.instance().getModel().getBasics().getRegionId();
				} else {
					//saugen
					m_sUserRegionID = MyConnector.instance().fetchRegionID(teamID);
				}
				return m_sUserRegionID;
			}
			return m_sUserRegionID;
		} 
		return MyConnector.instance().fetchRegionID(teamID);
	}

	/**
	 * Get the formation experience for the given system.
	 */
	private int getTeamErfahrung(Team team, byte system) {
		if (team == null) {
			return -1;
		}
		return switch (system) {
			case Lineup.SYS_MURKS -> -1;
			case Lineup.SYS_451 -> team.getFormationExperience451();
			case Lineup.SYS_352 -> team.getFormationExperience352();
			case Lineup.SYS_442 -> team.getFormationExperience442();
			case Lineup.SYS_343 -> team.getFormationExperience343();
			case Lineup.SYS_433 -> team.getFormationExperience433();
			case Lineup.SYS_532 -> team.getFormationExperience532();
			case Lineup.SYS_541 -> team.getFormationExperience541();
			case Lineup.SYS_523 -> team.getFormationExperience523();
			case Lineup.SYS_550 -> team.getFormationExperience550();
			case Lineup.SYS_253 -> team.getFormationExperience253();
			default -> -1;
		};
	}

	/**
	 * Get the confidence.
	 */
	private int getTeamSelbstvertrauen(Team team) {
		if (team == null) {
			return -1;
		}
		return team.getConfidence();

	}

	/**
	 * Get the team spirit.
	 */
	private int getTeamStimmung(Team team) {
		if (team == null) {
			return -1;
		}
		return team.getTeamSpirit();
	}
	
}
