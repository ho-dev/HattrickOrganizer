package core.training;

import java.io.InputStream;
import java.util.*;

import core.constants.player.PlayerSkill;
import core.file.FileLoader;
import core.module.config.ModuleConfig;
import core.util.HODateTime;
import core.util.HOLogger;


public class SkillDrops {

	//http://translate.google.com/translate?hl=en&sl=ru&tl=en&u=http://olal.su/index.php%3Foption%3Dcom_content%26view%3Darticle%26id%3D87:2008-05-15-22-25-29%26catid%3D34:2008-01-30-22-36-28%26Itemid%3D53&prev=hp&rurl=translate.google.com

	// Order on the site is:
	//	Goalie
	//	Defense
	//	Midfield
	//	Winger
	//	Scoring


	private static double[][] keeper = {
		{7.2, 8.6, 10.4, 99, 99, 99,  99, 99},
		{5.4, 6.6, 8.0, 9.8, 99, 99,  99, 99},
		{4.0, 5.0, 6.2, 7.6, 9.6, 99, 99, 99},
		{2.8,  3.6,  4.6,  5.8,  7.7,  99,  99,  99},
		{1.8,  2.4,  3.2,  4.3,  6.1,  8.5,  99,  99},
		{1.0,  1.5,  2.1,  3.1,  4.9,  7.3,  99,  99},
		{0.5,  0.9,  1.4,  2.3,  4.0,  6.3,  10.6,  99},
		{0.1,  0.4,  0.8,  1.6,  3.2,  5.4,  9.3,  99},
		{0,  0.2,  0.4,  1.0,  2.4,  4.4,  7.6,  12.4},
		{0,  0.1,  0.2,  0.7,  1.8,  3.6,  6.0,  9.0},
		{0,  0,  0.2,  0.5,  1.2,  2.7,  4.5,  6.6},
		{0,  0,  0.2,  0.5,  0.9,  1.9,  3.0,  4.8},
		{0,  0,  0.1,  0.4,  0.8,  1.5,  2.4,  4.0}};
	
	
	private static double[][] defending =  {
		{7.2,  8.0,  99,  99,  99,  99,  99,  99},  
		{5.4,  6.2,  7.4,  99,  99,  99,  99,  99},  
		{4.0,  4.7,  5.8,  8.0,  99,  99,  99,  99},  
		{2.8,  3.4,  4.4,  6.5,  9.1,  99,  99,  99},  
		{1.8,  2.3,  3.2,  5.1,  7.6,  99,  99,  99},  
		{1.0,  1.5,  2.4,  4.2,  6.5,  9.3,  99,  99},  
		{0.5,  1.0,  1.9,  3.4,  5.3,  7.6,  99,  99},  
		{0.1,  0.5,  1.3,  2.6,  4.1,  6.2,  10.0,  99},  
		{0,  0.3,  0.9,  1.9,  3.1,  5.0,  8.5,  12.5},  
		{0,  0.3,  0.7,  1.5,  2.6,  4.3,  7.4,  11.0},  
		{0,  0.2,  0.6,  1.3,  2.3,  3.6,  6.0,  9.4},  
		{0,  0.2,  0.6,  1.2,  1.9,  3.0,  5.0,  8.2},  
		{0,  0.2,  0.5,  1.1,  1.8,  2.8,  4.7,  7.8}};

	private static double[][] playmaking = {
		{7.2,  8.2,  9.8,  99,  99,  99,  99,  99}, 
		{5.4,  6.4,  7.9,  9.9,  99,  99,  99,  99}, 
		{4,  4.9,  6.4,  8.2,  11.8,  99,  99,  99}, 
		{2.8,  3.7,  5.1,  6.7,  10,  13.6,  99,  99}, 
		{1.8,  2.7,  4,  5.5,  8.5,  11.8,  99,  99}, 
		{1,  1.8,  3,  4.4,  7.1,  10.1,  15,  99}, 
		{0.5,  1.3,  2.3,  3.5,  6,  8.8,  13.2,  99}, 
		{0.1,  0.8,  1.7,  2.9,  5.2,  7.8,  11.8,  17.4}, 
		{0,  0.6,  1.3,  2.3,  4.2,  6.4,  10,  15.2}, 
		{0,  0.6,  1.2,  2,  3.5,  5.6,  8.7,  13.5}, 
		{0,  0.5,  1.1,  1.8,  3,  4.8,  7.5,  12}, 
		{0,  0.5,  1.1,  1.8,  2.9,  4.3,  6.3,  10.4}, 
		{0,  0.5,  1,  1.7,  2.8,  4.1,  6,  9.8}};

