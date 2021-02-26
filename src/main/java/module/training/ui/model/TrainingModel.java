package module.training.ui.model;

import core.db.DBManager;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.UserParameter;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.training.WeeklyTrainingType;
import core.util.HOLogger;
import core.util.HTDatetime;
import module.training.PastTrainingManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TrainingModel {

	/** The currently selected player */
	private Player activePlayer;
	private int numberOfCoTrainers;
	/** the current level of the coach */
	private int trainerLevel;
	private StaffMember staffMember = new StaffMember();
	private  List<StaffMember> staff = new ArrayList<>();
	private List<TrainingPerWeek> futureTrainings;
	private PastTrainingManager skillupManager;
	private FutureTrainingManager futureTrainingManager;
	private final List<ModelChangeListener> listeners = new ArrayList<>();

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player player) {
		if ((this.activePlayer == null && player != null)
				|| (this.activePlayer != null && player == null)
				|| (this.activePlayer != null && player != null && this.activePlayer.getPlayerID() != player
						.getPlayerID())) {
			this.activePlayer = player;
			this.skillupManager = null;
			resetFutureTrainings_();
			fireModelChanged(ModelChange.ACTIVE_PLAYER);
		}
	}

	public int getNumberOfCoTrainers() {
		return numberOfCoTrainers;
	}
	
	public List<StaffMember> getAssistants() {
		return staff;
	}

	public void setNumberOfCoTrainers(int numberOfCoTrainers) {
		if (this.numberOfCoTrainers != numberOfCoTrainers) {
			this.numberOfCoTrainers = numberOfCoTrainers;
			// create dummy staff for future training calculations
			if(staff.isEmpty()) {
				staffMember.setStaffType(StaffType.ASSISTANTTRAINER);
				staffMember.setLevel(this.numberOfCoTrainers);
				staff.add(staffMember);
				} else {
			staff.get(0).setLevel(numberOfCoTrainers);
				}
			resetFutureTrainings_();
			fireModelChanged(ModelChange.NUMBER_OF_CO_TRAINERS);
		}
	}

	public int getTrainerLevel() {
		return trainerLevel;
	}

	public void setTrainerLevel(int trainerLevel) {
		if (this.trainerLevel != trainerLevel) {
			this.trainerLevel = trainerLevel;
			resetFutureTrainings_();
			fireModelChanged(ModelChange.TRAINER_LEVEL);
		}
	}

	public PastTrainingManager getSkillupManager() {
		if (this.skillupManager == null) {
			this.skillupManager = new PastTrainingManager(this.activePlayer);
		}
		return this.skillupManager;
	}

	public List<TrainingPerWeek> getFutureTrainings() {
		if (futureTrainings == null) {
			var _futureTrainings = DBManager.instance().getFutureTrainingsVector();

			// remove old entries and add new to make sure the vector size match user preference settings
			_futureTrainings = adjustFutureTrainingsVector(_futureTrainings, UserParameter.instance().futureWeeks);

			DBManager.instance().clearFutureTrainingsTable();
			DBManager.instance().saveFutureTrainings(_futureTrainings);
			futureTrainings = _futureTrainings;
		}
		return futureTrainings;
	}

	public void saveFutureTrainings(List<TrainingPerWeek> trainings) {
		boolean needsReload = false;
		for (TrainingPerWeek training : trainings) {
			DBManager.instance().saveFutureTraining(training);
			if (!getFutureTrainings().contains(training)) {
				needsReload = true;
			}
		}

		if (needsReload) {
			this.futureTrainings = null;
		}
		fireModelChanged(ModelChange.FUTURE_TRAINING);
	}

	public void addModelChangeListener(ModelChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public FutureTrainingManager getFutureTrainingManager() {
		
		if (this.futureTrainingManager == null) {
			
			// gets the list of future trainings
			List<TrainingPerWeek> trainings = getFutureTrainings();

			// instantiate a future train manager to calculate the previsions */
			this.futureTrainingManager = new FutureTrainingManager(this.activePlayer, trainings);
		}
		return this.futureTrainingManager;
	}
	
	public void resetFutureTrainings() {
		resetFutureTrainings_();
		fireModelChanged(ModelChange.FUTURE_TRAINING);
	}
	
	private void resetFutureTrainings_() {
		this.futureTrainings = null;
		this.futureTrainingManager = null;
	}

	private void fireModelChanged(ModelChange change) {
		for (int i = listeners.size() - 1; i >= 0; i--) {
			listeners.get(i).modelChanged(change);
		}
	}

	// full training: Primary Training Position && ! set pieces || Bonus Position (&& set pieces)
	// partial training: Secondary Training Position || primary Training Position && set pieces)
	public boolean isPartialTrainingAvailable(int[] weeks) {
		for ( var w : weeks){
			var t = getFutureTrainings().get(w);
			if ( isPartialTrainingAvailable(t)) return true;
		}
		return false;
	}

    public boolean isOsmosisTrainingAvailable(int[] weeks) {
		for ( var w : weeks){
			var t = getFutureTrainings().get(w);
			if ( isOsmosisTrainingAvailable(t)) return true;
		}
		return false;
    }

	public boolean isPartialTrainingAvailable() {
		for ( var t : getFutureTrainings()){
			if ( isPartialTrainingAvailable(t)) return true;
		}
		return false;
	}

	private boolean isPartialTrainingAvailable(TrainingPerWeek t) {
		var tt = WeeklyTrainingType.instance(t.getTrainingType());
		return tt.getTrainingSkillPartlyTrainingPositions().length > 0 ||
				tt.getTrainingSkillBonusPositions().length > 0;
	}

	public boolean isOsmosisTrainingAvailable() {
		for ( var t : getFutureTrainings()){
			if ( isOsmosisTrainingAvailable(t)) return true;
		}
		return false;
	}

	private boolean isOsmosisTrainingAvailable(TrainingPerWeek t) {
		return WeeklyTrainingType.instance(t.getTrainingType()).getTrainingSkillOsmosisTrainingPositions().length > 0;
	}

	private List<TrainingPerWeek> adjustFutureTrainingsVector(List<TrainingPerWeek> _futureTrainings,int requiredNBentries) {
		Instant nextTrainingDate = TrainingManager.instance().getNextWeekTraining().getTrainingDate();
		Optional<TrainingPerWeek> optionallastTraining = _futureTrainings.stream().max(Comparator.comparing(TrainingPerWeek::getTrainingDate));
		List<TrainingPerWeek> newfutureTrainings = new ArrayList<>();

		if (optionallastTraining.isPresent()) {
			// removal of old entries
			for (var entry : _futureTrainings) {
				if (!entry.getTrainingDate().isBefore(nextTrainingDate)) {
					newfutureTrainings.add(entry);
					if(newfutureTrainings.size() == requiredNBentries){
						break;
					}
				}
			}

			if(newfutureTrainings.size() < requiredNBentries) {
				// Adding new entries
				TrainingPerWeek latestTraining = optionallastTraining.get();
				int nbWeek = 1;
				ZonedDateTime zdtFutureTrainingDate;

				HTDatetime oTrainingDate = new HTDatetime(latestTraining.getTrainingDate());
				ZonedDateTime zdtrefDate = oTrainingDate.getHattrickTime();
				TrainingPerWeek futureTraining;

				while (newfutureTrainings.size() < requiredNBentries) {
					zdtFutureTrainingDate = zdtrefDate.plus(nbWeek * 7, ChronoUnit.DAYS);
					futureTraining = new TrainingPerWeek(zdtFutureTrainingDate.toInstant(), latestTraining.getTrainingType(), latestTraining.getTrainingIntensity(),
							latestTraining.getStaminaShare(), latestTraining.getTrainingAssistantsLevel(), latestTraining.getCoachLevel());
					newfutureTrainings.add(futureTraining);
					nbWeek++;
				}
			}

		}
		else {
			HOLogger.instance().error(getClass(), "Can't create new entries in FutureTrainings table");
		}

		return newfutureTrainings;

	}
}
