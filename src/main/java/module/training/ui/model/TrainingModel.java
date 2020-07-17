package module.training.ui.model;

import core.constants.TrainingType;
import core.db.DBManager;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.TrainingPerWeek;
import core.training.TrainingWeekManager;
import core.training.WeeklyTrainingType;
import module.training.OldTrainingManager;

import java.util.ArrayList;
import java.util.List;

public class TrainingModel {

	/** The currently selected player */
	private Player activePlayer;
	private int numberOfCoTrainers;
	/** the current level of the coach */
	private int trainerLevel;
	private StaffMember staffMember = new StaffMember();
	private  List<StaffMember> staff = new ArrayList<StaffMember>();
	private List<TrainingPerWeek> futureTrainings;
	private OldTrainingManager skillupManager;
	private FutureTrainingManager futureTrainingManager;
	private final List<ModelChangeListener> listeners = new ArrayList<ModelChangeListener>();

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player player) {
		if ((this.activePlayer == null && player != null)
				|| (this.activePlayer != null && player == null)
				|| (this.activePlayer != null && player != null && this.activePlayer.getSpielerID() != player
						.getSpielerID())) {
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

	public OldTrainingManager getSkillupManager() {
		if (this.skillupManager == null) {
			this.skillupManager = new OldTrainingManager(this.activePlayer);
		}
		return this.skillupManager;
	}

	public List<TrainingPerWeek> getFutureTrainings() {
		if (this.futureTrainings == null) {
			this.futureTrainings = new ArrayList<TrainingPerWeek>();
			Object[] aobj;

			TrainingPerWeek oldTrain = null;
			List<TrainingPerWeek> futureTrainings = DBManager.instance().getFutureTrainingsVector();
			List<TrainingPerWeek> futureTrainingsToSave = new ArrayList<TrainingPerWeek>();

			for (TrainingPerWeek training : futureTrainings) {

				// if not found create it and saves it
				if (training.getTrainingIntensity() == -1) {
					if (oldTrain != null) {
						training.setTrainingIntensity(oldTrain.getTrainingIntensity());
						training.setStaminaPart(oldTrain.getStaminaPart());
						training.setTrainingType(oldTrain.getTrainingType());
					} else {
						training.setTrainingIntensity(100);
						training.setStaminaPart(5);
						training.setTrainingType(TrainingType.SET_PIECES);
					}
					futureTrainingsToSave.add(training);
				}

				this.futureTrainings.add(training);
				oldTrain = training;
			}

			if (!futureTrainingsToSave.isEmpty()) {
				saveFutureTrainings(futureTrainingsToSave);
			}
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

	public void saveFutureTraining(TrainingPerWeek training) {
		DBManager.instance().saveFutureTraining(training);
		if (!getFutureTrainings().contains(training)) {
			this.futureTrainings = null;
		}
		fireModelChanged(ModelChange.FUTURE_TRAINING);
	}

	public void addModelChangeListener(ModelChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeModelChangeListener(ModelChangeListener listener) {
		this.listeners.remove(listener);
	}

	public FutureTrainingManager getFutureTrainingManager() {
		
		if (this.futureTrainingManager == null) {
			
			// gets the list of user defined future trainings
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

	public boolean isPartialTrainingAvailable(int[] weeks) {
		for ( var w : weeks){
			var t = getFutureTrainings().get(w);
			if (WeeklyTrainingType.instance(t.getTrainingType()).getPrimaryTrainingSkillSecondaryTrainingPositions().length > 0 ) {
				return true;
			}
		}
		return false;
	}

    public boolean isOsmosisTrainingAvailable(int[] weeks) {
		for ( var w : weeks){
			var t = getFutureTrainings().get(w);
			if (WeeklyTrainingType.instance(t.getTrainingType()).getPrimaryTrainingSkillOsmosisTrainingPositions().length > 0 ) {
				return true;
			}
		}
		return false;
    }
}
