package module.matchesanalyzer.data;

import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;

import java.util.Comparator;
import java.util.Date;


public class MatchesAnalyzerMatch {

	private final int id;
	private final Date date;

	private final MatchesAnalyzerVenue venue;
	
	private final String homeTeam;
	private final String awayTeam;

	private final int homeScore;
	private final int awayScore;

	private final MatchesAnalyzerMatchType type;
	private final MatchesAnalyzerWeather weather;
	private final MatchesAnalyzerLineup lineup;

	//private final MatchesAnalyzerTactic tactic;
	private final int tactic;
	private final int tacticSkill;
	private final int styleOfPlay;

	private final MatchesAnalyzerAttitude attitude;

	private final int midfield;
	private final int rightDefence;
	private final int centralDefence;
	private final int leftDefence;
	private final int rightAttack;
	private final int centralAttack;
	private final int leftAttack;

	private final double midfieldRatio;
	private final double rightDefenceRatio;
	private final double centralDefenceRatio;
	private final double leftDefenceRatio;
	private final double rightAttackRatio;
	private final double centralAttackRatio;
	private final double leftAttackRatio;

	private int hatStats;

	public MatchesAnalyzerMatch(int teamId, MatchKurzInfo info, Matchdetails details, MatchLineupTeam lineupTeam) {
		boolean homeMatch = (info.getHeimID() == teamId);

		// @todo support derby matches
		venue = (homeMatch ? MatchesAnalyzerVenue.HOME : MatchesAnalyzerVenue.AWAY);
		
		id = info.getMatchID();
		date = new Date(info.getMatchDateAsTimestamp().getTime());

		homeTeam = details.getHeimName();
		awayTeam = details.getGastName();

		homeScore = details.getHomeGoals();
		awayScore = details.getGuestGoals();

		weather = MatchesAnalyzerWeather.values()[details.getWetterId()];

		type = MatchesAnalyzerMatchType.values()[info.getMatchTyp().ordinal()];

		lineup = new MatchesAnalyzerLineup(lineupTeam, details);

		/*tactic = MatchesAnalyzerTactic.values()[homeMatch ? details.getHomeTacticType() : details.getGuestTacticType()];
		if(tactic != MatchesAnalyzerTactic.NORMAL && tactic != MatchesAnalyzerTactic.PLAY_CREATIVELY) {
			tacticSkill = (homeMatch ? details.getHomeTacticSkill() : details.getGuestTacticSkill());
		} else {
			tacticSkill = -1;
		}*/
		tactic = homeMatch ? details.getHomeTacticType() : details.getGuestTacticType();
		// if tactic is not normal or play creatively
		if (tactic != 0 && tactic != 7) {
			tacticSkill = (homeMatch ? details.getHomeTacticSkill() : details.getGuestTacticSkill());
		} else {
			tacticSkill = -1;	
		}
		
		styleOfPlay = lineupTeam.getStyleOfPlay();
		attitude = MatchesAnalyzerAttitude.toEnum(homeMatch ? details.getHomeEinstellung() : details.getGuestEinstellung());

		double ratio[] = new double[1];
		int temp = getStat(homeMatch ? details.getHomeMidfield() : details.getGuestMidfield(), !homeMatch ? details.getHomeMidfield() : details.getGuestMidfield(), ratio);
		midfield = temp;
		midfieldRatio = ratio[0];
		
		temp = getStat(homeMatch ? details.getHomeRightDef() : details.getGuestRightDef(), !homeMatch ? details.getHomeLeftAtt() : details.getGuestLeftAtt(), ratio);
		rightDefence = temp;
		rightDefenceRatio = ratio[0];

		temp = getStat(homeMatch ? details.getHomeMidDef() : details.getGuestMidDef(), !homeMatch ? details.getHomeMidAtt() : details.getGuestMidAtt(), ratio);
		centralDefence = temp;
		centralDefenceRatio = ratio[0];

		temp = getStat(homeMatch ? details.getHomeLeftDef() : details.getGuestLeftDef(), !homeMatch ? details.getHomeRightAtt() : details.getGuestRightAtt(), ratio);
		leftDefence = temp;
		leftDefenceRatio = ratio[0];

		temp = getStat(homeMatch ? details.getHomeRightAtt() : details.getGuestRightAtt(), !homeMatch ? details.getHomeLeftDef() : details.getGuestLeftDef(), ratio);
		rightAttack = temp;
		rightAttackRatio = ratio[0];

		temp = getStat(homeMatch ? details.getHomeMidAtt() : details.getGuestMidAtt(), !homeMatch ? details.getHomeMidDef() : details.getGuestMidDef(), ratio);
		centralAttack = temp;
		centralAttackRatio = ratio[0];

		temp = getStat(homeMatch ? details.getHomeLeftAtt() : details.getGuestLeftAtt(), !homeMatch ? details.getHomeRightDef() : details.getGuestRightDef(), ratio);
		leftAttack = temp;
		leftAttackRatio = ratio[0];

		hatStats = (homeMatch ? details.getHomeHatStats() : details.getAwayHatStats());
	}

