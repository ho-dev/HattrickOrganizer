package tool.arenasizer;

import core.util.HODateTime;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
		stadium.setTerraces(TERRACES);
		stadium.setBasicSeating(BASIC_SEATS);
		stadium.setUnderRoofSeating(SEATS_UNDER_ROOF);
		stadium.setVipBox(VIP);

		if (expansion) {
			stadium.setUnderConstruction(true);
			stadium.setTerracesUnderConstruction(EXPANSION_TERRACES);
			stadium.setBasicSeatingUnderConstruction(EXPANSION_BASIC_SEATS);
			stadium.setUnderRoofSeatingUnderConstruction(EXPANSION_SEATS_UNDER_ROOF);
			stadium.setVipBoxUnderConstruction(EXPANSION_VIP);
			stadium.setExpansionDate(EXPANSION_DATE);
			stadium.setExpansionCosts(ArenaRebuild.calculateCosts(
					stadium.getTerraces(),
					stadium.getBasicSeating(),
					stadium.getUnderRoofSeating(),
					stadium.getVipBox()));
		} else {
			stadium.setRebuiltDate(REBUILT_DATE);
		}

		return stadium;
	}

	@Test
	void getTotalSize() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getTotalSize()).isEqualTo(TOTAL);
	}

	@Test
	void getTotalSizeUnderConstruction_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getTotalSizeUnderConstruction()).isEqualTo(Optional.of(EXPANSION_TOTAL));
	}

	@Test
	void getTotalSizeUnderConstruction_withoutExpansion() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getTotalSizeUnderConstruction()).isEmpty();
	}

	@Test
	void getFutureTotalSize_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getFutureTotalSize()).isEqualTo(Optional.of(FUTURE_TOTAL));
	}

	@Test
	void getFutureTotalSize_withoutExpansion() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getFutureTotalSize()).isEmpty();
	}

	@Test
	void getZukunftStehplaetze() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getFutureTerraces()).isEqualTo(Optional.of(FUTURE_TERRACES));
	}

	@Test
	void getFutureBasicSeating_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getFutureBasicSeating()).isEqualTo(Optional.of(FUTURE_BASIC_SEATS));
	}

	@Test
	void getFutureUnderRoofSeating_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getFutureUnderRoofSeating()).isEqualTo(Optional.of(FUTURE_SEATS_UNDER_ROOF));
	}

	@Test
	void getFutureVipBoxes_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getFutureVipBoxes()).isEqualTo(Optional.of(FUTURE_VIP));
	}

	@Test
	void getRebuiltDate_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getRebuiltDate()).isNull();
	}

	@Test
	void getRebuiltDate_withoutExpansion() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getRebuiltDate()).isEqualTo(REBUILT_DATE);
	}

	@Test
	void getExpansionDate_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getExpansionDate()).isEqualTo(EXPANSION_DATE);
	}

	@Test
	void getExpansionDate_withoutExpansion() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getExpansionDate()).isNull();
	}

	@Test
	void getExpansionCostsInSwedishKrona_withoutExpansion() {
		// given
		final var stadium = createStadium(false);

		// when-then
		assertThat(stadium.getExpansionCostsInSwedishKrona()).isNull();
	}

	@Test
	void getExpansionCostsInSwedishKrona_withExpansion() {
		// given
		final var stadium = createStadium(true);

		// when-then
		assertThat(stadium.getExpansionCostsInSwedishKrona()).isEqualTo(BigDecimal.valueOf(38350000));
	}
}
