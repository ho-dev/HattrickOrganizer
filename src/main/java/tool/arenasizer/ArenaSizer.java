// %2354158977:de.hattrickorganizer.logik%
/*
 * ArenaSizer.java
 *
 * Created on 21. März 2003, 08:19
 */
package tool.arenasizer;

import core.util.AmountOfMoney;
import java.math.BigDecimal;
import java.math.RoundingMode;


public class ArenaSizer {

	public static final AmountOfMoney ADMISSION_PRICE_TERRACES = new AmountOfMoney( 70);
	public static final AmountOfMoney ADMISSION_PRICE_BASICS = new AmountOfMoney(100);
	public static final AmountOfMoney ADMISSION_PRICE_ROOF = new AmountOfMoney(190);
	public static final AmountOfMoney ADMISSION_PRICE_VIP = new AmountOfMoney(350);

	private static final AmountOfMoney MAINTENANCE_TERRACES = new AmountOfMoney(5);
	private static final AmountOfMoney MAINTENANCE_BASICS = new AmountOfMoney(7);
	private static final AmountOfMoney MAINTENANCE_ROOF = new AmountOfMoney(10);
	private static final AmountOfMoney MAINTENANCE_VIP = new AmountOfMoney(25);

	//CREATE
	private static final AmountOfMoney STEH_AUSBAU = new AmountOfMoney(450);
	private static final AmountOfMoney SITZ_AUSBAU = new AmountOfMoney(750);
	private static final AmountOfMoney DACH_AUSBAU = new AmountOfMoney(900);
	private static final AmountOfMoney LOGEN_AUSBAU = new AmountOfMoney(3000);
	private static final AmountOfMoney ABRISS = new AmountOfMoney(60);
	private static final AmountOfMoney FIXKOSTEN = new AmountOfMoney(100000);

	static final BigDecimal TERRACES_PERCENT = new BigDecimal("0.60").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal BASICS_PERCENT = new BigDecimal("0.235").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal ROOF_PERCENT = new BigDecimal("0.14").setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal VIP_PERCENT = new BigDecimal("0.025").setScale(3, RoundingMode.HALF_DOWN);

	//SUPPORTER-DISTRIBUTION
	static final Integer SUPPORTER_NORMAL = 20;

	ArenaSizer() {}

	//~ Methods ------------------------------------------------------------------------------------

	final AmountOfMoney calcMaxIncome(Stadium arena) {
		var income = ADMISSION_PRICE_TERRACES.times(BigDecimal.valueOf(arena.getTerraces()));
		income.add(ADMISSION_PRICE_BASICS.times(BigDecimal.valueOf(arena.getBasicSeating())));
		income.add(ADMISSION_PRICE_ROOF.times(BigDecimal.valueOf(arena.getUnderRoofSeating())));
		income.add(ADMISSION_PRICE_VIP.times(BigDecimal.valueOf(arena.getVipBox())));
		return income;
	}

	final Stadium[] calcConstructionArenas(Stadium currentArena, int supporter) {
		Stadium arenaMax = createArena(supporter * (SUPPORTER_NORMAL + 5), currentArena);
		Stadium arenaNormal = createArena(supporter * SUPPORTER_NORMAL, currentArena);
		Stadium arenaMin = createArena(supporter * (SUPPORTER_NORMAL - 5), currentArena);
		return new Stadium[]{arenaMax, arenaNormal, arenaMin};
	}


	final Stadium[] calcConstructionArenas(Stadium currentArena, int maxSupporter, int normalSupporter, int minSupporter) {
		Stadium arenaMax = createArena(maxSupporter, currentArena);
		Stadium arenaNormal = createArena(normalSupporter, currentArena);
		Stadium arenaMin = createArena(minSupporter, currentArena);
		return new Stadium[]{arenaMax, arenaNormal, arenaMin};
	}

	private Stadium createArena(int size, Stadium current) {
		Stadium tmp = new Stadium();
		BigDecimal sizeNumber = new BigDecimal(size);
		tmp.setTerraces(TERRACES_PERCENT.multiply(sizeNumber).intValue());
		tmp.setTerracesUnderConstruction(tmp.getTerraces() - current.getTerraces());
		tmp.setBasicSeating(BASICS_PERCENT.multiply(sizeNumber).intValue());
		tmp.setBasicSeatingUnderConstruction(tmp.getBasicSeating() - current.getBasicSeating());
		tmp.setUnderRoofSeating(ROOF_PERCENT.multiply(sizeNumber).intValue());
		tmp.setUnderRoofSeatingUnderConstruction(tmp.getUnderRoofSeating() - current.getUnderRoofSeating());
		tmp.setVipBox(VIP_PERCENT.multiply(sizeNumber).intValue());
		tmp.setVipBoxUnderConstruction(tmp.getVipBox() - current.getVipBox());

		tmp.setExpansionCosts(calcConstructionCosts(tmp.getTerracesUnderConstruction(),
			tmp.getBasicSeatingUnderConstruction(),
			tmp.getUnderRoofSeatingUnderConstruction(),
			tmp.getVipBoxUnderConstruction()));
		return tmp;
	}

	final AmountOfMoney calcConstructionCosts(float steh, float sitz, float dach, float logen) {
        if ( steh == 0. && sitz == 0. && dach == 0. && logen == 0.) return new AmountOfMoney(0);
		var expansionCosts = new AmountOfMoney(FIXKOSTEN.getSwedishKrona());

		if (steh > 0) {
			expansionCosts.add(STEH_AUSBAU.times(BigDecimal.valueOf(steh)));
		} else {
			expansionCosts.subtract(ABRISS.times(BigDecimal.valueOf(steh)));
		}

		if (sitz > 0) {
			expansionCosts.add(SITZ_AUSBAU.times(BigDecimal.valueOf(sitz)));
		} else {
			expansionCosts.subtract(ABRISS.times(BigDecimal.valueOf(sitz)));
		}

		if (dach > 0) {
			expansionCosts.add(DACH_AUSBAU.times(BigDecimal.valueOf(dach)));
		} else {
			expansionCosts.subtract(ABRISS.times(BigDecimal.valueOf(dach)));
		}

		if (logen > 0) {
			expansionCosts.add(LOGEN_AUSBAU.times(BigDecimal.valueOf(logen)));
		} else {
			expansionCosts.subtract(ABRISS.times(BigDecimal.valueOf(logen)));
		}

		return  expansionCosts;
	}

	final AmountOfMoney calcMaintenance(Stadium arena) {
		var costs = MAINTENANCE_TERRACES.times(BigDecimal.valueOf(arena.getTerraces()));
		costs.add(MAINTENANCE_BASICS.times(BigDecimal.valueOf(arena.getBasicSeating())));
		costs.add(MAINTENANCE_ROOF.times(BigDecimal.valueOf(arena.getUnderRoofSeating())));
		costs.add(MAINTENANCE_VIP.times(BigDecimal.valueOf(arena.getVipBox())));
		return costs;
	}

}
