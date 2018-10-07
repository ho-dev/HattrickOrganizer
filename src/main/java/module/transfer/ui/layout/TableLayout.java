// %1116523449125:hoplugins.commons.ui.info.clearthought.layout%
/*
 * ====================================================================
 *
 * The Clearthought Software License, Version 1.0
 *
 * Copyright (c) 2001 Daniel Barbalace.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. The original software may not be altered.  However, the classes
 *    provided may be subclasses as long as the subclasses are not
 *    packaged in the info.clearthought package or any subpackage of
 *    info.clearthought.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR, AFFILATED BUSINESSES,
 * OR ANYONE ELSE BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 */
package module.transfer.ui.layout;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * TableLayout is a layout manager that arranges components in rows and columns like a spreadsheet.
 * TableLayout allows each row or column to be a different size.  A row or column can be given an
 * absolute size in pixels, a percentage of the available space, or it can grow and shrink to fill
 * the remaining space after other rows and columns have been resized.
 *
 * <p>
 * Using spreadsheet terminology, a cell is the intersection of a row and column.  Cells have
 * finite, non-negative sizes measured in pixels.  The dimensions of a cell depend solely upon the
 * dimensions of its row and column.
 * </p>
 *
 * <p>
 * A component occupies a rectangular group of one or more cells.  The component can be aligned in
 * four ways within that cell.
 * </p>
 *
 * <p>
 * A component can be stretched horizontally to fit the cell set (full justification), or it can be
 * placed in the center of the cell.  The component could also be left justified or right
 * justified.  Similarly, the component can be full, center, top, or bottom justified along the
 * <pre>
 * public static void main (String args[])
 * {
 *     // Create a frame
 *     Frame frame = new Frame("Example of TableLayout");
 *     frame.setBounds (100, 100, 300, 300);
 * <spc>
 *     // Create a TableLayout for the frame
 *     double border = 10;
 *     double size[][] =
 *         {{border, 0.10, 20, TableLayout.FILL, 20, 0.20, border},  // Columns
 *          {border, 0.20, 20, TableLayout.FILL, 20, 0.20, border}}; // Rows
 * <spc>
 *     frame.setLayout (new TableLayout(size));
 * <spc>
 *     // Create some buttons
 *     String label[] = {"Top", "Bottom", "Left", "Right", "Center", "Overlap"};
 *     Button button[] = new Button[label.length];
 * <spc>
 *     for (int i = 0; i < label.length; i++)
 *         button[i] = new Button(label[i]);
 * <spc>
 *     // Add buttons
 *     frame.add (button[0], "1, 1, 5, 1"); // Top
 *     frame.add (button[1], "1, 5, 5, 5"); // Bottom
 *     frame.add (button[2], "1, 3      "); // Left
 *     frame.add (button[3], "5, 3      "); // Right
 *     frame.add (button[4], "3, 3, c, c"); // Center
 *     frame.add (button[5], "3, 3, 3, 5"); // Overlap
 * <spc>
 *     // Allow user to close the window to terminate the program
 *     frame.addWindowListener
 *         (new WindowListener()
 *             {
 *                 public void windowClosing (WindowEvent e)
 *                 {
 *                     System.exit (0);
 *                 }
 * <spc>
 *                 public void windowOpened (WindowEvent e) {}
 *                 public void windowClosed (WindowEvent e) {}
 *                 public void windowIconified (WindowEvent e) {}
 *                 public void windowDeiconified (WindowEvent e) {}
 *                 public void windowActivated (WindowEvent e) {}
 *                 public void windowDeactivated (WindowEvent e) {}
 *             }
 *         );
 * <spc>
 *     // Show frame
 *     frame.show();
 * }
 * </pre>
 * </p>
 *
 * @author Daniel E. Barbalace
 * @version 3.2 5/9/04
 */
public class TableLayout implements LayoutManager2, Serializable, TableLayoutConstants {
    /*
       Note: In this file, a cr refers to either a column or a row.  cr[C] always
       means column and cr[R] always means row.  A cr size is either a column
       width or a row Height.  TableLayout views columns and rows as being
       conceptually symmetric.  Therefore, much of the code applies to both
       columns and rows, and the use of the cr terminology eliminates redundancy.
       Also, for ease of reading, z always indicates a parameter whose value is
       either C or R.
     */

    /**
	 * 
	 */
	private static final long serialVersionUID = -3520320698489212868L;

	/** Default row/column size */
    protected static final double[][] defaultSize = {
            {  },
            {  }
        };

    /** Indicates a column */
    protected static final int C = 0;

    /** Indicates a row */
    protected static final int R = 1;

    /** Used to minimize reflection calls */
    protected static boolean checkForComponentOrientationSupport = true;

    /**
     * Method used to get component orientation while preserving compatability with earlier
     * versions of java.awt.Container.  Necessary for supporting older JDKs and MicroEdition
     * versions of Java.
     */
    protected static Method methodGetComponentOrientation;

    /** List of components and their sizes */
    protected LinkedList<Entry> list;

    /**
     * Offsets of crs in pixels.  The left boarder of column n is at crOffset[C][n] and the right
     * boarder is at cr[C][n + 1] for all columns including the last one.  crOffset[C].length =
     * crSize[C].length + 1
     */
    protected int[][] crOffset = { null, null };

    /** Sizes of crs in pixels */
    protected int[][] crSize = { null, null };

    /** Sizes of crs expressed in absolute and relative terms */
    protected double[][] crSpec = { null, null };

    /**
     * Indicates whether or not the size of the cells are known for the last known size of the
     * container.  If dirty is true or the container has been resized, the cell sizes must be
     * recalculated using calculateSize.
     */
    protected boolean dirty;

    /** Horizontal gap between columns */
    protected int hGap;

    /** Previous known height of the container */
    protected int oldHeight;

    /** Previous known width of the container */
    protected int oldWidth;

    /** Vertical gap between rows */
    protected int vGap;

    //******************************************************************************
    //** Constructors                                                            ***
    //******************************************************************************

    /**
     * Constructs an instance of TableLayout.  This TableLayout will have no columns or rows.  This
     * constructor is most useful for bean-oriented programming and dynamically adding columns and
     * rows.
     */
    public TableLayout() {
        init(defaultSize[C], defaultSize[R]);
    }

    /**
     * Constructs an instance of TableLayout.
     *
     * @param size widths of columns and heights of rows in the format, {{col0, col1, col2, ...,
     *        colN}, {row0, row1, row2, ..., rowM}} If this parameter is invalid, the TableLayout
     *        will have exactly one row and one column.
     *
     * @throws IllegalArgumentException
     */
    public TableLayout(double[][] size) {
        // Make sure columns and rows and nothing else is specified
        if ((size != null) && (size.length == 2)) {
            init(size[C], size[R]);
        }
        else {
            throw new IllegalArgumentException(
                "Parameter size should be an array, a[2], where a[0] is the " //$NON-NLS-1$
                + "is an array of column widths and a[1] is an array or row " //$NON-NLS-1$
                + "heights."); //$NON-NLS-1$
        }
    }

