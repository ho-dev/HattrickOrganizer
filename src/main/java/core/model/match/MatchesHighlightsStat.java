package core.model.match;

import core.model.HOVerwaltung;

import java.util.List;

public class MatchesHighlightsStat {

	private final String description;
	private int noGoals;
	private int goals;
	private final List<MatchEvent.MatchEventID> subtypes;
	
	public MatchesHighlightsStat(String key,List<MatchEvent.MatchEventID> subtypes){
		description = HOVerwaltung.instance().getLanguageString(key);
		this.subtypes = subtypes;
		goals=-1;
	}

	public String getDescription() {
		return description;
	}

	public void setNoGoals(int noGoals) {
		this.noGoals = noGoals;
	}
	public int getGoals() {
		return goals;
	}
	public void setGoals(int goals) {
		this.goals = goals;
	}
	public List<MatchEvent.MatchEventID> getSubtyps() {
		return subtypes;
	}

	public String getTotalString(){
		if(isTitle())
			return "";
		if(goals>-1)
			return String.valueOf((goals+noGoals));
		
		return noGoals+"";
	}
	
	public String getGoalsString(){
		if(isTitle())
			return "";
		if(goals == -1)
			return " - ";
		return String.valueOf(goals);
	}
	public String getPerformanceString(){
		if(isTitle())
			return "";
		if((goals == -1) || ((goals+noGoals)==0)) {return " - ";}
		return goals*100/(goals+noGoals)+" %";
	}
	public boolean isTitle() {
		return goals==-1&&noGoals==-1;
	}
	
	@Override
	public String toString(){
		return getDescription();
	}
}
