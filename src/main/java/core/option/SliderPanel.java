package core.option;

import core.gui.comp.panel.ImagePanel;
import core.util.HOLogger;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;


/**
 * Panel with slider and formattedJTextField
 */
public final class SliderPanel extends ImagePanel implements ChangeListener {

    private JSlider m_jslSlider;
    private JFormattedTextField m_jtfTextfield;
    private final float m_fFaktor;
    private float m_fTextfeldFaktor = 1;
    private final int m_iTextbreite;
    private final int decimals = 0;
    private boolean bDeactivateTxtLister = false;


    /**
     * @param text Text des Labels
     * @param max Maximaler Wert
     * @param min Minimaler Wert
     * @param faktor Faktor, mit dem Werte eingangs multipliziert und durch die sie ausgangs wieder
     *        dividiert werden
     * @param textfeldfaktor Faktor für die Anzeige des Sliderwerts in Textfeld
     * @param textbreite Width intended for the label
     */
    public SliderPanel(String text, int max, int min, float faktor, float textfeldfaktor,
                       int textbreite) {
        m_fFaktor = faktor;
        m_fTextfeldFaktor = textfeldfaktor;
        m_iTextbreite = textbreite;
        initComponents(text, max, min);
    }

    /**
     * Wert mit faktor multiplizieren und auf int casten
     */
    public void setValue(float value) {
        m_jslSlider.setValue((int) (value * m_fFaktor));
    }

    public final float getValue() {
        HOLogger.instance().log(getClass(),(float) m_jslSlider.getValue() + " : "
                           + (m_jslSlider.getValue() / m_fFaktor));
        return m_jslSlider.getValue() / m_fFaktor;
    }

    public void addChangeListener(ChangeListener listener) {
        m_jslSlider.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        m_jslSlider.removeChangeListener(listener);
    }

    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        bDeactivateTxtLister = true;
        if (decimals != 0){
            m_jtfTextfield.setText(core.util.Helper.round(m_jslSlider.getValue() * m_fTextfeldFaktor, decimals) + "");}
        else {
            m_jtfTextfield.setText((int)(m_jslSlider.getValue() * m_fTextfeldFaktor) + "");
        }
        bDeactivateTxtLister = false;
    }

    private void initComponents(String text, int max, int min) {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        setLayout(layout);

        JLabel m_jlLabel = new JLabel(text, SwingConstants.LEFT);
        m_jlLabel.setMaximumSize(new Dimension(m_iTextbreite, 35));
        m_jlLabel.setPreferredSize(m_jlLabel.getMaximumSize());
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jlLabel, constraints);
        add(m_jlLabel);

        m_jslSlider = new JSlider(min, max);
        m_jslSlider.setMaximumSize(new Dimension(150, 35));
        m_jslSlider.setPreferredSize(m_jslSlider.getMaximumSize());
        m_jslSlider.setOpaque(false);
        m_jslSlider.setValue(0);
        m_jslSlider.addChangeListener(this);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1.0;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jslSlider, constraints);
        add(m_jslSlider);

        NumberFormat format = NumberFormat.getInstance();
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(100);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);

        m_jtfTextfield = new JFormattedTextField(formatter);
        m_jtfTextfield.setColumns(4);

        if (decimals != 0) {
            m_jtfTextfield.setText(core.util.Helper.round(m_jslSlider.getValue() * m_fTextfeldFaktor, decimals) + "");
        }
        else
        {
            m_jtfTextfield.setText((int)(m_jslSlider.getValue() * m_fTextfeldFaktor) + "");
        }

        m_jtfTextfield.setEditable(true);
        m_jtfTextfield.setHorizontalAlignment(SwingConstants.RIGHT);


        m_jtfTextfield.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent arg0) {
                if (!bDeactivateTxtLister) {
                    if(m_jtfTextfield.getText().equals("")){
                        updateSliderWithoutEvent(0);
                    }
                    else {
                        updateSliderWithoutEvent(Integer.parseInt(m_jtfTextfield.getText()));
                    }
                }
            }

            @Override
            public void insertUpdate(DocumentEvent arg0) {
                if (!bDeactivateTxtLister) {
                    if(m_jtfTextfield.getText().equals("")){
                        updateSliderWithoutEvent(0);
                    }
                    else {
                        updateSliderWithoutEvent(Integer.parseInt(m_jtfTextfield.getText()));
                    }
                }
            }

            @Override
            public void changedUpdate(DocumentEvent arg0) {
               if (!bDeactivateTxtLister) {
                   updateSliderWithoutEvent(Integer.parseInt(m_jtfTextfield.getText()));
               }
            }
        });

        constraints.anchor = GridBagConstraints.EAST;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.gridx = 2;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jtfTextfield, constraints);
        add(m_jtfTextfield);
    }

    private void updateSliderWithoutEvent(int value){
        m_jslSlider.removeChangeListener(this);
        m_jslSlider.setValue(value);
        m_jslSlider.addChangeListener(this);
    }


}
