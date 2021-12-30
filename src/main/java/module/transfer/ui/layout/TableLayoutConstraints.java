// %1116523449328:hoplugins.commons.ui.info.clearthought.layout%
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

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

/**
 * TableLayoutConstraints binds components to their constraints.
 *
 * @author Daniel E. Barbalace
 * @version 1.2 3/15/04
 */
public class TableLayoutConstraints implements TableLayoutConstants {
    /** Cell in which the upper left corner of the component lays */
    public int col1;

    /** Cell in which the lower right corner of the component lays */
    public int col2;

    /** Horizontal justification if component occupies just one cell */
    public int hAlign;

    /** Cell in which the upper left corner of the component lays */
    public int row1;

    /** Cell in which the lower right corner of the component lays */
    public int row2;

    /** Verical justification if component occupies just one cell */
    public int vAlign;

    /**
     * Constructs an TableLayoutConstraints with the default settings.  This constructor is
     * equivalent to TableLayoutConstraints(0, 0, 0, 0, FULL, FULL).
     */
    public TableLayoutConstraints() {
        col1 = row1 = col2 = row2 = 0;
        hAlign = vAlign = FULL;
    }

    /**
     * Constructs an TableLayoutConstraints from a string.
     *
     * @param constraints indicates TableLayoutConstraints's position and justification as a string
     *        in the form "row, column" or "row, column, horizontal justification, vertical
     *        justification" or "row 1, column 1, row 2, column 2". It is also acceptable to
     *        delimit the paramters with spaces instead of commas.
     *
     * @throws IllegalArgumentException -
     */
    public TableLayoutConstraints(String constraints) {
        // Use default values for any parameter not specified or specified
        // incorrectly.  The default parameters place the component in a single
        // cell at column 0, row 0.  The component is fully justified.
        col1 = 0;
        row1 = 0;
        col2 = 0;
        row2 = 0;
        hAlign = FULL;
        vAlign = FULL;

        // Parse constraints using spaces or commas
        StringTokenizer st = new StringTokenizer(constraints, ", "); //$NON-NLS-1$
        int numToken = st.countTokens();

        try {
            // Check constraints
            if ((numToken != 2) && (numToken != 4) && (numToken != 6)) {
                throw new RuntimeException();
            }

            // Get the first column (assume component is in only one column)
            String tokenA = st.nextToken();

            col1 = parseInt(tokenA);
            col2 = col1;

            // Get the first row (assume component is in only one row)
            String tokenB = st.nextToken();

            row1 = parseInt(tokenB);
            row2 = row1;

            // Get next two tokens
            tokenA = st.nextToken();
            tokenB = st.nextToken();

            try {
                // Attempt to use tokens A and B as col2 and row2
                col2 = parseInt(tokenA);
                row2 = parseInt(tokenB);

                // Get next two tokens
                tokenA = st.nextToken();
                tokenB = st.nextToken();
            }
            catch (NumberFormatException error) {
                col2 = col1;
                row2 = row1;
            }

            // Check if token means horizontally justification the component
            if ((tokenA.equalsIgnoreCase("L"))
                || (tokenA.equalsIgnoreCase("LEFT"))) { //$NON-NLS-1$ 
                hAlign = LEFT;
            }
            else if ((tokenA.equalsIgnoreCase("C")) //$NON-NLS-1$
                || (tokenA.equalsIgnoreCase("CENTER"))) { //$NON-NLS-1$
                hAlign = CENTER;
            }
            else if ((tokenA.equalsIgnoreCase("F")) //$NON-NLS-1$
                || (tokenA.equalsIgnoreCase("FULL"))) { //$NON-NLS-1$
                hAlign = FULL;
            }
            else if ((tokenA.equalsIgnoreCase("R")) //$NON-NLS-1$
                || (tokenA.equalsIgnoreCase("RIGHT"))) { //$NON-NLS-1$
                hAlign = RIGHT;
            }
            else if ((tokenA.equalsIgnoreCase("LD")) //$NON-NLS-1$
                || (tokenA.equalsIgnoreCase("LEADING"))) { //$NON-NLS-1$
                hAlign = LEADING;
            }
            else if ((tokenA.equalsIgnoreCase("TL")) //$NON-NLS-1$
                || (tokenA.equalsIgnoreCase("TRAILING"))) { //$NON-NLS-1$
                hAlign = TRAILING;
            }
            else {
                throw new RuntimeException();
            }

            // Check if token means horizontally justification the component
            if ((tokenB.equalsIgnoreCase("T"))
                || (tokenB.equalsIgnoreCase("TOP"))) { //$NON-NLS-1$ 
                vAlign = TOP;
            }
            else if ((tokenB.equalsIgnoreCase("C")) //$NON-NLS-1$
                || (tokenB.equalsIgnoreCase("CENTER"))) { //$NON-NLS-1$
                vAlign = CENTER;
            }
            else if ((tokenB.equalsIgnoreCase("F")) //$NON-NLS-1$
                || (tokenB.equalsIgnoreCase("FULL"))) { //$NON-NLS-1$
                vAlign = FULL;
            }
            else if ((tokenB.equalsIgnoreCase("B")) //$NON-NLS-1$
                || (tokenB.equalsIgnoreCase("BOTTOM"))) { //$NON-NLS-1$
                vAlign = BOTTOM;
            }
            else {
                throw new RuntimeException();
            }
        }
        catch (NoSuchElementException error) {
        }
        catch (RuntimeException error) {
            throw new IllegalArgumentException(
                "Expected constraints in one of the following formats:\n" //$NON-NLS-1$
                + "  col1, row1\n  col1, row1, col2, row2\n" //$NON-NLS-1$
                + "  col1, row1, hAlign, vAlign\n" //$NON-NLS-1$
                + "  col1, row1, col2, row2, hAlign, vAlign\n" //$NON-NLS-1$
                + "Constraints provided '" + constraints + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        // Make sure row2 >= row1
        if (row2 < row1) {
            row2 = row1;
        }

        // Make sure col2 >= col1
        if (col2 < col1) {
            col2 = col1;
        }
    }

    /**
     * Constructs an TableLayoutConstraints a set of constraints.
     *
     * @param col1 column where upper-left cornor of the component is placed
     * @param row1 row where upper-left cornor of the component is placed
     * @param col2 column where lower-right cornor of the component is placed
     * @param row2 row where lower-right cornor of the component is placed
     * @param hAlign horizontal justification of a component in a single cell
     * @param vAlign vertical justification of a component in a single cell
     */
    public TableLayoutConstraints(int col1, int row1, int col2, int row2,
        int hAlign, int vAlign) {
        this.col1 = col1;
        this.row1 = row1;
        this.col2 = col2;
        this.row2 = row2;

        if ((hAlign == LEFT) || (hAlign == RIGHT) || (hAlign == CENTER)
            || (hAlign == FULL) || (hAlign == TRAILING)) {
            this.hAlign = hAlign;
        }
        else {
            this.hAlign = FULL;
        }

        if ((vAlign == LEFT) || (vAlign == RIGHT) || (vAlign == CENTER)) {
            this.vAlign = vAlign;
        }
        else {
            this.vAlign = FULL;
        }
    }

    /**
     * Gets a string representation of this TableLayoutConstraints.
     *
     * @return a string in the form "row 1, column 1, row 2, column 2, horizontal justification,
     *         vertical justification"
     */
    @Override
	public String toString() {
        StringBuffer buffer = new StringBuffer();

        buffer.append(col1);
        buffer.append(", "); //$NON-NLS-1$
        buffer.append(row1);
        buffer.append(", "); //$NON-NLS-1$

        buffer.append(col2);
        buffer.append(", "); //$NON-NLS-1$
        buffer.append(row2);
        buffer.append(", "); //$NON-NLS-1$

        final String[] h = {
                "left", "center", "full", "right", "leading", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                "trailing"
            }; 
        final String[] v = { "top", "center", "full", "bottom" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

        buffer.append(h[hAlign]);
        buffer.append(", "); //$NON-NLS-1$
        buffer.append(v[vAlign]);

        return buffer.toString();
    }
}
