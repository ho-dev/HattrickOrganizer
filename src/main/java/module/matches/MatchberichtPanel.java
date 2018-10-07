// %3087735495:de.hattrickorganizer.gui.matches%
package module.matches;

import core.gui.HOMainFrame;
import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyImagePanel;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JDialog;

class MatchberichtPanel extends LazyImagePanel {

	private static final long serialVersionUID = -9014579382145462648L;
	private JButton maxButton;
	private MatchberichtEditorPanel matchberichtEditorPanel;
	private boolean initialized = false;
	private boolean needsRefresh = false;
	private boolean withButton;
	private final MatchesModel matchesModel;

	MatchberichtPanel(boolean withButton, MatchesModel matchesModel) {
		this.matchesModel = matchesModel;
		this.withButton = withButton;
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		if (this.matchesModel.getMatch() != null
				&& this.matchesModel.getMatch().getMatchDateAsTimestamp()
						.before(new Timestamp(System.currentTimeMillis()))) {
			this.matchberichtEditorPanel.setText(this.matchesModel.getDetails().getMatchreport());
			this.maxButton.setEnabled(true);
		} else {
			this.maxButton.setEnabled(false);
			this.matchberichtEditorPanel.clear();
		}
	}

	private void addListeners() {
		this.matchesModel.addMatchModelChangeListener(new MatchModelChangeListener() {

			@Override
			public void matchChanged() {
				setNeedsRefresh(true);
			}
		});

		this.maxButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				showInDialog();
			}
		});
	}

	private void initComponents() {
		setLayout(new BorderLayout());

		this.matchberichtEditorPanel = new MatchberichtEditorPanel();
		add(this.matchberichtEditorPanel, BorderLayout.CENTER);

		if (this.withButton) {
			GridBagLayout layout = new GridBagLayout();
			GridBagConstraints constraints = new GridBagConstraints();
			constraints.anchor = GridBagConstraints.SOUTHEAST;
			constraints.fill = GridBagConstraints.NONE;
			constraints.weighty = 1.0;
			constraints.weightx = 1.0;
			constraints.insets = new Insets(4, 6, 4, 6);

			ImagePanel buttonPanel = new ImagePanel(layout);
			this.maxButton = new JButton(ThemeManager.getIcon(HOIconName.MAXLINEUP));
			this.maxButton.setToolTipText(HOVerwaltung.instance().getLanguageString(
					"tt_Matchbericht_Maximieren"));
			this.maxButton.setEnabled(false);
			this.maxButton.setPreferredSize(new Dimension(25, 25));
			layout.setConstraints(this.maxButton, constraints);
			buttonPanel.add(this.maxButton);
			add(buttonPanel, BorderLayout.SOUTH);
		}
	}

	private void showInDialog() {
		// Dialog mit Matchbericht erzeugen
		String titel = this.matchesModel.getMatch().getHeimName() + " - "
				+ this.matchesModel.getMatch().getGastName() + " ( "
				+ this.matchesModel.getMatch().getHeimTore() + " : "
				+ this.matchesModel.getMatch().getGastTore() + " )";
		JDialog matchdialog = new JDialog(HOMainFrame.instance(), titel);
		matchdialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		matchdialog.getContentPane().setLayout(new BorderLayout());
		matchdialog.getContentPane().add(MatchberichtPanel.this, BorderLayout.CENTER);
		matchdialog.setLocation(50, 50);
		matchdialog.setSize(600, HOMainFrame.instance().getHeight() - 100);
		matchdialog.setVisible(true);
	}
}
