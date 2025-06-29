// %2354158977:de.hattrickorganizer.logik%
/*
 * ArenaSizer.java
 *
 * Created on 21. März 2003, 08:19
 */
package tool.arenasizer;

import core.util.AmountOfMoney;
import core.util.Helper;

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
	private static AmountOfMoney STEH_AUSBAU = new AmountOfMoney(450);
	private static AmountOfMoney SITZ_AUSBAU = new AmountOfMoney(750);
	private static AmountOfMoney DACH_AUSBAU = new AmountOfMoney(900);
	private static AmountOfMoney LOGEN_AUSBAU = new AmountOfMoney(3000);
	private static AmountOfMoney ABRISS = new AmountOfMoney(60);
	private static AmountOfMoney FIXKOSTEN = new AmountOfMoney(100000);

	static final BigDecimal TERRACES_PERCENT = new BigDecimal(0.60).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal BASICS_PERCENT = new BigDecimal(0.235).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal ROOF_PERCENT = new BigDecimal(0.14).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal VIP_PERCENT = new BigDecimal(0.025).setScale(3, RoundingMode.HALF_DOWN);

	//SUPPORTER-DISTRIBUTION
	static final Integer SUPPORTER_NORMAL = 20;

	float currencyFactor = core.model.UserParameter.instance().currencyRate;

	ArenaSizer() {

	}

	//~ Methods ------------------------------------------------------------------------------------

	final AmountOfMoney calcMaxIncome(Stadium arena) {
		var income = ADMISSION_PRICE_TERRACES.times(arena.getTerraces());
		income.add(ADMISSION_PRICE_BASICS.times(arena.getBasicSeating()));
		income.add(ADMISSION_PRICE_ROOF.times(arena.getUnderRoofSeating()));
		income.add(ADMISSION_PRICE_VIP.times(arena.getVipBox()));
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
		var kosten = FIXKOSTEN;

		if (steh > 0) {
			kosten.add(STEH_AUSBAU.times(steh));
		} else {
			kosten.subtract(ABRISS.times(steh));
		}

		if (sitz > 0) {
			kosten.add(SITZ_AUSBAU.times(sitz));
		} else {
			kosten.subtract(ABRISS.times(sitz));
		}

		if (dach > 0) {
			kosten.add(DACH_AUSBAU.times(dach));
		} else {
			kosten.subtract(ABRISS.times(dach));
		}

		if (logen > 0) {
			kosten.add(LOGEN_AUSBAU.times(logen));
		} else {
			kosten.subtract(ABRISS.times(logen));
		}

		return  kosten;
	}

	final int calcDistribution(float arenaSize, float percent) {
		return (int) ((arenaSize / 100.0f) * percent);
	}

	final AmountOfMoney calcMaintenance(Stadium arena) {
		var costs = MAINTENANCE_TERRACES.times(arena.getTerraces());
		costs.add(MAINTENANCE_BASICS.times(arena.getBasicSeating()));
		costs.add(MAINTENANCE_ROOF.times(arena.getUnderRoofSeating()));
		costs.add(MAINTENANCE_VIP.times(arena.getVipBox()));;
		return costs;
	}

}
