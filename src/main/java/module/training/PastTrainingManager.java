// %3190202247:hoplugins.trainingExperience%
package module.training;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.ISkillChange;
import core.model.player.Player;
import core.util.HODateTime;
import core.util.HelperWrapper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Class that keeps track of the past skillup for the active user
 */
public class PastTrainingManager {

	/** List of all skill up */
	private List<ISkillChange> allSkillups = new ArrayList<>();
	/** List of trained skill up */
	private List<ISkillChange> trainSkillups = new ArrayList<>();

	/**
	 * Calculates data for the player
	 * 
	 * @param player Player
	 */
	public PastTrainingManager(Player player) {
		if (player == null) {
			return;
		}

		allSkillups = new ArrayList<>();
		trainSkillups = new ArrayList<>();

		for (int skill = 0; skill < 10; skill++) {
			// Skip Form ups
			if (skill == PlayerSkill.FORM) {
				continue;
			}

			List<Object[]> levelUps = player.getAllLevelUp(skill);
			int count = 0;

			for (Object[] element : levelUps) {
				var skillUpDate =  (HODateTime)element[0];
				var trainingDate = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate();
				while (skillUpDate.isAfter(trainingDate)) trainingDate = trainingDate.plus(7, ChronoUnit.DAYS);
				while (skillUpDate.isBefore(trainingDate)) trainingDate = trainingDate.minus(7, ChronoUnit.DAYS);
				var su = getSkillup(trainingDate);
				su.setValue((Integer) element[2]);
				su.setType(skill);
				su.setTrainType(ISkillChange.SKILLUP_REAL);
				su.setAge(player.getAgeWithDaysAsString(trainingDate));
				allSkillups.add(su);

				if (skill == PlayerSkill.KEEPER || skill == PlayerSkill.DEFENDING
						|| skill == PlayerSkill.WINGER || skill == PlayerSkill.PLAYMAKING
						|| skill == PlayerSkill.SCORING || skill == PlayerSkill.PASSING
						|| skill == PlayerSkill.SET_PIECES) {
					trainSkillups.add(su);
				}

				count++;
			}
		}

		SkillupComperator comp = new SkillupComperator();

		allSkillups.sort(comp);
		trainSkillups.sort(comp);
	}

	/**
	 * Returns the list of all calculated Skillups for the active player.
	 * 
	 * @return list of all skillups
	 */
	public List<ISkillChange> getAllSkillups() {
		return allSkillups;
	}

	/**
	 * Returns the list of calculated Skillups for the active player as a result
	 * of training.
	 * 
	 * @return list of trained skillups
	 */
	public List<ISkillChange> getTrainedSkillups() {
		return trainSkillups;
	}

	/**
	 * Calculates the HT Week and Season from the SkillupDate and initialize the
	 * Skillup Object
	 * 
	 * @param skillupDate
	 *            Skillup Date
	 * 
	 * @return a skillup object with season and week value
	 */
	private PastSkillChange getSkillup(HODateTime skillupDate) {
		PastSkillChange skillup = new PastSkillChange();
		var htWeek = skillupDate.toLocaleHTWeek();
		skillup.setHtSeason(htWeek.season);
		skillup.setHtWeek(htWeek.week);
		skillup.setDate(skillupDate);
		return skillup;
	}

	private class SkillupComperator implements Comparator<ISkillChange> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ISkillChange o1, ISkillChange o2) {
			var ret = o1.getDate().compareTo(o2.getDate());
			if ( ret == 0){
				if (o1.getType() == o2.getType()) {
					if (o1.getValue() > o2.getValue()) {
						ret = 1;
					} else {
						ret = -1;
					}
				}
			}
			return ret;
		}
	}
}
