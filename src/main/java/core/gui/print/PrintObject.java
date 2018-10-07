// %982440500:de.hattrickorganizer.gui.print%
package core.gui.print;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.JPanel;


abstract class PrintObject extends JPanel implements Printable {
	
	private static final long serialVersionUID = 5459294801833127527L;
	
    protected PageFormat pf;

    // Druckbereich (obere linke Ecke, Breite, Höhe)
    protected double dh;
    protected double dw;
    protected double dx;
    protected double dy;

    // Seitenformat
    protected double ph;
    protected double pw;

    // Seitenrand
    protected int sr;

    public PrintObject(PageFormat pf) {
        // Werte übernehmen
        this.pf = pf;

        // Papiergrösse
        pw = pf.getWidth();
        ph = pf.getHeight();

        // Druckbereich
        dx = pf.getImageableX() - sr;
        dy = pf.getImageableY();
        dh = pf.getImageableHeight();
        dw = pf.getImageableWidth();

        // Grösse des Panels festlegen
        setPreferredSize(new Dimension((int) ((pw + (2 * sr)) /*zoomfaktor*/),
                                       (int) ((ph + (2 * sr)) /*zoomfaktor*/)));
    }


    public final int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
      throws PrinterException   {
        final Graphics2D g2 = (Graphics2D) graphics;

        // Ausgabebereich festlegen
        g2.translate((int) dx, (int) dy);
        g2.setClip(0, 0, (int) dw, (int) dh);

        // Grafik ausgeben
        paintMe(g2);
        return Printable.PAGE_EXISTS;
    }

    protected abstract void paintMe(Graphics2D g2);
}
