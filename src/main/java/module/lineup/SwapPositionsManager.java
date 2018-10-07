package module.lineup;

import core.gui.Updateable;
import core.model.HOVerwaltung;
import core.model.player.ISpielerPosition;
import core.model.player.Spieler;

public class SwapPositionsManager {

	private final Updateable parentPanel;
	private SwapPositionFeature swapCandidate = null;

	public SwapPositionsManager(Updateable parentPanel) {
		this.parentPanel = parentPanel;
	}

	boolean hasSwapCandidate() {
		return getSwapCandidate() != null;
	}

	SwapPositionFeature getSwapCandidate() {
		return swapCandidate;
	}

	void swapWithCandidate(SwapPositionFeature swapPositionA) {
		SwapPositionFeature swapPositionB = getSwapCandidate();

		if (positionsAreSwapable(swapPositionA, swapPositionB)) {
			swapPositionsInLineup(swapPositionA, swapPositionB);
			unmarkSwapCandidate();
			updateGui(swapPositionA, swapPositionB);
		}
	}

	private void updateGui(SwapPositionFeature swapPositionA,
			SwapPositionFeature swapPositionB) {
		swapPositionA.unpressButton();
		swapPositionB.unpressButton();
		parentPanel.update();
	}

	private boolean positionsAreSwapable(SwapPositionFeature swapPositionA,
			SwapPositionFeature swapPositionB) {
		return swapPositionA.getPositionsID() != swapPositionB.getPositionsID();
	}

	private void swapPositionsInLineup(SwapPositionFeature swapPositionA,
			SwapPositionFeature swapPositionB) {
		Lineup lineup = HOVerwaltung.instance().getModel()
				.getAufstellung();

		// Changed to allow swapping players to empty positions - Blaghaid

		int positionA = swapPositionA.getPositionsID();
		int positionB = swapPositionB.getPositionsID();
		Spieler playerA = lineup.getPlayerByPositionID(positionA);
		Spieler playerB = lineup.getPlayerByPositionID(positionB);
		
		// We don't want to swap a substitute into an empty position unless there is less
		// than 11 players on the field.
		if ((playerA==null && positionB >= ISpielerPosition.startReserves) ||
				(playerB==null && positionA >= ISpielerPosition.startReserves))   {
			if (lineup.hasFreePosition() != true) {
				// HOLogger.instance().debug(getClass(), "Stopped swap due to sub-check!");
				return;
			} else {
				// HOLogger.instance().debug(getClass(), "Allowed swap due to sub-check!");
			}
		}
			
		int playerA_id = 0;
		int playerB_id = 0;
		
		if  (playerA != null) {
			playerA_id = playerA.getSpielerID();
		}
		
		if  (playerB != null) {
			playerB_id = playerB.getSpielerID();
		}
		
		lineup.setSpielerAtPosition(positionA, playerB_id);
		lineup.setSpielerAtPosition(positionB, playerA_id);
	}

	void markAsSwapCandidate(SwapPositionFeature swapPositionFeature) {
		swapCandidate = swapPositionFeature;
	}

	void unmarkSwapCandidate() {
		swapCandidate = null;
	}

	/**
	 * Initializes the swap feature for this player position.
	 * 
	 * @param swapPositionsManager
	 *            the {@link SwapPositionsManager} to register this position to.
	 */
	public void addSwapCapabilityTo(PlayerPositionPanel spielerPositionsPanel) {
		SwapPositionFeature swapPositionFeature = new SwapPositionFeature(
				spielerPositionsPanel, this);
	}
}
