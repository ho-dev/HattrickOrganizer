// %2354158977:de.hattrickorganizer.logik%
/*
 * ArenaSizer.java
 *
 * Created on 21. März 2003, 08:19
 */
package tool.arenasizer;

import core.util.Helper;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class ArenaSizer {

	public static final float ADMISSION_PRICE_TERRACES = 70f;
	public static final float ADMISSION_PRICE_BASICS = 100f;
	public static final float ADMISSION_PRICE_ROOF = 190f;
	public static final float ADMISSION_PRICE_VIP = 350f;

	private static final float MAINTENANCE_TERRACES = 5f;
	private static final float MAINTENANCE_BASICS = 7f;
	private static final float MAINTENANCE_ROOF = 10f;
	private static final float MAINTENANCE_VIP = 25f;

	//CREATE
	private static float STEH_AUSBAU = 450f;
	private static float SITZ_AUSBAU = 750f;
	private static float DACH_AUSBAU = 900f;
	private static float LOGEN_AUSBAU = 3000f;
	private static float ABRISS = 60f;
	private static float FIXKOSTEN = 100000f;

	static final BigDecimal TERRACES_PERCENT = new BigDecimal(0.60).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal BASICS_PERCENT = new BigDecimal(0.235).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal ROOF_PERCENT = new BigDecimal(0.14).setScale(3, RoundingMode.HALF_DOWN);
	static final BigDecimal VIP_PERCENT = new BigDecimal(0.025).setScale(3, RoundingMode.HALF_DOWN);

	//SUPPORTER-DISTRIBUTION
	static final Integer SUPPORTER_NORMAL = 20;

	float currencyFactor = core.model.UserParameter.instance().FXrate;

	ArenaSizer() {

	}

	//~ Methods ------------------------------------------------------------------------------------

	final int calcMaxIncome(Stadium arena) {
		int income = 0;

		income += ((arena.getTerraces() * ADMISSION_PRICE_TERRACES) / currencyFactor);
		income += ((arena.getBasicSeating() * ADMISSION_PRICE_BASICS) / currencyFactor);
		income += ((arena.getUnderRoofSeating() * ADMISSION_PRICE_ROOF) / currencyFactor);
		income += ((arena.getVipBox() * ADMISSION_PRICE_VIP) / currencyFactor);

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

	final int calcConstructionCosts(float steh, float sitz, float dach, float logen) {
		float kosten = FIXKOSTEN / currencyFactor;

		if (steh > 0) {
			kosten += ((steh * STEH_AUSBAU) / currencyFactor);
		} else {
			kosten -= ((steh * ABRISS) / currencyFactor);
		}

		if (sitz > 0) {
			kosten += ((sitz * SITZ_AUSBAU) / currencyFactor);
		} else {
			kosten -= ((sitz * ABRISS) / currencyFactor);
		}

		if (dach > 0) {
			kosten += ((dach * DACH_AUSBAU) / currencyFactor);
		} else {
			kosten -= ((dach * ABRISS) / currencyFactor);
		}

		if (logen > 0) {
			kosten += ((logen * LOGEN_AUSBAU) / currencyFactor);
		} else {
			kosten -= ((logen * ABRISS) / currencyFactor);
		}

		return (int) kosten;
	}

	final int calcDistribution(float arenaSize, float percent) {
		return (int) ((arenaSize / 100.0f) * percent);
	}

	final float calcMaintenance(Stadium arena) {
		float costs = 0.0f;

		costs += ((arena.getTerraces() * MAINTENANCE_TERRACES) / currencyFactor);
		costs += ((arena.getBasicSeating() * MAINTENANCE_BASICS) / currencyFactor);
		costs += ((arena.getUnderRoofSeating() * MAINTENANCE_ROOF) / currencyFactor);
		costs += ((arena.getVipBox() * MAINTENANCE_VIP) / currencyFactor);

		return Helper.round(costs, 1);
	}

}
