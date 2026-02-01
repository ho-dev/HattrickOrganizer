package module.specialevents;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.IHOTableCellEntry;
import core.gui.comp.table.HOTableModel;
import core.gui.comp.table.UserColumn;
import core.gui.model.UserColumnController;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.match.IMatchDetails;
import core.model.match.MatchEvent;
import core.model.match.Matchdetails;
import core.util.HODateTime;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.nonNull;

public class SpecialEventsTableModel extends HOTableModel {

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
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        var ret = new ColorLabelEntry(
                            HODateTime.toEpochSecond(entry.getMatch().getMatchDate()),
                            HODateTime.toLocaleDateTime(entry.getMatch().getMatchDate()),
                            ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        Optional.ofNullable(entry.getMatch().getMatchType().getIcon()).ifPresent(ret::setIcon);
						return ret;
					}
				},
				new SpecialEventsColumn("ls.team.tactic") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
						var tacticId = entry.getMatch().getHostingTeamTactic();
						var ret = new ColorLabelEntry(tacticId, Matchdetails.getShortTacticName(tacticId), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setIcon(getTacticIcon(tacticId));
						return ret;
					}
				},
				new SpecialEventsColumn("Heim") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        return new ColorLabelEntry(entry.getMatch().getHostingTeam(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("ls.match.result") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        return new ColorLabelEntry(entry.getMatch().getMatchResult(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("Gast") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
						return new ColorLabelEntry(entry.getMatch().getVisitingTeam(), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("ls.team.tactic") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
						var tacticId = entry.getMatch().getVisitingTeamTactic();
						var ret = new ColorLabelEntry(tacticId, Matchdetails.getShortTacticName(tacticId), ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
						ret.setIcon(getTacticIcon(tacticId));
						return ret;
					}
				},
				new SpecialEventsColumn("ls.match.minute") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        final var minute = entry.getMatchHighlight().map(MatchEvent::getMinute);
                        return new ColorLabelEntry(
                            minute.orElse(0),
                            minute.map(String::valueOf).orElse(StringUtils.EMPTY),
                            ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				},
				new SpecialEventsColumn("Event") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        var ret = new ColorLabelEntry(StringUtils.EMPTY, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        entry.getMatchHighlight().ifPresent(highlight -> highlight.getIcons().forEach(ret::addIcon));
						return ret;
					}
				},
				new SpecialEventsColumn("ls.match.event.details") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
						final var matchHighlight = entry.getMatchHighlight();
						var ret =  new ColorLabelEntry(
                            matchHighlight.map(SpecialEventsDM::getSEText).orElse(StringUtils.EMPTY),
                            ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
                        matchHighlight.map(MatchEvent::getEventText).ifPresent(eventText ->
                            ret.setToolTipText("<html>" + "<table width='300'><tr><td>" + eventText + "</td></tr></table></html>"));
						return ret;
					}
				},
				new SpecialEventsColumn("Spieler") {
					@Override
					public IHOTableCellEntry getTableEntry(MatchRow entry) {
                        final String text = entry.getMatchHighlight().map(matchEvent -> {
                                String involvedPlayers = matchEvent.getPlayerName();
                                if (StringUtils.isNotEmpty(matchEvent.getPlayerName())) {
                                    var model = HOVerwaltung.instance().getModel();
                                    if (nonNull(model.getCurrentPlayer(matchEvent.getAssistingPlayerId())) ||
                                        model.getFormerPlayers().stream().anyMatch(player -> player.getPlayerId() == matchEvent.getAssistingPlayerId())) {
                                        involvedPlayers = involvedPlayers + " - %s".formatted(matchEvent.getAssistingPlayerName());
                                    }
                                }
                                return involvedPlayers;
                            }
                        ).orElse(StringUtils.EMPTY);

						return new ColorLabelEntry(text, ColorLabelEntry.FG_STANDARD, ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
					}
				}
		)).toArray(new SpecialEventsColumn[0]);
	}

	private static Icon getTacticIcon(int tacticId) {
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
}
