package tool.arenasizer;

import core.util.AmountOfMoney;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

public class ArenaRebuild {

    private ArenaRebuild() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final AmountOfMoney TERRACES = new AmountOfMoney(450);
    public static final AmountOfMoney BASIC_SEATING = new AmountOfMoney(750);
    public static final AmountOfMoney UNDER_ROOF = new AmountOfMoney(900);
    public static final AmountOfMoney VIP_BOX = new AmountOfMoney(3000);
    public static final AmountOfMoney DEMOLITION = new AmountOfMoney(60);
    public static final AmountOfMoney FIXED_COSTS = new AmountOfMoney(100000);

    public static AmountOfMoney calculateCosts(int terraces, int basicSeating, int underRoof, int vipBox) {
        if (terraces == 0 && basicSeating == 0 && underRoof == 0 && vipBox == 0) {
            return new AmountOfMoney(ZERO);
        }

        var expansionCosts = new AmountOfMoney(FIXED_COSTS.getSwedishKrona());

        if (terraces > 0) {
            expansionCosts.add(TERRACES.times(BigDecimal.valueOf(terraces)));
        } else if (terraces < 0) {
            expansionCosts.subtract(DEMOLITION.times(BigDecimal.valueOf(terraces)));
        }

        if (basicSeating > 0) {
            expansionCosts.add(BASIC_SEATING.times(BigDecimal.valueOf(basicSeating)));
        } else if (basicSeating < 0) {
            expansionCosts.subtract(DEMOLITION.times(BigDecimal.valueOf(basicSeating)));
        }

        if (underRoof > 0) {
            expansionCosts.add(UNDER_ROOF.times(BigDecimal.valueOf(underRoof)));
        } else if (underRoof < 0) {
            expansionCosts.subtract(DEMOLITION.times(BigDecimal.valueOf(underRoof)));
        }

        if (vipBox > 0) {
            expansionCosts.add(VIP_BOX.times(BigDecimal.valueOf(vipBox)));
        } else if (vipBox < 0) {
            expansionCosts.subtract(DEMOLITION.times(BigDecimal.valueOf(vipBox)));
        }

        return expansionCosts;
    }
}
