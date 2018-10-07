package module.playerOverview;

import core.constants.player.PlayerAbility;
import core.constants.player.PlayerSpeciality;
import core.datatype.CBItem;
//import core.epv.EPVData;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.comp.entry.DoppelLabelEntry;
import core.gui.comp.panel.ImagePanel;
import core.gui.theme.ImageUtilities;
import core.model.HOVerwaltung;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;
import core.model.player.SpielerPosition;
import core.module.IModule;
import core.util.Helper;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



/**
 * This is a Skill Tester, where parameters of a player can be changed to see
 * what effect this will have on ratings for the player.
 */
final class SpielerTrainingsSimulatorPanel extends ImagePanel
    implements core.gui.Refreshable, ItemListener, ActionListener, FocusListener
{

	private static final long serialVersionUID = 7657564758631332932L;

	//~ Static fields/initializers -----------------------------------------------------------------

    private static Dimension CBSIZE = new Dimension(Helper.calcCellWidth(120),
                                                    Helper.calcCellWidth(25));
    private static Dimension PFEILSIZE = new Dimension(20, 20);

    //~ Instance fields ----------------------------------------------------------------------------

    private final ColorLabelEntry m_jpBestPos = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
    		ColorLabelEntry.BG_STANDARD, SwingConstants.LEFT);
    private final DoppelLabelEntry m_jpWertAussenVert = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertAussenVertDef = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertAussenVertIn = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertAussenVertOff = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertFluegel = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertFluegelDef = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertFluegelIn = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertFluegelOff = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertInnenVert = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertInnenVertAus = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertInnenVertOff = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertMittelfeld = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertMittelfeldAus = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertMittelfeldDef = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertMittelfeldOff = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertSturm = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertSturmAus = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertSturmDef = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSSUBPOSITIONVALUES);
    private final DoppelLabelEntry m_jpWertTor = new DoppelLabelEntry(ColorLabelEntry.BG_PLAYERSPOSITIONVALUES);
    private final ColorLabelEntry m_jpEPV = new ColorLabelEntry("", ColorLabelEntry.FG_STANDARD,
    		ColorLabelEntry.BG_STANDARD, SwingConstants.RIGHT);
    private final JButton m_jbAddTempSpieler = new JButton(HOVerwaltung.instance().getLanguageString("AddTempspieler"));
    private final JButton m_jbRemoveTempSpieler = new JButton(HOVerwaltung.instance().getLanguageString("RemoveTempspieler"));
    private final JComboBox m_jcbErfahrung = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbFluegel = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbForm = new JComboBox(Helper.EINSTUFUNG_FORM);
    private final JComboBox m_jcbKondition = new JComboBox(Helper.EINSTUFUNG_KONDITION);
    private final JComboBox m_jcbPasspiel = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbSpielaufbau = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbStandard = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbTorschuss = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbTorwart = new JComboBox(PlayerAbility.ITEMS);
    private final JComboBox m_jcbVerteidigung = new JComboBox(PlayerAbility.ITEMS);
	private final JComboBox m_jcbSpeciality = new JComboBox(PlayerSpeciality.ITEMS);
	private final JComboBox m_jcbLoyalty = new JComboBox(PlayerAbility.ITEMS);
	private final JCheckBox m_jchHomegrown = new JCheckBox();
	private JTextField jtfAge = new JTextField("17.0");
    private final JLabel m_jlErfahrung = new JLabel();
    private final JLabel m_jlFluegel = new JLabel();
    private final JLabel m_jlForm = new JLabel();
    private final JLabel m_jlKondition = new JLabel();
    private final JLabel m_jlName = new JLabel();
    private final JLabel m_jlPasspiel = new JLabel();
    private final JLabel m_jlSpielaufbau = new JLabel();
    private final JLabel m_jlStandard = new JLabel();
    private final JLabel m_jlTorschuss = new JLabel();
    private final JLabel m_jlTorwart = new JLabel();
    private final JLabel m_jlVerteidigung = new JLabel();
    private final JLabel m_jlLoyalty = new JLabel();
    private final JLabel m_jlHomeGrown = new JLabel();
    private Spieler m_clSpieler;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SpielerTrainingsSimulatorPanel object.
     */
    protected SpielerTrainingsSimulatorPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public final void setSpieler(Spieler spieler) {
        m_clSpieler = spieler;

        if (spieler != null) {
            setLabels();
            setCBs();

            //Remove for Temp player
            if (spieler.getSpielerID() < 0) {
                m_jbRemoveTempSpieler.setEnabled(true);
            } else {
                m_jbRemoveTempSpieler.setEnabled(false);
            }
        } else {
            resetLabels();
            resetCBs();
            m_jbRemoveTempSpieler.setEnabled(false);
        }
        invalidate();
        validate();
        repaint();
    }

    public final void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(m_jbAddTempSpieler)) {
            final Spieler tempSpieler = new Spieler();
            tempSpieler.setNationalitaet(HOVerwaltung.instance().getModel().getBasics().getLand());
            tempSpieler.setSpielerID(module.transfer.scout.TransferEingabePanel
                                     .getNextTempSpielerID());
            tempSpieler.setName("Temp " + Math.abs(1000 + tempSpieler.getSpielerID()));
			tempSpieler.setAlter(getAge());
			tempSpieler.setAgeDays(getAgeDays());
            tempSpieler.setErfahrung(((CBItem) m_jcbErfahrung.getSelectedItem()).getId());
            tempSpieler.setForm(((CBItem) m_jcbForm.getSelectedItem()).getId());
            tempSpieler.setKondition(((CBItem) m_jcbKondition.getSelectedItem()).getId());
            tempSpieler.setVerteidigung(((CBItem) m_jcbVerteidigung.getSelectedItem()).getId());
            tempSpieler.setSpezialitaet(((CBItem) m_jcbSpeciality.getSelectedItem()).getId());
            tempSpieler.setTorschuss(((CBItem) m_jcbTorschuss.getSelectedItem()).getId());
            tempSpieler.setTorwart(((CBItem) m_jcbTorwart.getSelectedItem()).getId());
            tempSpieler.setFluegelspiel(((CBItem) m_jcbFluegel.getSelectedItem()).getId());
            tempSpieler.setPasspiel(((CBItem) m_jcbPasspiel.getSelectedItem()).getId());
            tempSpieler.setStandards(((CBItem) m_jcbStandard.getSelectedItem()).getId());
            tempSpieler.setSpielaufbau(((CBItem) m_jcbSpielaufbau.getSelectedItem()).getId());
            tempSpieler.setLoyalty(((CBItem) m_jcbLoyalty.getSelectedItem()).getId());
            tempSpieler.setHomeGrown(m_jchHomegrown.isSelected());
            HOVerwaltung.instance().getModel().addSpieler(tempSpieler);
            RefreshManager.instance().doReInit();
            HOMainFrame.instance().showTab(IModule.PLAYEROVERVIEW);
        } else if (e.getSource().equals(m_jbRemoveTempSpieler)) {
            HOVerwaltung.instance().getModel().removeSpieler(m_clSpieler);
            RefreshManager.instance().doReInit();
            HOMainFrame.instance().showTab(IModule.PLAYEROVERVIEW);
        }
    }

    public final void itemStateChanged(ItemEvent itemEvent) {
        if ((itemEvent.getStateChange() == ItemEvent.SELECTED) || (itemEvent.getSource() == m_jchHomegrown)) {
            if (m_clSpieler != null) {
                setLabels();
            } else {
                resetLabels();
            }
        }
    }

    public final void reInit() {
        setSpieler(null);
    }

    public final void refresh() {
        setSpieler(null);
    }

    private void setCBs() {
        m_jlName.setText(m_clSpieler.getName());
		jtfAge.setText(m_clSpieler.getAlter()+"."+m_clSpieler.getAgeDays());
        Helper.markierenComboBox(m_jcbForm, m_clSpieler.getForm());
        Helper.markierenComboBox(m_jcbErfahrung, m_clSpieler.getErfahrung());
        Helper.markierenComboBox(m_jcbKondition, m_clSpieler.getKondition());
        Helper.markierenComboBox(m_jcbSpielaufbau, m_clSpieler.getSpielaufbau());
        Helper.markierenComboBox(m_jcbFluegel, m_clSpieler.getFluegelspiel());
        Helper.markierenComboBox(m_jcbTorschuss, m_clSpieler.getTorschuss());
        Helper.markierenComboBox(m_jcbTorwart, m_clSpieler.getTorwart());
        Helper.markierenComboBox(m_jcbPasspiel, m_clSpieler.getPasspiel());
        Helper.markierenComboBox(m_jcbVerteidigung, m_clSpieler.getVerteidigung());
		Helper.markierenComboBox(m_jcbSpeciality, m_clSpieler.getSpezialitaet());
        Helper.markierenComboBox(m_jcbStandard, m_clSpieler.getStandards());
        Helper.markierenComboBox(m_jcbLoyalty, m_clSpieler.getLoyalty());
        m_jchHomegrown.setSelected(m_clSpieler.isHomeGrown());

        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));

        m_jcbForm.setEnabled(true);
        m_jcbErfahrung.setEnabled(true);
        m_jcbKondition.setEnabled(true);
        m_jcbSpielaufbau.setEnabled(true);
        m_jcbFluegel.setEnabled(true);
        m_jcbTorschuss.setEnabled(true);
        m_jcbTorwart.setEnabled(true);
        m_jcbPasspiel.setEnabled(true);
        m_jcbVerteidigung.setEnabled(true);
		m_jcbSpeciality.setEnabled(true);
        m_jcbStandard.setEnabled(true);
        m_jcbLoyalty.setEnabled(true);
        m_jchHomegrown.setEnabled(true);
    }

    private void setLabels() {
        final Spieler tempSpieler = new Spieler();
        tempSpieler.setForm(((CBItem) m_jcbForm.getSelectedItem()).getId());
        tempSpieler.setErfahrung(((CBItem) m_jcbErfahrung.getSelectedItem()).getId());
        tempSpieler.setKondition(((CBItem) m_jcbKondition.getSelectedItem()).getId());
        tempSpieler.setVerteidigung(((CBItem) m_jcbVerteidigung.getSelectedItem()).getId());
		tempSpieler.setSpezialitaet(((CBItem) m_jcbSpeciality.getSelectedItem()).getId());
        tempSpieler.setTorschuss(((CBItem) m_jcbTorschuss.getSelectedItem()).getId());
        tempSpieler.setTorwart(((CBItem) m_jcbTorwart.getSelectedItem()).getId());
        tempSpieler.setFluegelspiel(((CBItem) m_jcbFluegel.getSelectedItem()).getId());
        tempSpieler.setPasspiel(((CBItem) m_jcbPasspiel.getSelectedItem()).getId());
        tempSpieler.setStandards(((CBItem) m_jcbStandard.getSelectedItem()).getId());
        tempSpieler.setSpielaufbau(((CBItem) m_jcbSpielaufbau.getSelectedItem()).getId());
        tempSpieler.setLoyalty(((CBItem) m_jcbLoyalty.getSelectedItem()).getId());
        tempSpieler.setHomeGrown(m_jchHomegrown.isSelected());

        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getForm()- m_clSpieler.getForm(),true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getKondition()- m_clSpieler.getKondition(),true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getErfahrung()- m_clSpieler.getErfahrung(),true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getSpielaufbau()- m_clSpieler.getSpielaufbau(),true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getFluegelspiel()- m_clSpieler.getFluegelspiel(),true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getTorschuss()- m_clSpieler.getTorschuss(),true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getTorwart()- m_clSpieler.getTorwart(),true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getPasspiel()- m_clSpieler.getPasspiel(),true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getVerteidigung()- m_clSpieler.getVerteidigung(),true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getStandards()- m_clSpieler.getStandards(),true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(tempSpieler.getLoyalty()- m_clSpieler.getLoyalty(), true));
        int hg = 0;
        if (m_clSpieler.isHomeGrown() != tempSpieler.isHomeGrown())
        {
        	if (m_clSpieler.isHomeGrown())
        		hg = -1;
        	else
        		hg = 1;
        }
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(hg, true));

        m_jpBestPos.setText(SpielerPosition.getNameForPosition(tempSpieler.getIdealPosition())
        		+ " (" + Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(tempSpieler.getIdealPosition(), true)) + ")");
        m_jpWertTor.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen).
        		format(tempSpieler.calcPosValue(ISpielerPosition.KEEPER, true)));
        m_jpWertTor.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.KEEPER, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.KEEPER,true), false);
        m_jpWertInnenVert.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER, true)));
        m_jpWertInnenVert.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER,true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER,true), false);
        m_jpWertInnenVertAus.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_TOWING, true)));
        m_jpWertInnenVertAus.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_TOWING, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_TOWING, true), false);
        m_jpWertInnenVertOff.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_OFF, true)));
        m_jpWertInnenVertOff.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_OFF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.CENTRAL_DEFENDER_OFF, true), false);
        m_jpWertAussenVert.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.BACK, true)));
        m_jpWertAussenVert.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.BACK, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.BACK, true), false);
        m_jpWertAussenVertIn.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen).
        		format(tempSpieler.calcPosValue(ISpielerPosition.BACK_TOMID, true)));
        m_jpWertAussenVertIn.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.BACK_TOMID, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.BACK_TOMID, true), false);
        m_jpWertAussenVertOff.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.BACK_OFF, true)));
        m_jpWertAussenVertOff.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.BACK_OFF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.BACK_OFF, true), false);
        m_jpWertAussenVertDef.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.BACK_DEF, true)));
        m_jpWertAussenVertDef.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.BACK_DEF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.BACK_DEF, true), false);
        m_jpWertMittelfeld.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER, true)));
        m_jpWertMittelfeld.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.MIDFIELDER, true), false);
        m_jpWertMittelfeldAus.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_TOWING, true)));
        m_jpWertMittelfeldAus.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_TOWING, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_TOWING, true), false);
        m_jpWertMittelfeldOff.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_OFF, true)));
        m_jpWertMittelfeldOff.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_OFF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_OFF, true), false);
        m_jpWertMittelfeldDef.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_DEF, true)));
        m_jpWertMittelfeldDef.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_DEF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.MIDFIELDER_DEF, true), false);
        m_jpWertFluegel.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.WINGER, true)));
        m_jpWertFluegel.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.WINGER, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.WINGER, true), false);
        m_jpWertFluegelIn.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.WINGER_TOMID, true)));
        m_jpWertFluegelIn.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.WINGER_TOMID, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.WINGER_TOMID, true), false);
        m_jpWertFluegelOff.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.WINGER_OFF, true)));
        m_jpWertFluegelOff.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.WINGER_OFF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.WINGER_OFF, true), false);
        m_jpWertFluegelDef.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.WINGER_DEF, true)));
        m_jpWertFluegelDef.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.WINGER_DEF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.WINGER_DEF, true), false);
        m_jpWertSturm.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.FORWARD, true)));
        m_jpWertSturm.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.FORWARD, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.FORWARD, true), false);
        m_jpWertSturmAus.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.FORWARD_TOWING, true)));
        m_jpWertSturmAus.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.FORWARD_TOWING, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.FORWARD_TOWING, true), false);
        m_jpWertSturmDef.getLinks().setText(Helper.getNumberFormat(false, core.model.UserParameter.instance().anzahlNachkommastellen)
        		.format(tempSpieler.calcPosValue(ISpielerPosition.FORWARD_DEF, true)));
        m_jpWertSturmDef.getRechts().setSpecialNumber(tempSpieler.calcPosValue(ISpielerPosition.FORWARD_DEF, true)
        		- m_clSpieler.calcPosValue(ISpielerPosition.FORWARD_DEF, true), false);
		tempSpieler.setAlter(getAge());
		tempSpieler.setAgeDays(getAgeDays());
		tempSpieler.setFuehrung(m_clSpieler.getFuehrung());
		tempSpieler.setSpezialitaet(m_clSpieler.getSpezialitaet());
