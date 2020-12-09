package module.lineup.assistant;

import core.model.match.Weather;
import module.lineup.lineup.PlayerPositionPanel;

import java.util.List;
import java.util.Map;

public interface ILineupAssistantPanel {

	boolean isExcludeLastMatch();

	boolean isConsiderForm();

	boolean isIgnoreSuspended();

	String getGroup();
	
	List<String> getGroups();

	boolean isGroupFilter();

	boolean isIdealPositionZuerst();

	boolean isNotGroup();

	int getOrder();

	boolean isIgnoreInjured();

	Weather getWeather();

	void setWeather(Weather weather);

	void addToAssistant(PlayerPositionPanel positionPanel);

	/**
	 * Returns a Map of statuses from the position selection.
	 * The keys are integers containing roleIDs for the positions.
	 * The values are booleans for whether the role should be included or not.
	 * The map does not contain all positions, only those being sent through filtering.
	 * 
	 * @return The Map
	 */
	 Map<Integer, Boolean> getPositionStatuses();

}