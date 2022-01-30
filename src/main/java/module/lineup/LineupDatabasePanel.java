package module.lineup;

import core.db.DBManager;
import core.gui.Refreshable;
import core.model.HOVerwaltung;
import core.model.UserParameter;
import core.model.enums.MatchType;
import core.model.match.MatchKurzInfo;
import core.model.match.MatchLineupTeam;
import core.util.Helper;
import module.teamAnalyzer.ui.MatchComboBoxRenderer;
import module.teamAnalyzer.vo.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Objects;

import static module.lineup.LineupPanel.TITLE_FG;

public class LineupDatabasePanel extends JPanel implements Refreshable {
    private final int MAX_PREVIOUS_LINEUP = 10;

    private LineupPanel lineupPanel;
    private JComboBox<Team> m_jcbLoadLineup;
    private JCheckBox includeHTIntegrated;
    private JCheckBox includeTemplates;
    private JTextField templateName;
    private JButton store;

    private ArrayList<MatchKurzInfo> previousPlayedMatches = null;
    private ArrayList<MatchLineupTeam> templateLineups = null;

    public LineupDatabasePanel(LineupPanel parent) {
        lineupPanel = parent;
        initComponents();
        core.gui.RefreshManager.instance().registerRefreshable(this);
    }

    private void initComponents() {
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints gbc = new GridBagConstraints();

        setLayout(layout);

        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_START;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.load_lineup"));

        gbc.gridx = 1;
        m_jcbLoadLineup = new JComboBox<>();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        m_jcbLoadLineup.setRenderer(new MatchComboBoxRenderer(MatchComboBoxRenderer.RenderType.TYPE_2));
        layout.setConstraints(m_jcbLoadLineup, gbc);
        add(m_jcbLoadLineup);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.fill = GridBagConstraints.NONE;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.includeHTO"));
        gbc.gridx = 1;
        includeHTIntegrated = new JCheckBox();
        includeHTIntegrated.setSelected(UserParameter.instance().includeHTOLineups);
        layout.setConstraints(includeHTIntegrated, gbc);
        add(includeHTIntegrated);

        gbc.gridx = 0;
        gbc.gridy++;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.templates"));
        gbc.gridx = 1;
        includeTemplates = new JCheckBox();
        includeTemplates.setSelected(UserParameter.instance().includeLineupTemplates);
        layout.setConstraints(includeTemplates, gbc);
        add(includeTemplates);

        gbc.gridx = 0;
        gbc.gridy++;
        addLabel(gbc, layout, Helper.getTranslation("ls.module.lineup.store.template"));
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        templateName = new JTextField(getTemplateDefaultName());
        layout.setConstraints(templateName, gbc);
        add(templateName);

        gbc.gridy++;
        store = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.save"));
        layout.setConstraints(store, gbc);
        add(store);

        addListeners();
    }

    private void addListeners() {
        m_jcbLoadLineup.addActionListener(e -> adoptLineup());
        includeHTIntegrated.addActionListener(e -> update_jcbLoadLineup(false));
        includeTemplates.addActionListener(e -> update_jcbLoadLineup(false));
        store.addActionListener(e->storeTemplate());
    }

    private void removeListeners() {
        for (ActionListener al : m_jcbLoadLineup.getActionListeners()) {
            m_jcbLoadLineup.removeActionListener(al);
        }
        for (ActionListener al : includeTemplates.getActionListeners()) {
            includeTemplates.removeActionListener(al);
        }
        for (ActionListener al : includeHTIntegrated.getActionListeners()) {
            includeHTIntegrated.removeActionListener(al);
        }
        for (ActionListener al : store.getActionListeners()) {
            store.removeActionListener(al);
        }
    }

    private void addLabel(GridBagConstraints constraints, GridBagLayout layout, String sLabel) {
        JLabel label = new JLabel(sLabel);
        label.setForeground(TITLE_FG);
        label.setFont(getFont().deriveFont(Font.BOLD));
        label.setHorizontalAlignment(SwingConstants.LEFT);
        layout.setConstraints(label, constraints);
        add(label);
    }

    @Override
    public void refresh() {
        removeListeners();
        update_jcbLoadLineup();
        addListeners();
    }

    @Override
    public void reInit() {
        refresh();
    }

    private void update_jcbLoadLineup(){
        update_jcbLoadLineup(true);
    }

