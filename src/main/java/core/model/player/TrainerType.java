package core.model.player;

public enum TrainerType {
    /*
    0 = defensive, 1 = offensive, 2 = balanced
     */
    None(-1),
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
            case 2 -> Balanced;
            default -> None;
        };
    }

    public static int toInt(TrainerType trainerTyp) {
        if ( trainerTyp != null) return trainerTyp.toInt();
        return None.id;
    }

    public int toInt(){
        return this.id;
    }
}
