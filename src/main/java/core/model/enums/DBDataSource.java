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

}
