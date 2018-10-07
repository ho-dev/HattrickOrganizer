// %3889649867:de.hattrickorganizer.gui.menu.option%
package core.option;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * Panel zum editieren der Farben
 */
final class FarbPanel extends ImagePanel implements ActionListener {
    //~ Instance fields ----------------------------------------------------------------------------

	private static final long serialVersionUID = 1L;
	private JButton bruisedButton = new JButton();
    private JButton redCardButton = new JButton();
    private JButton transferButton= new JButton();
    private JButton injuredButton = new JButton();
    private JButton twoCardsButton = new JButton();
    private JComboBox themeComboBox;

    private UserParameter temp = core.model.UserParameter.temp();
    /**
     * Creates a new FarbPanel object.
     */
    protected FarbPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    //---------------Listener-------------------------------------------
    @Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
    	HOVerwaltung hoVerwaltung = HOVerwaltung.instance();
        if (actionEvent.getSource()==bruisedButton) {
            Color color = JColorChooser.showDialog(this,
            		hoVerwaltung.getLanguageString("ls.player.injurystatus.bruised"), temp.FG_ANGESCHLAGEN);

            if (color != null) {
                temp.FG_ANGESCHLAGEN = color;
                OptionManager.instance().setReInitNeeded();
                refresh();
            }
        } else if (actionEvent.getSource()==injuredButton) {
            Color color = JColorChooser.showDialog(this,
            		hoVerwaltung.getLanguageString("ls.player.injurystatus.injured"),temp.FG_VERLETZT);

            if (color != null) {
            	temp.FG_VERLETZT = color;
                OptionManager.instance().setReInitNeeded();
                refresh();
            }
        } else if (actionEvent.getSource()==twoCardsButton) {
            Color color = JColorChooser.showDialog(this,
            		hoVerwaltung.getLanguageString("ls.player.warningstatus.twobookings"),temp.FG_ZWEIKARTEN);

            if (color != null) {
            	temp.FG_ZWEIKARTEN = color;
                OptionManager.instance().setReInitNeeded();
                refresh();
            }
        } else if (actionEvent.getSource()==redCardButton) {
            Color color = JColorChooser.showDialog(this,
            		hoVerwaltung.getLanguageString("ls.player.warningstatus.suspended"),temp.FG_GESPERRT);

            if (color != null) {
            	temp.FG_GESPERRT = color;
                OptionManager.instance().setReInitNeeded();
                refresh();
            }
        } else if (actionEvent.getSource()==transferButton) {
            Color color = JColorChooser.showDialog(this,
            		hoVerwaltung.getLanguageString("Transfermarkt"),temp.FG_TRANSFERMARKT);

            if (color != null) {
            	temp.FG_TRANSFERMARKT = color;
                OptionManager.instance().setReInitNeeded();
                refresh();
            }
        } else if( actionEvent.getSource() == themeComboBox) {
        	temp.theme = themeComboBox.getSelectedItem().toString();
        	OptionManager.instance().setRestartNeeded();
        }
    }

    //---------------Hilfsmethoden--------------------------------------
    public final void refresh() {
        bruisedButton.setBackground(temp.FG_ANGESCHLAGEN);
        injuredButton.setBackground(temp.FG_VERLETZT);
        twoCardsButton.setBackground(temp.FG_ZWEIKARTEN);
        redCardButton.setBackground(temp.FG_GESPERRT);
        transferButton.setBackground(temp.FG_TRANSFERMARKT);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(150, 4, 150, 4);

        setLayout(layout);

        final JPanel panel = new ImagePanel();
        panel.setLayout(new GridLayout(6, 2, 4, 10));
        panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));


        JLabel label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("Farben"));
        panel.add(label);

        themeComboBox = new JComboBox(ThemeManager.instance().getAvailableThemeNames());
        themeComboBox.setSelectedItem(core.model.UserParameter.temp().theme);
        themeComboBox.addActionListener(this);
        panel.add(themeComboBox);

        label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("ls.player.injurystatus.bruised"));
        addRowPanel(panel,label, bruisedButton, temp.FG_ANGESCHLAGEN);

        label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("ls.player.injurystatus.injured"));
        addRowPanel(panel,label, injuredButton, temp.FG_VERLETZT);

        label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("ls.player.warningstatus.twobookings"));
        addRowPanel(panel,label, twoCardsButton, temp.FG_ZWEIKARTEN);

        label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("ls.player.warningstatus.suspended"));
        addRowPanel(panel,label, redCardButton, temp.FG_GESPERRT);

        label = new JLabel("  " + HOVerwaltung.instance().getLanguageString("Transfermarkt"));
        addRowPanel(panel,label, transferButton, temp.FG_TRANSFERMARKT);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        layout.setConstraints(panel, constraints);
        add(panel);

        refresh();
    }

    private void addRowPanel(JPanel panel,JLabel label, JButton button,Color color){
    	panel.add(label);
    	button.setBackground(color);
        button.addActionListener(this);
        panel.add(button);
    }
}
