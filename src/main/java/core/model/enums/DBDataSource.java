package core.model.enums;


public enum DBDataSource {
    HRF(1),
    MANUAL(2),
    GUESS(3);


    private int value;

    DBDataSource(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static DBDataSource getCode(int value){

            return switch (value){
                case 1 -> HRF;
                case 2 -> MANUAL;
                case 3 -> GUESS;
                default -> throw new IllegalStateException("Unexpected DBDataSource value: " + value);
            };
    }

}
