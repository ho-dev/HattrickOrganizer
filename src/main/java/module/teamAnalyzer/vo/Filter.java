// %3124307367:hoplugins.teamAnalyzer.vo%
package module.teamAnalyzer.vo;

import core.module.config.ModuleConfig;

import java.util.List;


/**
 * Filter Object class that holds the user settings for selecting analyzed matches
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class Filter {
    //~ Instance fields ----------------------------------------------------------------------------

    /** List of manually selected match ids */
    private List<String> matches;

    /** Automatic or manual selection enabled */
    private boolean automatic = true;

    /** Consider away games */
    private boolean awayGames = true;

    /** Consider cup games */
    private boolean cup = true;

    /** Consider lost games */
    private boolean defeat = true;

    /** Consider draw games */
    private boolean draw = true;

    /** Consider friendly games */
    private boolean friendly = false;

    /** Consider home games */
    private boolean homeGames = true;

    /** Consider league games */
    private boolean league = true;

    /** Consider qualifier games */
    private boolean qualifier = true;

    /** Consider won games */
    private boolean win = true;
    
    /** Consider tournament games */
    private boolean tournament = false;
    
    /** Consider masters games */
    private boolean masters = false;

    /** Maximum number of games */
    private int number = 10;
    
    private static final String STORAGE_FIELD_NAME = "TAfilter";

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new Filter object.
     */
    public Filter() {
        loadFilters();
        
    }

    //~ Methods ------------------------------------------------------------------------------------

    public void setAutomatic(boolean b) {
        automatic = b;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAwayGames(boolean b) {
        awayGames = b;
    }

    public boolean isAwayGames() {
        return awayGames;
    }

    public void setCup(boolean b) {
        cup = b;
    }

    public boolean isCup() {
        return cup;
    }

    public void setDefeat(boolean b) {
        defeat = b;
    }

    public boolean isDefeat() {
        return defeat;
    }

    public void setDraw(boolean b) {
        draw = b;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setFriendly(boolean b) {
        friendly = b;
    }

    public boolean isFriendly() {
        return friendly;
    }

    public void setHomeGames(boolean b) {
        homeGames = b;
    }

    public boolean isHomeGames() {
        return homeGames;
    }

    public void setLeague(boolean b) {
        league = b;
    }

    public boolean isLeague() {
        return league;
    }
    
    public void setMasters(boolean b) {
    	masters = b;
    }
    
    public boolean isMasters() {
    	return masters;
    }

    public void setMatches(List<String> list) {
        matches = list;
    }

    public List<String> getMatches() {
        return matches;
    }

    /** Maximum number of games */
    public void setNumber(int i) {
        number = Math.min(i, 99);
        if (number < 0) {
        	number = 0;
        }
    }

    /** Maximum number of games */
    public int getNumber() {
        return number;
    }

    public void setQualifier(boolean b) {
        qualifier = b;
    }

    public boolean isQualifier() {
        return qualifier;
    }
    
    public void setTournament (boolean b) {
    	tournament = b;
    }
    
    public boolean isTournament () {
    	return tournament;
    }

    public void setWin(boolean b) {
        win = b;
    }

    public boolean isWin() {
        return win;
    }
    
    public boolean isAcceptedMatch(Match match) {
    	
    	// Check home and away
    	if ((!homeGames && match.isHome())
    			|| (!awayGames && !match.isHome())) {
    		return false;
    	}
    	
    	// if we care about result, check result
    	if (!draw && (match.getAwayGoals() == match.getHomeGoals())) {
    		return false;
    	}

    	boolean homeWin = (match.getHomeGoals() > match.getAwayGoals());
    	
    	if (!win && ((homeWin && match.isHome()) || (!homeWin && !match.isHome()))) {
    	   	return false;
    	}
    	
    	if (!defeat && ((!homeWin && match.isHome()) || (homeWin && !match.isHome()))) {
    		return false;
    	}
    	
    	// Match types
    	
    	if (match.getMatchType().isFriendly()) {
    		return friendly;
    	}
    	
    	if (match.getMatchType().isTournament()) {
    		return tournament;
    	}
    	
    	switch (match.getMatchType()) {
    	case LEAGUE : 
    		return league;
    	case CUP :
    		return cup;
    	case MASTERS :
    		return masters;
    	case QUALIFICATION :
    		return qualifier;
   	
    	}
    	
    	
    	return false;
    }
    
    public void loadFilters() {
    	
    	/* The filter values are stored in a string where 1 signals true, and everything else false. A single
    	 * character per value, ending with 2 digits for the number of matches. The order is:
    	 * 
    	 * 0 automatic
    	 * 1 awayGames
    	 * 2 homeGames
    	 * 3 Win
    	 * 4 Defeat
    	 * 5 Draw
    	 * 6 League
    	 * 7 Cup
    	 * 8 Friendly
    	 * 9 Qualifier
    	 * 10 Tournament
    	 * 11 Masters
    	 * 12-13 number (two digit number)
    	 */
    	
    	String filters = ModuleConfig.instance().getString(STORAGE_FIELD_NAME);
  
    	if (filters == null || filters.length() != 14) {
    		return;
    	}
    	
    	automatic = (filters.charAt(0) == '1') ? true : false;
    	awayGames = (filters.charAt(1) == '1') ? true : false;
    	homeGames = (filters.charAt(2) == '1') ? true : false;
    	win = (filters.charAt(3) == '1') ? true : false;
    	defeat = (filters.charAt(4) == '1') ? true : false;
    	draw = (filters.charAt(5) == '1') ? true : false;
    	league = (filters.charAt(6) == '1') ? true : false;
    	cup = (filters.charAt(7) == '1') ? true : false;
    	friendly = (filters.charAt(8) == '1') ? true : false;
    	qualifier = (filters.charAt(9) == '1') ? true : false;
    	tournament = (filters.charAt(10) == '1') ? true : false;
    	masters = (filters.charAt(11) == '1') ? true : false;
    	number = Integer.parseInt(filters.substring(12, 14));
    }
    
    public void saveFilters() {
    	// See loadFilters() for description of the fields
    	String filter = "";
    	
    	filter += (automatic) ? "1" : "0";
    	filter += (awayGames) ? "1" : "0";
    	filter += (homeGames) ? "1" : "0";
    	filter += (win) ? "1" : "0";
    	filter += (defeat) ? "1" : "0";
    	filter += (draw) ? "1" : "0";
    	filter += (league) ? "1" : "0";
    	filter += (cup) ? "1" : "0";
    	filter += (friendly) ? "1" : "0";
    	filter += (qualifier) ? "1" : "0";
    	filter += (tournament) ? "1" : "0";
    	filter += (masters) ? "1" : "0";
    	filter += String.valueOf(number);
    	
    	ModuleConfig.instance().setString(STORAGE_FIELD_NAME, filter);
    	ModuleConfig.instance().save();
    }
    

    /**
     * toString method: creates a String representation of the object
     *
     * @return the String representation
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append("Filter[");
        buffer.append("number = " + number);
        buffer.append(", awayGames = " + awayGames);
        buffer.append(", homeGames = " + homeGames);
        buffer.append(", win = " + win);
        buffer.append(", draw = " + draw);
        buffer.append(", defeat = " + defeat);
        buffer.append(", automatic = " + automatic);
        buffer.append(", matches = " + matches);
        buffer.append(", tournament = " + tournament);
        buffer.append(", master = " + masters);
        buffer.append("]");

        return buffer.toString();
    }
}
