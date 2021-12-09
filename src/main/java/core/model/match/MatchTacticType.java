package core.model.match;

public enum MatchTacticType {
    /*

Value	Description
0	Normal
1	Pressing
2	Counter-attacks
3	Attack in the middle
4	Attack in wings
7	Play creatively
8	Long shots
     */

    Normal(0),
    Pressing(1),
    CounterAttacks(2),
    AttackInTheMiddle(3),
    AttackInWings(4),
    PlayCreatively(7),
    LongShots(8);

    final int id;

    MatchTacticType(int i) {
        this.id = i;
    }

    public static MatchTacticType fromInt(Integer tactic) {
        if (tactic == null) return null;
        return switch (tactic) {
            default -> Normal;
            case 1 -> Pressing;
            case 2 -> CounterAttacks;
            case 3 -> AttackInTheMiddle;
            case 4 -> AttackInWings;
            case 7 -> PlayCreatively;
            case 8 -> LongShots;
        };
    }

    public int toInt() {
        return this.id;
    }
}
