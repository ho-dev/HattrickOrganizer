package core.gui.comp;

import core.gui.theme.ImageUtilities;
import core.model.UserParameter;

import java.awt.*;
import javax.swing.JPanel;

public class CustomProgressBar extends JPanel{
    private final Color m_colorFill;
    private final Color m_colorBorder;
    private final Color m_colorBG;
    private double m_value = 100.0;
    private final int m_width;
    private final int m_height;
    private String m_leftText = "";
    private String m_rightText = "";
    private final Font m_f;

    public CustomProgressBar(Color colorBG, Color colorFill, Color colorBorder, int iWidth, int iHeight, Font f) {
        m_colorFill = colorFill;
        m_colorBorder = colorBorder;
        m_colorBG = colorBG;
        m_width = iWidth;
        m_height = iHeight;
        m_f = f.deriveFont(Font.BOLD, UserParameter.instance().fontSize + 4);
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
            double m_minimum = 0.0;
            double m_maximum = 100.0;
            final int drawAmount = (int) (((m_value - m_minimum) / (m_maximum - m_minimum)) * m_width);
            final int leftBlockWidth = drawAmount - 2;
            final int rightBlockWidth = m_width - 2 - leftBlockWidth;

            g.setColor(m_colorFill);
            g.fillRect(1, 1, leftBlockWidth, m_height - 2); //-2 to account for border
            g.setFont(m_f);
            Canvas c = new Canvas();
            FontMetrics fm = c.getFontMetrics(m_f);
            var textHeight = m_f.getSize();
            final int y = (m_height+textHeight)/2;

            if (!m_leftText.isEmpty()) {
                final int leftTextWidth = fm.stringWidth(m_leftText);
                g.setColor(ImageUtilities.getColorForContrast(m_colorFill));
                g.drawString(m_leftText, ((leftBlockWidth-leftTextWidth) / 2), y);
            }

            if (!m_rightText.isEmpty()) {
                final int rightTextWidth = fm.stringWidth(m_rightText);
                g.setColor(ImageUtilities.getColorForContrast(m_colorBG));
                g.drawString(m_rightText, (leftBlockWidth + (rightBlockWidth-rightTextWidth) / 2), y);
            }
        }
    }


    public void setValue(int val1, int val2, double min_width){

        m_value = (double)val1 / (double)(val1 + val2);

        int leftValue = (int) Math.round(m_value*100);
        int rightValue = 100 - leftValue;

        m_leftText = leftValue + "%";
        m_rightText = rightValue + "%";

        if (m_value < min_width) {
            m_value = min_width;
        }
        else if (m_value > 1 - min_width) {
            m_value = 1 - min_width;
        }

        m_value = m_value*100;

    }

    public void resetValue(){
        m_value = 0d;
        m_leftText = "";
        m_rightText =  "";
    }


    public double getValue(){
        return m_value;
    }


}