    private void update_jcbLoadLineup(boolean bForceRefresh) {

        boolean refreshMatchlist=false;
        if ( UserParameter.instance().includeHTOLineups != includeHTIntegrated.isSelected()){
            refreshMatchlist=true;
            UserParameter.instance().includeHTOLineups = includeHTIntegrated.isSelected();
        }
        boolean refreshTemplates=false;
        if ( UserParameter.instance().includeLineupTemplates != includeTemplates.isSelected() ){
            refreshTemplates=true;
            UserParameter.instance().includeLineupTemplates=includeTemplates.isSelected();
        }

        m_jcbLoadLineup.removeAllItems();
        Team oTeam;
        if (previousPlayedMatches == null || bForceRefresh || refreshMatchlist) {
            previousPlayedMatches = DBManager.instance().getOwnPlayedMatchInfo(MAX_PREVIOUS_LINEUP, !includeHTIntegrated.isSelected());
        }
        if (includeTemplates.isSelected() && (templateLineups == null || bForceRefresh || refreshTemplates)) {
            templateLineups = DBManager.instance().loadTemplateMatchLineupTeams();
        }

        m_jcbLoadLineup.addItem(null);
        int select = 0;
        int i = 1;
        if ( includeTemplates.isSelected()) {
            for (var team : templateLineups) {
                oTeam = new Team();
                oTeam.setName(team.getTeamName());
                oTeam.setTeamId(team.getTeamID());
                oTeam.setMatchType(MatchType.NONE);
                oTeam.setMatchID(-1);
                m_jcbLoadLineup.addItem(oTeam);
                if (Objects.equals(team.getTeamName(), templateName.getText())) select=i;
                i++;
            }
        }

        for (MatchKurzInfo match : previousPlayedMatches) {
            oTeam = new Team();
            if (match.getHomeTeamID() == HOVerwaltung.instance().getModel().getBasics().getTeamId()) {
                oTeam.setName(match.getGuestTeamName());
                oTeam.setTeamId(match.getGuestTeamID());
                oTeam.setHomeMatch(true);
            } else {
                oTeam.setName(match.getHomeTeamName());
                oTeam.setTeamId(match.getHomeTeamID());
                oTeam.setHomeMatch(false);
            }
            oTeam.setTime(match.getMatchDateAsTimestamp());
            oTeam.setMatchType(match.getMatchTypeExtended());
            oTeam.setMatchID(match.getMatchID());
            m_jcbLoadLineup.addItem(oTeam);
            i++;
        }
        m_jcbLoadLineup.setMaximumRowCount(i);
        m_jcbLoadLineup.setSelectedIndex(select);
    }

    private void adoptLineup() {
        if (m_jcbLoadLineup.getSelectedItem() != null) {
            lineupPanel.setAssistantGroupFilter(false);
            var team = (Team) m_jcbLoadLineup.getSelectedItem();
            int teamId;
            if (isMatchTeam(team)) {
                teamId = HOVerwaltung.instance().getModel().getBasics().getTeamId();
                templateName.setText(getTemplateDefaultName());
            } else {
                templateName.setText(team.getName());
                teamId = team.getTeamId();
            }

            var matchLineupTeam = DBManager.instance().loadMatchLineupTeam(team.getMatchType().getMatchTypeId(), team.getMatchID(), teamId);
            if (matchLineupTeam != null) {
                HOVerwaltung.instance().getModel().setLineup(matchLineupTeam);
            }

            lineupPanel.update();
        } else {
            templateName.setText(getTemplateDefaultName());
        }
    }

    private boolean isMatchTeam(Team team) {
        return team.getTeamId() >= 0 || team.getMatchID() >= 0 || team.getMatchType() != MatchType.NONE;
    }

    private void storeTemplate() {
        var team = (Team) m_jcbLoadLineup.getSelectedItem();
        int templateId;
        boolean isNewTemplate = team==null || isMatchTeam(team);
        if (isNewTemplate) {
            templateId = DBManager.instance().getTemplateMatchLineupTeamNextNumber();
        } else {
            templateId = team.getTeamId();
        }
        var name = templateName.getText();
        var lineupTeam = new MatchLineupTeam(MatchType.NONE, -1, name, templateId, 0);
        lineupTeam.setLineup(HOVerwaltung.instance().getModel().getLineupWithoutRatingRecalc());
        DBManager.instance().storeMatchLineupTeam(lineupTeam);

        if ( isNewTemplate) {
            templateDefaultName=null;
            this.update_jcbLoadLineup();
        }
    }

    private String templateDefaultName=null;
    private String getTemplateDefaultName() {
        if ( templateDefaultName==null){
            templateDefaultName = HOVerwaltung.instance().getLanguageString("ls.module.lineup.template") + DBManager.instance().getTemplateMatchLineupTeamNextNumber();
        }
        return templateDefaultName;
    }
}
