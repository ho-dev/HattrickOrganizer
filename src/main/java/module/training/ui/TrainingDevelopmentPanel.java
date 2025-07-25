package module.training.ui;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.util.Helper;
import module.training.ui.model.SkillupTableModel;
import module.training.ui.model.TrainingModel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class TrainingDevelopmentPanel extends LazyPanel {

	private FixedColumnsTable table;
	private final TrainingModel model;

	private JLabel title;

	/**
	 * Creates a new TrainingDevelopmentPanel object.
	 */
	public TrainingDevelopmentPanel(TrainingModel model) {
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
		this.model.addModelChangeListener(change -> setNeedsRefresh(true));
	}

	/**
	 * Populate the table
	 */
	private void loadFromModel() {
		if (this.model.getActivePlayer() != null) {
			this.title.setText(Helper.getTranslation("ls.module.training.training_development")+ " " + this.model.getActivePlayer().getFullName());
		}
		((SkillupTableModel) this.table.getModel()).setTrainingModel(this.model);
	}

	/**
	 * Resize the column
	 * 
	 * @param col
	 *            column to resize
	 * @param width
	 *            new width
	 */
//	private void setColumnWidth(int col, int width) {
//		table.getTableHeader().getColumnModel().getColumn(col).setWidth(width);
//		table.getTableHeader().getColumnModel().getColumn(col).setPreferredWidth(width);
//		table.getTableHeader().getColumnModel().getColumn(col).setMaxWidth(200);
//		table.getTableHeader().getColumnModel().getColumn(col).setMinWidth(0);
//	}

	/**
	 * Initialize the object layout
	 */
	private void initComponents() {
		table = new FixedColumnsTable(UserColumnController.instance().getSkillupTableModel());
//		table.setDefaultRenderer(Object.class, new SkillupTableRenderer());

//		setColumnWidth(1, 50);
//		setColumnWidth(2, 50);

//		JScrollPane scrollPane = new JScrollPane(table.getContainerComponent());
//		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
//		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//
		JPanel headerPanel = new ImagePanel();
		headerPanel.setOpaque(false);

		title = new JLabel(" ", SwingConstants.CENTER);

		title.setOpaque(false);
		headerPanel.add(title, BorderLayout.CENTER);

		setLayout(new BorderLayout());
		add(headerPanel, BorderLayout.NORTH);
		add(table.getContainerComponent(), BorderLayout.CENTER);
	}
}
