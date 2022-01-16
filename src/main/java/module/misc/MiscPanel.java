package module.misc;

import core.constants.player.PlayerAbility;
import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.misc.Economy;
import core.model.misc.Verein;
import core.model.player.Player;
import core.util.Helper;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import static core.util.Helper.getTranslation;


/**
 * Zeigt die Sonstige Informationen an
 */
final class MiscPanel extends JPanel {

	private final ColorLabelEntry m_jpAnzahlSpieler = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpDAlter = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpDErfahrung = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpDForm = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpAvgEPV = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpAvgTSI = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpFans = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpFansAnzahl = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpSumEPV = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpSumTSI = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpJugend = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpSiegeInFolge = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpSponsoren = new ColorLabelEntry("");
    private final ColorLabelEntry m_jpUngeschlagen = new ColorLabelEntry("");

    final GridBagLayout layout = new GridBagLayout();
    final GridBagConstraints constraints = new GridBagConstraints();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new SonstigesPanel object.
     */
    protected MiscPanel() {
        initComponents();
     }


    void setLabels() {
        final Verein verein = HOVerwaltung.instance().getModel().getClub();
        final Economy economy = HOVerwaltung.instance().getModel().getEconomy();

        DecimalFormat df = new DecimalFormat("###,###,###,##0");

        m_jpJugend.setText(PlayerAbility.getNameForSkill(verein.getJugend()));
        m_jpFansAnzahl.setText(verein.getFans() + "");
        m_jpFans.setText(Economy.getNameForLevelFans(economy.getSupportersPopularity()));
        m_jpSponsoren.setText(Economy.getNameForLevelSponsors(economy.getSponsorsPopularity()));
        m_jpUngeschlagen.setText(verein.getUngeschlagen() + "");
        m_jpSiegeInFolge.setText(verein.getSiege() + "");
        m_jpAnzahlSpieler.setText(HOVerwaltung.instance().getModel().getCurrentPlayers().size() + "");
        m_jpAvgTSI.setText(df.format(getAvgTSI()));
        m_jpSumTSI.setText(df.format(getSumTSI()));
//        m_jpAvgEPV.setSpecialNumber(Math.round(getAvgEPV()), true);
//        m_jpSumEPV.setSpecialNumber(Math.round(getSumEPV()), true);
        m_jpDAlter.setText(getDurchschnittsAlter() + "");
        m_jpDForm.setText(getDurchschnittsForm() + "");
        m_jpDErfahrung.setText(getDurchschnittsErfahrung() + "");
    }

    private void initComponents() {
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        this.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

        var title = getTranslation("Verschiedenes");
        var titledBorder = BorderFactory.createTitledBorder(title);
        titledBorder.setTitleColor(ThemeManager.getColor(HOColorName.LINEUP_HIGHLIGHT_FG));
        setBorder(titledBorder);

        JLabel label;

        setLayout(layout);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("Jugend"));
        add(label,m_jpJugend.getComponent(false),1);
 
        label = new JLabel(HOVerwaltung.instance().getLanguageString("Fans"));
        add(label,m_jpFansAnzahl.getComponent(false),2);
 
        label = new JLabel(HOVerwaltung.instance().getLanguageString("Fans"));
        add(label,m_jpFans.getComponent(false),3);
 
        label = new JLabel(HOVerwaltung.instance().getLanguageString("Sponsoren"));
        add(label,m_jpSponsoren.getComponent(false),4);
 
        label = new JLabel(HOVerwaltung.instance().getLanguageString("Ungeschlagen"));
        add(label,m_jpUngeschlagen.getComponent(false),5);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("SiegeInFolge"));
        add(label,m_jpSiegeInFolge.getComponent(false),6);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("AnzahlSpieler"));
        add(label,m_jpAnzahlSpieler.getComponent(false),7);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("AverageTSI"));
        add(label,m_jpAvgTSI.getComponent(false),8);
 
        label = new JLabel(HOVerwaltung.instance().getLanguageString("TotalTSI"));
        add(label,m_jpSumTSI.getComponent(false),9);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("AverageEPV"));
        add(label,m_jpAvgEPV.getComponent(false),10);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("TotalEPV"));
        add(label,m_jpSumEPV.getComponent(false),11);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("DurchschnittForm"));
        add(label,m_jpDForm.getComponent(false),12);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("DurchschnittErfahrung"));
        add(label,m_jpDErfahrung.getComponent(false),13);

        label = new JLabel(HOVerwaltung.instance().getLanguageString("DurchschnittAlter"));
        add(label,m_jpDAlter.getComponent(false),14);
    }
    
    private void add(JLabel label,Component comp, int y){
    	constraints.anchor = GridBagConstraints.WEST;
    	constraints.gridx = 0;
    	constraints.gridy = y;
    	constraints.gridwidth = 1;
    	layout.setConstraints(label, constraints);
    	add(label);
    	constraints.anchor = GridBagConstraints.EAST;
    	constraints.gridx = 1;
    	constraints.gridy = y;
    	constraints.gridwidth = 1;
    	layout.setConstraints(comp, constraints);
    	add(comp);
    }
    
    /**
     * Returns the average TSI
     *
     * @return average TSI
     */
    float getAvgTSI() {
        int numPlayers = HOVerwaltung.instance().getModel().getCurrentPlayers().size();
        //Trainer abziehen // without trainer
        if (numPlayers <= 1)
        	return 0;
       	return Helper.round(getSumTSI() / (numPlayers - 1));
    }

    /**
     * Gibt den Durchschnittlichen Mannschaftswert zurück
     * Returns the average estimated market value (EPV)
     *
     * @return average EPV
     */
//    float getAvgEPV() {
//        int numPlayers = HOVerwaltung.instance().getModel().getAllSpieler().size();
//        //Trainer abziehen // without trainer
//        if (numPlayers <= 1)
//        	return 0;
//       	return Helper.round(getSumEPV() / (numPlayers - 1));
//    }

    /**
     * Gibt das Durchschnittsalter zurück
     */
    float getDurchschnittsAlter() {
        float summe = 0;
        final List<Player> vPlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();

        for ( Player p : vPlayer){
            if (!p.isTrainer()) {
            	// Age Years
                summe += p.getAlter();
                // Age Days
                summe += p.getAgeDays()/112.0;
            }
        }

        //Trainer abziehen
        return Helper.round(summe / (vPlayer.size() - 1));
    }

    /**
     * Gibt das Durchschnittserfahrung zurück
     */
    float getDurchschnittsErfahrung() {
        float summe = 0;
        final List<Player> vPlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();

        for ( Player p  : vPlayer){

            //Trainer nicht berücksichtigen
            if (!p.isTrainer()) {
                summe += p.getErfahrung();
            }
        }

        //Trainer abziehen
        return Helper.round(summe / (vPlayer.size() - 1), 3);
    }

    /**
     * Gibt das Durchschnittsform zurück
     */
    float getDurchschnittsForm() {
        float summe = 0;
        final List<Player> vPlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();

        for ( Player p: vPlayer){
            //Trainer nicht berücksichtigen
            if (!p.isTrainer()) {
                summe += p.getForm();
            }
        }

        //Trainer abziehen
        return Helper.round(summe / (vPlayer.size() - 1), 3);
    }

    /**
     * Returns the TSI sum
     */
    float getSumTSI() {
        float summe = 0;
        final List<Player> vPlayer = HOVerwaltung.instance().getModel().getCurrentPlayers();

        for (Player p: vPlayer) {
            //Trainer nicht berücksichtigen
            if (!p.isTrainer()) {
                summe += p.getTSI();
            }
        }

        return summe;
    }
}
