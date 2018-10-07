// %1127326955572:plugins%
/*
 * IHOTableEntry.java
 *
 * Created on 16. Oktober 2004, 10:15
 */
package core.gui.comp.entry;

/**
 * An Entry for a JTable, first set addHOTableRendere( javax.swing.JTable table ) so your table
 * shows the Entry correct
 *
 * @author FoolmooN
 */
public interface IHOTableEntry extends Comparable<IHOTableEntry> {
    //~ Methods ------------------------------------------------------------------------------------

    /**
     * This methode returns the JComponent, which shall be shown in the Table. The component should
     * be created be createComponent and only the background should be changed according to the
     * isSelected-Flag. Nevertheless you  can ignore the createComponent and updateComponent and
     * create a new one everytime getComponent is called, but that is much slower!
     */
    public javax.swing.JComponent getComponent(boolean isSelected);

    /**
     * Clear the Component and reset it to the defaultvalues You don´t have to use this method at
     * all, but it is recommend.
     */
    public void clear();

    /**
     * Useful to sort the table
     */
    @Override
	public int compareTo(IHOTableEntry obj);

    /**
     * Create the Component. Keep it in a variable, so the same Component can be returned by
     * calling getComponent.  You don´t have to use this method at all, but it is recommend.
     */
    public void createComponent();

    /**
     * Update the Component which was created bei createComponent instead of creating a new one.
     * You don´t have to use this method at all, but it is recommend.
     */
    public void updateComponent();
}
