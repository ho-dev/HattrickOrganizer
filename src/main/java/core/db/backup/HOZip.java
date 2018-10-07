package core.db.backup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class HOZip extends File {
    //~ Instance fields ----------------------------------------------------------------------------
	private static final long serialVersionUID = 2172587062884736633L;
	private ZipOutputStream zOut;
    private int fileCount = 0;

    //~ Constructors -------------------------------------------------------------------------------
    /**
     * Creates a new HOZip object.
     */
    public HOZip(String filename) throws Exception {
        super(filename);

        // If source archive already exists
        //	    if(this.exists()) {
        //	      throw new Exception("Target archive already exists");
        //	    }
        // Open the ZipOutputStream
        zOut = new ZipOutputStream(new FileOutputStream(this));
        zOut.setMethod(ZipOutputStream.DEFLATED);
        zOut.setLevel(5);
    }

    //~ Methods ------------------------------------------------------------------------------------
    public int getFileCount() {
        return fileCount;
    }

    public void addFile(File file) throws Exception {
        FileInputStream tFINS = new FileInputStream(file);
        final int bufLength = 1024;
        byte[] buffer = new byte[bufLength];
        int readReturn = 0;

        // Set next Entry
        zOut.putNextEntry(new ZipEntry(file.getName()));

        do {
            readReturn = tFINS.read(buffer);

            if (readReturn != -1) {
                zOut.write(buffer, 0, readReturn);
            }
        } while (readReturn != -1);

        zOut.closeEntry();
        fileCount++;
    }

    /**
     * Closes the archive if it is still open.
     *
     * @throws Exception
     */
    public void closeArchive() throws Exception {
        if (zOut != null) {
            zOut.finish();
            zOut.close();
        }
    }

    /**
     * Deconstructor
     *
     * @throws Exception
     */
    @Override
	public void finalize() throws Exception {
        if (zOut != null) {
            zOut.finish();
            zOut.close();
        }
    }

	public void addStringEntry(String filename, String data) throws Exception {

		// Set next Entry
		zOut.putNextEntry(new ZipEntry(filename));
		zOut.write(data.getBytes());
		zOut.closeEntry();
		
		fileCount++;
	}
}
