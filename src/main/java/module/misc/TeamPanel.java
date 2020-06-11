// %1301807047:de.hattrickorganizer.gui.info%
package module.misc;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.misc.Basics;
import core.model.series.Liga;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;



/**
 * Display basics
 */
class TeamPanel extends JPanel {

	private static final long serialVersionUID = 358240701613667104L;

	//~ Instance fields ----------------------------------------------------------------------------

    private final ColorLabelEntry leagueLabel 	= new ColorLabelEntry("");
    private final ColorLabelEntry managerLabel 	= new ColorLabelEntry("");
    private final ColorLabelEntry posLabel		= new ColorLabelEntry("");
    private final ColorLabelEntry pointsLabel	= new ColorLabelEntry("");
    private final ColorLabelEntry seasonLabel	= new ColorLabelEntry("");
    private final ColorLabelEntry matchRoundLabel = new ColorLabelEntry("");
    private final ColorLabelEntry arenaLabel	= new ColorLabelEntry("");
    private final ColorLabelEntry teamLabel	 	= new ColorLabelEntry("");
    private final ColorLabelEntry teamIdLabel 	= new ColorLabelEntry("");
    private final ColorLabelEntry goalsLabel	= new ColorLabelEntry("");

    final GridBagLayout layout = new GridBagLayout();
    final GridBagConstraints constraints = new GridBagConstraints();

    /**
     * Creates a new BasicsPanel object.
     */
    TeamPanel() {
        initComponents();
     }

    void setLabels() {
        final Basics basics = HOVerwaltung.instance().getModel().getBasics();
        final Liga liga = HOVerwaltung.instance().getModel().getLeague();
        final int teamId =  HOVerwaltung.instance().getModel().getBasics().getTeamId();
        if(teamId > 0){
	        teamIdLabel.setText(basics.getTeamId()+"");
	        teamLabel.setText(basics.getTeamName());
	        managerLabel.setText(basics.getManager());
	        arenaLabel.setText(HOVerwaltung.instance().getModel().getStadium().getStadienname());
	        seasonLabel.setText(basics.getSeason() + "");
	        if(liga != null){
	        	matchRoundLabel.setText(liga.getSpieltag() + "");
	        	leagueLabel.setText(liga.getLiga());
	        	posLabel.setText(liga.getPlatzierung() + "");
	        	pointsLabel.setText(liga.getPunkte() + "");
	        	goalsLabel.setText(liga.getToreFuer() + ":" + liga.getToreGegen());
	        }
        }
    }

    private void initComponents() {
        JLabel label;

        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0.0;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(4, 4, 4, 4);

        this.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));
        setBorder(BorderFactory.createTitledBorder(HOVerwaltung.instance().getLanguageString("Allgemein")));
        setLayout(layout);
        HOVerwaltung hoV = HOVerwaltung.instance();

        label = new JLabel(hoV.getLanguageString("ls.team.id"));
        add(label,teamIdLabel.getComponent(false),1);

        label = new JLabel(hoV.getLanguageString("Verein"));
        add(label,teamLabel.getComponent(false),2);

        label = new JLabel(hoV.getLanguageString("Manager"));
        add(label,managerLabel.getComponent(false),3);

        label = new JLabel(hoV.getLanguageString("Stadion"));
        add(label,arenaLabel.getComponent(false),4);

        label = new JLabel(hoV.getLanguageString("Season"));
        add(label,seasonLabel.getComponent(false),5);

        label = new JLabel(hoV.getLanguageString("Spieltag"));
        add(label,matchRoundLabel.getComponent(false),6);

        label = new JLabel(hoV.getLanguageString("Liga"));
        add(label,leagueLabel.getComponent(false),7);

        label = new JLabel(hoV.getLanguageString("Platzierung"));
        add(label,posLabel.getComponent(false),8);

        label = new JLabel(hoV.getLanguageString("Punkte"));
        add(label,pointsLabel.getComponent(false),9);

        label = new JLabel(hoV.getLanguageString("Torverhaeltnis"));
        add(label,goalsLabel.getComponent(false),10);

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

}
