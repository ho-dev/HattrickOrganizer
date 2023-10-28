/*
 * MatchPredictionSpieleTableModel.java
 *
 * Created on 4. Januar 2005, 13:19
 */
package core.prediction;

import core.prediction.engine.MatchResult;

import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

public abstract class AbstractMatchTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -7810048787047274663L;
	
	//~ Instance fields ----------------------------------------------------------------------------
	protected Object[][] m_clData;
	protected MatchResult matchResult;
	public abstract String[] getColumnNames();
	protected abstract void initData();	
	private boolean isHomeMatch = true;
	//~ Constructors -------------------------------------------------------------------------------
	/**
	 * Creates a new MatchPredictionSpieleTableModel object.
	 */
	public AbstractMatchTableModel(MatchResult matchresults, boolean ishome) {
		this.matchResult = matchresults;
		isHomeMatch = ishome;
		initData();
	}

	//~ Methods ------------------------------------------------------------------------------------
	@Override
	public final boolean isCellEditable(int row, int col) {
		return false;
	}

	@Override
	public final Class<?> getColumnClass(int columnIndex) {
		final Object obj = getValueAt(0, columnIndex);

		if (obj != null) {
			return obj.getClass();
		} 
		
		return "".getClass();
		
	}

	//-----Zugriffsmethoden----------------------------------------        
	public final int getColumnCount() {
		return getColumnNames().length;
	}

	@Override
	public final String getColumnName(int columnIndex) {
		if ((getColumnNames() != null) && (getColumnNames().length > columnIndex)) {
			return getColumnNames()[columnIndex];
		} 
		return null;
	}

	public final int getRowCount() {
		if (m_clData != null) {
			return m_clData.length;
		} 
		return 0;
	}

	public final Object getValue(int row, String columnName) {
		if ((getColumnNames() != null) && (m_clData != null)) {
			int i = 0;

			while ((i < getColumnNames().length) && !getColumnNames()[i].equals(columnName)) {
				i++;
			}

			return m_clData[row][i];
		} 
		return null;
	}

	@Override
	public final void setValueAt(Object value, int row, int column) {
		m_clData[row][column] = value;
	}

	public final Object getValueAt(int row, int column) {
		if (m_clData != null) {
			return m_clData[row][column];
		}
		return null;
	}

	/**
	 * Matches neu setzen
	 */
	public final void setValues(MatchResult matchresults) {
		this.matchResult = matchresults;
		initData();
	}

	//-----initialisierung-----------------------------------------

	protected JProgressBar getProgressBar(double value) {
		JProgressBar bar = new JProgressBar(0, 100);
		bar.setStringPainted(true);
		bar.setValue((int) (value * 100));
		return bar;
	}
	public boolean isHomeMatch() {
		return isHomeMatch;
	}
}