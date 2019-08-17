package core.rating;

import core.file.FileLoader;
import core.util.HOLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class RatingPredictionConfig {

	/* We check for changed rating parameter files regularily */
	private static long lastCheck = new Date().getTime();
	private static long checkInterval = 5000; // in millisecs

	private static long lastParse;
	
    private static RatingPredictionConfig config = null;
    
    private RatingPredictionParameter sideDefenseParam = new RatingPredictionParameter ();
    private RatingPredictionParameter centralDefenseParam = new RatingPredictionParameter ();
    private RatingPredictionParameter midfieldParam = new RatingPredictionParameter ();
    private RatingPredictionParameter sideAttackParam = new RatingPredictionParameter ();
    private RatingPredictionParameter centralAttackParam = new RatingPredictionParameter ();
    private RatingPredictionParameter playerStrengthParam = new RatingPredictionParameter ();
    private RatingPredictionParameter tacticsParam = new RatingPredictionParameter ();
    
    private String predictionName;
    private static String[] allPredictionNames = null;
    
    private static final String predDir = "prediction";
    private static final String predConfigFile = predDir + File.separatorChar + "predictionTypes.conf";
    

    private RatingPredictionConfig() {
    }

    public static RatingPredictionConfig getInstance()
    {
    	if (config == null)
    		return getInstance (0);
    	else
    		return getInstance(getInstancePredictionType());
    }

    public static RatingPredictionConfig getInstance (int type) {
    	if (getAllPredictionNames() != null) {
    		if (type < getAllPredictionNames().length) 
    			return getInstance (getAllPredictionNames()[type]);
    		else
    			return getInstance (getAllPredictionNames()[0]);
    	} else
    		return null;    		
    }
    
    public static RatingPredictionConfig getInstance(String predictionName)
    {
    	if (config == null) {
        	config = new RatingPredictionConfig();
        }
    	long now = new Date().getTime();
    	if (!predictionName.equals(config.getPredictionName()) || now > lastCheck + checkInterval) {
        	config.initArrays(predictionName);
    		lastCheck = now;
        }
       	return config;
    }

    public static String[] getAllPredictionNames() {
    	if (allPredictionNames != null)
    		return allPredictionNames;
    	else {
    		ArrayList<String> list = new ArrayList<String>();
    		try {
    			InputStream predictionIS = FileLoader.instance().getFileInputStream(new String[]{predConfigFile, predDir + "/predictionTypes.conf"});
    			if (predictionIS==null) {
    				HOLogger.instance().debug(RatingPredictionConfig.class, "Error while loading: " + predConfigFile + ", " + predDir + "/predictionTypes.conf");
    			} else {
    				BufferedReader br = new BufferedReader(new InputStreamReader(predictionIS));
    				while (br != null && br.ready()) {
        				String line = br.readLine();
        				// Remove Comments
        				line = line.replaceFirst("#.*", "");
        				// Trim
        				line = line.trim();
        				if (line.length() != 0) {
        					list.add(line);
        				}
        			}
    			}
    			HOLogger.instance().debug(RatingPredictionConfig.class, "Found predictionTypes: "+list);
    			allPredictionNames = new String[list.size()];
    			for (int i=0; i < allPredictionNames.length; i++) {
    				allPredictionNames[i] = list.get(i);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		if (allPredictionNames == null) {
    			allPredictionNames = new String[1];
    			allPredictionNames[0] = "not available";
    		}
    		return allPredictionNames;
    	}
    }
    
    public static String getInstancePredictionName () {
    	if (config == null)
    		return null;
    	else
    		return config.getPredictionName();
    }
    
    public static int getInstancePredictionType () {
    	if (config == null)
    		return 0;
    	else 
    		return config.getPredictionType ();
    }
    
    public static void setInstancePredictionName (String name) {
    	getInstance(name);
    }
    
    public static void setInstancePredictionType (int type) {
    	getInstance(type);    	
    }

    public int getPredictionType () {
    	String[] allPredictionNames = getAllPredictionNames();
    	for (int i=0; i <allPredictionNames.length; i++)
    		if (allPredictionNames[i].equalsIgnoreCase(config.getPredictionName()))
    			return i;
    	return 0;
    }
    
    public String getPredictionName () {
    	return predictionName;
    }
    
    private void initArrays (String predictionName) {
    	this.predictionName = predictionName;
//		HOLogger.instance().debug(this.getClass(), "Checking for changed prediction files for type "+predictionName);
		String prefix = predDir + File.separatorChar + predictionName + File.separatorChar;
    	sideDefenseParam.readFromFile(prefix + "sidedefense.dat");
    	centralDefenseParam.readFromFile(prefix + "centraldefense.dat");
    	midfieldParam.readFromFile(prefix + "midfield.dat");
    	sideAttackParam.readFromFile(prefix + "sideattack.dat");
    	centralAttackParam.readFromFile(prefix + "centralattack.dat");
    	playerStrengthParam.readFromFile(prefix + "playerstrength.dat");
    	tacticsParam.readFromFile(prefix + "tactics.dat");
    	
    	// Check all params for re-parsed files
    	RatingPredictionParameter allParams [] = 
    		{sideDefenseParam, centralDefenseParam, midfieldParam, 
    			sideAttackParam, centralAttackParam, playerStrengthParam, tacticsParam};
    	
    	for (RatingPredictionParameter curParam : allParams) {
        	if (curParam.getLastParse() > lastParse)
        		lastParse = curParam.getLastParse();    		
    	}
    }

    public RatingPredictionParameter getCentralAttackParameters()
    {
        return centralAttackParam;
    }

    public RatingPredictionParameter getSideAttackParameters()
    {
        return sideAttackParam;
    }

    public RatingPredictionParameter getCentralDefenseParameters()
    {
        return centralDefenseParam;
    }

    public RatingPredictionParameter getSideDefenseParameters()
    {
        return sideDefenseParam;
    }

    public RatingPredictionParameter getMidfieldParameters()
    {
        return midfieldParam;
    }
    
    public RatingPredictionParameter getPlayerStrengthParameters()
    {
    	return playerStrengthParam;
    }
    
    public RatingPredictionParameter getTacticsParameters()
    {
    	return tacticsParam;
    }
    
    /**
     * Get the date of the last file parse
     * @return
     */
    public long getLastParse () {
    	return lastParse;
    }
}
