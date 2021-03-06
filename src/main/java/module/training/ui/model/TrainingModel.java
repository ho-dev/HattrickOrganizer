package module.training.ui.model;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.StaffMember;
import core.model.StaffType;
import core.model.UserParameter;
import core.model.enums.DBDataSource;
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


	private Player activePlayer;
	private List<TrainingPerWeek> futureTrainings;
	private PastTrainingManager skillupManager;
	private FutureTrainingManager futureTrainingManager;
	private final List<ModelChangeListener> listeners = new ArrayList<>();
	private Instant nextTrainingDate;

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

	public PastTrainingManager getSkillupManager() {
		if (this.skillupManager == null) {
			this.skillupManager = new PastTrainingManager(this.activePlayer);
		}
		return this.skillupManager;
	}

	public List<TrainingPerWeek> getFutureTrainings() {
		var homodel = HOVerwaltung.instance().getModel();
		if (futureTrainings == null ||
				homodel.getBasics().getDatum().toInstant().isAfter(this.nextTrainingDate)) { // download happened
			this.nextTrainingDate = homodel.getXtraDaten().getNextTrainingDate().toInstant();
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

	private List<TrainingPerWeek> adjustFutureTrainingsVector(List<TrainingPerWeek> _futureTrainings, int requiredNBentries) {
		//Instant nextTrainingDate = TrainingManager.instance().getNextWeekTraining().getTrainingDate();
		Optional<TrainingPerWeek> optionallastTraining = _futureTrainings.stream().max(Comparator.comparing(TrainingPerWeek::getTrainingDate));
		List<TrainingPerWeek> newfutureTrainings = new ArrayList<>();

		if (_futureTrainings.size() != 0){
			// removal of old entries
			for (var entry : _futureTrainings) {
				if (!entry.getTrainingDate().isBefore(this.nextTrainingDate)) {
					newfutureTrainings.add(entry);
					if(newfutureTrainings.size() == requiredNBentries){
						break;
					}
				}
			}
		}

		TrainingPerWeek latestTraining;

		if (optionallastTraining.isPresent()) {
			latestTraining = optionallastTraining.get();
		}
		else {
			latestTraining = TrainingManager.instance().getNextWeekTraining();
		}


		if(newfutureTrainings.size() < requiredNBentries) {
			// Adding new entries

			int nbWeek = 1;
			ZonedDateTime zdtFutureTrainingDate;

			HTDatetime oTrainingDate = new HTDatetime(latestTraining.getTrainingDate());
			ZonedDateTime zdtrefDate = oTrainingDate.getHattrickTime();
			TrainingPerWeek futureTraining;

			while (newfutureTrainings.size() < requiredNBentries) {
				zdtFutureTrainingDate = zdtrefDate.plus(nbWeek * 7, ChronoUnit.DAYS);
				futureTraining = new TrainingPerWeek(zdtFutureTrainingDate.toInstant(), latestTraining.getTrainingType(), latestTraining.getTrainingIntensity(),
						latestTraining.getStaminaShare(), latestTraining.getTrainingAssistantsLevel(), latestTraining.getCoachLevel(), DBDataSource.GUESS);
				newfutureTrainings.add(futureTraining);
				nbWeek++;
			}
		}

		return newfutureTrainings;

	}
}