    /**
     * Constructs an instance of TableLayout.
     *
     * @param col widths of columns in the format, {{col0, col1, col2, ..., colN}
     * @param row heights of rows in the format, {{row0, row1, row2, ..., rowN}
     */
    public TableLayout(double[] col, double[] row) {
        init(col, row);
    }

    /**
     * Adjusts the number and sizes of rows in this layout.  After calling this method, the caller
     * should request this layout manager to perform the layout.  This can be done with the
     * following code:
     * <pre>
     *     layout.layoutContainer(container);
     *     container.repaint();
     * </pre>
     * or
     * <pre>
     *     window.pack()
     * </pre>
     * If this is not done, the changes in the layout will not be seen until the container is
     * resized.
     *
     * @param column widths of each of the columns
     *
     * @see #getColumn
     */
    public void setColumn(double[] column) {
        setCr(C, column);
    }

    /**
     * Adjusts the width of a single column in this layout.  After calling this method, the caller
     * should request this layout manager to perform the layout.  This can be done with the
     * following code: <code> layout.layoutContainer(container); container.repaint(); </code> or
     * <pre>
     *     window.pack()
     * </pre>
     * If this is not done, the changes in the layout will not be seen until the container is
     * resized.
     *
     * @param i zero-based index of column to set.  If this parameter is not valid, an
     *        ArrayOutOfBoundsException will be thrown.
     * @param size width of the column.  This parameter cannot be null.
     *
     * @see #getColumn
     */
    public void setColumn(int i, double size) {
        setCr(C, i, size);
    }

    /**
     * Gets the sizes of columns in this layout.
     *
     * @return widths of each of the columns
     *
     * @see #setColumn
     */
    public double[] getColumn() {
        // Copy columns
        double[] column = new double[crSpec[C].length];

        System.arraycopy(crSpec[C], 0, column, 0, column.length);

        return column;
    }

    /**
     * Gets the width of a single column in this layout.
     *
     * @param i zero-based index of row to get.  If this parameter is not valid, an
     *        ArrayOutOfBoundsException will be thrown.
     *
     * @return width of the requested column
     *
     * @see #setRow
     */
    public double getColumn(int i) {
        return crSpec[C][i];
    }

