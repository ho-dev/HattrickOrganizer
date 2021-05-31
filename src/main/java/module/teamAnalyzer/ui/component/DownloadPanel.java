// %3943495873:hoplugins.teamAnalyzer.ui.component%
package module.teamAnalyzer.ui.component;

import core.db.DBManager;
import core.model.HOVerwaltung;
import core.model.match.MatchType;
import core.model.match.Matchdetails;
import core.net.OnlineWorker;
import module.teamAnalyzer.ui.NumberTextField;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;


/**
 * A panel that allows the user to download a new match from HO
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DownloadPanel extends JPanel {
    //~ Instance fields ----------------------------------------------------------------------------

    /**
	 *
	 */
	private static final long serialVersionUID = -3212179990708350342L;

	String[] matchTypes = {HOVerwaltung.instance().getLanguageString("NormalMatch"),
   		 HOVerwaltung.instance().getLanguageString("TournamentMatch")};


	/** Download Button */
    JButton downloadButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.download"));

    /** Description label */
    JLabel jLabel1 = new JLabel();

    /** Status label */
    JLabel status = new JLabel();

    /** The matchid text field */
    NumberTextField matchId = new NumberTextField(10);

    JRadioButton normal = new JRadioButton(HOVerwaltung.instance().getLanguageString("NormalMatch"));
    JRadioButton tournament = new JRadioButton(HOVerwaltung.instance().getLanguageString("TournamentMatch"));
    ButtonGroup radioGroup = new ButtonGroup();

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Constructs a new instance.
     */
    public DownloadPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------

    /**
     * Initializes the state of this instance.
     */
    private void jbInit() {
        jLabel1.setText(HOVerwaltung.instance().getLanguageString("ls.match.id"));
        setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;

        constraints.gridx = 1;
        constraints.gridy = 1;
        add(jLabel1, constraints);

        constraints.gridx = 2;
        add(matchId, constraints);

        constraints.gridx = 1;
        constraints.gridy = 3;
        normal.setSelected(true);
        radioGroup.add(normal);
        add(normal, constraints);

        constraints.gridy = 4;
        radioGroup.add(tournament);
        add(tournament, constraints);

        constraints.gridy = 6;
        add(downloadButton, constraints);

        constraints.gridy = 7;
        add(status, constraints);

        downloadButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent e) {
                    int id = matchId.getValue();
                    MatchType type = MatchType.LEAGUE;
                    if (tournament.isSelected()) {
                    	type = MatchType.TOURNAMENTGROUP;
                    }

//                    if (id == 0) {
//                        status.setText(HOVerwaltung.instance().getLanguageString("ImportError"));
//
//                        return;
//                    }

                    if (OnlineWorker.downloadMatchData(id, type, false)) {

                    	Matchdetails md = DBManager.instance().loadMatchDetails(type.getId(), id);

	                    if (md.getFetchDatum() != null) {
	                        status.setText(HOVerwaltung.instance().getLanguageString("ImportOK"));
	                        matchId.setText("");
	                    } else {
	                        status.setText(HOVerwaltung.instance().getLanguageString("ImportError"));
	                    }
                    }
                }
            });
    }


}
