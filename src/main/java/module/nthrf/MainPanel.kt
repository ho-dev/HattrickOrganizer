package module.nthrf;

import core.model.HOVerwaltung;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;



/**
 * Main panel of the Nthrf plugin.
 */
public class MainPanel extends JPanel implements ActionListener {
	static final long serialVersionUID = 1;
	private static MainPanel instance = null;
	private JButton btnStart = null;

	/**
     * Constructs a new instance.
     */
    private MainPanel() {
    	buildGui();
    }

    public static MainPanel getInstance() {
    	if (instance == null) {
    		instance = new MainPanel();
    	}
    	return instance;
    }

    /**
     * Build the necessary GUI components.
     */
    private void buildGui() {
    	setLayout(new BorderLayout());
    	HOVerwaltung hoV = HOVerwaltung.instance();
    	JTextArea ta = new JTextArea();
    	ta.append(hoV.getLanguageString("nthrf.hint1")+"\n");
    	ta.append(hoV.getLanguageString("nthrf.hint2")+"\n");
    	ta.append(hoV.getLanguageString("nthrf.hint3")+"\n");
    	ta.append(hoV.getLanguageString("nthrf.hint4")+" '");
    	ta.append(hoV.getLanguageString("Start")+"' ");
    	ta.append(hoV.getLanguageString("nthrf.hint5"));
    	ta.setEditable(false);
    	add(new JScrollPane(ta), BorderLayout.CENTER);
    	btnStart = new JButton(HOVerwaltung.instance().getLanguageString("Start"));
    	btnStart.addActionListener(this);
    	add(btnStart, BorderLayout.SOUTH);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(btnStart)) {
			debug("START "  + " ...");
			btnStart.setEnabled(false);
			download();
			debug("DONE ");
		}
	}

	/**
	 * Download current NT data and create a HRF file.
	 */
	private boolean download() {
		try {

			List<String[]> teams = NthrfUtil.getNtTeams();
			if (teams == null || teams.size() < 1 || teams.get(0)[0] == null || teams.get(0)[0].length() < 1) {
				return false;
			}
			final long teamId;
			if (teams.size() > 1) {
				NtTeamChooser chooser = new NtTeamChooser(teams);
				chooser.setModal(true);
				chooser.setVisible(true);
				teamId = chooser.getSelectedTeamId();
				System.out.println("Result is: " + chooser.getSelectedTeamId());
				chooser.dispose();
			} else {
				teamId = Long.parseLong(teams.get(0)[0]);
			}
			debug("Compute for team " + teamId);

			if (teamId < 0) {
				btnStart.setEnabled(true);
				return false;
			}
			if (!isAllowedTeam(teamId)) {
				debug("Wrong team (id " + teamId + "), can't continue!");
				return false;
			}
			NthrfUtil.createNthrf(teamId);
			debug("finished");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isAllowedTeam(long teamId) {
		return true; // now allow all team
	}

	private void debug(String txt) {
		System.out.println("Nthrf: " + txt);
	}
}
