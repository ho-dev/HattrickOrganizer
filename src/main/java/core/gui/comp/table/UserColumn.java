package core.gui.comp.table;

import core.model.HOVerwaltung;
import javax.swing.table.TableColumn;


public abstract class UserColumn {



    //~ Instance fields ----------------------------------------------------------------------------
	/** unique column id **/
	protected int id;
	
	/** columnName properties representation, not display!! **/
	protected String columnName;
	
	/** tooltip properties representation, not display!! **/
	protected String tooltip;
	
	/** mininmum width of the column **/
	protected int minWidth;
	
	/** preferred width of the column **/
	protected int preferredWidth;
	
	/** index of the column in the JTable. position**/
	protected int index = 0;
	
	/** if a column is shown in the jtable. Only displayed columns are saved in db**/
	protected boolean display = false;

	protected UserColumn(int id,String name, String tooltip){
		this.id = id;
		columnName = name;
		this.tooltip = tooltip;
	}

	protected UserColumn(int id,String name){
		this(id,name,name);
	}
	
	/**
	 * returns the language dependency name of the column
	 * @return String
	 */
	public final String getColumnName() {
		return (columnName.equals("TSI") || columnName.equals(" "))?columnName:HOVerwaltung.instance().getLanguageString(columnName);
	}
	
	/**
	 * return  id
	 * @return int
	 */
	public final int getId() {
		return id;
	}
	
	/**
	 * returns the language dependency tooltip of the column
	 * @return
	 */
	public final String getTooltip() {
		return (columnName.equals("TSI") || tooltip.equals(" "))?tooltip:HOVerwaltung.instance().getLanguageString(tooltip);
	}
	
	/**
	 * Should a column be shown
	 * @return boolean
	 */
	public boolean isDisplay() {
		return display;
	}

	/**
	 * set a column to be showed
	 * @param display
	 */
	public final void setDisplay(boolean display) {
		this.display = display;
		if (!display)
			index = 0;
	}

	/**
	 * return the currently index of column
	 * only actual if user don´t move the column !!
	 * @return int
	 */
	public final int getIndex() {
		return index;
	}

	/**
	 * set index
	 * if columnModel should be saved index will set, or column is loaded
	 * @param index
	 */
	public final void setIndex(int index) {
		this.index = index;
	}

	/**
	 * String representation
	 * use in UserColumnsPanel in OptionsPanel
	 */
	@Override
	public String toString(){
		return getTooltip();
	}

	/**
	 * Some columns must be displayed, so some columns are not editable
	 * @return boolean
	 */
	public boolean isEditable(){
		return true;
	}

	/**
	 * set minWidth and prefWidth in the TableColumn
	 * @param column
	 */
	public void setSize(TableColumn column){
		column.setMinWidth(minWidth);
		column.setPreferredWidth(preferredWidth);
	}
	
	/**
	 * set preferredWidth for saving to DB
	 * @param width
	 */
	public void setPreferredWidth(int width){
		preferredWidth = width;
	}
	
	public int getPreferredWidth(){
		return preferredWidth;
	}
}
