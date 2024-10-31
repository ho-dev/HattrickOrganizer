package module.transfer.ui.sorter;

public class Directive {
    private int column;
    private int direction;

    /**
     * Creates a new Directive object.
     *
     * @param column
     * @param direction
     */
    public Directive(int column, int direction) {
        this.column = column;
        this.direction = direction;
    }

    public int getColumn() {
        return column;
    }

    public int getDirection() {
        return direction;
    }
}
