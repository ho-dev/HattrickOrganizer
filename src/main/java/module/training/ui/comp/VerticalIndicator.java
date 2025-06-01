// %3339242963:hoplugins.trainingExperience.ui.bar%
package module.training.ui.comp;

import core.gui.comp.entry.IHOTableCellEntry;
import org.jetbrains.annotations.NotNull;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.*;

import static java.lang.Math.min;

/**
 * VerticalIndicator that show percentage of training
 * 
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class VerticalIndicator extends JPanel implements IHOTableCellEntry {
	// ~ Instance fields
	// ----------------------------------------------------------------------------

	private double actual;
	private double total;

	// ~ Constructors
	// -------------------------------------------------------------------------------

	/**
	 * Creates a new VerticalIndicator object.
	 * 
	 * @param _actual
	 *            actual points
	 * @param _total
	 *            skillup points
	 */
	public VerticalIndicator(double _actual, double _total) {
		super();
		this.actual = _actual;
		this.total = _total;
		createComponent();
	}

	// ~ Methods
	// ------------------------------------------------------------------------------------

	/**
	 * Get Training Percentage
	 * 
	 * @return
	 */
	public double getPercentage() {
		return min(100., actual / total * 100d);
	}

	/**
	 * Get Text describing training situation
	 * 
	 * @return
	 */
	public String getText() {
		return actual + "(" + total + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Return toolTip text
	 * 
	 * @return
	 */
	@Override
	public String getToolTipText() {
		return actual + "(" + total + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Get number of training points for skillup
	 * 
	 * @return
	 */
	public double getTotal() {
		return total;
	}

	@Override
	public JComponent getComponent(boolean isSelected) {
		return this;
	}

	@Override
	public void clear() {}

	@Override
	public int compareTo(@NotNull IHOTableCellEntry obj) {
		if (obj instanceof VerticalIndicator other) {
			if (this.getPercentage() > other.getPercentage()) {
				return 1;
			}

			if (this.getPercentage() < other.getPercentage()) {
				return -1;
			}

			if (this.getTotal() > other.getTotal()) {
				return -1;
			}

			if (this.getTotal() < other.getTotal()) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public int compareToThird(IHOTableCellEntry obj) {
		return 0;
	}

	@Override
	public void createComponent() {
		setLayout(new BorderLayout());
		setOpaque(false);

		if (actual > 0) {
			StateBar bar = new StateBar((int)getPercentage(), 100, Color.GREEN, Color.RED);
			bar.setOpaque(false);
			bar.setBackground(Color.YELLOW);
			add(bar, BorderLayout.CENTER);
		}

	}

	@Override
	public void updateComponent() {
		var bar = (StateBar)this.getComponent(false);
		bar.change((int)getPercentage(), 100);
	}
}
