package core.option;

import core.model.TranslationFacility;
import core.model.Translator;
import core.option.SmartPlayerNamesPanel.Group;
import core.option.SmartPlayerNamesPanel.Location;
import core.option.SmartPlayerNamesPanel.NameFormat;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class SmartPlayerNamesPanelTest {

    private static Translator initialTranslator;

    @BeforeAll
    static void setUp() {
        initialTranslator = TranslationFacility.getTranslator();
        TranslationFacility.setTranslator(Translator.load(Translator.LANGUAGE_DEFAULT));
    }

    @AfterAll
    public static void tearDown() {
        TranslationFacility.setTranslator(initialTranslator);
    }

    @ParameterizedTest
    @EnumSource(NameFormat.class)
    void getTranslationKey_NameFormat_translationKeyExistsInDefaultLanguage(NameFormat nameFormat) {
        final String key = SmartPlayerNamesPanel.getTranslationKey(nameFormat);
        final String translation = TranslationFacility.tr(key);
        assertThat(translation)
            .as("Missing translation for %s with key '%s'", nameFormat, key)
            .isNotBlank()
            .isNotEqualTo(createTranslationForNotFound(key));
    }

    @ParameterizedTest
    @EnumSource(Location.class)
    void getTranslationKey_Location_translationKeyExistsInDefaultLanguage(Location location) {
        final String key = SmartPlayerNamesPanel.getTranslationKey(location);
        final String translation = TranslationFacility.tr(key);
        assertThat(translation)
            .as("Missing translation for %s with key '%s'", location, key)
            .isNotBlank()
            .isNotEqualTo(createTranslationForNotFound(key));
    }

    @ParameterizedTest
    @EnumSource(Group.class)
    void getTranslationKey_Group_translationKeyExistsInDefaultLanguage(Group group) {
        final String key = SmartPlayerNamesPanel.getTranslationKey(group);
        final String translation = TranslationFacility.tr(key);
        assertThat(translation)
            .as("Missing translation for %s with key '%s'", group, key)
            .isNotBlank()
            .isNotEqualTo(createTranslationForNotFound(key));
    }

    private static String createTranslationForNotFound(String translationKey) {
        return "!" + translationKey + "!";
    }
}
