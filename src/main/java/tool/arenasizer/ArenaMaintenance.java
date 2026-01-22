package tool.arenasizer;

import core.util.AmountOfMoney;

import java.math.BigDecimal;

public class ArenaMaintenance {

    private ArenaMaintenance() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final AmountOfMoney TERRACES = new AmountOfMoney(5);
    public static final AmountOfMoney BASIC_SEATING = new AmountOfMoney(7);
    public static final AmountOfMoney UNDER_ROOF = new AmountOfMoney(10);
    public static final AmountOfMoney VIP_BOX = new AmountOfMoney(25);

    public static AmountOfMoney calculateCosts(Stadium arena) {
        return calculateCosts(arena.getTerraces(), arena.getBasicSeating(), arena.getUnderRoofSeating(), arena.getVipBox());
    }

    public static AmountOfMoney calculateCosts(int terraces, int basicSeating, int underRoof, int vipBox) {
        var costs = TERRACES.times(BigDecimal.valueOf(terraces));
        costs.add(BASIC_SEATING.times(BigDecimal.valueOf(basicSeating)));
        costs.add(UNDER_ROOF.times(BigDecimal.valueOf(underRoof)));
        costs.add(VIP_BOX.times(BigDecimal.valueOf(vipBox)));
        return costs;
    }
}
