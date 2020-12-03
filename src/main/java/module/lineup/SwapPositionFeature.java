package module.lineup;

import core.gui.model.SpielerCBItem;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ImageUtilities;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JToggleButton;


public class SwapPositionFeature {

	private final class SpielerPositionSwapActionListener implements
			ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (swapButton.isSelected()) {
				JComboBox source = (JComboBox) event.getSource();
				SpielerCBItem selectedItem = (SpielerCBItem) source.getSelectedItem();
				if ((selectedItem != null)
						&& (selectedItem.getPlayer() == null)) {
					unpressButton();
					swapPositionsManager.unmarkSwapCandidate();
				}
			}
		}
	}

	private final class SwapPositionFeatureItemListener implements
			ActionListener {

		private final SwapPositionsManager swapPositionsManager;

		private SwapPositionFeatureItemListener(
				SwapPositionsManager swapPositionsManager) {
			this.swapPositionsManager = swapPositionsManager;
		}

		public void actionPerformed(ActionEvent e) {
			if (swapButton.isSelected()) {
				handlePressedButton();
			} else {
				swapPositionsManager.unmarkSwapCandidate();
			}
		}

		private void handlePressedButton() {
			// Check removed to allow the swap into empty positions.
//			if (playerIsSelectedOnThisPosition()) {
				actOnLegalPressedButton();
//			} else {
//				unpressButton();
//			}
		}

		private void actOnLegalPressedButton() {
			if (swapPositionsManager.hasSwapCandidate()) {
				swapPositionsManager
						.swapWithCandidate(SwapPositionFeature.this);
			} else {
				swapPositionsManager
						.markAsSwapCandidate(SwapPositionFeature.this);
			}
		}

		private boolean playerIsSelectedOnThisPosition() {
			Lineup lineup = HOVerwaltung.instance().getModel().getLineup();
			if (lineup.getPlayerByPositionID(getPositionsID()) != null) {
				return true;
			}
			return false;
		}
	}

	private final JToggleButton swapButton = new JToggleButton();
	private final SwapPositionsManager swapPositionsManager;
	private final int positionsID;

	public SwapPositionFeature(PlayerPositionPanel spielerPositionsPanel,
			final SwapPositionsManager swapPositionsManager) {
		this.positionsID = spielerPositionsPanel.getPositionsID();
		this.swapPositionsManager = swapPositionsManager;
		initSwapButton(spielerPositionsPanel);
	}

	private void initSwapButton(PlayerPositionPanel spielerPositionsPanel) {
		customizeSwapButton();
		addButtonToPanel(spielerPositionsPanel);
	}

	private void customizeSwapButton() {
		swapButton.setToolTipText(HOVerwaltung.instance().getLanguageString("Lineup.Swap.ToolTip"));
		swapButton.setIcon(ImageUtilities.getSvgIcon(HOIconName.SWAP, Map.of("fillColor", HOColorName.SWAP_COLOR), 18, 18));
		swapButton.setSelectedIcon(ImageUtilities.getSvgIcon(HOIconName.SWAP, Map.of("fillColor", HOColorName.SWAP_COLOR_PRESSED), 18, 18));
		swapButton.setPreferredSize(new Dimension(18, 18));
		swapButton.setMaximumSize(new Dimension(18, 18));
		swapButton.setMinimumSize(new Dimension(18, 18));
		swapButton.setBorderPainted(false);
		swapButton.setBorder(null);
		swapButton.setMargin(new Insets(0, 0, 0, 0));
		swapButton.setContentAreaFilled(false);
		swapButton.addActionListener(new SwapPositionFeatureItemListener(this.swapPositionsManager));
	}

	private void addButtonToPanel(PlayerPositionPanel spielerPositionsPanel) {
		GridBagLayout layout = (GridBagLayout) spielerPositionsPanel.getSwapLayout();
		layout.setConstraints(getSwapButton(), createSwapButtonConstraints());
		spielerPositionsPanel.addSwapItem(getSwapButton());
		spielerPositionsPanel.getPlayerComboBox().addActionListener(new SpielerPositionSwapActionListener());
	}

	private GridBagConstraints createSwapButtonConstraints() {
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new java.awt.Insets(5, 0, 0, 8);
		constraints.gridx = 1;
		constraints.gridy = 0;
		constraints.fill = GridBagConstraints.NONE;
		constraints.weightx = 0.0;
		return constraints;
	}

	public JToggleButton getSwapButton() {
		return swapButton;
	}

	public void unpressButton() {
		swapButton.setSelected(false);
	}

	public int getPositionsID() {
		return positionsID;
	}

}
