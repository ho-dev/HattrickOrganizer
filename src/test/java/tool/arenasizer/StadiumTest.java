package tool.arenasizer;

import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class StadiumTest {

    private static final int TERRACES = 10000;
    private static final int BASIC_SEATS = 5000;
    private static final int SEATS_UNDER_ROOF = 30000;
    private static final int VIP = 1000;
    private static final int TOTAL =
            TERRACES + BASIC_SEATS + SEATS_UNDER_ROOF + VIP;

    private static final int EXPANSION_TERRACES = 1000;
    private static final int EXPANSION_BASIC_SEATS = 500;
    private static final int EXPANSION_SEATS_UNDER_ROOF = 300;
    private static final int EXPANSION_VIP = 100;
    private static final int EXPANSION_TOTAL =
            EXPANSION_TERRACES + EXPANSION_BASIC_SEATS + EXPANSION_SEATS_UNDER_ROOF + EXPANSION_VIP;

    private static final int FUTURE_TERRACES = TERRACES + EXPANSION_TERRACES;
    private static final int FUTURE_BASIC_SEATS = BASIC_SEATS + EXPANSION_BASIC_SEATS;
    private static final int FUTURE_SEATS_UNDER_ROOF = SEATS_UNDER_ROOF + EXPANSION_SEATS_UNDER_ROOF;
    private static final int FUTURE_VIP = VIP + EXPANSION_VIP;
    private static final int FUTURE_TOTAL = TOTAL + EXPANSION_TOTAL;

    private static final HODateTime REBUILT_DATE = HODateTime.fromHT("2024-08-25 22:00:00");
    private static final HODateTime EXPANSION_DATE = HODateTime.fromHT("2024-01-09 00:00:00");

    private static Stadium createStadium(boolean expansion) {
        Stadium stadium = new Stadium();
        stadium.setStehplaetze(TERRACES);
        stadium.setSitzplaetze(BASIC_SEATS);
        stadium.setUeberdachteSitzplaetze(SEATS_UNDER_ROOF);
        stadium.setLogen(VIP);

        if (expansion) {
            stadium.setAusbau(true);
            stadium.setAusbauStehplaetze(EXPANSION_TERRACES);
            stadium.setAusbauSitzplaetze(EXPANSION_BASIC_SEATS);
            stadium.setAusbauUeberdachteSitzplaetze(EXPANSION_SEATS_UNDER_ROOF);
            stadium.setAusbauLogen(EXPANSION_VIP);
            stadium.setExpansionDate(EXPANSION_DATE);
        } else {
            stadium.setRebuiltDate(REBUILT_DATE);
        }

        return stadium;
    }

    @Test
    void getGesamtgroesse() {
        // given
        final var stadium = createStadium(false);

        // when-then
        assertThat(stadium.getGesamtgroesse()).isEqualTo(TOTAL);
    }

    @Test
    void getAusbauGesamtgroesse_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getAusbauGesamtgroesse()).isEqualTo(Optional.of(EXPANSION_TOTAL));
    }

    @Test
    void getAusbauGesamtgroesse_ohneAusbau() {
        // given
        final var stadium = createStadium(false);

        // when-then
        assertThat(stadium.getAusbauGesamtgroesse()).isEmpty();
    }

    @Test
    void getZukunftGesamtgroesse_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getZukunftGesamtgroesse()).isEqualTo(Optional.of(FUTURE_TOTAL));
    }

    @Test
    void getZukunftGesamtgroesse_ohneAusbau() {
        // given
        final var stadium = createStadium(false);

        // when-then
        assertThat(stadium.getZukunftGesamtgroesse()).isEmpty();
    }

    @Test
    void getZukunftStehplaetze() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getZukunftStehplaetze()).isEqualTo(Optional.of(FUTURE_TERRACES));
    }

    @Test
    void getZukunftSitzplaetze_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getZukunftSitzplaetze()).isEqualTo(Optional.of(FUTURE_BASIC_SEATS));
    }

    @Test
    void getZukunftUeberdachteSitzplaetze_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getZukunftUeberdachteSitzplaetze()).isEqualTo(Optional.of(FUTURE_SEATS_UNDER_ROOF));
    }

    @Test
    void getZukunftLogen_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getZukunftLogen()).isEqualTo(Optional.of(FUTURE_VIP));
    }

    @Test
    void getRebuiltDate_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getRebuiltDate()).isNull();
    }

    @Test
    void getRebuiltDate_ohneAusbau() {
        // given
        final var stadium = createStadium(false);

        // when-then
        assertThat(stadium.getRebuiltDate()).isEqualTo(REBUILT_DATE);
    }

    @Test
    void getExpansionDate_mitAusbau() {
        // given
        final var stadium = createStadium(true);

        // when-then
        assertThat(stadium.getExpansionDate()).isEqualTo(EXPANSION_DATE);
    }

    @Test
    void getExpansionDate_ohneAusbau() {
        // given
        final var stadium = createStadium(false);

        // when-then
        assertThat(stadium.getExpansionDate()).isNull();
    }
}