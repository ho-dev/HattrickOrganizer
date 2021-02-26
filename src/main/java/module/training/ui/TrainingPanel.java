package module.training.ui;

import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.TrainingManager;
import core.training.TrainingPerWeek;
import core.util.Helper;
import module.training.ui.comp.DividerListener;
import module.training.ui.comp.FutureSettingPanel;
import module.training.ui.comp.trainingParametersEditor;
import module.training.ui.comp.TrainingComboBox;
import module.training.ui.model.FutureTrainingsTableModel;
import module.training.ui.model.ModelChange;
import module.training.ui.model.PastTrainingsTableModel;
import module.training.ui.model.TrainingModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Panel where past and future training are shown
 */
public class TrainingPanel extends JPanel {

	/** Future trainings table model */
	private FutureTrainingsTableModel futureTrainingsTableModel;
	/** Past trainings table model */
	private PastTrainingsTableModel pastTrainingsTableModel;

	private JTable futureTrainingsTable;
	private JButton setAllButton;
	private JSplitPane splitPane;

	private final TrainingModel model;

	/**
	 * Creates a new TrainingPanel object.
	 */
	public TrainingPanel(TrainingModel _model) {
		super();
		model = _model;
		initComponents();
		addListeners();
		reload();
	}

	/**
	 * Populate the table is called everytime a refresh command is issued
	 */
	public void reload() {
		pastTrainingsTableModel.populate(TrainingManager.instance().getHistoricalTrainings());
		futureTrainingsTableModel.populate(model.getFutureTrainings());
	}

