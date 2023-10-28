package core.datatype;

/**
 * Faktor für Geld mit Id fürs Land
 */
public class GeldFaktorCBItem extends CBItem implements Comparable<GeldFaktorCBItem> {
    //~ Instance fields ----------------------------------------------------------------------------

    private float m_fFaktor;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new GeldFaktorCBItem object.
     */
    public GeldFaktorCBItem(String text, float faktor, int id) {
        super(text, id);
        m_fFaktor = faktor;
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void setFaktor(float faktor) {
        m_fFaktor = faktor;
    }

    public final float getFaktor() {
        return m_fFaktor;
    }

    public final int compareTo(GeldFaktorCBItem obj) {
        
        final GeldFaktorCBItem item = (GeldFaktorCBItem) obj;
        return this.getText().compareTo(item.getText());

    }
}
