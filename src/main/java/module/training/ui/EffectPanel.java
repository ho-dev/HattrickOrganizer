package module.training.ui;

import core.gui.comp.panel.ImagePanel;
import core.gui.comp.panel.LazyPanel;
import core.gui.comp.table.FixedColumnsTable;
import core.gui.model.UserColumnController;
import core.model.TranslationFacility;
import module.training.EffectDAO;
import javax.swing.*;
import java.awt.*;

/**
 * Shows a table with training effect.
 * 
 * @author NetHyperon
 */
public class EffectPanel extends LazyPanel {

	private FixedColumnsTable effectTable;

	@Override
	protected void initialize() {
		initComponents();
		registerRefreshable(true);
		update();
		setNeedsRefresh(false);
	}

	@Override
	protected void update() {
		EffectDAO.reload();
        UserColumnController.instance().getTrainingEffectTableModel().initData();
	}

//	/**
//	 * Sets the model for effect table.
//	 *
//	 * @param values
//	 *            List of values
//	 */
//	private void setEffectModel(List<TrainWeekEffect> values) {
//        var effectTableModel = UserColumnController.instance().getTrainingEffectTableModel();
//		effectTable.setModel(effectTableModel);
//		effectTable.getColumnModel().getColumn(0).setPreferredWidth(50);
//		effectTable.getColumnModel().getColumn(1).setPreferredWidth(50);
//		effectTable.getColumnModel().getColumn(4).setPreferredWidth(100);
//		effectTable.getColumnModel().getColumn(6).setPreferredWidth(100);
//		effectTable.getColumnModel().getColumn(7).setPreferredWidth(25);
//		effectTable.getColumnModel().getColumn(7).setCellRenderer(new SkillupsTableCellRenderer());

//		// Hide column 8
//		effectTable.getTableHeader().getColumnModel().getColumn(8).setPreferredWidth(0);
//		effectTable.getTableHeader().getColumnModel().getColumn(8).setMinWidth(0);
//		effectTable.getTableHeader().getColumnModel().getColumn(8).setMaxWidth(0);
//	}

	/**
	 * Initialize panel.
	 */
	private void initComponents() {
		setOpaque(false);
		setLayout(new BorderLayout());

		JPanel p = new ImagePanel();
		p.setOpaque(false);
		p.setLayout(new BorderLayout());

		JLabel label = new JLabel(TranslationFacility.tr("TAB_EFFECT"),
				SwingConstants.CENTER);

		label.setOpaque(false);
		p.add(label, BorderLayout.NORTH);

		JPanel mainpanel = new ImagePanel();
		mainpanel.setLayout(new BorderLayout());

		this.effectTable = new FixedColumnsTable(UserColumnController.instance().getTrainingEffectTableModel(), 2);
//		JScrollPane effectPane = new JScrollPane(this.effectTable);
//		effectPane.setOpaque(false);
		mainpanel.add(effectTable.getContainerComponent(), BorderLayout.CENTER);

		p.add(mainpanel, BorderLayout.CENTER);
		add(p, BorderLayout.CENTER);

		// Add legend panel.
		p.add(new TrainingLegendPanel(), BorderLayout.SOUTH);
		add(p, BorderLayout.CENTER);
	}

    public void storeUserSettings() {
        if ( this.effectTable != null){
            var tableModel = (EffectTableModel) this.effectTable.getModel();
            tableModel.storeUserSettings();
        }
    }
}
