package core.training;

import core.db.DBManager;
import core.db.JDBCAdapter;
import core.model.HOModel;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import core.util.HOLogger;
import core.util.HelperWrapper;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;


/**
 * Class that create a list of TrainingPerWeek objects between 2 dates
 *
 */
public class TrainingWeekManager {

	private static final Instant m_NextTrainingDate = HOVerwaltung.instance().getModel().getXtraDaten().getTrainingDate().toInstant();

    private List<TrainingPerWeek> m_Trainings;
	private Instant m_StartDate;
	private Instant m_EndDate;
	private Boolean m_IncludeUpcomingGames;


	@Deprecated
	private static TrainingWeekManager m_clInstance;

	/**
	 * Constructor (both dates are included)
	 */
	public TrainingWeekManager(Instant startDate, Instant endDate) {
		this(startDate, endDate, false);
	}

	/**
	 * Constructor (both dates are included)
	 */
	public TrainingWeekManager(Instant startDate, Instant endDate, boolean includeUpcomingGames) {
		m_StartDate = startDate;
		m_EndDate = endDate;
		m_IncludeUpcomingGames = includeUpcomingGames;
		m_Trainings = computeTrainingList();
	}

	/**
	 * Create the list of trainings, by getting information from the database and completing the missing values
	 */
	private List<TrainingPerWeek> computeTrainingList(){

		List<TrainingPerWeek>  trainings = new ArrayList<>();
		HashMap<Instant, TrainingPerWeek>  trainingsInDB = fetchTrainingListFromDB();
		int trainingsSize;

		if (m_StartDate.isAfter(m_NextTrainingDate)){
			HOLogger.instance().error(this.getClass(), "It was assumed that start date will always be before next training date in database");
			return trainings;
		}

		long nbWeeks = ChronoUnit.DAYS.between(m_StartDate, m_NextTrainingDate) / 7;

		Instant currDate = m_NextTrainingDate.minus(nbWeeks * 7, ChronoUnit.DAYS);

		while((currDate.isBefore(m_EndDate) || currDate.equals(m_EndDate))){
			if (trainingsInDB.containsKey(currDate)){
				trainings.add(trainingsInDB.get(currDate));
			}
			else{
				trainingsSize = trainings.size();
				if(trainingsSize != 0)
				{
					var previousTraining = trainings.get(trainingsSize - 1);
					trainings.add(previousTraining);
				}
				else{
					var tpw = new TrainingPerWeek(currDate, -1, 0, 0, 0);
					trainings.add(tpw);
				}
			}

			currDate = currDate.plus(7, ChronoUnit.DAYS);
		}


		return trainings;

	}


	/**
	 * Fetch all trainings information from database
	 */
	private HashMap<Instant, TrainingPerWeek> fetchTrainingListFromDB() {

		HashMap<Instant, TrainingPerWeek> output = new HashMap<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.from(ZoneOffset.UTC));
		String startDate = formatter.format(m_StartDate);
		String endDate = formatter.format(m_EndDate.plus(1, ChronoUnit.DAYS));
		String sql = String.format("""
					SELECT TRAININGDATE, TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, COTRAINER
					FROM XTRADATA
					INNER JOIN TEAM on XTRADATA.HRF_ID = TEAM.HRF_ID
					INNER JOIN VEREIN on XTRADATA.HRF_ID = VEREIN.HRF_ID
					INNER JOIN (
					     SELECT TRAININGDATE, max(HRF_ID) MAX_HR_ID FROM XTRADATA GROUP BY TRAININGDATE
					) IJ1 ON XTRADATA.HRF_ID = IJ1.MAX_HR_ID
					WHERE XTRADATA.TRAININGDATE >= '%s' AND XTRADATA.TRAININGDATE < '%s'""",startDate, endDate);


		int trainType, trainIntensity, trainStaminaPart, hrfId, trainingLevel;
		Instant trainingDate;


