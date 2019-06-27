// %3802060737:de.hattrickorganizer.gui.menu.option%
package core.option;

import core.constants.player.PlayerSkill;
import core.datatype.CBItem;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.FactorObject;
import core.model.FormulaFactors;
import core.model.HOVerwaltung;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeListener;




/**
 * Panel zum editieren der Formelwerte der Spielerstärkenberechnung
 */
final class FormelPanel extends ImagePanel implements ActionListener, ItemListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	//~ Instance fields ----------------------------------------------------------------------------

    private JButton m_jbExport;
    private JButton m_jbImport;
    private JButton m_jbResetToDefaults;
    private JComboBox m_jcbPosition;
    private SliderPanel m_jpFluegelspiel;
    private SliderPanel m_jpPasspiel;
    private SliderPanel m_jpSpielaufbau;
    private SliderPanel m_jpStandard;
    private SliderPanel m_jpTorschuss;
    private SliderPanel m_jpTorwart;
    private SliderPanel m_jpVerteidigung;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new FormelPanel object.
     */
    protected FormelPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    //---------------Listener-------------------------------------------
    @Override
	public final void actionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(m_jbResetToDefaults)) {
            final int value = JOptionPane.showConfirmDialog(this,
                                                            HOVerwaltung.instance().getLanguageString("FrageFormelwertReset"),
                                                            HOVerwaltung.instance().getLanguageString("confirmation.title"),
                                                            JOptionPane.YES_NO_OPTION);

            if (value == JOptionPane.YES_OPTION) {
                //Alle Werte reseten
                FormulaFactors.instance().importDefaults();

                //Alle anderen Werte in GUI setzen
                refresh();

                OptionManager.instance().setReInitNeeded();
            }
        } else if (actionEvent.getSource().equals(m_jbImport)) {
            //Filechooser
            final javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle(HOVerwaltung.instance().getLanguageString("FormelwertImport"));

            final java.io.File pfad = new java.io.File(core.model.UserParameter.temp().hrfImport_HRFPath);

            if (pfad.exists() && pfad.isDirectory()) {
                fileChooser.setCurrentDirectory(new java.io.File(core.model.UserParameter.temp().hrfImport_HRFPath));
            }

            final core.file.ExampleFileFilter filter = new core.file.ExampleFileFilter();
            filter.addExtension("xml");
            filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.xml"));
            fileChooser.setFileFilter(filter);

            final int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                FormulaFactors.instance().readFromXML(fileChooser.getSelectedFile().getAbsolutePath());
                refresh();
                OptionManager.instance().setReInitNeeded();
            }
        } else if (actionEvent.getSource().equals(m_jbExport)) {
            //Filechooser
            final javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
            fileChooser.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle(HOVerwaltung.instance().getLanguageString("FormelwertExport"));

            final java.io.File pfad = new java.io.File(core.model.UserParameter.temp().hrfImport_HRFPath);

            if (pfad.exists() && pfad.isDirectory()) {
                fileChooser.setCurrentDirectory(new java.io.File(core.model.UserParameter.temp().hrfImport_HRFPath));
            }

            final core.file.ExampleFileFilter filter = new core.file.ExampleFileFilter();
            filter.addExtension("xml");
            filter.setDescription(HOVerwaltung.instance().getLanguageString("filetypedescription.xml"));
            fileChooser.setFileFilter(filter);

            final int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                String selectedpfad = fileChooser.getSelectedFile().getAbsolutePath();

                if (!selectedpfad.endsWith(".xml")) {
                    selectedpfad += ".xml";
                }

                FormulaFactors.instance().write2XML(selectedpfad);
            }
        }
    }

    @Override
	public final void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
            refresh();
        }
    }

    //---------------Hilfsmethoden--------------------------------------
    public final void refresh() {
    	final FormulaFactors factors = FormulaFactors.instance();
        FactorObject factorObject = null;

        factorObject = factors.getPositionFactor((byte) ((CBItem) m_jcbPosition.getSelectedItem()).getId());

        m_jpSpielaufbau.removeChangeListener(this);
        m_jpFluegelspiel.removeChangeListener(this);
        m_jpTorschuss.removeChangeListener(this);
        m_jpTorwart.removeChangeListener(this);
        m_jpPasspiel.removeChangeListener(this);
        m_jpVerteidigung.removeChangeListener(this);
        m_jpStandard.removeChangeListener(this);

        m_jpSpielaufbau.setValue(factorObject.getPMfactor());
        m_jpFluegelspiel.setValue(factorObject.getWIfactor());
        m_jpTorschuss.setValue(factorObject.getSCfactor());
        m_jpTorwart.setValue(factorObject.getGKfactor());
        m_jpPasspiel.setValue(factorObject.getPSfactor());
        m_jpVerteidigung.setValue(factorObject.getDEfactor());
        m_jpStandard.setValue(factorObject.getSPfactor());

        m_jpSpielaufbau.addChangeListener(this);
        m_jpFluegelspiel.addChangeListener(this);
        m_jpTorschuss.addChangeListener(this);
        m_jpTorwart.addChangeListener(this);
        m_jpPasspiel.addChangeListener(this);
        m_jpVerteidigung.addChangeListener(this);
        m_jpStandard.addChangeListener(this);
    }

    @Override
	public final void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        //saven der aktuellen Einstellungen
    	final FormulaFactors factors = FormulaFactors.instance();
        final FactorObject factorObject = new FactorObject((byte) (((CBItem) m_jcbPosition
                                                                    .getSelectedItem()).getId()),
                                                           m_jpTorwart.getValue(),
                                                           m_jpSpielaufbau.getValue(),
                                                           m_jpPasspiel.getValue(),
                                                           m_jpFluegelspiel.getValue(),
                                                           m_jpVerteidigung.getValue(),
                                                           m_jpTorschuss.getValue(),
                                                           m_jpStandard.getValue());

        factors.setPositionFactor(factorObject.getPosition(),factorObject);
        OptionManager.instance().setReInitNeeded();
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();

        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        setLayout(layout);

        final JLabel label = new JLabel(HOVerwaltung.instance().getLanguageString("Position"));
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(label, constraints);
        add(label);

        m_jcbPosition = new JComboBox(core.util.Helper.SPIELERPOSITIONEN);
        m_jcbPosition.addItemListener(this);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jcbPosition, constraints);
        add(m_jcbPosition);

        //----Slider -----------
        final JPanel panel = new ImagePanel();
        panel.setLayout(new GridLayout(7, 1, 4, 4));
        panel.setBorder(BorderFactory.createLineBorder(ThemeManager.getColor(HOColorName.PANEL_BORDER)));

        m_jpSpielaufbau = new SliderPanel(PlayerSkill.toString(PlayerSkill.PLAYMAKING),
                                          100, 0, 10, 1.0f, 80);
        m_jpSpielaufbau.addChangeListener(this);
        panel.add(m_jpSpielaufbau);

        m_jpFluegelspiel = new SliderPanel(PlayerSkill.toString(PlayerSkill.WINGER),
                                           100, 0, 10, 1.0f, 80);
        m_jpFluegelspiel.addChangeListener(this);
        panel.add(m_jpFluegelspiel);

        m_jpTorschuss = new SliderPanel(PlayerSkill.toString(PlayerSkill.SCORING),
                                        100, 0, 10, 1.0f, 80);
        m_jpTorschuss.addChangeListener(this);
        panel.add(m_jpTorschuss);

        m_jpTorwart = new SliderPanel(PlayerSkill.toString(PlayerSkill.KEEPER),
                                      100, 0, 10, 1.0f, 80);
        m_jpTorwart.addChangeListener(this);
        panel.add(m_jpTorwart);

        m_jpPasspiel = new SliderPanel(PlayerSkill.toString(PlayerSkill.PASSING),
                                       100, 0, 10, 1.0f, 80);
        m_jpPasspiel.addChangeListener(this);
        panel.add(m_jpPasspiel);

        m_jpVerteidigung = new SliderPanel(PlayerSkill.toString(PlayerSkill.DEFENDING),
                                           100, 0, 10, 1.0f, 80);
        m_jpVerteidigung.addChangeListener(this);
        panel.add(m_jpVerteidigung);

        m_jpStandard = new SliderPanel(PlayerSkill.toString(PlayerSkill.SET_PIECES),
                                       100, 0, 10, 1.0f, 80);
        m_jpStandard.addChangeListener(this);
        panel.add(m_jpStandard);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 2;
        layout.setConstraints(panel, constraints);
        add(panel);

        m_jbResetToDefaults = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.reset"));
        m_jbResetToDefaults.addActionListener(this);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 2;
        layout.setConstraints(m_jbResetToDefaults, constraints);
        add(m_jbResetToDefaults);

        m_jbImport = new JButton(HOVerwaltung.instance().getLanguageString("FormelwertImport"));
        m_jbImport.addActionListener(this);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jbImport, constraints);
        add(m_jbImport);

        m_jbExport = new JButton(HOVerwaltung.instance().getLanguageString("FormelwertExport"));
        m_jbExport.addActionListener(this);

        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 1;
        constraints.gridy = 3;
        constraints.gridwidth = 1;
        layout.setConstraints(m_jbExport, constraints);
        add(m_jbExport);

        refresh();
    }
}
