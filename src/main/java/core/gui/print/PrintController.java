// %2567997551:de.hattrickorganizer.gui.print%
/*
 * Created on 29.02.2004
 *
 */
package core.gui.print;

import java.awt.print.Book;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;



public final class PrintController {

    private static PrintController printController;

    private Book book;
    private PageFormat pf;
    private PrinterJob job;
    private int page = 1;

    private PrintController() {
        initialize();
    }

    public static PrintController getInstance() {
        if (printController == null) {
            printController = new PrintController();
        }

        return printController;
    }

    public void setPf(PageFormat format) {
        pf = format;
    }

    public PageFormat getPf() {
        return pf;
    }

    public void add(PrintObject printObject) throws Exception {
        book.append(printObject, pf, page);
        page++;
    }

    public void print() throws PrinterException {
        job.setPageable(book);

        final HashPrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();

        //job.pageDialog(attributes);
        if (job.printDialog(attributes)) {
            final core.net.login.LoginWaitDialog waitDialog = new core.net.login.LoginWaitDialog(core.gui.HOMainFrame
                                                                                                                                 .instance());
            waitDialog.setVisible(true);

            job.print(attributes);

            waitDialog.setVisible(false);
        }

        initialize();
    }

    private void initialize() {
        job = PrinterJob.getPrinterJob();
        job.setJobName("HO! - Printing");
        pf = job.defaultPage();
        pf.setOrientation(PageFormat.LANDSCAPE);
        book = new Book();
        page = 1;
    }
}