		try {

			final JDBCAdapter ijdbca = DBManager.instance().getAdapter();
			final ResultSet rs = ijdbca.executeQuery(sql);
			rs.beforeFirst();

			while (rs.next()) {
				trainType = rs.getInt("TRAININGSART");
				trainIntensity = rs.getInt("TRAININGSINTENSITAET");
				trainStaminaPart = rs.getInt("STAMINATRAININGPART");
				trainingDate = rs.getTimestamp("TRAININGDATE").toInstant();
				trainingLevel = rs.getInt("COTRAINER");
				TrainingPerWeek tpw = new TrainingPerWeek(trainingDate, trainType, trainIntensity, trainStaminaPart, trainingLevel);
				output.put(trainingDate, tpw);
			}
		}
		catch (Exception e) {
			HOLogger.instance().error(this.getClass(), "Error while performing fetchTrainingListFromDB():  " + e);
		}

		return output;
	}


	@Deprecated
    /**
     * Creates a new instance of TrainingsManager
     */
    private TrainingWeekManager() {
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Returns the instance of TrainingWeekManager.
     * 
     * @return instance of TrainingWeekManager
     */
	@Deprecated
    public static TrainingWeekManager instance() {
        if (m_clInstance == null) {
            m_clInstance = new TrainingWeekManager();
        }

        return m_clInstance;
    }

    
    /**
     * returns the current Training List
     *
     * @return Training List, List of TrainingPerWeek
     */
    public List<TrainingPerWeek> getTrainingList() {
    	if (m_Trainings == null) {
    		m_Trainings = generateTrainingList();
    	}
    	return m_Trainings;
    }

	public static Instant getNextTrainingDate() {
		return m_NextTrainingDate;
	}

    
    /**
     * Forces a new training list to be generated, and returns the result.
     * 
     * @return The list of Team Training Weeks. 
     */
	@Deprecated
    public List<TrainingPerWeek> refreshTrainingList() {
    	m_Trainings = generateTrainingList();
    	return m_Trainings;
    }


	@Deprecated
    public TrainingPerWeek getLastTrainingWeek(){
    	var list = getTrainingList();
    	if ( list.size()>0){
    		return list.get(list.size()-1);
		}
    	return null;
	}
    
    /** Returns a list of TraingPerWeek, one for each week since the first hrf.
     * 
     * @return The list of TrainingPerWeek.
     */
	@Deprecated
    private static List<TrainingPerWeek> generateTrainingList() {
    	
    	List<TrainingPerWeek> output = fetchTrainingListFromHrf();
    	if (output != null) {
	    	output = washTrainingList(output);
	    	output = updateWithOverrides(output, DBManager.instance().getTrainingOverrides());
	    	output = updateHattrickDates(output);
    	}
    	return output;
    }
    
    
    /** Adjusts the content of items in trainingList where an item in overrides is present for the same
     *  year and week. Apart from this the return is trainingList. Only week and year fields are used from the overrides,
     *  in addition to training settings.
     * 
     *  Both lists are assumed to be sorted on date, with the first training date first, but overrides does not have
     *  to include every training, and can be just a list of a few differences. 
     * 
     * @param trainingList A sorted list of all trainingWeeks.
     * @param overrides A list of overrides for values in the trainingList.
     * @return the adjusted trainingList.
     */
	@Deprecated
    private static List<TrainingPerWeek> updateWithOverrides (List<TrainingPerWeek> trainingList
    																, List<TrainingPerWeek> overrides) {
    	// This will break badly if they are not sorted with first date first.
    	
    	// do for each override.
    	for (TrainingPerWeek over : overrides) {
    		// Search forwards through the trainingList, continuing from where we left off.
    		 for (TrainingPerWeek tpw : trainingList) {
    			if ((tpw != null) 
    					&& (tpw.getWeek() == over.getWeek())
    					&& (tpw.getYear() == over.getYear())) {
    				tpw.setStaminaPart(over.getStaminaPart());
    				tpw.setTrainingIntensity(over.getTrainingIntensity());
    				tpw.setTrainingType(over.getTrainingType());
    				break;
    			}
    		}
    	}
    	
    	return trainingList;
    }

    
    /** This one filters the list so that there is one per week.
     *  The training data of a week is the content of the first hrf of that training week.
     *  This is not the correct final result, but will be corrected when hattrick weeks are updated.
     *  
     *  Objects in the input are included in the output, and will be modified by this function.
     * 
     * @param input list to be filtered
     * @return the filtered list
     */
	@Deprecated
    private static List<TrainingPerWeek> washTrainingList(List<TrainingPerWeek> input) {
    	
    	ArrayList<TrainingPerWeek> output = new ArrayList<TrainingPerWeek>();
    	TrainingPerWeek old = null;
    	Calendar previousTraining = Calendar.getInstance(Locale.US);
    	previousTraining.setFirstDayOfWeek(Calendar.SUNDAY);
    	
    	Calendar actualTraining = Calendar.getInstance(Locale.US);
    	actualTraining.setFirstDayOfWeek(Calendar.SUNDAY);
    	
    	for (TrainingPerWeek tpw: input) {
    		
    		if (tpw == null) {
    			continue;
    		}
    		
    		if (old == null) {
    			// first item
    			old = tpw;
    			Calendar cal = Calendar.getInstance(Locale.US);
    			cal.setFirstDayOfWeek(Calendar.SUNDAY);
    			cal.setTime(tpw.getNextTrainingDate());
    			cal.add(Calendar.WEEK_OF_YEAR, -1);
    			tpw.setTrainingDate(new Timestamp(cal.getTimeInMillis()));
    			output.add(tpw);
    	
    			previousTraining.setTime(tpw.getNextTrainingDate());
    			continue;
    		}
    		
    		actualTraining.setTime(tpw.getNextTrainingDate());
    		
    		if (!actualTraining.after(previousTraining)) {
    			// The same week, we ignore this one
    			continue;
    		}
    		
   			while (actualTraining.after(previousTraining)) {
   				
   				// Advance the previous date one week.
   				previousTraining.add(Calendar.WEEK_OF_YEAR, 1);
   				
   				if (actualTraining.after(previousTraining)) {
   					// We have a skipped week. Create a new item
   					
   			       int trainWeek = previousTraining.get(Calendar.WEEK_OF_YEAR);
	               int trainYear= previousTraining.get(Calendar.YEAR);

	               if (old == null) {
	            	   // Should never, ever, happen, but makes the compiler shut up.
	            	   continue;
	               }
	               
	               TrainingPerWeek newTpw = new TrainingPerWeek(trainWeek, trainYear
	            		   						, old.getTrainingType()
	            		   						, old.getTrainingIntensity()
	            		   						, old.getStaminaPart());
   					newTpw.setNextTrainingDate(new Timestamp(previousTraining.getTimeInMillis()));
   					newTpw.setHrfId(-1);
   					newTpw.setTrainingDate(old.getNextTrainingDate());
   					newTpw.setO_TrainingAssistantsLevel(old.getTrainingAssistantsLevel());

   					var newWeek = old.getHattrickDate();
   					if ( newWeek != null ) {
						newWeek.addWeeks(1);
						newTpw.setHattrickDate(newWeek);
					}
   					old = newTpw;
   					output.add(newTpw);
   					// The previous date is already set at the start of loop.
   					
   				} else {
   					// We found our current date, add this object. 
   					// The previousDate is correct already.
   					tpw.setTrainingDate(old.getNextTrainingDate());
   					old = tpw;
   					output.add(tpw);
   				}
   			}     				
    	}
    	
    	return output;
    }
    
    /**
     * Fetches a TrainingPerWeek for each hrf in the database. This could be 0, 1, or more per week.
     * 
     * @return A list of TrainingPerWeek
     */
	@Deprecated
    private static List<TrainingPerWeek> fetchTrainingListFromHrf() {
    	try {
    		HOModel model = HOVerwaltung.instance().getModel();
    		Timestamp currentTraining = model.getXtraDaten().getTrainingDate();
    		Basics basics = model.getBasics();
    		List<TrainingPerWeek> output = new ArrayList<TrainingPerWeek>();
    		String sql = " SELECT TRAININGSART, TRAININGSINTENSITAET, STAMINATRAININGPART, " +
    					"HRF_ID, DATUM, TRAININGDATE, COTRAINER FROM HRF,TEAM,XTRADATA,VEREIN WHERE " + 
    					"HRF.HRF_ID=TEAM.HRF_ID and HRF.HRF_ID=XTRADATA.HRF_ID and HRF.HRF_ID=VEREIN.HRF_ID " +
    					"ORDER BY DATUM";
    	
	
	         int lastTrainWeek = -1;
	
	         boolean isFirstTrain = true;
	         int trainIntensity = 0;
	         int trainStaminaTrainPart = 0;
	         int hrfId = 0;
	         int trainType = 0;
	         int assistants = 0;
	         Timestamp tStamp = null;
	         Calendar calendar = null;
	         int trainWeek = 0;
	         int trainYear = 0;
	         int weeks = 0;
	         Timestamp nextTraining = null;
	         
	         final JDBCAdapter ijdbca = DBManager.instance().getAdapter();
	         final ResultSet rs = ijdbca.executeQuery(sql);
	         rs.beforeFirst();
	         
	         while (rs.next()) {
	        	 trainType = rs.getInt("TRAININGSART");
	             if (trainType != -1) {
	                 trainIntensity = rs.getInt("TRAININGSINTENSITAET");
	                 trainStaminaTrainPart = rs.getInt("STAMINATRAININGPART");
	                 hrfId = rs.getInt("HRF_ID");
	                 tStamp = rs.getTimestamp("DATUM");
	                 nextTraining = rs.getTimestamp("TRAININGDATE");
	                 assistants = rs.getInt("COTRAINER");
	                 
	                 calendar = HelperWrapper.instance().getLastTrainingDate(new Date(tStamp
                             .getTime()), currentTraining);
	                 
	                 trainWeek = calendar.get(Calendar.WEEK_OF_YEAR);
	                 trainYear = calendar.get(Calendar.YEAR);
	                 
	                 TrainingPerWeek tpw = new TrainingPerWeek(trainWeek, trainYear, 
	                		 					trainType, trainIntensity, trainStaminaTrainPart);
	                 tpw.setTrainingDate(new Timestamp(calendar.getTimeInMillis()));
	                 tpw.setNextTrainingDate(nextTraining);
	                 tpw.setHrfId(hrfId);
	                 tpw.setO_TrainingAssistantsLevel(assistants);
	                 output.add(tpw);
	             }
	         }
         
	         return output;
    	} catch (Exception e) {
	        HOLogger.instance().log(null,e);
		}
         
    	return null;
    }
    
    
    /**
     * Updates the input list objects with hattrick season, hattrick week, and previousHrf.
     *
     * @param input A list of TrainingPerWeek. Sorted on date.
     *
     * @return The input list.
     */
	@Deprecated
    private static List<TrainingPerWeek> updateHattrickDates(List<TrainingPerWeek> input) {
    	
    	HOModel hom = HOVerwaltung.instance().getModel();
    	Basics bas = hom.getBasics();
        int actualSeason = bas.getSeason();
        int actualWeek = bas.getSpieltag();
        int trainNumber = input.size();
        try {
            // We are between the training and the match date, and should increase the week by 1.
            if (hom.getXtraDaten().getTrainingDate() != null &&
					hom.getXtraDaten().getTrainingDate().after(hom.getXtraDaten().getSeriesMatchDate())) {
                actualWeek++;
                if (actualWeek == 17) {
                    actualWeek = 1;
                    actualSeason++;
                }
            }
        } catch (Exception e) {
        	HOLogger.instance().log(null,"TrainingsWeekManager.updateHattrickDates: " + e);
            return input;
        }
        TrainingPerWeek train = null;
        HattrickDate htDate = null;
        for (int index = 0; index < trainNumber; index++) {
            train = input.get(index);
            htDate = calculateByDifference(actualSeason, actualWeek, trainNumber - index);
            train.setHattrickDate(htDate);
            train.setHrfId(train.getHrfId());
            train.setPreviousHrfId(DBManager.instance().getPreviousHRF(train.getHrfId()));
         }

        return input;
    }

    
    /**
     * Method that calculate the HT date for past week before
     *
     * @param actualSeason Actual Hattrick Season
     * @param actualWeek Actual Hattrick Week
     * @param pastWeek How many week to go back
     *
     * @return Hattrick Date
     */
	@Deprecated
    private static HattrickDate calculateByDifference(int actualSeason, int actualWeek, int pastWeek) {

        // We need to subtract 1 week because we got the first hrf after download. This contains
        // the training info for the previous week.
        actualWeek = (actualWeek - pastWeek) - 1;

        if (actualWeek <= 0) {
            final int a = Math.abs(actualWeek / 16) + 1;
            actualSeason = actualSeason - a;
            actualWeek = actualWeek + (a * 16);
        }
        var s = actualSeason + (actualWeek / 16);
        var w = (actualWeek % 16) + 1;

        return new HattrickDate(s,w);
    }
}
