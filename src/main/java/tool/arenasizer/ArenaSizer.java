package tool.arenasizer;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ArenaSizer {

	static final BigDecimal TERRACES_PERCENT = new BigDecimal("0.60").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal BASICS_PERCENT = new BigDecimal("0.235").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal ROOF_PERCENT = new BigDecimal("0.14").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal VIP_PERCENT = new BigDecimal("0.025").setScale(3, RoundingMode.HALF_DOWN);

	// SUPPORTER-DISTRIBUTION
	private static final int SUPPORTER_VARIATION = 5;
	static final Integer SUPPORTER_NORMAL = 20;
	static final Integer SUPPORTER_MIN = SUPPORTER_NORMAL - SUPPORTER_VARIATION;
	static final Integer SUPPORTER_MAX = SUPPORTER_NORMAL + SUPPORTER_VARIATION;

	private ArenaSizer() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static Stadium[] calcConstructionArenas(Stadium currentArena, int supporter) {
		Stadium arenaMax = createArena(currentArena, supporter * SUPPORTER_MAX);
		Stadium arenaNormal = createArena(currentArena, supporter * SUPPORTER_NORMAL);
		Stadium arenaMin = createArena(currentArena, supporter * SUPPORTER_MIN);
		return new Stadium[]{arenaMax, arenaNormal, arenaMin};
	}

	public static Stadium[] calcConstructionArenas(Stadium currentArena, int maxSize, int normalSize, int minSize) {
		Stadium arenaMax = createArena(currentArena, maxSize);
		Stadium arenaNormal = createArena(currentArena, normalSize);
		Stadium arenaMin = createArena(currentArena, minSize);
		return new Stadium[]{arenaMax, arenaNormal, arenaMin};
	}

	private static Stadium createArena(Stadium currentArena, int size) {
		Stadium arena = new Stadium();
		BigDecimal sizeNumber = new BigDecimal(size);
		arena.setTerraces(TERRACES_PERCENT.multiply(sizeNumber).intValue());
		arena.setTerracesUnderConstruction(arena.getTerraces() - currentArena.getTerraces());
		arena.setBasicSeating(BASICS_PERCENT.multiply(sizeNumber).intValue());
		arena.setBasicSeatingUnderConstruction(arena.getBasicSeating() - currentArena.getBasicSeating());
		arena.setUnderRoofSeating(ROOF_PERCENT.multiply(sizeNumber).intValue());
		arena.setUnderRoofSeatingUnderConstruction(arena.getUnderRoofSeating() - currentArena.getUnderRoofSeating());
		arena.setVipBox(VIP_PERCENT.multiply(sizeNumber).intValue());
		arena.setVipBoxUnderConstruction(arena.getVipBox() - currentArena.getVipBox());

		arena.setExpansionCosts(ArenaRebuild.calculateCosts(arena.getTerracesUnderConstruction(),
			arena.getBasicSeatingUnderConstruction(),
			arena.getUnderRoofSeatingUnderConstruction(),
			arena.getVipBoxUnderConstruction()));
		return arena;
	}
}
