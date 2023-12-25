package module.specialEvents;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.util.HODateTime;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class SpecialEventsTableModel extends HOTableModel {

//	static final int MATCH_DATE_TYPE_COLUMN = 0;
//	static final int HOMETACTICCOLUMN = 1;
//	static final int HOMETEAMCOLUMN = 2;
//	static final int RESULTCOLUMN = 3;
//	static final int AWAYTEAMCOLUMN = 4;
//	static final int AWAYTACTICCOLUMN = 5;
//	static final int MINUTECOLUMN = 6;
//	static final int EVENTCOLUMN = 7;
//	static final int PLAYER_NAME_COLUMN = 8;
//	static final List<Integer> HEADER_ROWS = List.of(MATCH_DATE_TYPE_COLUMN, HOMETACTICCOLUMN, HOMETEAMCOLUMN, RESULTCOLUMN, AWAYTEAMCOLUMN, AWAYTACTICCOLUMN);

	private List<MatchRow> data;

	/**
	 * constructor
	 *
	 * @param id   model id
	 */
	public SpecialEventsTableModel(UserColumnController.ColumnModelId id) {
		super(id, "SpecialEvents");
		this.columns = new ArrayList<>(List.of(
				new SpecialEventsColumn("SpieleDetails") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var highlight = entry.getMatchHighlight();
						var ret = new ColorLabelEntry(HODateTime.toEpochSecond(highlight.getMatchDate()), HODateTime.toLocaleDateTime(highlight.getMatchDate()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setIcon(highlight.getMatchType().getIcon());
						return ret;
					}
				},
				new SpecialEventsColumn("ls.team.tactic") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var tacticId = entry.getMatch().getHostingTeamTactic();
						var ret = new ColorLabelEntry(tacticId, "", ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setIcon(getTacticIcon(tacticId));
						return ret;
					}
				},
				new SpecialEventsColumn("Heim") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						return  new ColorLabelEntry(entry.getMatch().getHostingTeam(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("ls.match.result") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						return  new ColorLabelEntry(entry.getMatch().getMatchResult(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("Gast") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						return  new ColorLabelEntry(entry.getMatch().getVisitingTeam(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("ls.team.guest.tactic") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var tacticId = entry.getMatch().getVisitingTeamTactic();
						var ret = new ColorLabelEntry(tacticId, "", ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setIcon(getTacticIcon(tacticId));
						return ret;
					}
				},
				new SpecialEventsColumn("ls.match.minute") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						return  new ColorLabelEntry(entry.getMatchHighlight().getMinute(), String.valueOf(entry.getMatchHighlight().getMinute()), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("Event") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var ret =  new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						var highlight = entry.getMatchHighlight();
						for ( var icon : highlight.getIcons()){
							ret.addIcon(icon);
						}
						return ret;
					}
				},
				new SpecialEventsColumn("ls.match.event.details") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var matchHighlight = entry.getMatchHighlight();
						var ret =   new ColorLabelEntry(SpecialEventsDM.getSEText(matchHighlight), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						var eventText = matchHighlight.getEventText();
						if (eventText != null) {
							var sb = new StringBuilder("<html>");
							sb.append("<table width='300'><tr><td>").append(eventText).append("</td></tr></table></html>");
							ret.setToolTipText(sb.toString());
						}
						return ret;
					}
				},
				new SpecialEventsColumn("Spieler") {
					@Override
					public IHOTableEntry getTableEntry(MatchRow entry) {
						var highlight = entry.getMatchHighlight();
						var playerName = highlight.getPlayerName();
						var sb = new StringBuilder();
						if (!playerName.isEmpty()){
							sb.append(playerName);
							var assistingPlayerId = highlight.getAssistingPlayerId();
							if (HOVerwaltung.instance().getModel().getCurrentPlayer(assistingPlayerId) != null ||
                                    HOVerwaltung.instance().getModel().getFormerPlayers().stream().anyMatch(i->i.getPlayerId()==assistingPlayerId)){
								sb.append(" - ").append(highlight.getAssistingPlayerName());
							}
						}

						return  new ColorLabelEntry(sb.toString(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				}
		)).toArray(new SpecialEventsColumn[0]);
	}

	private Icon getTacticIcon(int tacticId) {
		return  switch (tacticId) {
			case IMatchDetails.TAKTIK_PRESSING -> ThemeManager.getIcon(HOIconName.TACTIC_PRESSING);
			case IMatchDetails.TAKTIK_KONTER -> ThemeManager.getIcon(HOIconName.TACTIC_COUNTER_ATTACKING);
			case IMatchDetails.TAKTIK_MIDDLE -> ThemeManager.getIcon(HOIconName.TACTIC_AIM);
			case IMatchDetails.TAKTIK_WINGS -> ThemeManager.getIcon(HOIconName.TACTIC_AOW);
			case IMatchDetails.TAKTIK_CREATIVE -> ThemeManager.getIcon(HOIconName.TACTIC_PLAY_CREATIVELY);
			case IMatchDetails.TAKTIK_LONGSHOTS -> ThemeManager.getIcon(HOIconName.TACTIC_LONG_SHOTS);
			default -> null;
		};
	}

	public void setData(List<MatchRow> data) {
		this.data = data;
		initData();
	}
	@Override
	protected void initData() {
		UserColumn[] displayedColumns = getDisplayedColumns();
		m_clData = new Object[data.size()][columns.length];
		int rownum = 0;
		for (var row : data) {
			int columnnum = 0;
			for (var col : displayedColumns) {
				m_clData[rownum][columnnum] = ((SpecialEventsColumn) col).getTableEntry(row);
				columnnum++;
			}
			rownum++;
		}
		fireTableDataChanged();
	}

	public Match getMatch(int row) {
		return this.data.get(row).getMatch();
	}

	public int getMatchCount(int row) {
		return this.data.get(row).getMatchCount();
	}
}
