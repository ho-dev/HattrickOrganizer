package module.training;

public enum TrainingType {
    PAST_TRAINING(1),
    FUTURE_TRAINING(2);

    private final int value;

    TrainingType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}