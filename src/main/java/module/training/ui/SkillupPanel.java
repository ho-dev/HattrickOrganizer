// %1303949933:hoplugins.trainingExperience.ui%
package module.training.ui;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.model.HOVerwaltung;
import core.model.player.ISkillup;
import module.training.ui.model.ModelChange;
import module.training.ui.model.ModelChangeListener;
import module.training.ui.model.SkillupTableModel;
import module.training.ui.model.TrainingModel;
import module.training.ui.renderer.SkillupTableRenderer;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

/**
 * Panel of past skillups table ("Training History")
 */
public class SkillupPanel extends LazyPanel {

	private static final long serialVersionUID = 57377377617909870L;
	private SkillupTable table;
	private TrainingModel model;

	/**
	 * Creates a new SkillupPanel object.
	 */
	public SkillupPanel(TrainingModel model) {
		this.model = model;
	}

	@Override
	protected void initialize() {
		initComponents();
		addListeners();
		// will load data if showing
		setNeedsRefresh(true);
	}

	@Override
	protected void update() {
		loadFromModel();
	}

	private void addListeners() {
		this.model.addModelChangeListener(new ModelChangeListener() {

			@Override
			public void modelChanged(ModelChange change) {
				setNeedsRefresh(true);
			}
		});
	}

	/**
	 * Populate the table
	 */
	private void loadFromModel() {
		List<ISkillup> skillups = new ArrayList<ISkillup>();
		if (this.model.getActivePlayer() != null) {
			skillups.addAll(this.model.getSkillupManager().getTrainedSkillups());
			skillups.addAll(this.model.getFutureTrainingManager().getFutureSkillups());
			Collections.reverse(skillups);
		}
		((SkillupTableModel) this.table.getModel()).setData(skillups);
	}

	/**
	 * Resize the column
	 * 
	 * @param col
	 *            column to resize
	 * @param width
	 *            new width
	 */
	private void setColumnWidth(int col, int width) {
		table.getTableHeader().getColumnModel().getColumn(col).setWidth(width);
		table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(width);
		table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(200);
		table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
	}

	/**
	 * Initialize the object layout
	 */
	private void initComponents() {
		table = new SkillupTable(new SkillupTableModel());
		table.setDefaultRenderer(Object.class, new SkillupTableRenderer());

		setColumnWidth(1, 50);
		setColumnWidth(2, 50);

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		JPanel headerPanel = new ImagePanel();
		headerPanel.setOpaque(false);

		JLabel l = new JLabel(HOVerwaltung.instance().getLanguageString("TrainingHistory"),
				SwingConstants.CENTER);

		l.setOpaque(false);
		headerPanel.add(l, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
	}
}
