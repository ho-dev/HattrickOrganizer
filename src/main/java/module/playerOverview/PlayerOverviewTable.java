package module.playerOverview;

import core.db.DBManager;
import core.gui.HOMainFrame;
import core.gui.RefreshManager;
import core.gui.comp.renderer.HODefaultTableCellRenderer;
import core.gui.comp.table.TableSorter;
import core.gui.comp.table.ToolTipHeader;
import core.gui.comp.table.UserColumn;
import core.gui.model.PlayerOverviewModel;
import core.gui.model.UserColumnController;
import core.gui.model.UserColumnFactory;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.match.MatchKurzInfo;
import core.model.player.Player;
import core.net.HattrickLink;
import core.util.Helper;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serial;


/**
 * The Squad table, listing all the players on the team.
 *
 * <p>The actual model for that table is defined in {@link PlayerOverviewModel}, which defines
 * all the columns to be displayed; the columns are initiated by a factory, {@link UserColumnFactory},
 * which in particular sets their preferred width.</p>
 *
 * <p>Sorting in the table is handled by {@link TableSorter} which decorates the model, and is set
 * as the {@link javax.swing.table.TableModel} for this table.  Triggering sorting by a click sorts
 * the entries in the table model itself.  The new sorting order is then used by re-displaying the
 * table.  This approach differs from the “normal” Swing approach of using
 * {@link JTable#setRowSorter(RowSorter)}.</p>
 * 
 * @author Thorsten Dietz
 */
public class PlayerOverviewTable extends JTable implements core.gui.Refreshable {

	@Serial
	private static final long serialVersionUID = -6074136156090331418L;
	private PlayerOverviewModel tableModel;
	private TableSorter tableSorter;

	public PlayerOverviewTable() {
		super();
		initModel();
		setDefaultRenderer(Object.class, new HODefaultTableCellRenderer());
		setSelectionBackground(HODefaultTableCellRenderer.SELECTION_BG);
		RefreshManager.instance().registerRefreshable(this);

		// Add a mouse listener that, when clicking on the “Last match” column
		// - opens the Hattrick page for the player if you shift-click,
		// - or opens the match in HO if you double-click.
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int rowIndex = getSelectedRow();
				if (rowIndex >= 0) {
					// Last match column
					int columnAtPoint = columnAtPoint(e.getPoint());
					// Get name of the actual column at columnAtPoint, i.e. post-ordering of the columns
					// based on preferences.
					String columnName = PlayerOverviewTable.this.getColumnName(columnAtPoint);
					String lastMatchRating = (HOVerwaltung.instance().getLanguageString("LastMatchRating"));

					Player player = tableSorter.getSpieler(rowIndex);
					if (player != null) {
						if (columnName.equalsIgnoreCase(lastMatchRating)) {
							if (e.isShiftDown()) {
								int matchId = player.getLastMatchId();
								// TODO: get match type ?
								MatchKurzInfo info = DBManager.instance().getMatchesKurzInfoByMatchID(matchId, null);
								HattrickLink.showMatch(String.valueOf(matchId), info.getMatchType().isOfficial());
							} else if (e.getClickCount() == 2) {
								HOMainFrame.instance().showMatch(player.getLastMatchId());
							}
						}
					}
				}
			}
		});
	}

	/**
	 * Returns the width of the Best position column.
	 */
	public final int getBestPosWidth() {
		return getColumnModel()
				.getColumn(getColumnModel()
						.getColumnIndex(tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION)))
				.getWidth();
	}

	public final TableSorter getSorter() {
		return tableSorter;
	}

	public final void saveColumnOrder() {
		UserColumn[] columns = tableModel.getDisplayedColumns();
		TableColumnModel tableColumnModel = getColumnModel();
		for (int i = 0; i < columns.length; i++) {
			columns[i].setIndex(convertColumnIndexToView(i));
			columns[i].setPreferredWidth(tableColumnModel.getColumn(convertColumnIndexToView(i)).getWidth());
		}
		tableModel.setCurrentValueToColumns(columns);
		DBManager.instance().saveHOColumnModel(tableModel);
	}

	public final void setSpieler(int playerId) {
		final int index = tableSorter.getRow4Spieler(playerId);

		if (index >= 0) {
			this.setRowSelectionInterval(index, index);
		}
	}

	@Override
	public final void reInit() {
		initModel();
		repaint();
	}

	public final void reInitModel() {
		((PlayerOverviewModel) getSorter().getModel()).reInitData();
	}

	public final void reInitModelHRFComparison() {
		((PlayerOverviewModel) getSorter().getModel()).reInitDataHRFComparison();
	}

	@Override
	public final void refresh() {
		reInitModel();
		repaint();
	}

	public final void refreshHRFComparison() {
		reInitModelHRFComparison();
		repaint();
	}

	/**
	 * Returns the sorting column.
	 */
	private int getSortSpalte() {
		return switch (UserParameter.instance().standardsortierung) {
			case UserParameter.SORT_NAME -> tableModel.getPositionInArray(UserColumnFactory.NAME);
			case UserParameter.SORT_AUFGESTELLT -> tableModel.getPositionInArray(UserColumnFactory.LINEUP);
			case UserParameter.SORT_GRUPPE -> tableModel.getPositionInArray(UserColumnFactory.GROUP);
			case UserParameter.SORT_BEWERTUNG -> tableModel.getPositionInArray(UserColumnFactory.RATING);
			default -> tableModel.getPositionInArray(UserColumnFactory.BEST_POSITION);
		};
	}

	/**
	 * Initialises the model.
	 */
	private void initModel() {
		setOpaque(false);

		if (tableModel == null) {
			tableModel = UserColumnController.instance().getPlayerOverviewModel();
			tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
			tableSorter = new TableSorter(tableModel,
					tableModel.getPositionInArray(UserColumnFactory.ID),
					getSortSpalte(),
					tableModel.getPositionInArray(UserColumnFactory.NAME)
			);

			ToolTipHeader header = new ToolTipHeader(getColumnModel());
			header.setToolTipStrings(tableModel.getTooltips());
			header.setToolTipText("");
			setTableHeader(header);

			setModel(tableSorter);

			TableColumnModel tableColumnModel = getColumnModel();
			for (int i = 0; i < tableModel.getColumnCount(); i++) {
				tableColumnModel.getColumn(i).setIdentifier(i);
			}

			int[][] targetColumn = tableModel.getColumnOrder();

			// Sort according to [x][1]
			targetColumn = Helper.sortintArray(targetColumn, 1);

			if (targetColumn != null) {
				for (int[] ints : targetColumn) {
					this.moveColumn(getColumnModel().getColumnIndex(ints[0]), ints[1]);
				}
			}

			tableSorter.addMouseListenerToHeaderInTable(this);
			tableModel.setColumnsSize(getColumnModel());
		} else {
			// Set new value.
			tableModel.setValues(HOVerwaltung.instance().getModel().getCurrentPlayers());
			tableSorter.reallocateIndexes();
		}

		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setRowSelectionAllowed(true);
		tableSorter.initsort();
	}
}
