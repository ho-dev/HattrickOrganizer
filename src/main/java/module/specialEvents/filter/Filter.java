package module.specialEvents.filter;

import module.specialEvents.SeasonFilterValue;

import java.util.ArrayList;
import java.util.List;

public class Filter {

	// matches
	private boolean showMatchesWithSEOnly = true;
	private SeasonFilterValue seasonFilterValue = SeasonFilterValue.CURRENT_SEASON;
	private Integer tactic;
	// the single matchtypes
	private boolean showFriendlies = true;
	private boolean showLeague = true;
	private boolean showRelegation = true;
	private boolean showTournament = true;
	private boolean showCup = true;
	private boolean showMasters = true;
	// SE filters
	private boolean showSpecialitySE = true;
	private boolean showWeatherSE = true;
	private boolean showCounterAttack = true;
	private boolean showFreeKick = true;
	private boolean showManMarking = true;
	private boolean showPenalty = true;
	private boolean showLongShot = true;
	// player
	private Integer playerId;
	private boolean showCurrentOwnPlayersOnly;
	private boolean showOwnPlayersOnly;
	
	private final List<FilterChangeListener> listeners = new ArrayList<FilterChangeListener>();
	
	public Integer getTactic() {
		return tactic;
	}

	public void setTactic(Integer tactic) {
		if ((this.tactic != null && tactic == null)
				|| (this.tactic == null && tactic != null)
				|| (this.tactic != null && tactic != null && this.tactic.compareTo(tactic) != 0)) {
			this.tactic = tactic;
			fireFilterChanged();
		}
	}

	public boolean isShowMatchesWithSEOnly() {
		return showMatchesWithSEOnly;
	}

	public void setShowMatchesWithSEOnly(boolean showMatchesWithSEOnly) {
		if (this.showMatchesWithSEOnly != showMatchesWithSEOnly) {
			this.showMatchesWithSEOnly = showMatchesWithSEOnly;
			fireFilterChanged();
		}
	}

	public boolean isShowFriendlies() {
		return showFriendlies;
	}

	public void setShowFriendlies(boolean showFriendlies) {
		if (this.showFriendlies != showFriendlies) {
			this.showFriendlies = showFriendlies;
			fireFilterChanged();
		}
		this.showFriendlies = showFriendlies;
	}

	public boolean isShowLeague() {
		return showLeague;
	}

	public void setShowLeague(boolean showLeague) {
		if (this.showLeague != showLeague) {
			this.showLeague = showLeague;
			fireFilterChanged();
		}
	}

	public boolean isShowRelegation() {
		return showRelegation;
	}

	public void setShowRelegation(boolean showRelegation) {
		if (this.showRelegation != showRelegation) {
			this.showRelegation = showRelegation;
			fireFilterChanged();
		}
	}

	public boolean isShowTournament() {
		return showTournament;
	}

	public void setShowTournament(boolean showTournament) {
		if (this.showTournament != showTournament) {
			this.showTournament = showTournament;
			fireFilterChanged();
		}
	}

	public boolean isShowCup() {
		return showCup;
	}

	public void setShowCup(boolean showCup) {
		if (this.showCup != showCup) {
			this.showCup = showCup;
			fireFilterChanged();
		}
	}

	public boolean isShowMasters() {
		return showMasters;
	}

	public void setShowMasters(boolean showMasters) {
		if (this.showMasters != showMasters) {
			this.showMasters = showMasters;
			fireFilterChanged();
		}
	}

	public boolean isShowSpecialitySE() {
		return showSpecialitySE;
	}

	public void setShowSpecialitySE(boolean showSpecialitySE) {
		if (this.showSpecialitySE != showSpecialitySE) {
			this.showSpecialitySE = showSpecialitySE;
			fireFilterChanged();
		}
	}

	public boolean isShowWeatherSE() {
		return showWeatherSE;
	}

	public void setShowWeatherSE(boolean showWeatherSE) {
		if (this.showWeatherSE != showWeatherSE) {
			this.showWeatherSE = showWeatherSE;
			fireFilterChanged();
		}
	}

	public boolean isShowCounterAttack() {
		return showCounterAttack;
	}

	public void setShowCounterAttack(boolean showCounterAttack) {
		if (this.showCounterAttack != showCounterAttack) {
			this.showCounterAttack = showCounterAttack;
			fireFilterChanged();
		}
	}

	public boolean isShowFreeKick() {
		return showFreeKick;
	}

	public void setShowFreeKick(boolean showFreeKick) {
		if (this.showFreeKick != showFreeKick) {
			this.showFreeKick = showFreeKick;
			fireFilterChanged();
		}
	}

	public boolean isShowManMarking() {
		return showManMarking;
	}

	public void setShowManMarking(boolean _showManMarking) {
		if (this.showManMarking != _showManMarking) {
			this.showManMarking = _showManMarking;
			fireFilterChanged();
		}
	}

	public boolean isShowPenalty() {
		return showPenalty;
	}

	public void setShowPenalty(boolean showPenalty) {
		if (this.showPenalty != showPenalty) {
			this.showPenalty = showPenalty;
			fireFilterChanged();
		}
	}

	public boolean isShowLongShot() {
		return showLongShot;
	}

	public void setShowLongShot(boolean showLongShot) {
		if (this.showLongShot != showLongShot) {
			this.showLongShot = showLongShot;
			fireFilterChanged();
		}
	}

	public SeasonFilterValue getSeasonFilterValue() {
		return seasonFilterValue;
	}

	public void setSeasonFilterValue(SeasonFilterValue seasonFilterValue) {
		if (this.seasonFilterValue != seasonFilterValue) {
			this.seasonFilterValue = seasonFilterValue;
			fireFilterChanged();
		}
	}

	public Integer getPlayerId() {
		return playerId;
	}

	public void setPlayerId(Integer playerId) {
		if ((this.playerId != null && playerId == null)
				|| (this.playerId == null && playerId != null)
				|| (this.playerId != null && playerId != null && this.playerId.compareTo(playerId) != 0)) {
			this.playerId = playerId;
			fireFilterChanged();
		}
	}

	public boolean isShowCurrentOwnPlayersOnly() {
		return showCurrentOwnPlayersOnly;
	}

	public void setShowCurrentOwnPlayersOnly(boolean showCurrentOwnPlayersOnly) {
		if (this.showCurrentOwnPlayersOnly != showCurrentOwnPlayersOnly) {
			if (showCurrentOwnPlayersOnly) {
				// bypass the setter to avoid firing an extra event
				this.showOwnPlayersOnly = true;
			}
			this.showCurrentOwnPlayersOnly = showCurrentOwnPlayersOnly;
			fireFilterChanged();
		}
	}

	public boolean isShowOwnPlayersOnly() {
		return showOwnPlayersOnly;
	}

	public void setShowOwnPlayersOnly(boolean showOwnPlayersOnly) {
		if (this.showOwnPlayersOnly != showOwnPlayersOnly) {
			this.showOwnPlayersOnly = showOwnPlayersOnly;
			fireFilterChanged();
		}
	}
	
	public void addFilterChangeListener(FilterChangeListener listener) {
		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	public void removeFilterChangeListener(FilterChangeListener listener) {
		this.listeners.remove(listener);
	}

	private void fireFilterChanged() {
		FilterChangeEvent evt = new FilterChangeEvent();
		for (int i = this.listeners.size() - 1; i >= 0; i--) {
			this.listeners.get(i).filterChanged(evt);
		}
	}
}
