// %1128099595437:de.hattrickorganizer.gui.transferscout%
package module.transfer.scout;

import core.constants.player.PlayerSpeciality;
import core.datatype.CBItem;
//import core.epv.EPVData;
import core.gui.HOMainFrame;
import core.gui.comp.HyperLinkLabel;
import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.model.player.Player;
import core.model.player.MatchRoleID;
import core.util.HOLogger;
import core.util.Helper;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;

/**
 * MiniScout dialog
 *
 * @author Marco Senn
 */
class MiniScoutDialog extends JFrame implements ItemListener, ActionListener, FocusListener {

	private static final long serialVersionUID = -2092930481559683730L;

    //~ Instance fields ----------------------------------------------------------------------------
	private JButton jbApply = new JButton(core.model.HOVerwaltung.instance().getLanguageString("ls.button.ok"));
    private JButton jbApplyScout = new JButton(core.model.HOVerwaltung.instance().getLanguageString("ls.button.apply"));
    private JButton jbCancel = new JButton(core.model.HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
    private JComboBox jcbAttacking = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbDefense = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbLeadership = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbExperience = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbForm = new JComboBox(core.util.Helper.EINSTUFUNG_FORM);
    private JComboBox jcbKeeper = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbPassing = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbPlaymaking = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbSpeciality = new JComboBox(PlayerSpeciality.ITEMS);
    private JComboBox jcbStamina = new JComboBox(core.util.Helper.EINSTUFUNG_KONDITION);
    private JComboBox jcbStandards = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbWinger = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JComboBox jcbLoyalty = new JComboBox(core.constants.player.PlayerAbility.ITEMS);
    private JCheckBox jchHomegrown = new JCheckBox();
    private JLabel jlRating = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("Unbestimmt")
                                         + ": 0.0");
    private JLabel jlStatus = new JLabel("<html><p>" + HOVerwaltung.instance().getLanguageString("scout_status") + ":&nbsp<br />&nbsp<br />&nbsp</p></html>");
    private JTextArea jtaCopyPaste = new JTextArea(5, 20);
    private JTextArea jtaNotes = new JTextArea(5, 20);
    private JTextField jtfAge = new JTextField("17.0");
    private JTextField jtfName = new JTextField();
    private JTextField jtfPlayerID = new JTextField("0");
    private JTextField jtfPrice = new JTextField("0");
    private JTextField jtfTSI = new JTextField("1000");
    private JLabel jtfEPV = new JLabel("",SwingConstants.RIGHT);
    private SpinnerDateModel clSpinnerModel = new SpinnerDateModel();
    private JSpinner jsSpinner = new JSpinner(clSpinnerModel);
    private TransferEingabePanel clOwner;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new MiniScoutDialog object.
     *
     * @param owner the parent control holding this dialog
     */
    MiniScoutDialog(TransferEingabePanel owner) {
        super(HOVerwaltung.instance().getLanguageString("ScoutMini"));
        this.setIconImage(HOMainFrame.instance().getIconImage());
        clOwner = owner;
        initComponents();

        addWindowListener(new WindowAdapter() {
        	@Override
        	public void windowClosing(WindowEvent e) {
        		core.gui.HOMainFrame.instance().setVisible(true);
        	}
		});
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Save position of MiniScout window
     *
     * @param isVisible visible when isVisible equals true
     */
    @Override
	public final void setVisible(boolean isVisible) {
        super.setVisible(isVisible);
        if (!isVisible) {
            core.model.UserParameter.instance().miniscout_PositionX = this.getLocation().x;
            core.model.UserParameter.instance().miniscout_PositionY = this.getLocation().y;
        }
    }

    /**
     * Fired when any button is pressed. Corresponding operation is then called
     *
     * @param actionEvent event given when pressed a button
     */
    @Override
	public final void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource().equals(jbApply)) {
            clOwner.setScoutEintrag(createScoutEintrag());
            close();
        } else if (actionEvent.getSource().equals(jbCancel)) {
        	close();
        } else if (actionEvent.getSource().equals(jbApplyScout)) {
            copyPaste();
		} else if (actionEvent.getSource().equals(jtfAge)) {
			spielervalueChanged();
        }
    }

    /**
     * Called when dialog gets focus
     *
     * @param e event when window gets focus
     */
    @Override
	public void focusGained(FocusEvent e) {
    }

    /**
     * Called when dialog loses focus
     *
     * @param e event when window loses focus
     */
    @Override
	public final void focusLost(FocusEvent e) {
        if (!Helper.parseInt(HOMainFrame.instance(), jtfTSI, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPlayerID, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPrice, false)) {
            return;
        }
		if (e.getSource().equals(jtfAge)) {
			spielervalueChanged();
		}
    }

    /**
     * Called when combobox value has changed
     *
     * @param itemEvent event when combobox changes
     */
    @Override
	public final void itemStateChanged(ItemEvent itemEvent) {
        if (itemEvent.getStateChange() == ItemEvent.SELECTED || itemEvent.getSource() == jchHomegrown) {
            spielervalueChanged();
        }
    }

    /**
     * Calls playerconverter and fills boxes to the corresponding values
     */
    private void copyPaste() {
        final PlayerConverter pc = new PlayerConverter();
        String message = "";
        List<String> errorFields = new ArrayList<>();

        try {
            final module.transfer.scout.Player player;
            player = pc.build(jtaCopyPaste.getText());

            if (player != null) {
                jtfPlayerID.setText(player.getPlayerID() + "");
                jtfName.setText(player.getPlayerName());
                jtfAge.setText(player.getAge() + "." + player.getAgeDays());

                jtfPrice.setText(player.getPrice() + "");
                jtfTSI.setText(player.getTSI() + "");
                jtaNotes.setText(player.getInfo());

                jcbSpeciality.removeItemListener(this);
                Helper.setComboBoxFromID(jcbSpeciality, player.getSpeciality());
                jcbSpeciality.addItemListener(this);
                jcbExperience.removeItemListener(this);
                Helper.setComboBoxFromID(jcbExperience, player.getExperience());
                jcbExperience.addItemListener(this);
                jcbLeadership.removeItemListener(this);
                Helper.setComboBoxFromID(jcbLeadership, player.getLeadership());
                jcbLeadership.addItemListener(this);
                jcbForm.removeItemListener(this);
                Helper.setComboBoxFromID(jcbForm, player.getForm());
                jcbForm.addItemListener(this);
                jcbStamina.removeItemListener(this);
                Helper.setComboBoxFromID(jcbStamina, player.getStamina());
                jcbStamina.addItemListener(this);
                jcbDefense.removeItemListener(this);
                Helper.setComboBoxFromID(jcbDefense, player.getDefense());
                jcbDefense.addItemListener(this);
                jcbAttacking.removeItemListener(this);
                Helper.setComboBoxFromID(jcbAttacking, player.getAttack());
                jcbAttacking.addItemListener(this);
                jcbKeeper.removeItemListener(this);
                Helper.setComboBoxFromID(jcbKeeper, player.getGoalKeeping());
                jcbKeeper.addItemListener(this);
                jcbWinger.removeItemListener(this);
                Helper.setComboBoxFromID(jcbWinger, player.getWing());
                jcbWinger.addItemListener(this);
                jcbPassing.removeItemListener(this);
                Helper.setComboBoxFromID(jcbPassing, player.getPassing());
                jcbPassing.addItemListener(this);
                jcbStandards.removeItemListener(this);
                Helper.setComboBoxFromID(jcbStandards, player.getSetPieces());
                jcbStandards.addItemListener(this);
                jcbLoyalty.removeItemListener(this);
                Helper.setComboBoxFromID(jcbLoyalty, player.getLoyalty());
                jcbLoyalty.addItemListener(this);
                jchHomegrown.removeItemListener(this);
                jchHomegrown.setSelected(player.isHomwGrown());
                jchHomegrown.addItemListener(this);

                // Listener stays here for recalculation of rating
                Helper.setComboBoxFromID(jcbPlaymaking, player.getPlayMaking());

                // Normally not working. Thus last positioned
                jsSpinner.setValue(pc.getDeadline());

                spielervalueChanged();
            }
        } catch (Exception e) {
            HOLogger.instance().debug(getClass(), e);
            message = core.model.HOVerwaltung.instance().getLanguageString("scout_error");
            message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
        }

        jtaCopyPaste.setText("");

        if (message.equals("")) {
            switch (pc.getStatus()) {
                case PlayerConverter.WARNING -> {
                    message = HOVerwaltung.instance().getLanguageString("scout_warning");
                    message += " " + getFieldsTextList(pc.getErrorFields());
                    message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
                    if (pc.getNotSupportedFields().size() > 0) {
                        message += " <br>" + HOVerwaltung.instance().getLanguageString("scout_not_supported_fields");
                        message += " " + getFieldsTextList(pc.getNotSupportedFields());
                    }
                }
                case PlayerConverter.ERROR -> {
                    message = HOVerwaltung.instance().getLanguageString("scout_error");
                    message += " <br>" + HOVerwaltung.instance().getLanguageString("bug_ticket");
                }
                case PlayerConverter.EMPTY_INPUT_ERROR -> message = HOVerwaltung.instance().getLanguageString("scout_error_input_empty");
                default -> {
                    message = HOVerwaltung.instance().getLanguageString("scout_success");
                    if (pc.getNotSupportedFields().size() > 0) {
                        message += " <br>" + HOVerwaltung.instance().getLanguageString("scout_not_supported_fields");
                        message += " " + getFieldsTextList(pc.getNotSupportedFields());
                    }
                }
            }
        }
        jlStatus.setText("<html><p>" + HOVerwaltung.instance().getLanguageString("scout_status") + ": " + message + "</p></html>");
    }

    private String getFieldsTextList(List<String> fields){
        StringBuilder errorFieldsTxt = new StringBuilder();
        if (fields.size()>0){
            //errorFieldsTxt = " (";
            for (int i=0;i<fields.size();i++) {
                if(i>=1) {
                    errorFieldsTxt.append(", ");
                }
                errorFieldsTxt.append(fields.get(i));
            }
            // errorFieldsTxt += ")";
        }
        return errorFieldsTxt.toString();
    }

    private void close() {
        setVisible(false);
        dispose();
        core.gui.HOMainFrame.instance().setVisible(true);
    }

    /**
     * Create new Scout entry
     *
     * @return Returns new scout entry
     */
    private ScoutEintrag createScoutEintrag() {
        final ScoutEintrag entry = new ScoutEintrag();

        if (!Helper.parseInt(HOMainFrame.instance(), jtfTSI, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPlayerID, false)
            || !Helper.parseInt(HOMainFrame.instance(), jtfPrice, false)) {
            return entry;
        }
        entry.setPlayerID(Integer.parseInt(jtfPlayerID.getText()));
        entry.setAlter(Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", "")));
        entry.setAgeDays(Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", "")));
        entry.setPrice(Integer.parseInt(jtfPrice.getText()));
        entry.setTSI(Integer.parseInt(jtfTSI.getText()));
        entry.setSpeciality(((CBItem) jcbSpeciality.getSelectedItem()).getId());
        entry.setErfahrung(((CBItem) jcbLeadership.getSelectedItem()).getId());
        entry.setErfahrung(((CBItem) jcbExperience.getSelectedItem()).getId());
        entry.setForm(((CBItem) jcbForm.getSelectedItem()).getId());
        entry.setKondition(((CBItem) jcbStamina.getSelectedItem()).getId());
        entry.setVerteidigung(((CBItem) jcbDefense.getSelectedItem()).getId());
        entry.setTorschuss(((CBItem) jcbAttacking.getSelectedItem()).getId());
        entry.setTorwart(((CBItem) jcbKeeper.getSelectedItem()).getId());
        entry.setFluegelspiel(((CBItem) jcbWinger.getSelectedItem()).getId());
        entry.setPasspiel(((CBItem) jcbPassing.getSelectedItem()).getId());
        entry.setStandards(((CBItem) jcbStandards.getSelectedItem()).getId());
        entry.setSpielaufbau(((CBItem) jcbPlaymaking.getSelectedItem()).getId());
        entry.setLoyalty(((CBItem) jcbLoyalty.getSelectedItem()).getId());
        entry.setHomegrown(jchHomegrown.isSelected());
        entry.setName(jtfName.getText());
        entry.setInfo(jtaNotes.getText());
        //entry.setDeadline(new java.sql.Timestamp( zeitlong ) );
        entry.setDeadline(new java.sql.Timestamp(clSpinnerModel.getDate().getTime()));
        return entry;
    }

	/**
     * Creates all the components on the panel
     */
    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        JPanel panel;
        JPanel copyPastePanel;
        JLabel jlExplainGuide;
        JPanel buttonPanel;
        JLabel label;

        setContentPane(new ImagePanel());
        getContentPane().setLayout(layout);

        // Textfields and Comboboxes
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(13, 4, 4, 4));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.id"));
        panel.add(label);
        jtfPlayerID.setHorizontalAlignment(SwingConstants.RIGHT);
        jtfPlayerID.addFocusListener(this);
        panel.add(jtfPlayerID);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"));
        panel.add(label);
        jtfName.addFocusListener(this);
        panel.add(jtfName);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"));
        panel.add(label);
        jtfAge.setHorizontalAlignment(SwingConstants.RIGHT);
        jtfAge.addFocusListener(this);
        panel.add(jtfAge);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.tsi"));
        panel.add(label);
        jtfTSI.setHorizontalAlignment(SwingConstants.RIGHT);
        jtfTSI.addFocusListener(this);
        panel.add(jtfTSI);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("scout_price"));
        panel.add(label);
        jtfPrice.setHorizontalAlignment(SwingConstants.RIGHT);
        jtfPrice.addFocusListener(this);
        panel.add(jtfPrice);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Ablaufdatum"));
        panel.add(label);
        jsSpinner.addFocusListener(this);
        panel.add(jsSpinner);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.speciality"));
        panel.add(label);
        jcbSpeciality.addItemListener(this);
        panel.add(jcbSpeciality);

		label = new JLabel("");
		panel.add(label);
		panel.add(jtfEPV);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.leadership"));
        panel.add(label);
        jcbLeadership.addItemListener(this);
        panel.add(jcbLeadership);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
        panel.add(label);
        jcbExperience.addItemListener(this);
        panel.add(jcbExperience);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.form"));
        panel.add(label);
        jcbForm.addItemListener(this);
        panel.add(jcbForm);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
        panel.add(label);
        jcbStamina.addItemListener(this);
        panel.add(jcbStamina);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        panel.add(label);
        jcbKeeper.addItemListener(this);
        panel.add(jcbKeeper);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        panel.add(label);
        jcbPlaymaking.addItemListener(this);
        panel.add(jcbPlaymaking);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        panel.add(label);
        jcbPassing.addItemListener(this);
        panel.add(jcbPassing);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        panel.add(label);
        jcbWinger.addItemListener(this);
        panel.add(jcbWinger);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        panel.add(label);
        jcbDefense.addItemListener(this);
        panel.add(jcbDefense);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        panel.add(label);
        jcbAttacking.addItemListener(this);
        panel.add(jcbAttacking);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        panel.add(label);
        jcbStandards.addItemListener(this);
        panel.add(jcbStandards);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.loyalty"));
        panel.add(label);
        jcbLoyalty.addItemListener(this);
        panel.add(jcbLoyalty);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.motherclub"));
        panel.add(label);
        jchHomegrown.addItemListener(this);
        panel.add(jchHomegrown);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        getContentPane().add(panel);

        panel = new ImagePanel();
        panel.setLayout(new GridLayout(1, 3, 4, 4));
        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition")
                           + ":");
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize() + 2));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(label);
        jlRating.setFont(new Font(jlRating.getFont().getName(), Font.BOLD,
                                  jlRating.getFont().getSize() + 2));
        jlRating.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(jlRating);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.CENTER;
        layout.setConstraints(panel, constraints);
        getContentPane().add(panel);

        // Notes
        panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("Notizen")));
        panel.add(new JScrollPane(jtaNotes), BorderLayout.CENTER);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        getContentPane().add(panel);

        // Copy & Paste
        panel = new ImagePanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(javax.swing.BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("CopyPaste")));
        jtaCopyPaste.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_CopyPaste"));

        copyPastePanel = new ImagePanel();
        copyPastePanel.setLayout(new BorderLayout());
        jlExplainGuide = new JLabel(HOVerwaltung.instance().getLanguageString("ExplainHowToUseTransferScout"));
        copyPastePanel.add(jlExplainGuide ,BorderLayout.NORTH);
        JLabel linkLabel = new HyperLinkLabel("https://github.com/akasolace/HO/wiki/Transfer-Scout", "https://github.com/akasolace/HO/wiki/Transfer-Scout");
        copyPastePanel.add(linkLabel, BorderLayout.CENTER);
        copyPastePanel.add(new JScrollPane(jtaCopyPaste),BorderLayout.SOUTH);
        panel.add(copyPastePanel, BorderLayout.NORTH);

        buttonPanel = new ImagePanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        jbApplyScout.setToolTipText(HOVerwaltung.instance().getLanguageString("ls.button.apply"));
        jbApplyScout.addActionListener(this);
        layout.setConstraints(jbApplyScout, constraints);
        buttonPanel.add(jbApplyScout, BorderLayout.WEST);

        panel.add(buttonPanel, BorderLayout.CENTER);

        panel.add(jlStatus, BorderLayout.SOUTH);

        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        getContentPane().add(panel);

        panel = new ImagePanel();
        panel.setLayout(new GridLayout(1, 2, 4, 4));

        jbApply.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_uebernehmen"));
        jbApply.addActionListener(this);
        panel.add(jbApply);
        jbCancel.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_Transferscout_abbrechen"));
        jbCancel.addActionListener(this);
        panel.add(jbCancel);

        constraints.fill = GridBagConstraints.NONE;
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        getContentPane().add(panel);

        spielervalueChanged();
        pack();

        setLocation(core.model.UserParameter.instance().miniscout_PositionX,
                    core.model.UserParameter.instance().miniscout_PositionY);
        HOMainFrame.instance().setVisible(false);
        setVisible(true);
    }

    /**
     * Called whenever value changes. Positions are recalculated
     */
    private void spielervalueChanged() {
        final Player tempPlayer = new Player();
        tempPlayer.setPlayerSpecialty(((CBItem) jcbSpeciality.getSelectedItem()).getId());
        tempPlayer.setExperience(((CBItem) jcbExperience.getSelectedItem()).getId());
		tempPlayer.setLeadership(((CBItem) jcbLeadership.getSelectedItem()).getId());
        tempPlayer.setForm(((CBItem) jcbForm.getSelectedItem()).getId());
        tempPlayer.setStamina(((CBItem) jcbStamina.getSelectedItem()).getId());
        tempPlayer.setVerteidigung(((CBItem) jcbDefense.getSelectedItem()).getId());
        tempPlayer.setTorschuss(((CBItem) jcbAttacking.getSelectedItem()).getId());
        tempPlayer.setTorwart(((CBItem) jcbKeeper.getSelectedItem()).getId());
        tempPlayer.setFluegelspiel(((CBItem) jcbWinger.getSelectedItem()).getId());
        tempPlayer.setPasspiel(((CBItem) jcbPassing.getSelectedItem()).getId());
        tempPlayer.setStandards(((CBItem) jcbStandards.getSelectedItem()).getId());
        tempPlayer.setSpielaufbau(((CBItem) jcbPlaymaking.getSelectedItem()).getId());
        tempPlayer.setLoyalty(((CBItem) jcbLoyalty.getSelectedItem()).getId());
        tempPlayer.setHomeGrown(jchHomegrown.isSelected());
        tempPlayer.setAlter(Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", "")));
        tempPlayer.setAgeDays(Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", "")));
//		EPVData data = new EPVData(tempPlayer);
//		double price = HOVerwaltung.instance().getModel().getEPV().getPrice(data);
//		jtfEPV.setText(NumberFormat.getCurrencyInstance().format(price));
        jlRating.setText(MatchRoleID.getNameForPosition(tempPlayer.getIdealPosition()) + " ("
                         + tempPlayer.calcPosValue(tempPlayer.getIdealPosition(), true, null, false) + ")");
    }
}
