package module.training.ui.model;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.enums.DBDataSource;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.TrainingPerWeek;
import core.training.WeeklyTrainingType;
import module.training.PastTrainingManager;

import java.util.ArrayList;
import java.util.List;

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
			futureTrainings = DBManager.instance().getFutureTrainingsVector();
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
			this.futureTrainingManager = new FutureTrainingManager(this.activePlayer, trainings,
					this.numberOfCoTrainers, this.trainerLevel, this.staff);
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
		//this.staff.clear();
	}

	private void fireModelChanged(ModelChange change) {
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).modelChanged(change);
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
}
