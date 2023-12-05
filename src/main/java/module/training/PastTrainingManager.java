// %3190202247:hoplugins.trainingExperience%
package module.training;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.model.player.SkillChange;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class that keeps track of the past skillup for the active user
 */
public class PastTrainingManager {

	/** List of all skill up */
	private List<SkillChange> allSkillups = new ArrayList<>();
	/** List of trained skill up */
	private List<SkillChange> trainSkillups = new ArrayList<>();

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

		for (var skill : PlayerSkill.values()) {
			// Skip Form ups
			if (skill == PlayerSkill.FORM) {
				continue;
			}

			var skillChanges = player.getAllSkillChanges(skill);
			for (var element : skillChanges) {
				var skillUpDate =  element.getDate();
				var trainingDate = HOVerwaltung.instance().getModel().getXtraDaten().getNextTrainingDate();
				while (skillUpDate.isAfter(trainingDate)) trainingDate = trainingDate.plus(7, ChronoUnit.DAYS);
				while (skillUpDate.isBefore(trainingDate)) trainingDate = trainingDate.minus(7, ChronoUnit.DAYS);
				element.setDate(trainingDate);
				allSkillups.add(element);

				if (skill == PlayerSkill.KEEPER || skill == PlayerSkill.DEFENDING
						|| skill == PlayerSkill.WINGER || skill == PlayerSkill.PLAYMAKING
						|| skill == PlayerSkill.SCORING || skill == PlayerSkill.PASSING
						|| skill == PlayerSkill.SETPIECES) {
					trainSkillups.add(element);
				}

			}
		}

		var comp = new SkillChangeComparator();

		allSkillups.sort(comp);
		trainSkillups.sort(comp);
	}

	/**
	 * Returns the list of all calculated Skillups for the active player.
	 * 
	 * @return list of all skillups
	 */
	public List<SkillChange> getAllSkillups() {
		return allSkillups;
	}

	/**
	 * Returns the list of calculated Skillups for the active player as a result
	 * of training.
	 * 
	 * @return list of trained skillups
	 */
	public List<SkillChange> getTrainedSkillups() {
		return trainSkillups;
	}
	private static class SkillChangeComparator implements Comparator<SkillChange> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(SkillChange o1, SkillChange o2) {
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
