// %3190202247:hoplugins.trainingExperience%
package module.training;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.ISkillChange;
import core.model.player.Player;
import core.util.HTDatetime;
import core.util.HelperWrapper;

import java.text.ParseException;
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
				PastSkillChange su;

				try {
					Date htDate = HelperWrapper.instance().getHattrickDate(
							String.valueOf(element[0]));
					Date trainingDate = HelperWrapper
							.instance()
							.getLastTrainingDate(
									htDate,
									HOVerwaltung.instance().getModel().getXtraDaten()
											.getNextTrainingDate()).getTime();

					su = getSkillup(trainingDate);
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
				} catch (ParseException e) {
					e.printStackTrace();
				}

				count++;
			}
		}

		SkillupComperator comp = new SkillupComperator();

		Collections.sort(allSkillups, comp);
		Collections.sort(trainSkillups, comp);
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
	private PastSkillChange getSkillup(Date skillupDate) {

		PastSkillChange skillup = new PastSkillChange();

		var htdatetime = new HTDatetime(skillupDate.toInstant());
		skillup.setHtSeason(htdatetime.getHTSeasonLocalized());
		skillup.setHtWeek(htdatetime.getHTWeekLocalized());
		skillup.setDate(skillupDate);

		return skillup;
	}

	private class SkillupComperator implements Comparator<ISkillChange> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ISkillChange o1, ISkillChange o2) {
			if (o1.getDate().before(o2.getDate())) {
				return -1;
			} else if (o1.getDate().after(o2.getDate())) {
				return 1;
			} else {
				if (o1.getType() == o2.getType()) {
					if (o1.getValue() > o2.getValue()) {
						return 1;
					} else {
						return -1;
					}
				}
				return 0;
			}
		}
	}
}
