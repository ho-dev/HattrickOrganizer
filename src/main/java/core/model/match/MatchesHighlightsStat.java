package core.model.match;

import core.model.HOVerwaltung;

public class MatchesHighlightsStat {

	private String description;
	private int noGoals;
	private int goals;
	private boolean ownTeam;
	private String types;
	private String subtyps;
	
	public MatchesHighlightsStat(String key){
		description = HOVerwaltung.instance().getLanguageString(key);
		goals=-1;
		noGoals=-1;
	}
	
	
	public MatchesHighlightsStat(String key,String subtyps){
		this(key,"1,2",subtyps);
	}
	
	public MatchesHighlightsStat(String key,String types,String subtyps){
		description = HOVerwaltung.instance().getLanguageString(key);
		this.types = types;
		this.subtyps = subtyps;
		goals=-1;
	}

	public void appendDescription(String append){
		description = description +" "+HOVerwaltung.instance().getLanguageString(append);
	}
	
	public String getDescription() {
		return description;
	}

	public int getNoGoals() {
		return noGoals;
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
	public boolean isOwnTeam() {
		return ownTeam;
	}
	public void setOwnTeam(boolean ownTeam) {
		this.ownTeam = ownTeam;
	}

	public String getSubtyps() {
		return subtyps;
	}

	public String getTypes() {
		return types;
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
		if(goals == -1)
			return " - ";
		return String.valueOf((goals*100/(goals+noGoals))+" %");
	}
	public boolean isTitle() {
		return goals==-1&&noGoals==-1;
	}
	
	@Override
	public String toString(){
		return getDescription();
	}
}
