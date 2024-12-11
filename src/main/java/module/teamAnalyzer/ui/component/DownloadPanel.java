// %3943495873:hoplugins.teamAnalyzer.ui.component%
package module.teamAnalyzer.ui.component;

import core.model.TranslationFacility;
import core.model.match.SourceSystem;
import core.net.OnlineWorker;
import module.teamAnalyzer.ui.NumberTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * A panel that allows the user to download a new match from HO
 *
 * @author <a href=mailto:draghetto@users.sourceforge.net>Massimiliano Amato</a>
 */
public class DownloadPanel extends JPanel {

	/** Download Button */
    JButton downloadButton = new JButton(TranslationFacility.tr("ls.button.download"));

    /** Description label */
    JLabel jLabel1 = new JLabel();

    /** Status label */
    JLabel status = new JLabel();

    /** The matchid text field */
    NumberTextField matchId = new NumberTextField(10);

    JRadioButton normal = new JRadioButton(TranslationFacility.tr("NormalMatch"));
    JRadioButton tournament = new JRadioButton(TranslationFacility.tr("TournamentMatch"));
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
        jLabel1.setText(TranslationFacility.tr("ls.match.id"));
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

        downloadButton.addActionListener(e -> {
            int id = matchId.getValue();
            SourceSystem sourceSystem = SourceSystem.HATTRICK;
            if (tournament.isSelected()) {
                sourceSystem = SourceSystem.HTOINTEGRATED;
            }
            if (OnlineWorker.downloadMatchData(id, sourceSystem, false)) {
                status.setText(TranslationFacility.tr("ImportOK"));
                matchId.setText("");
            } else {
                status.setText(TranslationFacility.tr("ImportError"));
            }
        });
    }


}