	private static double[][] winger = {
		{7.2,  8.4,  99,  99,  99,  99,  99,  99}, 
		{5.4,  6.5,  8.2,  10.4,  99,  99,  99,  99}, 
		{4,  5.1,  6.6,  8.5,  11.9,  99,  99,  99}, 
		{2.8,  3.8,  5.3,  7,  10.2,  13.8,  99,  99}, 
		{1.8,  2.8,  4.1,  5.7,  8.7,  12,  99,  99}, 
		{1,  1.9,  3.1,  4.6,  7.3,  10.3,  15.3,  99}, 
		{0.5,  1.3,  2.4,  3.8,  6.2,  9.1,  13.5,  99}, 
		{0.1,  0.8,  1.9,  3.2,  5.3,  8.1,  12.1,  99}, 
		{0,  0.7,  1.4,  2.5,  4.2,  6.7,  10.4,  15.6}, 
		{0,  0.7,  1.3,  2.2,  3.5,  5.9,  9,  13.9}, 
		{0,  0.6,  1.3,  2,  3,  5.1,  7.8,  12.4}, 
		{0,  0.6,  1.2,  1.9,  2.9,  4.4,  6.6,  10.7}, 
		{0,  0.6,  1.2,  1.9,  2.9,  4.3,  6.3,  10}};

	private static double[][] scoring = {
		{7.2, 9, 99, 99, 99, 99, 99, 99},
		{5.4, 7.1, 8.9, 11.4, 99, 99, 99, 99}, 
		{4, 5.6, 7.3, 9.7, 12.4, 16.4, 99, 99},
		{2.8, 4.3, 5.9, 8.2, 10.8, 14.5, 99, 99},
		{1.8, 3.2, 4.5, 6.7, 9.4, 13, 99, 99},
		{1, 2.2, 3.3, 5.3, 7.9, 11.3, 16.3, 99},
		{0.5, 1.7, 2.7, 4.6, 7, 10.2, 14.8, 99},
		{0.1, 1.3, 2.3, 4.1, 6.2, 9.1, 13.2, 99},
		{0, 1.1, 2, 3.5, 5.4, 8.1, 11.8, 17.4},
		{0, 1, 1.8, 3, 4.5, 7.1, 10.4, 15.6},
		{0, 1, 1.7, 2.7, 4.1, 6.3, 9.2, 13.8},
		{0, 1, 1.7, 2.6, 3.9, 5.5, 8, 12},
		{0, 1, 1.6, 2.5, 3.8, 5.3, 7.4, 10.8}};
	

	private static double[][] passing = {
		{7.2, 9, 99, 99, 99, 99, 99, 99},
		{5.4, 7.1, 8.9, 11.4, 99, 99, 99, 99}, 
		{4, 5.6, 7.3, 9.7, 12.4, 16.4, 99, 99},
		{2.8, 4.3, 5.9, 8.2, 10.8, 14.5, 99, 99},
		{1.8, 3.2, 4.5, 6.7, 9.4, 13, 99, 99},
		{1, 2.2, 3.3, 5.3, 7.9, 11.3, 16.3, 99},
		{0.5, 1.7, 2.7, 4.6, 7, 10.2, 14.8, 99},
		{0.1, 1.3, 2.3, 4.1, 6.2, 9.1, 13.2, 99},
		{0, 1.1, 2, 3.5, 5.4, 8.1, 11.8, 17.4},
		{0, 1, 1.8, 3, 4.5, 7.1, 10.4, 15.6},
		{0, 1, 1.7, 2.7, 4.1, 6.3, 9.2, 13.8},
		{0, 1, 1.7, 2.6, 3.9, 5.5, 8, 12},
		{0, 1, 1.6, 2.5, 3.8, 5.3, 7.4, 10.8}};
	

	private static double[][] setpieces = {
		{7.2, 9, 99, 99, 99, 99, 99, 99},
		{5.4, 7.1, 8.9, 11.4, 99, 99, 99, 99}, 
		{4, 5.6, 7.3, 9.7, 12.4, 16.4, 99, 99},
		{2.8, 4.3, 5.9, 8.2, 10.8, 14.5, 99, 99},
		{1.8, 3.2, 4.5, 6.7, 9.4, 13, 99, 99},
		{1, 2.2, 3.3, 5.3, 7.9, 11.3, 16.3, 99},
		{0.5, 1.7, 2.7, 4.6, 7, 10.2, 14.8, 99},
		{0.1, 1.3, 2.3, 4.1, 6.2, 9.1, 13.2, 99},
		{0, 1.1, 2, 3.5, 5.4, 8.1, 11.8, 17.4},
		{0, 1, 1.8, 3, 4.5, 7.1, 10.4, 15.6},
		{0, 1, 1.7, 2.7, 4.1, 6.3, 9.2, 13.8},
		{0, 1, 1.7, 2.6, 3.9, 5.5, 8, 12},
		{0, 1, 1.6, 2.5, 3.8, 5.3, 7.4, 10.8}};
	