	private int getStat(int stat1, int stat2, double[] ratio) {
		ratio[0] = Double.valueOf(stat1) / Double.valueOf(stat1 + stat2) * 100.0d;
		return stat1 * (stat1 < stat2 ? -1 : 1);
	}

	public MatchesAnalyzerVenue getVenue() {
		return venue;
	}
	
	public int getId() {
		return id;
	}

	public Date getDate() {
		return date;
	}

	public String getHomeTeam() {
		return homeTeam;
	}

	public String getAwayTeam() {
		return awayTeam;
	}

	public int getHomeScore() {
		return homeScore;
	}

	public int getAwayScore() {
		return awayScore;
	}

	public MatchesAnalyzerMatchType getType() {
		return type;
	}

	public MatchesAnalyzerWeather getWeather() {
		return weather;
	}

	public MatchesAnalyzerLineup getLineup() {
		return lineup;
	}

	public int getTactic() {
		return tactic;
	}

	public int getTacticSkill() {
		return tacticSkill;
	}

	public MatchesAnalyzerAttitude getAttitude() {
		return attitude;
	}

	public int getMidfield() {
		return midfield;
	}

	public int getRightDefence() {
		return rightDefence;
	}

	public int getCentralDefence() {
		return centralDefence;
	}

	public int getLeftDefence() {
		return leftDefence;
	}

	public int getRightAttack() {
		return rightAttack;
	}

	public int getCentralAttack() {
		return centralAttack;
	}

	public int getLeftAttack() {
		return leftAttack;
	}

	public int getHatStats() {
		return hatStats;
	}

	public double getMidfieldRatio() {
		return midfieldRatio;
	}

	public double getRightDefenceRatio() {
		return rightDefenceRatio;
	}

	public double getCentralDefenceRatio() {
		return centralDefenceRatio;
	}

	public double getLeftDefenceRatio() {
		return leftDefenceRatio;
	}

	public double getRightAttackRatio() {
		return rightAttackRatio;
	}

	public double getCentralAttackRatio() {
		return centralAttackRatio;
	}

	public double getLeftAttackRatio() {
		return leftAttackRatio;
	}
	
	public int getStyleOfPlay() {
		return styleOfPlay;
	}

	public void setHatStats(int hatStats) {
		this.hatStats = hatStats;
	}

	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(!(obj instanceof MatchesAnalyzerMatch)) return false;
		return(((MatchesAnalyzerMatch)obj).getId() == getId());
	}

	public static Comparator<MatchesAnalyzerMatch> comparator() {
		return new Comparator<MatchesAnalyzerMatch>() {
			@Override
			public int compare(MatchesAnalyzerMatch a, MatchesAnalyzerMatch b) {
				return a.getDate().compareTo(b.getDate());
			}
		};
	}

	public static Comparator<MatchesAnalyzerMatch> reverse_comparator() {
		return new Comparator<MatchesAnalyzerMatch>() {
			@Override
			public int compare(MatchesAnalyzerMatch a, MatchesAnalyzerMatch b) {
				return b.getDate().compareTo(a.getDate());
			}
		};
	}

}
