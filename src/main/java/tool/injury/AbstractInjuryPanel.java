package tool.injury;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Abstract Injury Calculator Panel Component
 *
 * @author draghetto
 */
public abstract class AbstractInjuryPanel extends JPanel {
	
	private static final long serialVersionUID = 4820048885216403402L;
	
    //~ Instance fields ----------------------------------------------------------------------------

	private DecimalFormat df = new DecimalFormat("00.00");
    private InjuryDialog parent;
    private JLabel header = new JLabel();
    private JLabel inputMsg = new JLabel();
    private JLabel outputMsg = new JLabel();
    private JTextField input = new JTextField(8);

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new AbstractInjuryPanel object.
     *
     * @param dialog the main injury dialog
     */
    public AbstractInjuryPanel(InjuryDialog dialog) {
        this.parent = dialog;
        init();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Action to be executed when the button is pressed, must be implemented
     */
    public abstract void doAction();

    /**
     * Set the Header message
     *
     * @param label message
     */
    public final void setHeader(String label) {
        header.setText(label);
    }

    /**
     * Returns the numeric value into the TextArea
     *
     * @return the int value
     */
    public final int getInput() {
        try {
            return Integer.parseInt(input.getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Set the Input Field message
     *
     * @param label message
     */
    public final void setInputMsg(String label) {
        inputMsg.setText(label);
    }

    /**
     * Set input value
     *
     * @param value the new value
     */
    public final void setInputValue(String value) {
        input.setText(value);
    }

    /**
     * Set the Output Field message
     *
     * @param label message
     */
    public final void setOutputMsg(String label) {
        outputMsg.setText(label);
    }

    /**
     * Returns the Detail Panel for use of calculator
     *
     * @return the Detail Panel
     */
    protected final InjuryDetailPanel getDetail() {
        return parent.getDetail();
    }

    /**
     * Format a Number for Rendering
     *
     * @param number number to format
     *
     * @return the string representation
     */
    protected final String formatNumber(double number) {
        return df.format(number);
    }

    /**
     * Initialize the GUI components
     */
    private void init() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder(1));
        header.setOpaque(false);
        add(header, BorderLayout.NORTH);

        final JPanel pan = new ImagePanel();
        pan.setLayout(new GridLayout(1, 4));

        final JButton button = new JButton(HOVerwaltung.instance().getLanguageString("Calculate"));
        pan.add(inputMsg);
        pan.add(input);
        pan.add(outputMsg);
        pan.add(button);
        add(pan, BorderLayout.SOUTH);

        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    doAction();
                }
            });
    }
}
