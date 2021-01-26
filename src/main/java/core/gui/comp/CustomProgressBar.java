package core.gui.comp;

import java.awt.*;
import javax.swing.JPanel;

public class CustomProgressBar extends JPanel{
    private Color m_colorFill, m_colorBorder, m_colorBG;
    private double m_minimum = 0.0;
    private double m_maximum = 100.0;
    private double m_value = 100.0;
    private int m_width;
    private int m_height;

    public CustomProgressBar(Color colorBG, Color colorFill, Color colorBorder, int iWidth, int iHeight) {
        m_colorFill = colorFill;
        m_colorBorder = colorBorder;
        m_colorBG = colorBG;
        m_width = iWidth;
        m_height = iHeight;
        setPreferredSize(new Dimension(m_width, m_height));
        setBackground(colorBG);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //border
        g.setColor(m_colorBorder);
        g.drawRect(0, 0, m_width-1, m_height-1);

        //fill progress
        if (m_value != 0) {
            int drawAmount = (int) (((m_value - m_minimum) / (m_maximum - m_minimum)) * m_width);
            g.setColor(m_colorFill);
            g.fillRect(1, 1, drawAmount - 2, m_height - 2); //-2 to account for border
        }

        //string painting
//        String stringToPaint = (int)value + "/" + (int)maximum;
//        Canvas c = new Canvas();
//        FontMetrics fm = c.getFontMetrics(font);
//        final int stringWidth = fm.stringWidth(stringToPaint);
//        final int stringHeight = fm.getHeight();
//        g.setColor(Color.YELLOW);
//        g.drawString(stringToPaint, (getWidth()/2) - (10/2), ((getHeight()/2) + (12/2))-2); //-2 to account for border
    }

//    public void setColorFill(Color _color){
//        this.colorFill = _color;
//    }
//
//    public void setMinimum(double _minimum){
//        this.minimum = _minimum;
//    }
//
//    public void setMaximum(double _maximum){
//        this.maximum = _maximum;
//    }

    public void setValue(double _value){
        m_value = _value;
    }

    public double getValue(){
        return m_value;
    }
}
