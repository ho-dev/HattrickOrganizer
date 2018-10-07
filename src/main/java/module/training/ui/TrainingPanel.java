// %1126721451963:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.training.TrainingManager;
import module.training.ui.comp.DividerListener;
import module.training.ui.comp.FutureSettingPanel;
import module.training.ui.comp.IntensityComboBox;
import module.training.ui.comp.TrainingComboBox;
import module.training.ui.model.FutureTrainingsTableModel;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.PastTrainingsTableModel;
import module.training.ui.model.TrainingModel;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 * Panel where past and future training are shown
 */
public class TrainingPanel extends JPanel {

	private static final long serialVersionUID = 5456485854278251422L;
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
	public TrainingPanel(TrainingModel model) {
		super();
		this.model = model;
		initComponents();
		addListeners();
		reload();
	}

	/**
	 * Populate the table is called everytime a refresh command is issued
	 */
	public void reload() {
		pastTrainingsTableModel.populate(TrainingManager.instance().getTrainingWeekList());
		futureTrainingsTableModel.populate(this.model.getFutureTrainings());
	}

	private void addListeners() {
		this.setAllButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TableCellEditor editor = futureTrainingsTable.getCellEditor();

				if (editor != null) {
					editor.stopCellEditing();
				}

				JOptionPane.showMessageDialog((JFrame) getTopLevelAncestor(),
						new FutureSettingPanel(model, futureTrainingsTableModel), HOVerwaltung
								.instance().getLanguageString("SetAll"), JOptionPane.PLAIN_MESSAGE);
			}
		});

		this.splitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY,
				new DividerListener(DividerListener.training_pastFutureTrainingsSplitPane));

		this.model.addModelChangeListener(new ModelChangeListener() {

			@Override
			public void modelChanged(ModelChange change) {
				if (change == ModelChange.FUTURE_TRAINING) {
					reload();
				}
			}
		});
	}

	/**
	 * Initialize the object layout
	 */
	private void initComponents() {
		JPanel pastTrainingsPanel = new JPanel(new GridBagLayout());
		GridBagConstraints uGbc = new GridBagConstraints();
		uGbc.anchor = GridBagConstraints.WEST;
		uGbc.insets = new Insets(3, 3, 3, 3);

		JLabel pastTrainingsLabel = new JLabel();
		pastTrainingsLabel.setText(HOVerwaltung.instance().getLanguageString("PastTrainings"));
		uGbc.gridx = 0;
		uGbc.gridy = 0;
		pastTrainingsPanel.add(pastTrainingsLabel, uGbc);

		this.pastTrainingsTableModel = new PastTrainingsTableModel();
		JTable pastTrainingsTable = new TrainingTable(this.pastTrainingsTableModel);
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
		this.futureTrainingsTable = new TrainingTable(this.futureTrainingsTableModel);
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

		private static final long serialVersionUID = -3824693600024962432L;

		public TrainingTable(TableModel arg0) {
			super(arg0);
			setComboBoxEditor();
		}

		/**
		 * Initiates combo box and editor
		 */
		private void setComboBoxEditor() {
			// Sets the combo box for selecting the training type
			JComboBox comboBox = new TrainingComboBox();
			TableColumn column = getColumnModel().getColumn(2);

			column.setCellEditor(new DefaultCellEditor(comboBox));
			column.setPreferredWidth(120);

			// Sets the combo box for selecting the intensity
			JComboBox intensiBox = new IntensityComboBox(0);
			TableColumn column2 = getColumnModel().getColumn(3);

			column2.setCellEditor(new DefaultCellEditor(intensiBox));
			column2.setPreferredWidth(50);

			// Sets the combo box for selecting the staminaTrainingPart
			JComboBox staminaTrainingPartBox = new IntensityComboBox(5);
			TableColumn column3 = getColumnModel().getColumn(4);

			column3.setCellEditor(new DefaultCellEditor(staminaTrainingPartBox));
			column3.setPreferredWidth(50);

			// Disable column resize
			getColumnModel().getColumn(0).setResizable(false);
			getColumnModel().getColumn(1).setResizable(false);
			getColumnModel().getColumn(2).setResizable(false);
			getColumnModel().getColumn(3).setResizable(false);
			getColumnModel().getColumn(4).setResizable(false);
		}
	}
}
