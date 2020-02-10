// %3190202247:hoplugins.trainingExperience%
package module.training;

import core.constants.player.PlayerSkill;
import core.model.HOVerwaltung;
import core.model.player.ISkillup;
import core.model.player.Player;
import core.util.HTCalendarFactory;
import core.util.HelperWrapper;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Class that keeps track of the past skillup for the active user
 */
public class OldTrainingManager {

	/** List of all skill up */
	private List<ISkillup> allSkillups = new ArrayList<ISkillup>();
	/** List of trained skill up */
	private List<ISkillup> trainSkillups = new ArrayList<ISkillup>();

	/**
	 * Calculates data for the player
	 * 
	 * @param player
	 */
	public OldTrainingManager(Player player) {
		if (player == null) {
			return;
		}

		allSkillups = new ArrayList<ISkillup>();
		trainSkillups = new ArrayList<ISkillup>();

		for (int skill = 0; skill < 10; skill++) {
			// Skip Form ups
			if (skill == PlayerSkill.FORM) {
				continue;
			}

			List<Object[]> levelUps = player.getAllLevelUp(skill);
			int count = 0;

			for (Object[] element : levelUps) {
				PastSkillup su = null;

				try {
					Date htDate = HelperWrapper.instance().getHattrickDate(
							String.valueOf(element[0]));
					Date trainingDate = HelperWrapper
							.instance()
							.getLastTrainingDate(
									htDate,
									HOVerwaltung.instance().getModel().getXtraDaten()
											.getTrainingDate()).getTime();

					su = getSkillup(trainingDate);
					su.setValue(player.getValue4Skill4(skill) - count);
					su.setType(skill);
					su.setTrainType(ISkillup.SKILLUP_REAL);
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
	public List<ISkillup> getAllSkillups() {
		return allSkillups;
	}

	/**
	 * Returns the list of calculated Skillups for the active player as a result
	 * of training.
	 * 
	 * @return list of trained skillups
	 */
	public List<ISkillup> getTrainedSkillups() {
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
	private PastSkillup getSkillup(Date skillupDate) {

		PastSkillup skillup = new PastSkillup();

		skillup.setHtSeason(HTCalendarFactory.getHTSeason(skillupDate));
		skillup.setHtWeek(HTCalendarFactory.getHTWeek(skillupDate));
		skillup.setDate(skillupDate);

		return skillup;
	}

	private class SkillupComperator implements Comparator<ISkillup> {

		/**
		 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(ISkillup o1, ISkillup o2) {
			ISkillup skillup1 = o1;
			ISkillup skillup2 = o2;

			if (skillup1.getDate().before(skillup2.getDate())) {
				return -1;
			} else if (skillup1.getDate().after(skillup2.getDate())) {
				return 1;
			} else {
				if (skillup1.getType() == skillup2.getType()) {
					if (skillup1.getValue() > skillup2.getValue()) {
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
