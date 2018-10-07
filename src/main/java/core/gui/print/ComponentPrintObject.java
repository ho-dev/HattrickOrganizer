// %2755357215:de.hattrickorganizer.gui.print%
package core.gui.print;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.print.PageFormat;

import javax.swing.JDialog;


/**
 * Druckt eine Componente und sorgt dafür, dass sie komplett auf die Seite passt
 */
public class ComponentPrintObject extends PrintObject {
	
	private static final long serialVersionUID = -4313912069362326800L;
	
    //~ Static fields/initializers -----------------------------------------------------------------

    public static int SICHTBAR;
    public static int SICHTBARMAXIMIEREN = 3;
    public static int NICHTSICHTBAR = 1;
    public static int NICHTSICHTBARMAXIMIEREN = 2;

    //~ Instance fields ----------------------------------------------------------------------------

    private Component m_clComponent;
    private String m_sTitel = "";
    private int m_iSichtbar;

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Konstruktor
     *
     * @param pageformat PageFormat
     * @param titel Titel des Drucks
     * @param component Component, die gedruckt werden soll
     * @param sichtbar Ist die Componente schon sichtbar, der muss sie noch in einem eigenen Dialog
     *        sichtbar gemacht werden, damit der Druck funktioniert.
     */
    public ComponentPrintObject(PageFormat pageformat, String titel, Component component,
                                int sichtbar) {
        super(pageformat);

        m_clComponent = component;
        m_sTitel = titel;
        m_iSichtbar = sichtbar;
    }

    //~ Methods ------------------------------------------------------------------------------------

    @Override
	protected final void paintMe(java.awt.Graphics2D g2) {
        JDialog dialog = null;

        if (m_iSichtbar != SICHTBAR) {
            dialog = new JDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            //Componente in einem Dialog sichtbar machen, damit sie gedruckt werden kann
            dialog.getContentPane().setLayout(new BorderLayout());
            dialog.getContentPane().add(m_clComponent, BorderLayout.CENTER);

            //Grösse - Nach Componente oder Maximieren?
            if (m_iSichtbar == NICHTSICHTBAR) {
                dialog.setSize(m_clComponent.getPreferredSize());

                //Maximale Papierausnutzung
            } else {
                dialog.setSize(new Dimension((int) dw, (int) dh));
            }

            //Ausserhalb des Sichtbaren Bereichs erstellen!
            try {
                final Toolkit kit = Toolkit.getDefaultToolkit();
                dialog.setLocation(kit.getScreenSize().width, kit.getScreenSize().height);
            } catch (Exception e) {
                //NIX
            }

            dialog.setVisible(true);
        }

        //Grösse der Componente holen
        Dimension dimension = m_clComponent.getSize();

        if (m_iSichtbar == SICHTBARMAXIMIEREN) {
            dimension = new Dimension((int) dw, (int) dh);
        }

        //Titel zeichnen
        g2.drawString(m_sTitel, 0, 10);

        //Druckbereich nach unten verschieben
        g2.translate(0, 15);

        //Skalierung setzten, damit die Componente das Blatt ausfüllt
        final double faktorX = dw / dimension.width;
        final double faktorY = (dh - 15) / dimension.height;
        final double faktor = Math.min(faktorX, faktorY);

        //setZoomfaktor( Math.min( faktorX, faktorY ) );
        g2.scale(faktor, faktor);

        //Componente zeichnen
        m_clComponent.print(g2);

        if (dialog != null) {
            //Dialog wieder verschwinden lassen
            dialog.setVisible(false);
            dialog.dispose();
        }
    }
}
