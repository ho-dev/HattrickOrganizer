package module.ifa;

import core.gui.theme.ImageUtilities;
import core.model.WorldDetailLeague;
import core.model.WorldDetailsManager;
import module.ifa.model.IfaModel;
import org.apache.commons.lang3.ObjectUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import static module.ifa.model.IfaModel.APACHE_LEAGUE_ID;

public class FlagPanel extends JPanel {

	private List<FlagLabel> flagLabels;
	private JLabel header;
	private JProgressBar percentState;

	public FlagPanel(boolean away, IfaModel ifaModel, FlagDisplayModel flagDisplayModel) {
		initialize(away, ifaModel, flagDisplayModel);
	}

	private void initialize(boolean away, IfaModel ifaModel, FlagDisplayModel flagDisplayModel) {
		createFlagLabels(away, ifaModel, flagDisplayModel);
		int totalCountryCount = flagLabels.size();
		int playedCountryCount = (away) ? ifaModel.getVistedCountriesCount() : ifaModel
				.getHostedCountriesCount();

		setLayout(new GridBagLayout());
		setBackground(Color.white);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 1.0;

		this.header = new JLabel("");
		this.header.setForeground(new Color(2522928));
		this.header.setHorizontalTextPosition(0);
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.gridwidth = flagDisplayModel.getFlagWidth();
		add(this.header, constraints);

		this.percentState = new JProgressBar();
		this.percentState.setMaximum(totalCountryCount);
		this.percentState.setValue(playedCountryCount);
		this.percentState.setPreferredSize(new Dimension(100, 14));
		this.percentState.setFont(new Font("Verdana", 1, 8));
		this.percentState.setString(playedCountryCount + "/" + totalCountryCount + " ("
				+ (int) (100.0D * this.percentState.getPercentComplete()) + "%)");
		this.percentState.setStringPainted(true);
		this.percentState.setBorder(BorderFactory.createLineBorder(Color.black));
		constraints.insets = new Insets(1, 1, 5, 1);
		constraints.gridy = 1;
		add(this.percentState, constraints);

		constraints.fill = GridBagConstraints.NONE;
		constraints.insets = new Insets(1, 1, 1, 1);
		constraints.weightx = 0.0;
		constraints.gridwidth = 1;

		if (this.flagLabels != null) {
			int i=0;
			for (var flagLabel : this.flagLabels) {
				constraints.gridx = i % flagDisplayModel.getFlagWidth();
				constraints.gridy = 2 + i / flagDisplayModel.getFlagWidth();
				add(flagLabel, constraints);
				i++;
			}
		}
	}

	void setHeaderText(String header) {
		this.header.setText(header);
	}

	void setHeaderVisible(boolean enable) {
		this.header.setVisible(enable);
		this.percentState.setVisible(enable);
	}

	private void createFlagLabels(boolean away, IfaModel ifaModel, FlagDisplayModel flagDisplayModel) {
		this.flagLabels = new ArrayList<>();
		WorldDetailsManager.instance().getLeagues().stream()
				.filter(l->l.getLeagueId()!=APACHE_LEAGUE_ID)
				.sorted((l1,l2)-> ObjectUtils.compare(l1.getCountryName(),l2.getCountryName()))
				.forEach(l->addFlagLabel(l, away, ifaModel, flagDisplayModel));
	}

	private void addFlagLabel(WorldDetailLeague league, boolean away, IfaModel ifaModel, FlagDisplayModel flagDisplayModel) {
		FlagLabel flagLabel = new FlagLabel(flagDisplayModel);
		flagLabel.setCountryId(league.getCountryId());
		flagLabel.setCountryName(league.getCountryName());
		flagLabel.setIcon(ImageUtilities.getCountryFlagIcon(flagLabel.getCountryId()));
		flagLabel.setToolTipText(flagLabel.getCountryName());

		if ((away && ifaModel.isVisited(league.getCountryId()))
				|| (!away && ifaModel.isHosted(league.getCountryId()))) {
			flagLabel.setEnabled(true);
		} else {
			flagLabel.setEnabled(false);
		}
		this.flagLabels.add(flagLabel);
	}
}
