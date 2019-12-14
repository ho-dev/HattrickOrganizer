// %2354158977:de.hattrickorganizer.logik%
/*
 * ArenaSizer.java
 *
 * Created on 21. März 2003, 08:19
 */
package tool.arenasizer;

import core.util.Helper;

import java.math.BigDecimal;


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

    static final BigDecimal TERRACES_PERCENT 	= new BigDecimal(0.60).setScale(3, BigDecimal.ROUND_HALF_DOWN);
    static final BigDecimal BASICS_PERCENT 		= new BigDecimal(0.235).setScale(3, BigDecimal.ROUND_HALF_DOWN);
    static final BigDecimal ROOF_PERCENT 		= new BigDecimal(0.14).setScale(3, BigDecimal.ROUND_HALF_DOWN);
    static final BigDecimal VIP_PERCENT 		= new BigDecimal(0.025).setScale(3, BigDecimal.ROUND_HALF_DOWN);

    //SUPPORTER-DISTRIBUTION
    static final Integer SUPPORTER_NORMAL = Integer.valueOf(20);

    float currencyFactor = core.model.UserParameter.instance().faktorGeld;

    ArenaSizer() {

    }

    //~ Methods ------------------------------------------------------------------------------------

    final int calcMaxIncome(Arena arena) {
        int income = 0;

        income += ((arena.getStehplaetze() * ADMISSION_PRICE_TERRACES) / currencyFactor);
        income += ((arena.getSitzplaetze() * ADMISSION_PRICE_BASICS) /currencyFactor);
        income += ((arena.getUeberdachteSitzplaetze() * ADMISSION_PRICE_ROOF) /currencyFactor);
        income += ((arena.getLogen() * ADMISSION_PRICE_VIP) / currencyFactor);

        return income;
    }

    final Arena[] calcConstructionArenas(Arena currentArena, int supporter){
    	Arena arenaMax = createArena(supporter * (SUPPORTER_NORMAL.intValue()+5) ,currentArena);
        Arena arenaNormal = createArena(supporter * SUPPORTER_NORMAL.intValue(),currentArena);
        Arena arenaMin = createArena(supporter * (SUPPORTER_NORMAL.intValue()-5),currentArena);
        return new Arena[]{arenaMax, arenaNormal, arenaMin};
    }


    final Arena[] calcConstructionArenas(Arena currentArena, int maxSupporter, int normalSupporter, int minSupporter){
        Arena arenaMax = createArena(maxSupporter,currentArena);
        Arena arenaNormal = createArena(normalSupporter,currentArena);
        Arena arenaMin = createArena(minSupporter,currentArena);
        return new Arena[]{arenaMax, arenaNormal, arenaMin};
    }

    private Arena createArena(int size, Arena current){
    	Arena tmp = new Arena();
    	BigDecimal sizeNumber = new BigDecimal(size);
    	tmp.setStehplaetze(TERRACES_PERCENT.multiply(sizeNumber).intValue());
    	tmp.setAusbauStehplaetze(tmp.getStehplaetze() - current.getStehplaetze());
    	tmp.setSitzplaetze(BASICS_PERCENT.multiply(sizeNumber).intValue());
    	tmp.setAusbauSitzplaetze(tmp.getSitzplaetze() - current.getSitzplaetze());
    	tmp.setUeberdachteSitzplaetze(ROOF_PERCENT.multiply(sizeNumber).intValue());
    	tmp.setAusbauUeberdachteSitzplaetze(tmp.getUeberdachteSitzplaetze()- current.getUeberdachteSitzplaetze());
    	tmp.setLogen(VIP_PERCENT.multiply(sizeNumber).intValue());
    	tmp.setAusbauLogen(tmp.getLogen() - current.getLogen());

    	tmp.setAusbauKosten(calcConstructionCosts(tmp.getAusbauStehplaetze(),
    			tmp.getAusbauSitzplaetze(),
    			tmp.getAusbauUeberdachteSitzplaetze(),
    			tmp.getAusbauLogen()));
        return tmp;
    }

    final float calcConstructionCosts(float steh, float sitz, float dach, float logen) {
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

        return kosten;
    }

    final int calcDistribution(float arenaSize,float percent) {
        return (int) ((arenaSize / 100.0f) * percent);
    }

    final float calcMaintenance(Arena arena) {
        float costs = 0.0f;

        costs += ((arena.getStehplaetze() * MAINTENANCE_TERRACES) / currencyFactor);
        costs += ((arena.getSitzplaetze() * MAINTENANCE_BASICS) / currencyFactor);
        costs += ((arena.getUeberdachteSitzplaetze() * MAINTENANCE_ROOF) / currencyFactor);
        costs += ((arena.getLogen() * MAINTENANCE_VIP) / currencyFactor);

        return Helper.round(costs, 1);
    }

}
