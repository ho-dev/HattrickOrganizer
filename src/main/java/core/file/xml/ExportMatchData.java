package core.file.xml;

import core.model.match.MatchKurzInfo;
import core.model.match.Matchdetails;
import core.model.player.Spieler;

import java.util.Map;


public class ExportMatchData {
	
	private Map<Integer,Spieler> players;
	private Matchdetails details;
	private MatchKurzInfo info;

	public Matchdetails getDetails() {
		return details;
	}

	public MatchKurzInfo getInfo() {
		return info;
	}

	public Map<Integer,Spieler> getPlayers() {
		return players;
	}

	public void setDetails(Matchdetails details) {
		this.details = details;
	}

	public void setInfo(MatchKurzInfo info) {
		this.info = info;
	}

	public void setPlayers(Map<Integer,Spieler> map) {
		players = map;
	}

}
