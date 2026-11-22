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
            of(1, "Spieleruebersicht"),
            of(2, "Aufstellung"),
            of(3, "Ligatabelle"),
            of(4, "Tab_Title_Matches"),
            of(5, "SpielerAnalyse"),
            of(6, "Statistik"),
            of(7, "Transfers"),
            of(8, "Training"),
            of(9, "Verschiedenes"),
            of(10, "TeamAnalyzer"),
            of(11, "Tab_TSForecast"),
            of(12, "Tab_SpecialEvents"),
            of(13, "Tab_TeamOfTheWeek"),
            of(16, "Tab_IFA"),
            of(20, "ls.youth"),
            of(21, "ls.hof")
        );
    }

    @ParameterizedTest
    @MethodSource
    void getTabId(int moduleId, String expectedTabId) {
        var module = ModuleManager.instance().getModule(moduleId);
        assertThat(module.getTabId()).isEqualTo(expectedTabId);
    }
}
