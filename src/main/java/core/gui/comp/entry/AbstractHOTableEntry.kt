package core.gui.comp.entry;

public abstract class AbstractHOTableEntry implements IHOTableEntry  {

    public final int compareToThird(IHOTableEntry obj) {
        return this.compareTo(obj);
    }
}
