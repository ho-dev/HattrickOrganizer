package module.training.ui.model;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.DBDataSource;
import core.model.player.Player;
import core.training.FutureTrainingManager;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.training.WeeklyTrainingType;
import core.util.HODateTime;
import core.util.HOLogger;
import module.training.PastTrainingManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

public class TrainingModel implements PropertyChangeListener {


	private Player activePlayer;
	private List<TrainingPerWeek> futureTrainings;
	private PastTrainingManager skillupManager;
	private FutureTrainingManager futureTrainingManager;
	private final List<ModelChangeListener> listeners = new ArrayList<>();

	public TrainingModel(){
		HOVerwaltung.instance().addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		futureTrainings = null;
	}

	public Player getActivePlayer() {
		return activePlayer;
	}

	public void setActivePlayer(Player player) {
		if (this.activePlayer == null && player != null ||
				this.activePlayer != null && player == null ||
				this.activePlayer != null && this.activePlayer.getPlayerID() != player.getPlayerID()) {
			this.activePlayer = player;
			this.skillupManager = null;
			resetFutureTrainings_();
			fireModelChanged(ModelChange.ACTIVE_PLAYER);
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
			List<TrainingPerWeek> adjustedFutureTrainings = adjustFutureTrainingsVector(_futureTrainings, UserParameter.instance().futureWeeks);
			DBManager.instance().saveFutureTrainings(adjustedFutureTrainings);
			futureTrainings = adjustedFutureTrainings;
		}
		return futureTrainings;
	}

	public void saveFutureTrainings(List<TrainingPerWeek> trainings) {
		for (TrainingPerWeek training : trainings) {
			DBManager.instance().saveFutureTraining(training);
		}
		futureTrainings = null; //force reload
		fireModelChanged(ModelChange.FUTURE_TRAINING);
	}

	public void saveFutureTraining(TrainingPerWeek training) {
		DBManager.instance().saveFutureTraining(training);
		futureTrainings = null; //force reload
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
		if ( tt != null ) return tt.getTrainingSkillPartlyTrainingPositions().length > 0 ||
				tt.getTrainingSkillBonusPositions().length > 0;
		return false;
	}

	public boolean isOsmosisTrainingAvailable() {
		for ( var t : getFutureTrainings()){
			if ( isOsmosisTrainingAvailable(t)) return true;
		}
		return false;
	}

	private boolean isOsmosisTrainingAvailable(TrainingPerWeek t) {
		var tt = WeeklyTrainingType.instance(t.getTrainingType());
		if ( tt != null ) return tt.getTrainingSkillOsmosisTrainingPositions().length > 0;
		return false;
	}

	private List<TrainingPerWeek> adjustFutureTrainingsVector(List<TrainingPerWeek> _futureTrainings, int requiredNBentries) {
		List<TrainingPerWeek> newfutureTrainings = new ArrayList<>();
		TrainingPerWeek previousTraining = TrainingManager.instance().getNextWeekTraining();
		HOLogger.instance().debug(TrainingModel.class, "Previous training date: " + previousTraining);

		if (previousTraining != null) {
			var trainingDate = previousTraining.getTrainingDate();
			//ZonedDateTime zdtrefDate = oTrainingDate.getHattrickTime();

			int nbWeek = 1;
			HODateTime futureTrainingDate;
			TrainingPerWeek futureTraining;

			while (newfutureTrainings.size() < requiredNBentries) {

				//first iteration equals to nextWeek training then increase per one week per iteration
				futureTrainingDate = trainingDate.plusDaysAtSameLocalTime(nbWeek*7);

				//ZonedDateTime finalZdtFutureTrainingDate = zdtFutureTrainingDate;
				HODateTime finalFutureTrainingDate = futureTrainingDate;
				var oFutureTraining = _futureTrainings.stream().filter(t -> finalFutureTrainingDate.equals(t.getTrainingDate())).findFirst();

				if (oFutureTraining.isPresent()) {
					// training present in Future Training table => we keep it
					futureTraining = oFutureTraining.get();
				} else {
					// training not present in Future Training table => we create a new one from previous training
					futureTraining = new TrainingPerWeek(futureTrainingDate, previousTraining.getTrainingType(), previousTraining.getTrainingIntensity(),
							previousTraining.getStaminaShare(), previousTraining.getTrainingAssistantsLevel(), previousTraining.getCoachLevel(), DBDataSource.GUESS);
				}

				newfutureTrainings.add(futureTraining);
				previousTraining = futureTraining;

				nbWeek++;
			}
		}
		return newfutureTrainings;
	}
}
