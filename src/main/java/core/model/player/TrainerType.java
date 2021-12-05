package core.model.player;

public enum TrainerType {
    /*
    0 = defensive, 1 = offensive, 2 = balanced
     */
    Defensive(0),
    Offensive(1),
    Balanced(2);
    final int id;

    TrainerType(int i) {
        this.id = i;
    }

    public static  TrainerType fromInt(int i){
        return switch (i) {
            case 0 -> Defensive;
            case 1 -> Offensive;
            default -> Balanced;
        };
    }

    public int toInt(){
        return this.id;
    }
}
