package core.model.player;

public enum TrainerStatus {
    // 1 = PlayingTrainer, 2 = OnlyTrainer, 3 = HoFTrainer
    PlayingTrainer(1),
    OnlyTrainer(2),
    HoFTrainer(3);

    private final int id;

    TrainerStatus(int i) {
        this.id = i;
    }

    public static TrainerStatus fromInt(Integer i){
        if ( i != null) {
            return switch (i) {
                case 1 -> PlayingTrainer;
                case 2 -> OnlyTrainer;
                case 3 -> HoFTrainer;
                default -> null;
            };
        }
        return null;
    }

    public static Integer toInteger(TrainerStatus trainerStatus) {
        if ( trainerStatus != null) return trainerStatus.toInt();
        return null;
    }

    public int toInt(){
        return this.id;
    }

}