    /**
     * Sets the constraints of a given component.
     *
     * @param component desired component.  This parameter cannot be null.
     * @param constraint new set of constraints.  This parameter cannot be null.
     *
     * @throws IllegalArgumentException
     */
    public void setConstraints(Component component,
        TableLayoutConstraints constraint) {
        // Check parameters
        if (component == null) {
            throw new IllegalArgumentException(
                "Parameter component cannot be null."); //$NON-NLS-1$
        }
        else if (constraint == null) {
            throw new IllegalArgumentException(
                "Parameter constraint cannot be null."); //$NON-NLS-1$
        }

        // Find and update constraints for the given component
        ListIterator<Entry> iterator = list.listIterator(0);

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (entry.component == component) {
                iterator.set(new Entry(component, constraint));
            }
        }
    }

    //******************************************************************************
    //** Get/Set methods                                                         ***
    //******************************************************************************

    /**
     * Gets the constraints of a given component.
     *
     * @param component desired component
     *
     * @return If the given component is found, the constraints associated with that component.  If
     *         the given component is null or is not found, null is returned.
     */
    public TableLayoutConstraints getConstraints(Component component) {
        ListIterator<Entry> iterator = list.listIterator(0);

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (entry.component == component) {
                return new TableLayoutConstraints(entry.cr1[C], entry.cr1[R],
                    entry.cr2[C], entry.cr2[R], entry.alignment[C],
                    entry.alignment[R]);
            }
        }

        return null;
    }

    /**
     * Sets the horizontal gap between colunns.
     *
     * @param hGap the horizontal gap in pixels
     *
     * @throws IllegalArgumentException
     */
    public void setHGap(int hGap) {
        if (hGap >= 0) {
            this.hGap = hGap;
        }
        else {
            throw new IllegalArgumentException(
                "Parameter hGap must be non-negative."); //$NON-NLS-1$
        }
    }

    /**
     * Gets the horizontal gap between colunns.
     *
     * @return the horizontal gap in pixels
     */
    public int getHGap() {
        return hGap;
    }

    /**
     * Determines whether or not there are any components with invalid constraints. An invalid
     * constraint is one that references a non-existing row or column. For example, on a table
     * with five rows, row -1 and row 5 are both invalid. Valid rows are 0 through 4, inclusively.
     *
     * @return a list of TableLayout.Entry instances refering to the invalid constraints and
     *         corresponding components
     *
     * @throws RuntimeException
     *
     * @see #getOverlappingEntry
     */
    public java.util.List<Object> getInvalidEntry() {
        LinkedList<Object> listInvalid = new LinkedList<Object>();

        try {
            ListIterator<Entry> iterator = list.listIterator(0);

            while (iterator.hasNext()) {
                Entry entry = iterator.next();

                if ((entry.cr1[R] < 0) || (entry.cr1[C] < 0)
                    || (entry.cr2[R] >= crSpec[R].length)
                    || (entry.cr2[C] >= crSpec[C].length)) {
                    listInvalid.add(entry.copy());
                }
            }
        }
        catch (CloneNotSupportedException error) {
            throw new RuntimeException("Unexpected CloneNotSupportedException"); //$NON-NLS-1$
        }

        return listInvalid;
    }

    /**
     * Returns the alignment along the x axis.  This specifies how the component would like to be
     * aligned relative to other components.  The value should be a number between 0 and 1 where 0
     * represents alignment along the origin, 1 is aligned the furthest away from the origin, 0.5
     * is centered, etc.
     *
     * @param parent -
     *
     * @return unconditionally, 0.5
     */
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how the component would like to be
     * aligned relative to other components.  The value should be a number between 0 and 1 where 0
     * represents alignment along the origin, 1 is aligned the furthest away from the origin, 0.5
     * is centered, etc.
     *
     * @param parent
     *
     * @return unconditionally, 0.5
     */
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    /**
     * Gets the number of columns in this layout.
     *
     * @return the number of columns
     */
    public int getNumColumn() {
        return crSpec[C].length;
    }

    /**
     * Gets the number of rows in this layout.
     *
     * @return the number of rows
     */
    public int getNumRow() {
        return crSpec[R].length;
    }

    /**
     * Gets a list of overlapping components and their constraints.  Two components overlap if they
     * cover at least one common cell.
     *
     * @return a list of zero or more TableLayout.Entry instances
     *
     * @throws RuntimeException -
     *
     * @see #getInvalidEntry
     */
    public java.util.List<Object> getOverlappingEntry() {
        LinkedList<Object> listOverlapping = new LinkedList<Object>();

        try {
            // Count contraints
            int numEntry = list.size();

            // If there are no components, they can't be overlapping
            if (numEntry == 0) {
                return listOverlapping;
            }

            // Put entries in an array
            Entry[] entry = list.toArray(new Entry[numEntry]);

            // Check all components
            for (int knowUnique = 1; knowUnique < numEntry; knowUnique++) {
                for (int checking = knowUnique - 1; checking >= 0;
                    checking--) {
                    if (((entry[checking].cr1[C] >= entry[knowUnique].cr1[C])
                        && (entry[checking].cr1[C] <= entry[knowUnique].cr2[C])
                        && (entry[checking].cr1[R] >= entry[knowUnique].cr1[R])
                        && (entry[checking].cr1[R] <= entry[knowUnique].cr2[R]))
                        || ((entry[checking].cr2[C] >= entry[knowUnique].cr1[C])
                        && (entry[checking].cr2[C] <= entry[knowUnique].cr2[C])
                        && (entry[checking].cr2[R] >= entry[knowUnique].cr1[R])
                        && (entry[checking].cr2[R] <= entry[knowUnique].cr2[R]))) {
                        listOverlapping.add(entry[checking].copy());
                    }
                }
            }
        }
        catch (CloneNotSupportedException error) {
            throw new RuntimeException("Unexpected CloneNotSupportedException"); //$NON-NLS-1$
        }

        return listOverlapping;
    }

    /**
     * Adjusts the number and sizes of rows in this layout.  After calling this method, the caller
     * should request this layout manager to perform the layout.  This can be done with the
     * following code: <code> layout.layoutContainer(container); container.repaint(); </code> or
     * <pre>
     *     window.pack()
     * </pre>
     * If this is not done, the changes in the layout will not be seen until the container is
     * resized.
     *
     * @param row heights of each of the rows.  This parameter cannot be null.
     *
     * @see #getRow
     */
    public void setRow(double[] row) {
        setCr(R, row);
    }

    /**
     * Adjusts the height of a single row in this layout.  After calling this method, the caller
     * should request this layout manager to perform the layout.  This can be done with the
     * following code: <code> layout.layoutContainer(container); container.repaint(); </code> or
     * <pre>
     *     window.pack()
     * </pre>
     * If this is not done, the changes in the layout will not be seen until the container is
     * resized.
     *
     * @param i zero-based index of row to set.  If this parameter is not valid, an
     *        ArrayOutOfBoundsException will be thrown.
     * @param size height of the row.  This parameter cannot be null.
     *
     * @see #getRow
     */
    public void setRow(int i, double size) {
        setCr(R, i, size);
    }

    /**
     * Gets the height of a single row in this layout.
     *
     * @return height of the requested row
     *
     * @see #setRow
     */
    public double[] getRow() {
        // Copy rows
        double[] row = new double[crSpec[R].length];

        System.arraycopy(crSpec[R], 0, row, 0, row.length);

        return row;
    }

    /**
     * Gets the sizes of a row in this layout.
     *
     * @param i zero-based index of row to get.  If this parameter is not valid, an
     *        ArrayOutOfBoundsException will be thrown.
     *
     * @return height of each of the requested row
     *
     * @see #setRow
     */
    public double getRow(int i) {
        return crSpec[R][i];
    }

    /**
     * Sets the vertical gap between rows.
     *
     * @param vGap the horizontal gap in pixels
     *
     * @throws IllegalArgumentException -
     */
    public void setVGap(int vGap) {
        if (vGap >= 0) {
            this.vGap = vGap;
        }
        else {
            throw new IllegalArgumentException(
                "Parameter vGap must be non-negative."); //$NON-NLS-1$
        }
    }

    /**
     * Gets the vertical gap between rows.
     *
     * @return the vertical gap in pixels
     */
    public int getVGap() {
        return vGap;
    }

    /**
     * Adds the specified component with the specified name to the layout.
     *
     * @param name indicates entry's position and anchor
     * @param component component to add
     */
    public void addLayoutComponent(String name, Component component) {
        addLayoutComponent(component, name);
    }

    //******************************************************************************
    //** java.awt.event.LayoutManager2 methods                                   ***
    //******************************************************************************

    /**
     * Adds the specified component with the specified name to the layout.
     *
     * @param component component to add
     * @param constraint indicates entry's position and alignment
     *
     * @throws IllegalArgumentException -
     */
    public void addLayoutComponent(Component component, Object constraint) {
        if (constraint instanceof String) {
            // Create an entry to associate component with its constraints
            constraint = new TableLayoutConstraints((String) constraint);

            // Add component and constraints to the list
            list.add(new Entry(component, (TableLayoutConstraints) constraint));

            // Indicate that the cell sizes are not known
            dirty = true;
        }
        else if (constraint instanceof TableLayoutConstraints) {
            // Add component and constraints to the list
            list.add(new Entry(component, (TableLayoutConstraints) constraint));

            // Indicate that the cell sizes are not known
            dirty = true;
        }
        else if (constraint == null) {
            throw new IllegalArgumentException(
                "No constraint for the component"); //$NON-NLS-1$
        }
        else {
            throw new IllegalArgumentException(
                "Cannot accept a constraint of class " + constraint.getClass()); //$NON-NLS-1$
        }
    }

    /**
     * Deletes a column in this layout.  All components to the right of the deletion point are
     * moved left one column.  The container will need to be laid out after this method returns.
     * See <code>setColumn</code>.
     *
     * @param i zero-based index of column to delete
     *
     * @see #setColumn
     * @see #deleteColumn
     */
    public void deleteColumn(int i) {
        deleteCr(C, i);
    }

    /**
     * Deletes a row in this layout.  All components below the deletion point are moved up one row.
     * The container will need to be laid out after this method returns.  See <code>setRow</code>.
     * There must be at least two rows in order to delete a row.
     *
     * @param i zero-based index of row to delete
     *
     * @see #setRow
     * @see #deleteRow
     */
    public void deleteRow(int i) {
        deleteCr(R, i);
    }

    //******************************************************************************
    //** Insertion/Deletion methods                                              ***
    //******************************************************************************

    /**
     * Inserts a column in this layout.  All components to the right of the insertion point are
     * moved right one column.  The container will need to be laid out after this method returns.
     * See <code>setColumn</code>.
     *
     * @param i zero-based index at which to insert the column
     * @param size size of the column to be inserted
     *
     * @see #setColumn
     * @see #deleteColumn
     */
    public void insertColumn(int i, double size) {
        insertCr(C, i, size);
    }

    /**
     * Inserts a cr for the methods insertRow or insertColumn.
     *
     * @param z indicates row or column
     * @param i zero-based index at which to insert the cr
     * @param size size of cr being inserted
     *
     * @throws IllegalArgumentException -
     */
    public void insertCr(int z, int i, double size) {
        // Make sure position is valid
        if ((i < 0) || (i > crSpec[z].length)) {
            throw new IllegalArgumentException("Parameter i is invalid.  i = "
                + i + ".  Valid range is [0, " //$NON-NLS-1$ 
                + crSpec[z].length + "]."); //$NON-NLS-1$
        }

        // Make sure row size is valid
        if ((size < 0.0) && (size != FILL) && (size != PREFERRED)
            && (size != MINIMUM)) {
            size = 0.0;
        }

        // Copy crs
        double[] cr = new double[crSpec[z].length + 1];

        System.arraycopy(crSpec[z], 0, cr, 0, i);
        System.arraycopy(crSpec[z], i, cr, i + 1, crSpec[z].length - i);

        // Insert cr
        cr[i] = size;
        crSpec[z] = cr;

        // Move all components that are below the new cr
        ListIterator<Entry> iterator = list.listIterator(0);

        while (iterator.hasNext()) {
            // Get next entry
            Entry entry = iterator.next();

            // Is the first cr below the new cr
            if (entry.cr1[z] >= i) {
                // Move first cr
                entry.cr1[z]++;
            }

            // Is the second cr below the new cr
            if (entry.cr2[z] >= i) {
                // Move second cr
                entry.cr2[z]++;
            }
        }

        // Indicate that the cell sizes are not known
        dirty = true;
    }

    /**
     * Inserts a row in this layout.  All components below the insertion point are moved down one
     * row.  The container will need to be laid out after this method returns.  See
     * <code>setRow</code>.
     *
     * @param i zero-based index at which to insert the row
     * @param size size of the row to be inserted
     *
     * @see #setRow
     * @see #deleteRow
     */
    public void insertRow(int i, double size) {
        insertCr(R, i, size);
    }

    /**
     * Invalidates the layout, indicating that if the layout manager has cached information it
     * should be discarded.
     *
     * @param target -
     */
    public void invalidateLayout(Container target) {
        dirty = true;
    }

    //******************************************************************************
    //** java.awt.event.LayoutManager methods                                    ***
    //******************************************************************************

    /**
     * To lay out the specified container using this layout.  This method reshapes the components
     * in the specified target container in order to satisfy the constraints of all components.
     *
     * <p>
     * User code should not have to call this method directly.
     * </p>
     *
     * @param container container being served by this layout manager
     */
    public void layoutContainer(Container container) {
        // Calculate sizes if container has changed size or components were added
        Dimension d = container.getSize();

        if (dirty || (d.width != oldWidth) || (d.height != oldHeight)) {
            calculateSize(container);
        }

        // Get components
        Component[] component = container.getComponents();

        // Layout components
        for (int counter = 0; counter < component.length; counter++) {
            try {
                // Get the entry for the next component
                ListIterator<Entry> iterator = list.listIterator(0);
                Entry entry = null;

                while (iterator.hasNext()) {
                    entry = iterator.next();

                    if (entry.component == component[counter]) {
                        break;
                    }
                    else {
                        entry = null;
                    }
                }

                // Skip any components that have not been place in a specific cell,
                // setting the skip component's bounds to zero
                if (entry == null) {
                    component[counter].setBounds(0, 0, 0, 0);

                    continue;
                }

                // The following block of code has been optimized so that the
                // preferred size of the component is only obtained if it is
                // needed.  There are components in which the getPreferredSize
                // method is extremely expensive, such as data driven controls
                // with a large amount of data.
                // Get the preferred size of the component
                int preferredWidth = 0;
                int preferredHeight = 0;

                if ((entry.alignment[C] != FULL)
                    || (entry.alignment[R] != FULL)) {
                    Dimension preferredSize = component[counter]
                        .getPreferredSize();

                    preferredWidth = preferredSize.width;
                    preferredHeight = preferredSize.height;
                }

                // Calculate the coordinates and size of the component
                int[] value = calculateSizeAndOffset(entry, preferredWidth, true);
                int x = value[0];
                int w = value[1];

                value = calculateSizeAndOffset(entry, preferredHeight, false);

                int y = value[0];
                int h = value[1];

                // Compensate for component orientation.
                ComponentOrientation co = getComponentOrientation(container);

                if (!co.isLeftToRight()) {
                    x = d.width - x - w;
                }

                // Move and resize component
                component[counter].setBounds(x, y, w, h);
            }
            catch (Exception error) {
                // If any error occurs, set the bounds of this component to zero
                // and continue
                component[counter].setBounds(0, 0, 0, 0);

                continue;
            }
        }
    }

    /**
     * Returns the maximum dimensions for this layout given the components in the specified target
     * container.
     *
     * @param target the component which needs to be laid out
     *
     * @return unconditionally, a Dimension of Integer.MAX_VALUE by Integer.MAX_VALUE since
     *         TableLayout does not limit the maximum size of a container
     */
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Determines the minimum size of the container argument using this layout. The minimum size is
     * the smallest size that, if used for the container's size, will ensure that all components
     * are at least as large as their minimum size.  This method cannot guarantee that all
     * components will be their minimum size.  For example, if component A and component B are
     * each allocate half of the container's width and component A wants to be 10 pixels wide
     * while component B wants to be 100 pixels wide, they cannot both be accommodated.  Since in
     * general components rather be larger than their minimum size instead of smaller, component
     * B's request will be fulfilled. The minimum size of the container would be 200 pixels.
     *
     * @param container container being served by this layout manager
     *
     * @return a dimension indicating the container's minimum size
     */
    public Dimension minimumLayoutSize(Container container) {
        return calculateLayoutSize(container, MINIMUM);
    }

    /**
     * Determines the preferred size of the container argument using this layout. The preferred
     * size is the smallest size that, if used for the container's size, will ensure that all
     * components are at least as large as their preferred size.  This method cannot guarantee
     * that all components will be their preferred size.  For example, if component A and
     * component B are each allocate half of the container's width and component A wants to be 10
     * pixels wide while component B wants to be 100 pixels wide, they cannot both be
     * accommodated.  Since in general components rather be larger than their preferred size
     * instead of smaller, component B's request will be fulfilled. The preferred size of the
     * container would be 200 pixels.
     *
     * @param container container being served by this layout manager
     *
     * @return a dimension indicating the container's preferred size
     */
    public Dimension preferredLayoutSize(Container container) {
        return calculateLayoutSize(container, PREFERRED);
    }

    /**
     * Removes the specified component from the layout.
     *
     * @param component component being removed
     */
    public void removeLayoutComponent(Component component) {
        // Remove the component
        ListIterator<Entry> iterator = list.listIterator(0);

        while (iterator.hasNext()) {
            Entry entry = iterator.next();

            if (entry.component == component) {
                iterator.remove();
            }
        }

        // Indicate that the cell sizes are not known since
        dirty = true;
    }

    //******************************************************************************
    //** Misc methods                                                            ***
    //******************************************************************************

    /**
     * Converts this TableLayout to a string.
     *
     * @return a string representing the columns and row sizes in the form "{{col0, col1, col2,
     *         ..., colN}, {row0, row1, row2, ..., rowM}}"
     */
    @Override
	public String toString() {
        int counter;

        String value = "TableLayout {{"; //$NON-NLS-1$

        if (crSpec[C].length > 0) {
            for (counter = 0; counter < (crSpec[C].length - 1); counter++) {
                value += (crSpec[C][counter] + ", "); //$NON-NLS-1$
            }

            value += (crSpec[C][crSpec[C].length - 1] + "}, {"); //$NON-NLS-1$
        }
        else {
            value += "}, {"; //$NON-NLS-1$
        }

        if (crSpec[R].length > 0) {
            for (counter = 0; counter < (crSpec[R].length - 1); counter++) {
                value += (crSpec[R][counter] + ", "); //$NON-NLS-1$
            }

            value += (crSpec[R][crSpec[R].length - 1] + "}}"); //$NON-NLS-1$
        }
        else {
            value += "}}"; //$NON-NLS-1$
        }

        return value;
    }

    /**
     * Gets the container's component orientation.  If a JDK that does not support component
     * orientation is being used, then null is returned.
     *
     * @param container Container whose orientation is being queried
     *
     * @return the container's orientation or null if no orientation is supported
     */
    protected ComponentOrientation getComponentOrientation(Container container) {
        // This method is implemented to only get the class and method objects
        // once so as to reduce expensive reflection operations.  If the reflection
        // fails, then component orientation is not supported.
        ComponentOrientation co = null;

        try {
            if (checkForComponentOrientationSupport) {
                methodGetComponentOrientation = Class.forName(
                        "java.awt.Container").getMethod //$NON-NLS-1$
                    ("getComponentOrientation", new Class[0]); //$NON-NLS-1$

                checkForComponentOrientationSupport = false;
            }

            if (methodGetComponentOrientation != null) {
                co = (ComponentOrientation) methodGetComponentOrientation
                    .invoke(container, new Object[0]);
            }
        }
        catch (Exception e) {
        }

        return co;
    }

    /**
     * Sets the sizes of rows or columns for the methods setRow or setColumn.
     *
     * @param z indicates row or column
     * @param size new cr size
     */
    protected void setCr(int z, double[] size) {
        // Copy crs
        crSpec[z] = new double[size.length];
        System.arraycopy(size, 0, crSpec[z], 0, crSpec[z].length);

        // Make sure rows are valid
        for (int counter = 0; counter < crSpec[z].length; counter++) {
            if ((crSpec[z][counter] < 0.0) && (crSpec[z][counter] != FILL)
                && (crSpec[z][counter] != PREFERRED)
                && (crSpec[z][counter] != MINIMUM)) {
                crSpec[z][counter] = 0.0;
            }
        }

        // Indicate that the cell sizes are not known
        dirty = true;
    }

    /**
     * Sets the sizes of rows or columns for the methods setRow or setColumn.
     *
     * @param z indicates row or column
     * @param i indicates which cr to resize
     * @param size new cr size
     */
    protected void setCr(int z, int i, double size) {
        // Make sure size is valid
        if ((size < 0.0) && (size != FILL) && (size != PREFERRED)
            && (size != MINIMUM)) {
            size = 0.0;
        }

        // Copy new size
        crSpec[z][i] = size;

        // Indicate that the cell sizes are not known
        dirty = true;
    }

    /**
     * Assigns absolute sizes.
     *
     * @param z indicates row or column
     * @param availableSize amount of space available in the container
     *
     * @return the amount of space available after absolute crs have been assigned sizes
     */
    protected int assignAbsoluteSize(int z, int availableSize) {
        int numCr = crSpec[z].length;

        for (int counter = 0; counter < numCr; counter++) {
            if ((crSpec[z][counter] >= 1.0) || (crSpec[z][counter] == 0.0)) {
                crSize[z][counter] = (int) (crSpec[z][counter] + 0.5);
                availableSize -= crSize[z][counter];
            }
        }

        return availableSize;
    }

    /**
     * Assigns FILL sizes.
     *
     * @param z indicates row or column
     * @param availableSize amount of space available in the container
     */
    protected void assignFillSize(int z, int availableSize) {
        // Skip if there is no more space to allocate
        if (availableSize <= 0) {
            return;
        }

        // Count the number of "fill" cells
        int numFillSize = 0;
        int numCr = crSpec[z].length;

        for (int counter = 0; counter < numCr; counter++) {
            if (crSpec[z][counter] == FILL) {
                numFillSize++;
            }
        }

        // If numFillSize is zero, the if statement below will always evaluate to
        // false and the division will not occur.
        // If there are more than one "fill" cell, slack may occur due to rounding
        // errors
        int slackSize = availableSize;

        // Assign "fill" cells equal amounts of the remaining space
        for (int counter = 0; counter < numCr; counter++) {
            if (crSpec[z][counter] == FILL) {
                crSize[z][counter] = availableSize / numFillSize;
                slackSize -= crSize[z][counter];
            }
        }

        // Assign one pixel of slack to each FILL cr, starting at the last one,
        // until all slack has been consumed
        for (int counter = numCr - 1; (counter >= 0) && (slackSize > 0);
            counter--) {
            if (crSpec[z][counter] == FILL) {
                crSize[z][counter]++;
                slackSize--;
            }
        }
    }

    /**
     * Assigned widths to preferred and minimum size columns and rows.  This reduces the available
     * width and height.  Minimum widths/heights must be calculated first because they affect
     * preferred widths/heights, but not vice versa.  The end result is that any component
     * contained wholly or partly in a column/row of minimum/preferred width will get at least its
     * minimum/preferred width, respectively.
     *
     * @param z indicates row or column
     * @param availableSize amount of space available in the container
     * @param typeOfSize indicates preferred or minimum
     *
     * @return the amount of space available after absolute crs have been assigned sizes
     */
    protected int assignPrefMinSize(int z, int availableSize, double typeOfSize) {
        // Get variables referring to columns or rows (crs)
        int numCr = crSpec[z].length;

        // Address every cr
        for (int counter = 0; counter < numCr; counter++) {
            // Is the current cr a preferred/minimum (based on typeOfSize) size
            if (crSpec[z][counter] == typeOfSize) {
                // Assume a maximum width of zero
                int maxSize = 0;

                // Find maximum preferred/min width of all components completely
                // or partially contained within this cr
                ListIterator<Entry> iterator = list.listIterator(0);

nextComponent: 
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();

                    // Skip invalid entries
                    if ((entry.cr1[z] < 0) || (entry.cr2[z] >= numCr)) {
                        continue nextComponent;
                    }

                    // Find the maximum desired size of this cr based on all crs
                    // the current component occupies
                    if ((entry.cr1[z] <= counter) && (entry.cr2[z] >= counter)) {
                        // Setup size and number of adjustable crs
                        Dimension p = (typeOfSize == PREFERRED)
                            ? entry.component.getPreferredSize()
                            : entry.component.getMinimumSize();

                        int size = (p == null) ? 0 : ((z == C) ? p.width
                                                               : p.height);
                        int numAdjustable = 0;

                        // Calculate for preferred size
                        if (typeOfSize == PREFERRED) {
                            // Consider all crs this component occupies
                            for (int entryCr = entry.cr1[z];
                                entryCr <= entry.cr2[z]; entryCr++) {
                                // Subtract absolute, relative, and minumum cr
                                // sizes, which have already been calculated
                                if ((crSpec[z][entryCr] >= 0.0)
                                    || (crSpec[z][entryCr] == MINIMUM)) {
                                    size -= crSize[z][entryCr];
                                }

                                // Count preferred/min width columns
                                else if (crSpec[z][entryCr] == PREFERRED) {
                                    numAdjustable++;
                                }

                                // Skip any component that occupies a fill cr
                                // because the fill should fulfill the size
                                // requirements
                                else if (crSpec[z][entryCr] == FILL) {
                                    continue nextComponent;
                                }
                            }
                        }

                        // Calculate for minimum size
                        else {
                            // Consider all crs this component occupies
                            for (int entryCr = entry.cr1[z];
                                entryCr <= entry.cr2[z]; entryCr++) {
                                // Subtract absolute and relative cr sizes, which
                                // have already been calculated
                                if (crSpec[z][entryCr] >= 0.0) {
                                    size -= crSize[z][entryCr];
                                }

                                // Count preferred/min width columns
                                else if ((crSpec[z][entryCr] == PREFERRED)
                                    || (crSpec[z][entryCr] == MINIMUM)) {
                                    numAdjustable++;
                                }

                                // Skip any component that occupies a fill cr
                                // because the fill should fulfill the size
                                // requirements
                                else if (crSpec[z][entryCr] == FILL) {
                                    continue nextComponent;
                                }
                            }
                        }

                        // Divide the size evenly among the adjustable crs
                        size = (int) Math.ceil(size / (double) numAdjustable);

                        // Take the maximumn size
                        if (maxSize < size) {
                            maxSize = size;
                        }
                    }
                }

                // Assign preferred size
                crSize[z][counter] = maxSize;

                // Reduce available size
                availableSize -= maxSize;
            }
        }

        return availableSize;
    }

    /**
     * Assigns relative sizes.
     *
     * @param z indicates row or column
     * @param availableSize amount of space available in the container
     *
     * @return the amount of space available after relative crs have been assigned sizes
     */
    protected int assignRelativeSize(int z, int availableSize) {
        int relativeSize = (availableSize < 0) ? 0 : availableSize;
        int numCr = crSpec[z].length;

        for (int counter = 0; counter < numCr; counter++) {
            if ((crSpec[z][counter] > 0.0) && (crSpec[z][counter] < 1.0)) {
                crSize[z][counter] = (int) ((crSpec[z][counter] * relativeSize)
                    + 0.5);

                availableSize -= crSize[z][counter];
            }
        }

        return availableSize;
    }

    /**
     * Calculates the preferred or minimum size for the methods preferredLayoutSize and
     * minimumLayoutSize.
     *
     * @param container container whose size is being calculated
     * @param typeOfSize indicates preferred or minimum
     *
     * @return a dimension indicating the container's preferred or minimum size
     */
    protected Dimension calculateLayoutSize(Container container,
        double typeOfSize) {
        //  Get preferred/minimum sizes
        Entry[] entryList = list.toArray(new Entry[list.size()]);
        int numEntry = entryList.length;
        Dimension[] prefMinSize = new Dimension[numEntry];

        for (int i = 0; i < numEntry; i++) {
            prefMinSize[i] = (typeOfSize == PREFERRED)
                ? entryList[i].component.getPreferredSize()
                : entryList[i].component.getMinimumSize();
        }

        // Calculate sizes
        int width = calculateLayoutSize(container, C, typeOfSize, entryList,
                prefMinSize);

        int height = calculateLayoutSize(container, R, typeOfSize, entryList,
                prefMinSize);

        // Compensate for container's insets
        Insets inset = container.getInsets();

        width += (inset.left + inset.right);
        height += (inset.top + inset.bottom);

        return new Dimension(width, height);
    }

    /**
     * Calculates the preferred or minimum size for the method calculateLayoutSize(Container
     * container, double typeOfSize).  This method is passed the preferred/minimum sizes of the
     * components so that the potentially expensive methods getPreferredSize()/getMinimumSize()
     * are not called twice for the same component.
     *
     * @param container container whose size is being calculated
     * @param z -
     * @param typeOfSize indicates preferred or minimum
     * @param entryList list of Entry objects
     * @param prefMinSize list of preferred or minimum sizes
     *
     * @return a dimension indicating the container's preferred or minimum size
     */
    protected int calculateLayoutSize(Container container, int z,
        double typeOfSize, Entry[] entryList, Dimension[] prefMinSize) {
        Dimension size; // Preferred/minimum size of current component
        int scaledSize = 0; // Preferred/minimum size of scaled components
        int temp; // Temporary variable used to compare sizes
        int counter; // Counting variable

        // Get number of crs
        int numCr = crSpec[z].length;

        // Determine percentage of space allocated to fill components.  This is
        // one minus the sum of all scalable components.
        double fillSizeRatio = 1.0;
        int numFillSize = 0;

        for (counter = 0; counter < numCr; counter++) {
            if ((crSpec[z][counter] > 0.0) && (crSpec[z][counter] < 1.0)) {
                fillSizeRatio -= crSpec[z][counter];
            }
            else if (crSpec[z][counter] == FILL) {
                numFillSize++;
            }
        }

        // Adjust fill ratios to reflect number of fill rows/columns
        if (numFillSize > 1) {
            fillSizeRatio /= numFillSize;
        }

        // Cap fill ratio bottoms to 0.0
        if (fillSizeRatio < 0.0) {
            fillSizeRatio = 0.0;
        }

        // Create array to hold actual sizes in pixels
        crSize[z] = new int[numCr];

        // Assign absolute sizes (must be done before assignPrefMinSize)
        // This is done to calculate absolute cr sizes
        assignAbsoluteSize(z, 0);

        // Assign preferred and minimum sizes (must be done after assignAbsoluteSize)
        // This is done to calculate preferred/minimum cr sizes
        assignPrefMinSize(z, 0, typeOfSize);

        int[] crPrefMin = new int[numCr];

        for (counter = 0; counter < numCr; counter++) {
            if ((crSpec[z][counter] == PREFERRED)
                || (crSpec[z][counter] == MINIMUM)) {
                crPrefMin[counter] = crSize[z][counter];
            }
        }

        // Find maximum preferred/minimum size of all scaled components
        int numColumn = crSpec[C].length;
        int numRow = crSpec[R].length;
        int numEntry = entryList.length;

        for (int entryCounter = 0; entryCounter < numEntry; entryCounter++) {
            // Get next entry
            Entry entry = entryList[entryCounter];

            // Make sure entry is in valid rows and columns
            if ((entry.cr1[C] < 0) || (entry.cr1[C] >= numColumn)
                || (entry.cr2[C] >= numColumn) || (entry.cr1[R] < 0)
                || (entry.cr1[R] >= numRow) || (entry.cr2[R] >= numRow)) {
                // Skip the bad component
                continue;
            }

            // Get preferred/minimum size of current component
            size = prefMinSize[entryCounter];

            //----------------------------------------------------------------------
            // Calculate portion of component that is not absolutely sized
            int scalableSize = (z == C) ? size.width : size.height;

            for (counter = entry.cr1[z]; counter <= entry.cr2[z]; counter++) {
                if (crSpec[z][counter] >= 1.0) {
                    scalableSize -= crSpec[z][counter];
                }
                else if ((crSpec[z][counter] == PREFERRED)
                    || (crSpec[z][counter] == MINIMUM)) {
                    scalableSize -= crPrefMin[counter];
                }
            }

            //----------------------------------------------------------------------
            // Determine total percentage of scalable space that the component
            // occupies by adding the relative columns and the fill columns
            double relativeSize = 0.0;

            for (counter = entry.cr1[z]; counter <= entry.cr2[z]; counter++) {
                // Cr is scaled
                if ((crSpec[z][counter] > 0.0) && (crSpec[z][counter] < 1.0)) {
                    // Add scaled size to relativeWidth
                    relativeSize += crSpec[z][counter];
                }

                // Cr is fill
                else if ((crSpec[z][counter] == FILL) && (fillSizeRatio != 0.0)) {
                    // Add fill size to relativeWidth
                    relativeSize += fillSizeRatio;
                }
            }

            // Determine the total scaled size as estimated by this component
            if (relativeSize == 0) {
                temp = 0;
            }
            else {
                temp = (int) ((scalableSize / relativeSize) + 0.5);
            }

            //----------------------------------------------------------------------
            // If the container needs to be bigger, make it so
            if (scaledSize < temp) {
                scaledSize = temp;
            }
        }

        // totalSize is the scaledSize plus the sum of all absolute sizes and all
        // preferred sizes
        int totalSize = scaledSize;

        for (counter = 0; counter < numCr; counter++) {
            // Is the current cr an absolute size
            if (crSpec[z][counter] >= 1.0) {
                totalSize += (int) (crSpec[z][counter] + 0.5);
            }

            // Is the current cr a preferred/minimum size
            else if ((crSpec[z][counter] == PREFERRED)
                || (crSpec[z][counter] == MINIMUM)) {
                // Add preferred/minimum width
                totalSize += crPrefMin[counter];
            }
        }

        // Compensate for horizontal and vertical gap
        if (numCr > 0) {
            totalSize += (((z == C) ? hGap : vGap) * (numCr - 1));
        }

        return totalSize;
    }

    /**
     * Calculates the offset of each cr.
     *
     * @param z indicates row or column
     * @param inset -
     */
    protected void calculateOffset(int z, Insets inset) {
        int numCr = crSpec[z].length;

        crOffset[z] = new int[numCr + 1];
        crOffset[z][0] = (z == C) ? inset.left : inset.top;

        for (int counter = 0; counter < numCr; counter++) {
            crOffset[z][counter + 1] = crOffset[z][counter]
                + crSize[z][counter];
        }
    }

    //******************************************************************************
    //** Calculation methods                                                     ***
    //******************************************************************************

    /**
     * Calculates the sizes of the rows and columns based on the absolute and relative sizes
     * specified in <code>crSpec[R]</code> and <code>crSpec[C]</code> and the size of the
     * container.  The result is stored in <code>crSize[R]</code> and <code>crSize[C]</code>.
     *
     * @param container container using this TableLayout
     */
    protected void calculateSize(Container container) {
        // Get the container's insets
        Insets inset = container.getInsets();

        // Get the size of the container's available space
        Dimension d = container.getSize();
        int availableWidth = d.width - inset.left - inset.right;
        int availableHeight = d.height - inset.top - inset.bottom;

        // Compensate for horiztonal and vertical gaps
        if (crSpec[C].length > 0) {
            availableWidth -= (hGap * (crSpec[C].length - 1));
        }

        if (crSpec[R].length > 0) {
            availableHeight -= (vGap * (crSpec[R].length - 1));
        }

        // Create array to hold actual sizes in pixels
        crSize[C] = new int[crSpec[C].length];
        crSize[R] = new int[crSpec[R].length];

        // Assign absolute sizes (must be done before assignPrefMinSize)
        availableWidth = assignAbsoluteSize(C, availableWidth);
        availableHeight = assignAbsoluteSize(R, availableHeight);

        // Assign preferred and minimum sizes (must be done after assignAbsoluteSize)
        availableWidth = assignPrefMinSize(C, availableWidth, MINIMUM);
        availableWidth = assignPrefMinSize(C, availableWidth, PREFERRED);
        availableHeight = assignPrefMinSize(R, availableHeight, MINIMUM);
        availableHeight = assignPrefMinSize(R, availableHeight, PREFERRED);

        // Assign relative sizes
        availableWidth = assignRelativeSize(C, availableWidth);
        availableHeight = assignRelativeSize(R, availableHeight);

        // Assign fill sizes
        assignFillSize(C, availableWidth);
        assignFillSize(R, availableHeight);

        // Calculate cr offsets for effeciency
        calculateOffset(C, inset);
        calculateOffset(R, inset);

        // Indicate that the size of the cells are known for the container's
        // current size
        dirty = false;
        oldWidth = d.width;
        oldHeight = d.height;
    }

    /**
     * Calculates the vertical/horizontal offset and size of a component.
     *
     * @param entry entry containing component and contraints
     * @param preferredSize previously calculated preferred width/height of component
     * @param isColumn if true, this method is being called to calculate the offset/size of a
     *        column.  if false,... of a row.
     *
     * @return an array, a, of two integers such that a[0] is the offset and a[1] is the size
     */
    protected int[] calculateSizeAndOffset(Entry entry, int preferredSize,
        boolean isColumn) {
        // Get references to cr properties
        int[] crOffset = isColumn ? this.crOffset[C] : this.crOffset[R];
        int entryAlignment = isColumn ? entry.alignment[C] : entry.alignment[R];

        // Determine cell set size
        int cellSetSize = isColumn
            ? (crOffset[entry.cr2[C] + 1] - crOffset[entry.cr1[C]])
            : (crOffset[entry.cr2[R] + 1] - crOffset[entry.cr1[R]]);

        // Determine the size of the component
        int size;

        if ((entryAlignment == FULL) || (cellSetSize < preferredSize)) {
            size = cellSetSize;
        }
        else {
            size = preferredSize;
        }

        // Since the component orientation is adjusted for in the layoutContainer
        // method, we can treat leading justification as left justification and
        // trailing justification as right justification.
        if (isColumn && (entryAlignment == LEADING)) {
            entryAlignment = LEFT;
        }

        if (isColumn && (entryAlignment == TRAILING)) {
            entryAlignment = RIGHT;
        }

        // Determine offset
        int offset;

        switch (entryAlignment) {
        case LEFT: // Align left/top side along left edge of cell
            offset = crOffset[isColumn ? entry.cr1[C] : entry.cr1[R]];

            break;

        case RIGHT: // Align right/bottom side along right edge of cell
            offset = crOffset[(isColumn ? entry.cr2[C] : entry.cr2[R]) + 1]
                - size;

            break;

        case CENTER: // Center justify component
            offset = crOffset[isColumn ? entry.cr1[C] : entry.cr1[R]]
                + ((cellSetSize - size) >> 1);

            break;

        case FULL: // Align left/top side along left/top edge of cell
            offset = crOffset[isColumn ? entry.cr1[C] : entry.cr1[R]];

            break;

        default: // This is a never should happen case, but just in case
            offset = 0;
        }

        // Compensate for gaps
        if (isColumn) {
            offset += (hGap * entry.cr1[C]);
            size += (hGap * (entry.cr2[C] - entry.cr1[C]));
        }
        else {
            offset += (vGap * entry.cr1[R]);
            size += (vGap * (entry.cr2[R] - entry.cr1[R]));
        }

        // Package return values
        int[] value = { offset, size };

        return value;
    }

    /**
     * Deletes a cr for the methods deleteRow or deleteColumn.
     *
     * @param z indicates row or column
     * @param i zero-based index of cr to delete
     *
     * @throws IllegalArgumentException -
     */
    protected void deleteCr(int z, int i) {
        // Make sure position is valid
        if ((i < 0) || (i >= crSpec[z].length)) {
            throw new IllegalArgumentException("Parameter i is invalid.  i = "
                + i + ".  Valid range is [0, " //$NON-NLS-1$ 
                + (crSpec[z].length - 1) + "]."); //$NON-NLS-1$
        }

        // Copy rows
        double[] cr = new double[crSpec[z].length - 1];

        System.arraycopy(crSpec[z], 0, cr, 0, i);
        System.arraycopy(crSpec[z], i + 1, cr, i, crSpec[z].length - i - 1);

        // Delete row
        crSpec[z] = cr;

        // Move all components that are to below the row deleted
        ListIterator<Entry> iterator = list.listIterator(0);

        while (iterator.hasNext()) {
            // Get next entry
            Entry entry = iterator.next();

            // Is the first row below the new row
            if (entry.cr1[z] > i) {
                // Move first row
                entry.cr1[z]--;
            }

            // Is the second row below the new row
            if (entry.cr2[z] > i) {
                // Move second row
                entry.cr2[z]--;
            }
        }

        // Indicate that the cell sizes are not known
        dirty = true;
    }

    /**
     * Initializes the TableLayout for all constructors.
     *
     * @param col widths of columns in the format, {{col0, col1, col2, ..., colN}
     * @param row heights of rows in the format, {{row0, row1, row2, ..., rowN}
     *
     * @throws IllegalArgumentException -
     */
    protected void init(double[] col, double[] row) {
        // Check parameters
        if (col == null) {
            throw new IllegalArgumentException("Parameter col cannot be null"); //$NON-NLS-1$
        }

        if (row == null) {
            throw new IllegalArgumentException("Parameter row cannot be null"); //$NON-NLS-1$
        }

        // Create new rows and columns
        crSpec[C] = new double[col.length];
        crSpec[R] = new double[row.length];

        // Copy rows and columns
        System.arraycopy(col, 0, crSpec[C], 0, crSpec[C].length);
        System.arraycopy(row, 0, crSpec[R], 0, crSpec[R].length);

        // Make sure rows and columns are valid
        for (int counter = 0; counter < crSpec[C].length; counter++) {
            if ((crSpec[C][counter] < 0.0) && (crSpec[C][counter] != FILL)
                && (crSpec[C][counter] != PREFERRED)
                && (crSpec[C][counter] != MINIMUM)) {
                crSpec[C][counter] = 0.0;
            }
        }

        for (int counter = 0; counter < crSpec[R].length; counter++) {
            if ((crSpec[R][counter] < 0.0) && (crSpec[R][counter] != FILL)
                && (crSpec[R][counter] != PREFERRED)
                && (crSpec[R][counter] != MINIMUM)) {
                crSpec[R][counter] = 0.0;
            }
        }

        // Create an empty list of components
        list = new LinkedList<Entry>();

        // Indicate that the cell sizes are not known
        dirty = true;
    }

    //******************************************************************************
    //*** Inner Class                                                            ***
    //******************************************************************************
    // The following inner class is used to bind components to their constraints
    public static class Entry implements Cloneable {
        /** Component bound by the constraints */
        public Component component;

        /** Horizontal and vertical alignment */
        public int[] alignment;

        /** Cell in which the upper-left corner of the component lies */
        public int[] cr1;

        /** Cell in which the lower-right corner of the component lies */
        public int[] cr2;

        /**
         * Constructs an Entry that binds a component to a set of constraints.
         *
         * @param component component being bound
         * @param constraint constraints being applied
         */
        public Entry(Component component, TableLayoutConstraints constraint) {
            int[] cr1 = { constraint.col1, constraint.row1 };
            int[] cr2 = { constraint.col2, constraint.row2 };
            int[] alignment = { constraint.hAlign, constraint.vAlign };

            this.cr1 = cr1;
            this.cr2 = cr2;
            this.alignment = alignment;
            this.component = component;
        }

        /**
         * Copies this Entry.
         *
         * @return -
         *
         * @throws CloneNotSupportedException -
         */
        public Object copy() throws CloneNotSupportedException {
            return clone();
        }

        /**
         * Gets the string representation of this Entry.
         *
         * @return a string in the form "(col1, row1, col2, row2, vAlign, hAlign) component"
         */
        @Override
		public String toString() {
            TableLayoutConstraints c = new TableLayoutConstraints(cr1[C],
                    cr1[R], cr2[C], cr2[R], alignment[C], alignment[R]);

            return "(" + c + ") " + component; //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
