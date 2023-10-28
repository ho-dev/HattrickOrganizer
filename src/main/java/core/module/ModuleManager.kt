package core.module;

import core.model.UserParameter;
import core.module.config.ModuleConfig;
import module.ifa.IfaModule;
import module.lineup.LineupModule;
import module.matches.MatchesModule;
import module.misc.MiscModule;
import module.playerOverview.PlayerOverviewModule;
import module.playeranalysis.PlayerAnalysisModule;
import module.series.SeriesModule;
import module.specialEvents.SpecialEventsModule;
import module.statistics.StatisticsModule;
import module.teamAnalyzer.TeamAnalyzerModule;
import module.teamOfTheWeek.TeamOfTheWeekModule;
import module.training.TrainingModule;
import module.transfer.TransfersModule;
import module.tsforecast.TSForecastModule;
import module.youth.YouthModule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class ModuleManager {

	public static int getFactor() {
		return factor;
	}

	private static final int factor = 10000;
	private final Map<Integer, IModule> all_modules = new HashMap<>();
	private Map<Integer, IModule> tmpModules;

	private static ModuleManager moduleManager;

	public static ModuleManager instance() {
		if (moduleManager == null)
			moduleManager = new ModuleManager();
		return moduleManager;
	}

	private ModuleManager() {
		initialize();
	}

	private void initMap(Map<Integer, IModule> map) {
		map.put(IModule.PLAYEROVERVIEW, new PlayerOverviewModule());
		map.put(IModule.LINEUP, new LineupModule());
		map.put(IModule.SERIES, new SeriesModule());
		map.put(IModule.MATCHES, new MatchesModule());
		map.put(IModule.PLAYERANALYSIS, new PlayerAnalysisModule());
		map.put(IModule.STATISTICS, new StatisticsModule());
		map.put(IModule.TRANSFERS, new TransfersModule());
		map.put(IModule.TRAINING, new TrainingModule());
		map.put(IModule.MISC, new MiscModule());
		map.put(IModule.TEAMANALYZER, new TeamAnalyzerModule());
		map.put(IModule.TSFORECAST, new TSForecastModule());
		map.put(IModule.SPECIALEVENTS, new SpecialEventsModule());
		map.put(IModule.TEAM_OF_THE_WEEK, new TeamOfTheWeekModule());
		map.put(IModule.IFA, new IfaModule());
		map.put(IModule.YOUTH, new YouthModule());
	}

	private void initialize() {
		initMap(all_modules);
		loadModuleInfos();
	}

	private void copy(Map<Integer, IModule> from, Map<Integer, IModule> to) {
		for (Integer key : to.keySet()) {
			to.get(key).setStatus(from.get(key).getStatus());
		}
	}

	public IModule getModule(int moduleId) {
		return all_modules.get(moduleId);
	}

	public IModule[] getAllModules() {
		return all_modules.values().toArray(new IModule[0]);
	}

	public IModule[] getTempModules() {
		if (tmpModules == null) {
			tmpModules = new HashMap<>();
			initMap(tmpModules);
			copy(all_modules, tmpModules);
		}
		return tmpModules.values().toArray(new IModule[all_modules.size()]);
	}

	public IModule[] getModules(boolean isActive) {
		return getModules(all_modules, isActive);
	}

	IModule[] getModules(Map<Integer, IModule> map, boolean isActive) {
		ArrayList<IModule> tmp = new ArrayList<>();
		Collection<IModule> c = map.values();
		for (IModule iModule : c) {
			if (iModule.isActive() == isActive)
				tmp.add(iModule);
		}
		return tmp.toArray(new IModule[0]);
	}

	public void savedModules() {
		IModule[] modules = getAllModules();
		int[] ids = new int[modules.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = modules[i].getStatus() * factor + modules[i].getModuleId();
		}
		ModuleConfig.instance().setIntArray("MM_Modules", ids);
	}

	private void loadModuleInfos() {
		int[] activModuleIds = ModuleConfig.instance()
				.getIntArray("MM_Modules");
		if (activModuleIds.length == 0) {
			firstStart();
			return;
		}
		for (int activModuleId : activModuleIds) {
			int id = 0;
			int status = IModule.STATUS_DEACTIVATED;
			if (activModuleId > IModule.STATUS_STARTUP * factor) {
				id = activModuleId - (IModule.STATUS_STARTUP * factor);
				status = IModule.STATUS_STARTUP;
			} else if (activModuleId > IModule.STATUS_ACTIVATED * factor
					&& activModuleId < IModule.STATUS_STARTUP * factor) {
				id = activModuleId - (IModule.STATUS_ACTIVATED * factor);
				status = IModule.STATUS_ACTIVATED;
			} else if (activModuleId < IModule.STATUS_ACTIVATED * factor) {
				id = activModuleId;
			}
			IModule module = all_modules.get(id);
			if (module != null) {
				module.setStatus(status);
			}
		}
	}

	private void firstStart() {
		UserParameter p = UserParameter.instance();

		if (!p.tempTabAufstellung) {
			getModule(IModule.LINEUP).setStatus(IModule.STATUS_STARTUP);
		}

		if (!p.tempTabInformation) {
			getModule(IModule.MISC).setStatus(IModule.STATUS_STARTUP);
		}

		if (!p.tempTabLigatabelle) {
			getModule(IModule.SERIES).setStatus(IModule.STATUS_STARTUP);
		}

		if (!p.tempTabSpiele) {
			getModule(IModule.MATCHES).setStatus(IModule.STATUS_STARTUP);
		}

		if (!p.tempTabSpieleranalyse) {
			getModule(IModule.PLAYERANALYSIS).setStatus(IModule.STATUS_STARTUP);
			ModuleConfig.instance().setBoolean(PlayerAnalysisModule.SHOW_PLAYERCOMPARE, true);
		}

		if (!p.tempTabSpieleruebersicht) {
			getModule(IModule.PLAYEROVERVIEW).setStatus(IModule.STATUS_STARTUP);
		}

		if (!p.tempTabStatistik) {
			getModule(IModule.STATISTICS).setStatus(IModule.STATUS_STARTUP);
		}

		//getModule(IModule.TRANSFERS).setStatus(IModule.STATUS_STARTUP);
		getModule(IModule.TRAINING).setStatus(IModule.STATUS_STARTUP);
		getModule(IModule.TEAMANALYZER).setStatus(IModule.STATUS_STARTUP);
	}

	public void saveTemp() {
		if (tmpModules != null) {
			copy(tmpModules, all_modules);
		}
		savedModules();
		ModuleConfig.instance().save();
	}

	public void clearTemp() {
		tmpModules = null;
	}

}
