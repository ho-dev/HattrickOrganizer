package core.gui.comp.entry;

import org.jetbrains.annotations.NotNull;

/**
 * Classes implementing this interface create a {@link javax.swing.JComponent} that will
 * be displayed as a cell in a {@link javax.swing.JTable}: they act as factories to
 * create and initialise the UI component.
 *
 * <p>To display the component, call {@link #getComponent(boolean)}</p>.
 */
public interface IHOTableEntry extends Comparable<IHOTableEntry> {


    /**
     * This method returns the JComponent, which shall be shown in the Table. The component should
     * be created be createComponent and only the background should be changed according to the
     * isSelected-Flag. Nevertheless you  can ignore the createComponent and updateComponent and
     * create a new one everytime getComponent is called, but that is much slower!
     */
    javax.swing.JComponent getComponent(boolean isSelected);

    /**
     * Clear the Component and reset it to the defaultvalues You don´t have to use this method at
     * all, but it is recommend.
     */
    void clear();

    /**
     * Useful to sort the table
     */
    @Override
	int compareTo(@NotNull IHOTableEntry obj);

    /**
     * Third sort the table
     */
    int compareToThird(IHOTableEntry obj);
    
    /**
     * Create the Component. Keep it in a variable, so the same Component can be returned by
     * calling getComponent.  You don´t have to use this method at all, but it is recommend.
     */
    void createComponent();

    /**
     * Update the Component which was created bei createComponent instead of creating a new one.
     * You don´t have to use this method at all, but it is recommend.
     */
    void updateComponent();
}
