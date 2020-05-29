package core.training;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.model.HOVerwaltung;
import core.model.match.*;
import core.model.player.Player;
import core.util.HOLogger;
import core.util.HTCalendar;
import core.util.HTCalendarFactory;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.swing.JOptionPane;

import static java.lang.Integer.min;

/**
 * Class that extract data from Database and calculates TrainingWeek and TrainingPoints earned from
 * players
 *
 * @author humorlos, Dragettho, thetom
 */
public class TrainingManager {
    //~ Static fields/initializers -----------------------------------------------------------------

	private static TrainingManager m_clInstance;

    //~ Instance fields ----------------------------------------------------------------------------
    private TrainingWeekManager _WeekManager;
    static final public boolean TRAININGDEBUG = false;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingManager() {
        _WeekManager = TrainingWeekManager.instance();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns a singleton TrainingManager object
     *
     * @return instance of TrainingManager
     */
    public static TrainingManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new TrainingManager();
        }
        return m_clInstance;
    }

    public List<TrainingPerWeek> getTrainingWeekList() {
        return _WeekManager.getTrainingList();
    }

    public List<TrainingPerWeek> refreshTrainingWeeks() {
        return _WeekManager.refreshTrainingList();
    }

    /**
     * Training for given player for each skill
     *
     * @param inputPlayer Player to use
     * @param train preset Trainingweeks
     * @param timestamp if not null, calculate training for this training date only
     *
     * @return TrainingPerPlayer
     */
    public TrainingPerPlayer calculateWeeklyTrainingForPlayer(Player inputPlayer,
                                                              TrainingPerWeek train, Timestamp timestamp) {
        //playerID HIER SETZEN
        final Player player = inputPlayer;
        final int playerID = player.getSpielerID();

        TrainingPerPlayer output = new TrainingPerPlayer(player);
        if (timestamp != null)
        	output.setTimestamp(timestamp);
        if (train == null || train.getTrainingType() < 0) {
            return output;
        }
        if (TRAININGDEBUG) {
        	HTCalendar htc1 = HTCalendarFactory.createTrainingCalendar();
        	HTCalendar htc2 = HTCalendarFactory.createTrainingCalendar();
        	String c1s = "";
        	String c2s = "";
        	if (timestamp != null) {
        		htc1.setTime(timestamp);
        		c1s = " ("+htc1.getHTSeason()+"."+htc1.getHTWeek()+")";
        	}
        	htc2.setTime(train.getTrainingDate());
        	c2s = " ("+htc2.getHTSeason()+"."+htc2.getHTWeek()+")";

        	HOLogger.instance().debug(getClass(),
        			"Start calcWeeklyTraining for "+ player.getFullName()+", zeitpunkt="+((timestamp!=null)?timestamp.toString()+c1s:"")
        			+ ", trainDate="+train.getTrainingDate().toString()+c2s);
        }

        Calendar trainingDate = Calendar.getInstance(Locale.UK);
        trainingDate.setTime(train.getTrainingDate());
        WeeklyTrainingType wt = WeeklyTrainingType.instance(train.getTrainingType());
        if (wt != null) {
	        try {
	        	List<Integer> matches = getOwnMatchesForTraining(trainingDate);
	        	int myID = HOVerwaltung.instance().getModel().getBasics().getTeamId();
	        	TrainingWeekPlayer tp = new TrainingWeekPlayer();
	            tp.Name(player.getFullName());
	            int minutes=0;
	        	for (int i=0; i<matches.size(); i++) {
	                final int matchId = (matches.get(i)).intValue();

	                //Get the MatchLineup by id
	                MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(matchId, myID);
	                MatchStatistics ms = new MatchStatistics(matchId, mlt);
					MatchType type = mlt.getMatchType();
					if ( type != MatchType.MASTERS) { // MASTERS counts only for experience
						tp.addPrimarySkillPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getPrimaryTrainingSkillPositions()));
						tp.addPrimarySkillBonusPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getPrimaryTrainingSkillBonusPositions()));
						tp.addPrimarySkillSecondaryPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getPrimaryTrainingSkillSecondaryTrainingPositions()));
						tp.addPrimarySkillOsmosisPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getPrimaryTrainingSkillOsmosisTrainingPositions()));
						tp.addSecondarySkillPrimaryMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getSecondaryTrainingSkillPositions()));
						tp.addSecondarySkillBonusMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getSecondaryTrainingSkillBonusPositions()));
						tp.addSecondarySkillSecondaryPositionMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getSecondaryTrainingSkillSecondaryTrainingPositions()));
						tp.addSecondarySkillOsmosisTrainingMinutes(ms.getTrainMinutesPlayedInPositions(playerID, wt.getSecondaryTrainingSkillOsmosisTrainingPositions()));
					}
					tp.addTotalMinutesPlayed(ms.getTrainMinutesPlayedInPositions(playerID, null));
					output.addExperienceIncrease(min(90,tp.getTotalMinutesPlayed() - minutes), type );
	                minutes = tp.getTotalMinutesPlayed();
				}
	            TrainingPoints trp = new TrainingPoints(wt.getPrimaryTraining(tp), wt.getSecondaryTraining(tp));

	        	// get experience increase of national matches
				if  ( player.getNationalTeamID() != 0 && player.getNationalTeamID() != myID){
					List<Integer> nationalMatches = getMatchesForTraining(player.getNationalTeamID(), trainingDate);
					for (Integer i : nationalMatches){
						MatchLineupTeam mlt = DBManager.instance().getMatchLineupTeam(i, player.getNationalTeamID());
						MatchStatistics ms = new MatchStatistics(i, mlt);
						minutes = ms.getStaminaMinutesPlayedInPositions(playerID);
						if ( minutes > 0 ) {
							output.addExperienceIncrease(min(90,minutes), mlt.getMatchType());
						}
					}
				}

	    		if (TrainingManager.TRAININGDEBUG) {
					HOLogger.instance().debug(getClass(), "Week " + train.getHattrickWeek()
	            		+": Player " + player.getFullName() + " (" + playerID + ")"
	            		+" played total " + tp.getTotalMinutesPlayed() + " mins for training purposes and got "
	            		+ wt.getPrimaryTraining(tp) + " primary training points and "
	            		+ wt.getSecondaryTraining(tp) + " secondary training points");
	    		}
	            output.setTrainingPair(trp);
	            output.setTrainingWeek(train);
	        } catch (Exception e) {
	            HOLogger.instance().log(getClass(),e);
	        }
        }
        return output;
    }
    
	/*
     * Recalculates all sub skills for all players
     *
     * @param showBar show progress bar
     */
    public void recalcSubskills(boolean showBar) {
        if (JOptionPane.showConfirmDialog(HOMainFrame.instance(),
        		HOVerwaltung.instance().getLanguageString("SubskillRecalcFull"),
				HOVerwaltung.instance().getLanguageString("ls.menu.file.subskillrecalculation"), JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
            HOVerwaltung.instance().recalcSubskills(showBar, null);
        }
    }

    //----------------------------------- Utility Methods ----------------------------------------------------------

	public List<Integer> getOwnMatchesForTraining (Calendar trainingDate) {
		final int teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
		return getMatchesForTraining(teamId, trainingDate);
	}

    /**
     * Creates a list of matches for the specified training
     *
	 * @param teamId	own team Id or national team Id
	 * @param trainingDate	use this trainingDate
     * @return	list of matchIds (type Integer)
     */
    public List<Integer> getMatchesForTraining (int teamId, Calendar trainingDate) {
        List<Integer> matches = new ArrayList<Integer>();
        try {
        	final ResultSet matchRS = DBManager.instance().getAdapter().executeQuery(createQuery(teamId, trainingDate));

        	if (matchRS == null) {
        		// in case of no return values
        		return matches;
        	}

        	while (matchRS.next()) {
        		final int matchId = matchRS.getInt("MATCHID");
        		matches.add(new Integer(matchId));
        	}

            matchRS.close();
        } catch (Exception e1) {
            HOLogger.instance().log(getClass(),e1);
        }

        return matches;
    }

    /**
     * Creates the query to extract the list of matchId for each Training
     *
     * @param calendar TrainingDate
     *
     * @return the query
     */
    private String createQuery(int teamId, Calendar calendar) {
		final Timestamp ts = new Timestamp(calendar.getTimeInMillis());
		final Calendar old = (Calendar) calendar.clone();

		// set time one week back
		old.add(Calendar.WEEK_OF_YEAR, -1);

		final Timestamp ots = new Timestamp(old.getTimeInMillis());
		final String sdbquery = "SELECT MATCHID FROM MATCHESKURZINFO WHERE " + "( HEIMID=" + teamId
				+ " OR GASTID=" + teamId + " ) " + "AND MatchDate BETWEEN '"
				+ ots.toString() + "' AND '" + ts.toString() + "' "
				+ " AND (MatchTyp=" + MatchType.QUALIFICATION.getId()
				+ " OR MatchTyp=" + MatchType.LEAGUE.getId()
				+ " OR MatchTyp=" + MatchType.CUP.getId()
				+ " OR MatchTyp=" + MatchType.FRIENDLYNORMAL.getId()
				+ " OR MatchTyp=" + MatchType.FRIENDLYCUPRULES.getId()
				+ " OR MatchTyp=" + MatchType.INTFRIENDLYCUPRULES.getId()
				+ " OR MatchTyp=" + MatchType.INTFRIENDLYNORMAL.getId()
				+ " OR MatchTyp=" + MatchType.EMERALDCUP.getId()
				+ " OR MatchTyp=" + MatchType.RUBYCUP.getId()
				+ " OR MatchTyp=" + MatchType.SAPPHIRECUP.getId()
				+ " OR MatchTyp=" + MatchType.CONSOLANTECUP.getId()
				+ " OR MatchTyp=" + MatchType.MASTERS.getId()				// masters and national team is needed for experience sub
				+ " OR MatchTyp=" + MatchType.NATIONALCOMPCUPRULES.getId()
				+ " OR MatchTyp=" + MatchType.NATIONALCOMPNORMAL.getId()
				+ " OR MatchTyp=" + MatchType.NATIONALFRIENDLY.getId()
				+ " ) AND STATUS=" + MatchKurzInfo.FINISHED
				+ " ORDER BY MatchDate DESC";

		return sdbquery;
	}
}
