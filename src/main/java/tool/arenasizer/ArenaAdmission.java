package tool.arenasizer;

import core.util.AmountOfMoney;

import java.math.BigDecimal;

public class ArenaAdmission {

    private ArenaAdmission() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static final AmountOfMoney TERRACES = new AmountOfMoney(70);
    public static final AmountOfMoney BASIC_SEATING = new AmountOfMoney(100);
    public static final AmountOfMoney UNDER_ROOF = new AmountOfMoney(190);
    public static final AmountOfMoney VIP_BOX = new AmountOfMoney(350);

    public static AmountOfMoney calculateIncome(Stadium arena) {
        return calculateIncome(arena.getTerraces(), arena.getBasicSeating(), arena.getUnderRoofSeating(), arena.getVipBox());
    }

    public static AmountOfMoney calculateTerracesIncome(int terraces) {
        return TERRACES.times(BigDecimal.valueOf(terraces));
    }

    public static AmountOfMoney calculateBasicSeatingIncome(int basicSeating) {
        return BASIC_SEATING.times(BigDecimal.valueOf(basicSeating));
    }

    public static AmountOfMoney calculateUnderRoofIncome(int underRoof) {
        return UNDER_ROOF.times(BigDecimal.valueOf(underRoof));
    }

    public static AmountOfMoney calculateVipBoxIncome(int vipBox) {
        return VIP_BOX.times(BigDecimal.valueOf(vipBox));
    }

    public static AmountOfMoney calculateIncome(int terraces, int basicSeating, int underRoof, int vipBox) {
        var income = calculateTerracesIncome(terraces);
        income.add(calculateBasicSeatingIncome(basicSeating));
        income.add(calculateUnderRoofIncome(underRoof));
        income.add(calculateVipBoxIncome(vipBox));
        return income;
    }
}
