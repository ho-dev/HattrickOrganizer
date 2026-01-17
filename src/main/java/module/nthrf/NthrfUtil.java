package module.nthrf;

import core.file.ExampleFileFilter;
import core.file.xml.XMLManager;
import core.model.TranslationFacility;
import core.net.Connector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NthrfUtil {

    private NthrfUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * TODO
     * @return success of the operation
     */
    public static String createNthrf(long teamId) {
        try {
            Connector dh = Connector.instance();
            NthrfConvertXml2Hrf x2h = new NthrfConvertXml2Hrf();

            var hrf = x2h.createHrf(teamId, dh);
            if ( hrf.isEmpty()) return "";

            JFileChooser fileChooser = new JFileChooser();

            final String fname = "/nt_"+teamId+"_"+new SimpleDateFormat("yyyyMMdd_HHmm").format(new Date())+".hrf";
            final File path = new File(core.model.UserParameter.instance().hrfImport_HRFPath);
            File file = new File(core.model.UserParameter.instance().hrfImport_HRFPath + File.separator + fname);
            fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
            fileChooser.setDialogTitle(TranslationFacility.tr("ls.button.save"));

            fileChooser.setFileFilter(new ExampleFileFilter("hrf"));
            try {
                if (path.exists() && path.isDirectory()) {
                    fileChooser.setCurrentDirectory(path);
                }
            } catch (Exception e) {
            }

            fileChooser.setSelectedFile(file);

            final int returnVal = fileChooser.showSaveDialog(MainPanel.getInstance());

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file = fileChooser.getSelectedFile();
            } else {
                file = null;
            }

            // TODO: overwrite question on existing file:
//            if (file.exists()) {
//				...
//			}

            if (file != null) {
                x2h.writeHRF(file, hrf);
                debug("wrote file " + file.getAbsolutePath());
                // save folder setting
                core.model.UserParameter.instance().hrfImport_HRFPath = file.getParentFile().getAbsolutePath();
            } else {
                debug("Could not write file, nothing selected!");
                return "";
            }

            return hrf;
        } catch (Exception e) {
            debug("Error: " + e);
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get the teamIDs and names of all national teams of the authenticated manager.
     */
    public static List<String[]> getNtTeams() {
    	List<String[]> ret = new ArrayList<>();
        try {
            String xmldata = Connector.instance().getHattrickXMLFile("/chppxml.axd?file=team");
            final Document doc = XMLManager.parseString(xmldata);
            Element ele;
            Element root;
            Element nt;

            if (doc == null) {
                return null;
            }
            root = doc.getDocumentElement();
            root = (Element) root.getElementsByTagName("User").item(0);
            root = (Element) root.getElementsByTagName("NationalTeamCoach").item(0);
            try {
            	int length = root.getElementsByTagName("NationalTeam").getLength();
            	for (int m=0; m<length; m++) {
            		nt = (Element) root.getElementsByTagName("NationalTeam").item(m);
            		ele = (Element) nt.getElementsByTagName("NationalTeamID").item(0);
            		Element eName = (Element) nt.getElementsByTagName("NationalTeamName").item(0);
            		String tid =XMLManager.getFirstChildNodeValue(ele);
            		String name = XMLManager.getFirstChildNodeValue(eName);
            		if (tid != null && !tid.isEmpty()) {
						ret.add(new String[] { tid, name });
					}
            	}
            } catch (Exception x) {
                /* nothing */
            	x.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Get the countryId according to a nativeLeagueId of a player.
     */
    public static int getCountryId(int nativeLeagueId, HashMap<Integer, Integer> countryMapping) {
        int ret = countryMapping.get(nativeLeagueId);
        if (ret > 0) {
            return ret;
        }
        return nativeLeagueId;
    }

    private static void debug(String txt) {
        System.out.println("Nthrf: " + txt);
    }
}