	private boolean active = true;
	private static SkillDrops instance = null;
	
	private static final String key = "SKILL_DROPS_ACTIVATED";
	
	
	public SkillDrops() {
		
		if (ModuleConfig.instance().containsKey(key)) {
			active = ModuleConfig.instance().getBoolean(key);
		} else {
			ModuleConfig.instance().setBoolean(key, true);
			ModuleConfig.instance().save();
		}
		readArrays();
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean b) {
		if (b != active) {
			active = b;
			ModuleConfig.instance().setBoolean(key, active);
			ModuleConfig.instance().save();
		}
	}
	
	


	public static SkillDrops instance() {
		if (instance == null) {
			instance = new SkillDrops();
		}
		
		return instance;
	}

	/**
	 * Returns the skill drop for the provided parameters.
	 * 
	 * @param skill The skill level of the player to drop
	 * @param age The age of the player to drop
	 * @param skillType As defined in PlayerSkill
	 * @return A percentage number for the skill to drop. On return 2, a skill of 4.50 should move to 4.52.
	 */
	public double getSkillDrop(int skill, int age, int skillType){

		if ( skillType == PlayerSkill.STAMINA) return 0; // don't calc stamina subs (curious usage in FutureTrainingManager)

		//https://www88.hattrick.org/Forum/Read.aspx?t=17404127&n=18&v=6
		/* formula provided by Schum
		I'll make a reservation right away.
		Since we are moving on to calculations, we use the calculation scale, where Excellent = 7 (Excellent = 7).

		Training update.

		In the training update, a decrease in skill cannot occur.
		But depending on the skill level and age of the player, there is a decrease in the growth of the trained skill.

		And this decrease in DropLevel consists of 2 components.
		DropLevel = DropL + DropLA
		DropL depends only on the skill level,
		DropLA depends on skill level and age.

		L - skill level on the scale of calculations.
		At L<14, DropL=0.

		At 14<=L<20
		DropL = a*L^3 + b*L^2 + c*L + d
		a = 0.000006111
		b = 0.000808
		c = -0.026017
		d = 0.192775

		With L>20, we count according to the same formula, but we add 0.39 to the skill level
		DropL = a*(L+0.39)^3 + b*(L+0.39)^2 + c*(L+0.39) + d
		*/
		double dropL;
		if ( skill < 14){
			dropL = 0;
		}
		else {
			var L = skill;
			if ( skill > 20) L+=0.39;
			dropL = 0.000006111 * Math.pow(L, 3) + 0.000808 * Math.pow(L, 2) -0.026017*L + 0.192775;
		}

		/*
		For Age<31, DropLA = 0

		DropLA = m*L + n
		The coefficients m and n depend on age.

		DropLA	m	n
		31	0.00031	0.00031
		32	0.00118	-0.01625
		33	0.00264	-0.03551
		34	0.00468	-0.06086
		35	0.00732	-0.09104
		36	0.01066	-0.12554
		37	0.01460	-0.16021


		With L>20, 1 must be added to the skill level.
		DropLA = m*(L+1) + n
		*/
		double dropLA;
		if ( age< 31) {
			dropLA = 0;
		}
		else {
			double m, n;
			switch (age) {
				case 31 -> {
					m = 0.00031;
					n = 0.00031;
				}
				case 32 -> {
					m = 0.00118;
					n = -0.01625;
				}
				case 33 -> {
					m = 0.00264;
					n = -0.03551;
				}
				case 34 -> {
					m = 0.00468;
					n = -0.06086;
				}
				case 35 -> {
					m = 0.00732;
					n = -0.09104;
				}
				case 36 -> {
					m = 0.01066;
					n = -0.12554;
				}
				default -> {
					m = 0.01460;
					n = -0.16021;
				}
			}
			var L = skill;
			if (skill > 20) L += 1;
			dropLA = m * L + n;
		}
		var dropLevel = dropL + dropLA;
		if ( dropLevel < 0 ){
			dropLevel = 0;
		}
		/*

		DropAge
		It's a decline in skill that happens on Mon,
		and depends on the skill itself and the age of the player.

		Different skills begin to fall when they reach different ages.

		AgeNoDrop
		GK	29
		Df	28
		PM	27
		Wg	27
		Ps	27
		Sc	26
		SP	30

		Age+	DropAge
		1	0.0003
		2	0.0014
		3	0.0037
		4	0.0074
		5	0.0127
		6	0.0197
		7	0.0285
		8	0.0393
		9	0.0522
		10	0.0673
		11	0.0846

		Age+ = Age - AgeNoDrop

		*/

		var ageP = switch (skillType){
			case PlayerSkill.KEEPER -> age-29;
			case PlayerSkill.DEFENDING -> age-28;
			case PlayerSkill.PLAYMAKING, PlayerSkill.WINGER, PlayerSkill.PASSING -> age-27;
			case PlayerSkill.SCORING -> age-26;
			case PlayerSkill.SET_PIECES -> age-30;
			default -> throw new IllegalStateException("Unexpected value: " + skillType);
		};

		double dropAge = 0;
		if ( ageP > 0) {
			dropAge = switch (ageP) {
				case 1 -> 0.0003;
				case 2 -> 0.0014;
				case 3 -> 0.0037;
				case 4 -> 0.0074;
				case 5 -> 0.0127;
				case 6 -> 0.0197;
				case 7 -> 0.0285;
				case 8 -> 0.0393;
				case 9 -> 0.0522;
				case 10 -> 0.0673;
				default -> 0.0846;
			};
		}
		return dropLevel + dropAge;
	}

