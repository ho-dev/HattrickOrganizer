package core.training;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import core.constants.player.PlayerSkill;
import core.file.FileLoader;
import core.module.config.ModuleConfig;
import core.util.HOLogger;


public class SkillDrops {

	//http://translate.google.com/translate?hl=en&sl=ru&tl=en&u=http://olal.su/index.php%3Foption%3Dcom_content%26view%3Darticle%26id%3D87:2008-05-15-22-25-29%26catid%3D34:2008-01-30-22-36-28%26Itemid%3D53&prev=hp&rurl=translate.google.com

	// Order on the site is:
	//	Goalie
	//	Defense
	//	Midfield
	//	Winger
	//	Scoring
	
	private static int LINES = 13;
	private static int LINE_LENGTH = 8;
	
	
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
	private SkillDrops drops;
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
	public float getSkillDrop(int skill,  int age,  int skillType) {
		double[][] array;
		switch (skillType) {
			case PlayerSkill.KEEPER : {
				array = keeper;
				break;
			}
			case PlayerSkill.DEFENDING : {
				array = defending;
				break;
			}
			case PlayerSkill.PLAYMAKING : {
				array = playmaking; 
				break;
			}
			case PlayerSkill.WINGER : {
				array = winger;
				break;
			}
			case PlayerSkill.SCORING : {
				array = scoring;
				break;
			}
			case PlayerSkill.PASSING : {
				// Ad hoc value choice
				array = passing;
				break;
			}
			case PlayerSkill.SET_PIECES : {
				//Ad hoc value choice
				array = setpieces;
				break;
			}

			default: {
				return 0;
			}
		}
		/* Top row is the highest skill Last row is for skill level 11 and below */
		int row = array.length - 1 - Math.min(array.length - 1,  Math.max(0,  skill - 11));
		/* First column is for age 29 and below */
		int col = Math.min(array[row].length - 1,  Math.max(0,  age-29));

		return (float) array[row][col];
	}
	
	
	private double[][] readArrayFromFile (String fileName) {
		
		try {
			InputStream fileIS = FileLoader.instance().getFileInputStream("prediction/skilldrops/" + fileName);
			if (fileIS==null) {
				HOLogger.instance().error(getClass(), "Failed to open skill drop file: " + fileName);
				return null;
			}
			
			List<double[]> lines = new ArrayList<double[]>();
		
			Scanner fileIn = new Scanner(fileIS);
			while (fileIn.hasNextLine()) {
			    // read a line, and turn it into the characters
			    String[] oneLine = fileIn.nextLine().split(",");
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
