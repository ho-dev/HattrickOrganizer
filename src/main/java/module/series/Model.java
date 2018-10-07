package module.series;

public class Model {

	private Spielplan currentSeries;
	private String currentTeam;

	public Spielplan getCurrentSeries() {
		return currentSeries;
	}

	public void setCurrentSeries(Spielplan currentSeries) {
		this.currentSeries = currentSeries;
	}

	public String getCurrentTeam() {
		return currentTeam;
	}

	public void setCurrentTeam(String currentTeam) {
		this.currentTeam = currentTeam;
	}
}
