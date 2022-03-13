package module.ifa.model;


import core.util.HODateTime;

public class IfaStatistic {

	private int matchesPlayed;
	private int matchesWon;
	private int matchesDraw;
	private int matchesLost;
	private Country country;
	private HODateTime lastMatchDate;

	public Country getCountry() {
		return this.country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public int getMatchesPlayed() {
		return this.matchesPlayed;
	}

	public int getMatchesWon() {
		return this.matchesWon;
	}

	public int getMatchesDraw() {
		return this.matchesDraw;
	}

	public int getMatchesLost() {
		return this.matchesLost;
	}

	public HODateTime getLastMatchDate() {
		return this.lastMatchDate;
	}
	
	public void setLastMatchDate(HODateTime timestamp) {
		this.lastMatchDate = timestamp;
	}

	public void increasePlayed() {
		this.matchesPlayed++;
	}

	public void increaseWon() {
		this.matchesWon++;
	}

	public void increaseDraw() {
		this.matchesDraw++;
	}

	public void increaseLost() {
		this.matchesLost++;
	}
}
