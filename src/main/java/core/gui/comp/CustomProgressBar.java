package core.gui.comp;

import java.awt.*;
import javax.swing.JPanel;

public class CustomProgressBar extends JPanel{
    private static final long serialVersionUID = 1L;
    private Color color;
    private double minimum = 0.0;
    private double maximum = 100.0;
    private double value = 100.0;
    private int width;
    private int height;

    public CustomProgressBar(Color color, int iWidth, int iHeight) {
//        super();
        this.color = color;
        width = iWidth;
        height = iHeight;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //border
        g.setColor(Color.BLACK); //TODO: set color from constructor
        g.drawRect(0, 0, width-1, height-1);

        //progress
        int drawAmount = (int) (((value - minimum) / (maximum - minimum)) * width);
        g.setColor(color);
        g.fillRect(1, 1, drawAmount-2, height-2); //-2 to account for border

        //string painting
//        String stringToPaint = (int)value + "/" + (int)maximum;
//        Canvas c = new Canvas();
//        FontMetrics fm = c.getFontMetrics(font);
//        final int stringWidth = fm.stringWidth(stringToPaint);
//        final int stringHeight = fm.getHeight();
//        g.setColor(Color.YELLOW);
//        g.drawString(stringToPaint, (getWidth()/2) - (10/2), ((getHeight()/2) + (12/2))-2); //-2 to account for border
    }

    public void setColor(Color _color){
        this.color = _color;
    }

    public void setMinimum(double _minimum){
        this.minimum = _minimum;
    }

    public void setMaximum(double _maximum){
        this.maximum = _maximum;
    }

    public void setValue(double _value){
        this.value = _value;
    }

    public double getValue(){
        return this.value;
    }
}
