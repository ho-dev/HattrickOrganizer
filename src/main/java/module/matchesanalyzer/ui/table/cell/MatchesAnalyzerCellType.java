package module.matchesanalyzer.ui.table.cell;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.MatchLineupTeam;
import core.model.match.Matchdetails;
import module.matchesanalyzer.data.MatchesAnalyzerVenue;
import module.matchesanalyzer.data.MatchesAnalyzerMatch;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellContent;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellIcon;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellLineup;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellStat;
import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellText;

import java.awt.Color;

import javax.swing.SwingConstants;


public enum MatchesAnalyzerCellType {
	VENUE			(HOVerwaltung.instance().getLanguageString("Venue"),								HOVerwaltung.instance().getLanguageString("Venue"),									new MatchesAnalyzerCellStyle(20,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_MATCH_BG),	null,																																						true,	false,	false, SwingConstants.CENTER)),
	MATCH			(HOVerwaltung.instance().getLanguageString("Spiele"),								HOVerwaltung.instance().getLanguageString("Spiele"),								new MatchesAnalyzerCellStyle(255,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_MATCH_BG),	null,																																						false,	true,	false, SwingConstants.LEFT)),
	RESULT			(HOVerwaltung.instance().getLanguageString("ls.match.result"),						HOVerwaltung.instance().getLanguageString("ls.match.result"),						new MatchesAnalyzerCellStyle(50,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_MATCH_BG),	null,																																						false,	true,	false, SwingConstants.CENTER)),
	TYPE			(HOVerwaltung.instance().getLanguageString("Spielart"),								HOVerwaltung.instance().getLanguageString("Spielart"),								new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_BG),	null,																																						true,	false,	false, SwingConstants.CENTER)),
	WEATHER			(HOVerwaltung.instance().getLanguageString("ls.match.weather"),						HOVerwaltung.instance().getLanguageString("ls.match.weather"),						new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_BG),	null,																																						true,	false,	false, SwingConstants.CENTER)),
	LINEUP			(HOVerwaltung.instance().getLanguageString("ls.team.formation"),					HOVerwaltung.instance().getLanguageString("ls.team.formation"),						new MatchesAnalyzerCellStyle(45,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_LINEUP_BG),	null,																																						true,	false,	false, SwingConstants.CENTER)),
	TACTIC			(HOVerwaltung.instance().getLanguageString("ls.team.tactic"),						HOVerwaltung.instance().getLanguageString("ls.team.tactic"),						new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_LINEUP_BG),	null,																																						false,	true,	false, SwingConstants.CENTER)),
	TACTIC_SKILL	(HOVerwaltung.instance().getLanguageString("ls.team.tacticalskill"),				HOVerwaltung.instance().getLanguageString("ls.team.tacticalskill"),					new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_LINEUP_BG),	null,																																						true,	true,	false, SwingConstants.CENTER)),
	STYLEOFPLAY		(HOVerwaltung.instance().getLanguageString("ls.team.styleofPlay"),					HOVerwaltung.instance().getLanguageString("ls.team.styleofPlay"),					new MatchesAnalyzerCellStyle(125,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_LINEUP_BG),	null,																																						false,	true,	false, SwingConstants.LEFT)),
	ATTITUDE		(HOVerwaltung.instance().getLanguageString("ls.team.teamattitude"),					HOVerwaltung.instance().getLanguageString("ls.team.teamattitude"),					new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_BG),	null,																																						true,	false,	false, SwingConstants.CENTER)),
	MIDFIELD		(HOVerwaltung.instance().getLanguageString("matchesanalyzer.midfield_short"),		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.midfield"),		new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	RIGHT_DEFENCE	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.rightdefence_short"),	HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightdefence"),	new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	CENTRAL_DEFENCE	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.centraldefence_short"),	HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centraldefence"),	new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	LEFT_DEFENCE	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.leftdefence_short"),	HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftdefence"),		new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	RIGHT_ATTACK	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.rightattack_short"),	HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.rightattack"),		new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	CENTRAL_ATTACK	(HOVerwaltung.instance().getLanguageString("matchesanalyzer.centralattack_short"),	HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.centralattack"),	new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	LEFT_ATTACK		(HOVerwaltung.instance().getLanguageString("matchesanalyzer.leftattack_short"),		HOVerwaltung.instance().getLanguageString("ls.match.ratingsector.leftattack"),		new MatchesAnalyzerCellStyle(35,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_STATS_BG),	new Color[] {ThemeManager.getColor(HOColorName.MATCHESANALYZER_NEGATIVE_BAR_BG),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_POSITIVE_BAR_BG)},	true,	true,	false, SwingConstants.CENTER)),
	HATSTATS		(HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.hatstats"),			HOVerwaltung.instance().getLanguageString("ls.match.ratingtype.hatstats"),			new MatchesAnalyzerCellStyle(40,	ThemeManager.getColor(HOColorName.MATCHESANALYZER_DEFAULT_FONT),	ThemeManager.getColor(HOColorName.MATCHESANALYZER_OVERALL_BG),	null,																																						false,	true,	false, SwingConstants.CENTER));
	
	private final String header;
	private final String tooltip;
	private final MatchesAnalyzerCellStyle style;

	private MatchesAnalyzerCellType(String header, String tooltip, MatchesAnalyzerCellStyle style) {
		this.header = header;
		this.tooltip = tooltip;
		this.style = style;
	}

	public String getHeader() {
		return header;
	}
	
	public String getTooltip() {
		return tooltip;
	}

	public MatchesAnalyzerCellStyle getStyle() {
		return style;
	}

	public static MatchesAnalyzerCellContent valueOf(MatchesAnalyzerMatch match, int columnIndex) {
		int stat = 0;
		MatchesAnalyzerCellType type = values()[columnIndex];

		switch(type) {
			case VENUE:
				MatchesAnalyzerVenue venue = match.getVenue();
				return new MatchesAnalyzerCellIcon(venue.getIcon(), venue.getName(), type);
			case MATCH:
				return new MatchesAnalyzerCellText(match.getHomeTeam() + " - " + match.getAwayTeam(), type);
			case RESULT:
				return new MatchesAnalyzerCellText(match.getHomeScore() + " - " + match.getAwayScore(), type);
			case WEATHER:
				return new MatchesAnalyzerCellIcon(match.getWeather().getIcon(), match.getWeather().getName(), type);
			case TYPE:
				return new MatchesAnalyzerCellIcon(match.getType().getIcon(), match.getType().getName(), type);
			case LINEUP:
				return new MatchesAnalyzerCellLineup(match.getLineup(), type);
			case TACTIC:
				//return new MatchesAnalyzerCellText(match.getTactic().getName(), type);
				// get short name for tactics to avoid scrollbar in the table
				return new MatchesAnalyzerCellText(Matchdetails.getShortTacticName(match.getTactic()), type);
			case TACTIC_SKILL:
				int skill = match.getTacticSkill();
				if(skill < 0) return new MatchesAnalyzerCellText("-", type);
				else return new MatchesAnalyzerCellStat(skill, MatchesAnalyzerCellValue.NONE, type);
			case STYLEOFPLAY:
				int styleOfPlay = match.getStyleOfPlay();
				if (styleOfPlay == -1000) return new MatchesAnalyzerCellText("", type);
				else return new MatchesAnalyzerCellText(MatchLineupTeam.getStyleOfPlayName(match.getStyleOfPlay()), type);
			case ATTITUDE:
				return new MatchesAnalyzerCellIcon(match.getAttitude().getIcon(), match.getAttitude().getName(), type);
			case MIDFIELD:
				return new MatchesAnalyzerCellStat(match.getMidfield(), match.getMidfieldRatio(), type);
			case RIGHT_DEFENCE:
				return new MatchesAnalyzerCellStat(match.getRightDefence(), match.getRightDefenceRatio(), type);
			case CENTRAL_DEFENCE:
				return new MatchesAnalyzerCellStat(match.getCentralDefence(), match.getCentralDefenceRatio(), type);
			case LEFT_DEFENCE:
				return new MatchesAnalyzerCellStat(match.getLeftDefence(), match.getLeftDefenceRatio(), type);
			case RIGHT_ATTACK:
				return new MatchesAnalyzerCellStat(match.getRightAttack(), match.getRightAttackRatio(), type);
			case CENTRAL_ATTACK:
				return new MatchesAnalyzerCellStat(match.getCentralAttack(), match.getCentralAttackRatio(), type);
			case LEFT_ATTACK:
				return new MatchesAnalyzerCellStat(match.getLeftAttack(), match.getLeftAttackRatio(), type);
			case HATSTATS:
				return new MatchesAnalyzerCellText(String.valueOf(match.getHatStats()), type);
			default:
				return null;
		}
	}

}
