package core.module;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.params.provider.Arguments.of;

public class ModuleManagerTest {

    private static Stream<Arguments> getTabId() {
        return Stream.of(
            of(IModule.PLAYEROVERVIEW, "Spieleruebersicht"),
            of(IModule.LINEUP, "Aufstellung"),
            of(IModule.SERIES, "Ligatabelle"),
            of(IModule.MATCHES, "Tab_Title_Matches"),
            of(IModule.PLAYERANALYSIS, "SpielerAnalyse"),
            of(IModule.STATISTICS, "Statistik"),
            of(IModule.TRANSFERS, "Transfers"),
            of(IModule.TRAINING, "Training"),
            of(IModule.MISC, "Verschiedenes"),
            of(IModule.TEAMANALYZER, "TeamAnalyzer"),
            of(IModule.TSFORECAST, "Tab_TSForecast"),
            of(IModule.SPECIALEVENTS, "Tab_SpecialEvents"),
            of(IModule.TEAM_OF_THE_WEEK, "Tab_TeamOfTheWeek"),
            of(IModule.IFA, "Tab_IFA"),
            of(IModule.YOUTH, "ls.youth"),
            of(IModule.HALL_OF_FAME, "ls.hof")
        );
    }

    @ParameterizedTest
    @MethodSource
    void getTabId(int moduleId, String expectedTabId) {
        var module = ModuleManager.instance().getModule(moduleId);
        assertThat(module.getTabId()).isEqualTo(expectedTabId);
    }
}
