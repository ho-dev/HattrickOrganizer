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