	private final HODateTime skillDropChanged = HODateTime.fromHT("2017-05-08 00:00:00");
	public double getSkillDropAtDate(int skill, int age, int skillType, HODateTime date){
		if ( date.isAfter(skillDropChanged)){
			return getSkillDrop(skill,age,skillType);
		}
		else {
			return getSkillDropBeforeMay082017(skill, age, skillType);
		}
	}
	private double getSkillDropBeforeMay082017(int skill,  int age,  int skillType) {
		// skill losses only begin at the age of 28 years
		if (age < 28) return 0;
		double[][] array;
		switch (skillType) {
			case PlayerSkill.KEEPER -> array = keeper;
			case PlayerSkill.DEFENDING -> array = defending;
			case PlayerSkill.PLAYMAKING -> array = playmaking;
			case PlayerSkill.WINGER -> array = winger;
			case PlayerSkill.SCORING -> array = scoring;
			case PlayerSkill.PASSING -> array = passing;
			case PlayerSkill.SET_PIECES -> array = setpieces;
			default -> {
				return 0;
			}
		}
		/* Top row is the highest skill Last row is for skill level 11 and below */
		int row = array.length - 1 - Math.min(array.length - 1, Math.max(0, skill - 11));
		/* First column is for age 29 and below */
		int col = Math.min(array[row].length - 1, Math.max(0, age - 29));

		return array[row][col] / 100.;
	}
	
	
	private double[][] readArrayFromFile (String fileName) {
		
		try {
			InputStream fileIS = FileLoader.instance().getFileInputStream("prediction/skilldrops/" + fileName);
			if (fileIS==null) {
				HOLogger.instance().error(getClass(), "Failed to open skill drop file: " + fileName);
				return null;
			}
			
			List<double[]> lines = new ArrayList<>();
		
			Scanner fileIn = new Scanner(fileIS);
			while (fileIn.hasNextLine()) {
			    // read a line, and turn it into the characters
			    String[] oneLine = fileIn.nextLine().split(",");
				int LINE_LENGTH = 8;
				if (oneLine.length != LINE_LENGTH) {
			    	HOLogger.instance().error(getClass(), "Failed to read skill drop file: " 
			    											+ fileName + ". error in line length");
			    	fileIn.close();
				    return null;
			    }
			    
			    double[] doubleLine = new double[oneLine.length];
			    
			    // we turn the characters into doubles
			    for(int i =0; i < doubleLine.length; i++){
			        if (oneLine[i].trim().equals(""))
			            doubleLine[i] = 0;
			        else
			            doubleLine[i] = Double.parseDouble(oneLine[i].trim());
			    }
			    // and then add the int[] to our output
			    lines.add(doubleLine);
			
			}
			fileIn.close();

			int LINES = 13;
			if (lines.size() != LINES) {
				HOLogger.instance().error(getClass(), "Failed to read skill drop file: " 
														+ fileName + ". wrong number of lines");
				return null;
			}
			
			return lines.toArray(new double[lines.size()][]);
		} catch (Exception e) {
			HOLogger.instance().error(getClass(), "Failed to read skill drop file: " + fileName + ". " + e.getMessage());
			return null;
		}
	}
	
	private void readArrays() {
		double[][] tmp;
		
		tmp = readArrayFromFile("keeper");
		if (tmp != null) {
			keeper = tmp;
		}
		
		tmp = readArrayFromFile("defending");
		if (tmp != null) {
			defending = tmp;
		}
		
		tmp = readArrayFromFile("playmaking");
		if (tmp != null) {
			playmaking = tmp;
		}
		
		tmp = readArrayFromFile("winger");
		if (tmp != null) {
			winger = tmp;
		}
		
		tmp = readArrayFromFile("scoring");
		if (tmp != null) {
			scoring = tmp;
		}
		
		tmp = readArrayFromFile("passing");
		if (tmp != null) {
			passing = tmp;
		}

		tmp = readArrayFromFile("setpieces");
		if (tmp != null) {
			setpieces = tmp;
		}
	}
}
