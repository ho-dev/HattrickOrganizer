package core.gui.comp.entry

abstract class AbstractHOTableEntry : IHOTableEntry {
    override fun compareToThird(obj: IHOTableEntry): Int {
        return this.compareTo(obj)
    }
}
