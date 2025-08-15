package core.gui.comp.entry;

public abstract class AbstractHOTableEntry implements IHOTableCellEntry {

    public final int compareToThird(IHOTableCellEntry obj) {
        return this.compareTo(obj);
    }
}
