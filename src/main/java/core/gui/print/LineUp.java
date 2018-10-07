package core.gui.print;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;


public final class LineUp extends PrintObject {
	
	private static final long serialVersionUID = -111352367289407651L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private LineUpRow defence;
    private LineUpRow forward;
    private LineUpRow goalkeeper;
    private LineUpRow midfield;
    private LineUpRow reserve1;
    private LineUpRow reserve2;

    //~ Constructors -------------------------------------------------------------------------------

    //	private LineUpRow values;

    /**
     * Creates a new LineUp object.
     */
    public LineUp(PageFormat pf) {
        super(pf);
        initialize();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Position: 1     2    3    4    5     6    7    8    9 10    11 17  12  18     13    14    15
     * 16     19
     *
     * @param position position
     * @param title title
     * @param content content
     */
    public void setPosition(int position, String title, String[] content) {
        switch (position) {
            case 1:
                goalkeeper.getBox(position).setTitle(title);
                goalkeeper.getBox(position).setRows(content);
                break;

            case 2:
            case 3:
            case 4:
            case 5:
                defence.getBox(position - 1).setTitle(title);
                defence.getBox(position - 1).setRows(content);
                break;

            case 6:
            case 7:
            case 8:
            case 9:
                midfield.getBox(position - 5).setTitle(title);
                midfield.getBox(position - 5).setRows(content);
                break;

            case 10:
            case 11:
                forward.getBox(position - 9).setTitle(title);
                forward.getBox(position - 9).setRows(content);
                break;

            case 12:
                reserve1.getBox(2).setTitle(title);
                reserve1.getBox(2).setRows(content);
                break;

            case 13:
            case 14:
            case 15:
            case 16:
                reserve2.getBox(position - 12).setTitle(title);
                reserve2.getBox(position - 12).setRows(content);
                break;

            case 17:
                reserve1.getBox(1).setTitle(title);
                reserve1.getBox(1).setRows(content);
                break;

            case 18:
                reserve1.getBox(3).setTitle(title);
                reserve1.getBox(3).setRows(content);
                break;

            case 19:

                //				values.getBox(1).setTitle(title);
                //				values.getBox(1).setRows(content);
                //				values.setWidth((int)dw);
                break;
        }
    }

    @Override
	protected void paintMe(Graphics2D g2) {
        int currentY = 0;
        final int margin = 20;

        final Font font = new Font("monospaced", Font.PLAIN, 8);
        g2.setFont(font);

        goalkeeper.draw(currentY, (int) dw, g2);
        defence.draw(currentY = currentY + goalkeeper.getTotalHeight() + margin, (int) dw, g2);
        midfield.draw(currentY = currentY + defence.getTotalHeight() + margin, (int) dw, g2);
        forward.draw(currentY = currentY + midfield.getTotalHeight() + margin, (int) dw, g2);
        reserve1.draw(currentY = currentY + forward.getTotalHeight() + margin + 10, (int) dw, g2);
        reserve2.draw(currentY = currentY + reserve1.getTotalHeight() + margin, (int) dw, g2);
    }

    private void initialize() {
        goalkeeper = new LineUpRow(1);
        defence = new LineUpRow(4);
        midfield = new LineUpRow(4);
        forward = new LineUpRow(2);
        reserve1 = new LineUpRow(3);
        reserve2 = new LineUpRow(4);

        //		values = new LineUpRow(1);
    }
}