	private void addListeners() {
		this.setAllButton.addActionListener(arg0 -> {
			TableCellEditor editor = futureTrainingsTable.getCellEditor();

			if (editor != null) {
				editor.stopCellEditing();
			}

			JOptionPane.showMessageDialog(getTopLevelAncestor(),
					new FutureSettingPanel(model, futureTrainingsTableModel), HOVerwaltung
							.instance().getLanguageString("SetAll"), JOptionPane.PLAIN_MESSAGE);
		});

		this.splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_pastFutureTrainingsSplitPane));

		this.model.addModelChangeListener(change -> {
			if (change == ModelChange.FUTURE_TRAINING) {
				reload();
			}
		});
	}

	/**
	 * Initialize the object layout
	 */
	private void initComponents() {
		JPanel pastTrainingsPanel = new JPanel(new GridBagLayout()){};
		GridBagConstraints uGbc = new GridBagConstraints();
		uGbc.anchor = GridBagConstraints.WEST;
		uGbc.insets = new Insets(3, 3, 3, 3);

		JLabel pastTrainingsLabel = new JLabel();
		pastTrainingsLabel.setText(HOVerwaltung.instance().getLanguageString("PastTrainings"));
		uGbc.gridx = 0;
		uGbc.gridy = 0;
		pastTrainingsPanel.add(pastTrainingsLabel, uGbc);

		this.pastTrainingsTableModel = new PastTrainingsTableModel();
		JTable pastTrainingsTable = new TrainingTable(this.pastTrainingsTableModel){

			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				int modelRow = convertRowIndexToModel(row);
				var histoTraining = TrainingManager.instance().getHistoricalTrainings();
				var nbRows = histoTraining.size();
				TrainingPerWeek tpw = histoTraining.get(nbRows- modelRow- 1);
				var source = tpw.getSource();
				switch (source) {
					case MANUAL -> c.setForeground(ThemeManager.getColor(HOColorName.BLUE));
					case GUESS -> c.setForeground(ThemeManager.getColor(HOColorName.RED));
					default -> c.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
				}
				return c;
			}

			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				try {
						int modelRow = convertRowIndexToModel(rowIndex);
						var histoTraining = TrainingManager.instance().getHistoricalTrainings();
						var nbRows = histoTraining.size();
						TrainingPerWeek tpw = histoTraining.get(nbRows- modelRow- 1);
						var source = tpw.getSource();
						tip = switch (source) {
							case MANUAL -> Helper.getTranslation("ls.module.training.manual_entry.tt");
							case GUESS -> Helper.getTranslation("ls.module.training.guess_entry.tt");
							default -> Helper.getTranslation("ls.module.training.hrf_entry.tt");
						};

				} catch (RuntimeException e1) {
					//catch null pointer exception if mouse is over an empty line
				}

				return tip;
			}

		};


		JScrollPane upperScrollPane = new JScrollPane(pastTrainingsTable);
		upperScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		uGbc.gridy = 1;
		uGbc.weightx = 1.0;
		uGbc.weighty = 1.0;
		uGbc.fill = GridBagConstraints.BOTH;
		pastTrainingsPanel.add(upperScrollPane, uGbc);

		JPanel futureTrainingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints lGbc = new GridBagConstraints();
		lGbc.anchor = GridBagConstraints.WEST;
		lGbc.insets = new Insets(3, 3, 3, 3);

		JLabel futureTrainingLabel = new JLabel();
		futureTrainingLabel.setText(HOVerwaltung.instance().getLanguageString("FutureTrainings"));
		lGbc.gridx = 0;
		lGbc.gridy = 0;
		futureTrainingsPanel.add(futureTrainingLabel, lGbc);

		this.setAllButton = new JButton(HOVerwaltung.instance().getLanguageString("SetAll"));
		lGbc.gridx = 1;
		lGbc.anchor = GridBagConstraints.EAST;
		futureTrainingsPanel.add(this.setAllButton, lGbc);

		this.futureTrainingsTableModel = new FutureTrainingsTableModel(this.model);

		futureTrainingsTable = new TrainingTable(futureTrainingsTableModel){

			public Component prepareRenderer(
					TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				int modelRow = convertRowIndexToModel(row);
				TrainingPerWeek tpw = model.getFutureTrainings().get(modelRow);
				var source = tpw.getSource();
				switch (source) {
					case MANUAL -> c.setForeground(ThemeManager.getColor(HOColorName.BLUE));
					case GUESS -> c.setForeground(ThemeManager.getColor(HOColorName.RED));
					default -> c.setForeground(ThemeManager.getColor(HOColorName.TABLEENTRY_FG));
				}
				return c;
			}

			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);

				try {
					int modelRow = convertRowIndexToModel(rowIndex);
					TrainingPerWeek tpw = model.getFutureTrainings().get(modelRow);
					var source = tpw.getSource();
					tip = switch (source) {
						case MANUAL -> Helper.getTranslation("ls.module.training.manual_entry.tt");
						case GUESS -> Helper.getTranslation("ls.module.training.guess_entry.tt");
						default -> Helper.getTranslation("ls.module.training.hrf_entry.tt");
					};

				} catch (RuntimeException e1) {
					//catch null pointer exception if mouse is over an empty line
				}

				return tip;
			}

		};
		JScrollPane lowerScrollPane = new JScrollPane(this.futureTrainingsTable);
		lowerScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		lGbc.gridx = 0;
		lGbc.gridy = 1;
		lGbc.gridwidth = 2;
		lGbc.weightx = 1.0;
		lGbc.weighty = 1.0;
		lGbc.fill = GridBagConstraints.BOTH;
		futureTrainingsPanel.add(lowerScrollPane, lGbc);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pastTrainingsPanel,
				futureTrainingsPanel);
		splitPane
				.setDividerLocation(UserParameter.instance().training_pastFutureTrainingsSplitPane);
		setLayout(new BorderLayout());
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * JTable class for past and future trainings table
	 */
	private class TrainingTable extends JTable {


		public TrainingTable(TableModel arg0) {
			super(arg0);
			setComboBoxEditor();
		}

		/**
		 * Initiates combo box and editor
		 */
		private void setComboBoxEditor() {
			// Sets the combo box for selecting the training type
			JComboBox jcbTrainingEditor = new TrainingComboBox();
			TableColumn trainingColumn = getColumnModel().getColumn(1);
			trainingColumn.setCellEditor(new DefaultCellEditor(jcbTrainingEditor));
			trainingColumn.setPreferredWidth(120);

			// Sets the combo box for selecting the intensity
			JComboBox jcbIntensityEditor = new trainingParametersEditor(1);
			TableColumn trainingIntensityColumn = getColumnModel().getColumn(2);
			trainingIntensityColumn.setCellEditor(new DefaultCellEditor(jcbIntensityEditor));
			trainingIntensityColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the staminaTrainingPart
			JComboBox jcbStaminaEditor = new trainingParametersEditor(10);
			TableColumn staminaColumn = getColumnModel().getColumn(3);
			staminaColumn.setCellEditor(new DefaultCellEditor(jcbStaminaEditor));
			staminaColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the coach skill
			JComboBox jcbCoachSkillEditor = new trainingParametersEditor(4, 8);
			TableColumn coachSkillColumn = getColumnModel().getColumn(4);
			coachSkillColumn.setCellEditor(new DefaultCellEditor(jcbCoachSkillEditor));
			coachSkillColumn.setPreferredWidth(50);

			// Sets the combo box for selecting the Assistant Coach Total Level
			JComboBox jcbAssitantsTotalLevelEditor = new trainingParametersEditor(0, 10);
			TableColumn assitantsLevelColumn = getColumnModel().getColumn(5);
			assitantsLevelColumn.setCellEditor(new DefaultCellEditor(jcbAssitantsTotalLevelEditor));
			assitantsLevelColumn.setPreferredWidth(50);
			
		}
	}
}