//        m_jpEPV.setText(java.text.NumberFormat.getCurrencyInstance()
//        		.format(HOVerwaltung.instance().getModel().getEPV()
//        		.getPrice(new EPVData(tempSpieler))));
    }

	private int getAge() {
		int age = 17;
		if (m_clSpieler != null) {
			age = m_clSpieler.getAlter();
		}
		try {
			age = Integer.parseInt(jtfAge.getText().replaceFirst("\\..*", ""));
		} catch (NumberFormatException e) {
		}
		return age;
	}

	private int getAgeDays() {
		int age = 0;
		if (m_clSpieler != null) {
			age = m_clSpieler.getAgeDays();
		}
		try {
			age = Integer.parseInt(jtfAge.getText().replaceFirst(".*\\.", ""));
		} catch (NumberFormatException e) {
		}
		return age;
	}

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(2, 2, 2, 2);

        JPanel panel;
        JLabel label;

        setLayout(layout);

        //Eingaben-------
        final GridBagLayout eingabenLayout = new GridBagLayout();
        final GridBagConstraints eingabenconstraints = new GridBagConstraints();
        eingabenconstraints.anchor = GridBagConstraints.WEST;
        eingabenconstraints.fill = GridBagConstraints.NONE;
        eingabenconstraints.weightx = 0.0;
        eingabenconstraints.weighty = 0.0;
        eingabenconstraints.insets = new Insets(4, 4, 4, 4);

        panel = new ImagePanel();
        panel.setLayout(eingabenLayout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.name"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 0;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 0;
        eingabenconstraints.gridwidth = 2;
        eingabenLayout.setConstraints(m_jlName, eingabenconstraints);
        panel.add(m_jlName);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.speciality"));
		eingabenconstraints.gridx = 0;
		eingabenconstraints.gridy = 1;
		eingabenconstraints.gridwidth = 1;
		eingabenLayout.setConstraints(label, eingabenconstraints);
		panel.add(label);
		m_jcbSpeciality.setPreferredSize(CBSIZE);
		m_jcbSpeciality.addItemListener(this);
		eingabenconstraints.gridx = 1;
		eingabenconstraints.gridy = 1;
		eingabenLayout.setConstraints(m_jcbSpeciality, eingabenconstraints);
		panel.add(m_jcbSpeciality);

		label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.age"));
		eingabenconstraints.gridx = 3;
		eingabenconstraints.gridy = 1;
		eingabenLayout.setConstraints(label, eingabenconstraints);
		panel.add(label);
		jtfAge.setPreferredSize(CBSIZE);
		jtfAge.addFocusListener(this);
		eingabenconstraints.gridx = 4;
		eingabenconstraints.gridy = 1;
		eingabenLayout.setConstraints(jtfAge, eingabenconstraints);
		panel.add(jtfAge);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.experience"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 2;
        eingabenconstraints.gridwidth = 1;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbErfahrung.setPreferredSize(CBSIZE);
        m_jcbErfahrung.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(m_jcbErfahrung, eingabenconstraints);
        panel.add(m_jcbErfahrung);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 2;
        m_jlErfahrung.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlErfahrung, eingabenconstraints);
        panel.add(m_jlErfahrung);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.form"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbForm.setPreferredSize(CBSIZE);
        m_jcbForm.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 2;
        eingabenLayout.setConstraints(m_jcbForm, eingabenconstraints);
        panel.add(m_jcbForm);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 2;
        m_jlForm.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlForm, eingabenconstraints);
        panel.add(m_jlForm);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.stamina"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbKondition.setPreferredSize(CBSIZE);
        m_jcbKondition.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(m_jcbKondition, eingabenconstraints);
        panel.add(m_jcbKondition);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 3;
        m_jlKondition.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlKondition, eingabenconstraints);
        panel.add(m_jlKondition);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.keeper"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbTorwart.setPreferredSize(CBSIZE);
        m_jcbTorwart.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 3;
        eingabenLayout.setConstraints(m_jcbTorwart, eingabenconstraints);
        panel.add(m_jcbTorwart);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 3;
        m_jlTorwart.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlTorwart, eingabenconstraints);
        panel.add(m_jlTorwart);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.playmaking"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbSpielaufbau.setPreferredSize(CBSIZE);
        m_jcbSpielaufbau.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(m_jcbSpielaufbau, eingabenconstraints);
        panel.add(m_jcbSpielaufbau);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 4;
        m_jlSpielaufbau.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlSpielaufbau, eingabenconstraints);
        panel.add(m_jlSpielaufbau);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.passing"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbPasspiel.setPreferredSize(CBSIZE);
        m_jcbPasspiel.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 4;
        eingabenLayout.setConstraints(m_jcbPasspiel, eingabenconstraints);
        panel.add(m_jcbPasspiel);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 4;
        m_jlPasspiel.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlPasspiel, eingabenconstraints);
        panel.add(m_jlPasspiel);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.winger"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbFluegel.setPreferredSize(CBSIZE);
        m_jcbFluegel.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(m_jcbFluegel, eingabenconstraints);
        panel.add(m_jcbFluegel);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 5;
        m_jlFluegel.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlFluegel, eingabenconstraints);
        panel.add(m_jlFluegel);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.defending"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbVerteidigung.setPreferredSize(CBSIZE);
        m_jcbVerteidigung.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 5;
        eingabenLayout.setConstraints(m_jcbVerteidigung, eingabenconstraints);
        panel.add(m_jcbVerteidigung);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 5;
        m_jlVerteidigung.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlVerteidigung, eingabenconstraints);
        panel.add(m_jlVerteidigung);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.scoring"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbTorschuss.setPreferredSize(CBSIZE);
        m_jcbTorschuss.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(m_jcbTorschuss, eingabenconstraints);
        panel.add(m_jcbTorschuss);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 6;
        m_jlTorschuss.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlTorschuss, eingabenconstraints);
        panel.add(m_jlTorschuss);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.skill.setpieces"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbStandard.setPreferredSize(CBSIZE);
        m_jcbStandard.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 6;
        eingabenLayout.setConstraints(m_jcbStandard, eingabenconstraints);
        panel.add(m_jcbStandard);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 6;
        m_jlStandard.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlStandard, eingabenconstraints);
        panel.add(m_jlStandard);

        // Add loyalty label and combo
        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.loyalty"));
        eingabenconstraints.gridx = 0;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jcbLoyalty.setPreferredSize(CBSIZE);
        m_jcbLoyalty.addItemListener(this);
        eingabenconstraints.gridx = 1;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(m_jcbLoyalty, eingabenconstraints);
        panel.add(m_jcbLoyalty);
        eingabenconstraints.gridx = 2;
        eingabenconstraints.gridy = 7;
        m_jlLoyalty.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlLoyalty, eingabenconstraints);
        panel.add(m_jlLoyalty);

        // Add homegrown label and checkbox
        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.motherclub"));
        eingabenconstraints.gridx = 3;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(label, eingabenconstraints);
        panel.add(label);
        m_jchHomegrown.addItemListener(this);
        eingabenconstraints.gridx = 4;
        eingabenconstraints.gridy = 7;
        eingabenLayout.setConstraints(m_jchHomegrown, eingabenconstraints);
        panel.add(m_jchHomegrown);
        eingabenconstraints.gridx = 5;
        eingabenconstraints.gridy = 7;
        m_jlHomeGrown.setPreferredSize(PFEILSIZE);
        eingabenLayout.setConstraints(m_jlHomeGrown, eingabenconstraints);
        panel.add(m_jlHomeGrown);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        //Button--------
        panel = new JPanel();
        m_jbAddTempSpieler.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_add_tempspieler"));
        m_jbAddTempSpieler.addActionListener(this);
        panel.add(m_jbAddTempSpieler);
        m_jbRemoveTempSpieler.setToolTipText(HOVerwaltung.instance().getLanguageString("tt_remove_tempspieler"));
        m_jbRemoveTempSpieler.addActionListener(this);
        m_jbRemoveTempSpieler.setEnabled(false);
        panel.add(m_jbRemoveTempSpieler);

        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.anchor = GridBagConstraints.NORTH;
        layout.setConstraints(panel, constraints);
        add(panel);

        //Werte---------
        panel = new ImagePanel();
        panel.setLayout(new GridLayout(21, 2, 2, 2));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("BestePosition"));
        panel.add(label);
        panel.add(m_jpBestPos.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.keeper"));
        panel.add(label);
        panel.add(m_jpWertTor.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefender"));
        panel.add(label);
        panel.add(m_jpWertInnenVert.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefendertowardswing"));
        panel.add(label);
        panel.add(m_jpWertInnenVertAus.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.centraldefenderoffensive"));
        panel.add(label);
        panel.add(m_jpWertInnenVertOff.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingback"));
        panel.add(label);
        panel.add(m_jpWertAussenVert.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbacktowardsmiddle"));
        panel.add(label);
        panel.add(m_jpWertAussenVertIn.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackoffensive"));
        panel.add(label);
        panel.add(m_jpWertAussenVertOff.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingbackdefensive"));
        panel.add(label);
        panel.add(m_jpWertAussenVertDef.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielder"));
        panel.add(label);
        panel.add(m_jpWertMittelfeld.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfieldertowardswing"));
        panel.add(label);
        panel.add(m_jpWertMittelfeldAus.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderoffensive"));
        panel.add(label);
        panel.add(m_jpWertMittelfeldOff.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.innermidfielderdefensive"));
        panel.add(label);
        panel.add(m_jpWertMittelfeldDef.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.winger"));
        panel.add(label);
        panel.add(m_jpWertFluegel.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingertowardsmiddle"));
        panel.add(label);
        panel.add(m_jpWertFluegelIn.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingeroffensive"));
        panel.add(label);
        panel.add(m_jpWertFluegelOff.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.wingerdefensive"));
        panel.add(label);
        panel.add(m_jpWertFluegelDef.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forward"));
        panel.add(label);
        panel.add(m_jpWertSturm.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forwardtowardswing"));
        panel.add(label);
        panel.add(m_jpWertSturmAus.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("ls.player.position.forwarddefensive"));
        panel.add(label);
        panel.add(m_jpWertSturmDef.getComponent(false));

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Marktwert"));
        panel.add(label);
        panel.add(m_jpEPV.getComponent(false));

        constraints.gridx = 1;
        constraints.gridy = 0;
        constraints.gridheight = 2;
        layout.setConstraints(panel, constraints);
        add(panel);
    }

    private void resetCBs() {
        m_jlName.setText("");
		jtfAge.setText("17.0");
        m_jlForm.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlKondition.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlErfahrung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlSpielaufbau.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlFluegel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlTorschuss.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlTorwart.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlPasspiel.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlVerteidigung.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlStandard.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlLoyalty.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));
        m_jlHomeGrown.setIcon(ImageUtilities.getImageIcon4Veraenderung(0,true));

        resetCB(m_jcbForm);
        resetCB(m_jcbErfahrung);
        resetCB(m_jcbKondition);
        resetCB(m_jcbSpielaufbau);
        resetCB(m_jcbFluegel);
        resetCB(m_jcbTorschuss);
        resetCB(m_jcbTorwart);
        resetCB(m_jcbPasspiel);
        resetCB(m_jcbVerteidigung);
        resetCB(m_jcbSpeciality);
        resetCB(m_jcbStandard);
        resetCB(m_jcbLoyalty);
        resetCheckBox(m_jchHomegrown);
    }

    private void resetCheckBox(JCheckBox chk){
    	chk.setSelected(false);
    	chk.setEnabled(false);
    }

    private void resetCB(JComboBox cb){
    	Helper.markierenComboBox(cb, PlayerAbility.DISASTROUS);
    	cb.setEnabled(false);
    }

    private void resetLabels() {
        m_jpBestPos.clear();
        m_jpWertTor.clear();
        m_jpWertInnenVert.clear();
        m_jpWertInnenVertAus.clear();
        m_jpWertInnenVertOff.clear();
        m_jpWertAussenVert.clear();
        m_jpWertAussenVertIn.clear();
        m_jpWertAussenVertOff.clear();
        m_jpWertAussenVertDef.clear();
        m_jpWertMittelfeld.clear();
        m_jpWertMittelfeldAus.clear();
        m_jpWertMittelfeldDef.clear();
        m_jpWertMittelfeldOff.clear();
        m_jpWertFluegel.clear();
        m_jpWertFluegelIn.clear();
        m_jpWertFluegelDef.clear();
        m_jpWertFluegelOff.clear();
        m_jpWertSturm.clear();
        m_jpWertSturmAus.clear();
        m_jpWertSturmDef.clear();
        m_jpEPV.clear();
    }

	public void focusGained(FocusEvent arg0) {
	}

	public void focusLost(FocusEvent e) {
		if (e.getSource().equals(jtfAge)) {
			if (m_clSpieler != null) {
				setLabels();
			} else {
				resetLabels();
			}
		}
	}
}
