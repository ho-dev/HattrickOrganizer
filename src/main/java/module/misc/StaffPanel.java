// %2109680998:de.hattrickorganizer.gui.info%
package module.misc;

import core.gui.comp.entry.ColorLabelEntry;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.StaffMember;
import core.model.misc.Verein;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * Zeigt die Vereininformationen an
 */
final class StaffPanel extends JPanel {

	private static final long serialVersionUID = 8873968321073527819L;

	//~ Instance fields ----------------------------------------------------------------------------

	private final ColorLabelEntry assistantCoachesLabel = new ColorLabelEntry("");
	private final ColorLabelEntry doctorsLabel 			= new ColorLabelEntry("");
	private final ColorLabelEntry spokepersonsLabel 	= new ColorLabelEntry("");
	private final ColorLabelEntry psychologistsLabel 	= new ColorLabelEntry("");
	private final ColorLabelEntry formCoachLabel = new ColorLabelEntry("");
	private final ColorLabelEntry financialdirectorLabel = new ColorLabelEntry("");
	private final ColorLabelEntry tacticalAssistantLabel = new ColorLabelEntry("");
	private final ColorLabelEntry formAssistantLabel = new ColorLabelEntry("");

	final GridBagLayout layout = new GridBagLayout();
	final GridBagConstraints constraints = new GridBagConstraints();
	/**
	 * Creates a new TrainerstabPanel object.
	 */
	protected StaffPanel() {
		initComponents();
	}

	void setLabels() {
		final Verein verein = HOVerwaltung.instance().getModel().getVerein();
		if(verein != null){
			assistantCoachesLabel.setText(verein.getCoTrainer() + "");
			doctorsLabel.setText(verein.getAerzte() + "");
			spokepersonsLabel.setText(verein.getPRManager() + "");
			psychologistsLabel.setText(verein.getPsychologen() + "");
//			formCoachLabel.setText(verein.getMasseure() + "");
			financialdirectorLabel.setText(verein.getFinancialDirectorLevels() + "");
			tacticalAssistantLabel.setText(verein.getTacticalAssistantLevels() + "");
			formAssistantLabel.setText(verein.getFormCoachLevels() + "");
			
		}
	}

	private void initComponents() {


		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		constraints.insets = new Insets(4, 4, 4, 4);

		this.setBackground(ThemeManager.getColor(HOColorName.PANEL_BG));

		HOVerwaltung hoV = HOVerwaltung.instance();
		setBorder(BorderFactory.createTitledBorder(hoV.getLanguageString("Trainerstab")));


		JLabel label;

		setLayout(layout);
		
		List<StaffMember> staff = HOVerwaltung.instance().getModel().getStaff();
		
		int nextYvalue = 0;
		
		if (!staff.isEmpty()) {
			
			for (StaffMember staffMember : staff) {

				constraints.anchor = GridBagConstraints.WEST;
				constraints.gridx = 0;
				constraints.gridwidth = 1;
				add(new JLabel(staffMember.getStaffType().getName()), constraints);
				constraints.gridx = 1;
				add(new JLabel(staffMember.getName()), constraints);
				String levelText = core.model.HOVerwaltung.instance().getLanguageString("ls.club.staff.level") +
									": " + staffMember.getLevel();
				constraints.gridx = 2;
				add(new JLabel(levelText), constraints);
				
				nextYvalue++;
			}
			
			constraints.gridx = 0;
			constraints.gridy = nextYvalue;
			constraints.gridwidth = 3;
			
			
			add(new JLabel("------ "+ hoV.getLanguageString("ls.club.staff.stafflevels") + "------"), constraints);
			nextYvalue++;
		}

		label = new JLabel(hoV.getLanguageString("ls.club.staff.assistantcoach"));
		add(label,assistantCoachesLabel.getComponent(false), nextYvalue);
		nextYvalue++;

		label = new JLabel(hoV.getLanguageString("ls.club.staff.medic"));
		add(label,doctorsLabel.getComponent(false), nextYvalue);
		nextYvalue++;
		
		label = new JLabel(hoV.getLanguageString("ls.club.staff.spokesperson"));
		add(label,spokepersonsLabel.getComponent(false), nextYvalue);
		nextYvalue++;
		
		label = new JLabel(hoV.getLanguageString("ls.club.staff.sportspsychologist"));
		add(label,psychologistsLabel.getComponent(false), nextYvalue);
		nextYvalue++;
		
		label = new JLabel(hoV.getLanguageString("ls.club.staff.formcoach"));
		add(label,formAssistantLabel.getComponent(false), nextYvalue);
		nextYvalue++;
		
		label = new JLabel(hoV.getLanguageString("ls.club.staff.financialdirector"));
		add(label,financialdirectorLabel.getComponent(false), nextYvalue);
		nextYvalue++;
		
		label = new JLabel(hoV.getLanguageString("ls.club.staff.tacticalassistant"));
		add(label,tacticalAssistantLabel.getComponent(false), nextYvalue);
		nextYvalue++;
			
		
		
		
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
