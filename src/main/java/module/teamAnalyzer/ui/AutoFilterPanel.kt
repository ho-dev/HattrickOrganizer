package module.teamAnalyzer.ui;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import module.teamAnalyzer.vo.Filter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

public class AutoFilterPanel extends JPanel implements ActionListener, KeyListener {
    //~ Instance fields ----------------------------------------------------------------------------
	private JCheckBox awayGames = new JCheckBox();
    private JCheckBox cup = new JCheckBox();
    private JCheckBox defeat = new JCheckBox();
    private JCheckBox draw = new JCheckBox();
    private JCheckBox friendly = new JCheckBox();
    private JCheckBox tournament = new JCheckBox();

    //  Filter filter = SystemManager.getFilter();
    private JCheckBox homeGames = new JCheckBox();
    private JCheckBox league = new JCheckBox();
    private JCheckBox qualifier = new JCheckBox();
    private JCheckBox masters = new JCheckBox();
    private JCheckBox win = new JCheckBox();
    private NumberTextField number = new NumberTextField(2);

    //~ Constructors -------------------------------------------------------------------------------

    /**
     * Creates a new instance of AutoFilterPanel
     */
    public AutoFilterPanel() {
        jbInit();
    }

    //~ Methods ------------------------------------------------------------------------------------
    public void reload() {
    	Filter filter = TeamAnalyzerPanel.filter;
    	filter.loadFilters();
        homeGames.setSelected(filter.isHomeGames());
        awayGames.setSelected(filter.isAwayGames());
        win.setSelected(filter.isWin());
        draw.setSelected(filter.isDraw());
        defeat.setSelected(filter.isDefeat());
        number.setText(String.valueOf(filter.getNumber()));
        league.setSelected(filter.isLeague());
        cup.setSelected(filter.isCup());
        qualifier.setSelected(filter.isQualifier());
        friendly.setSelected(filter.isFriendly());
        tournament.setSelected(filter.isTournament());
        masters.setSelected(filter.isMasters());
    }

    protected void setFilter() {
    	Filter filter = TeamAnalyzerPanel.filter;
    	filter.setAwayGames(awayGames.isSelected());
    	filter.setHomeGames(homeGames.isSelected());
    	filter.setWin(win.isSelected());
    	filter.setDefeat(defeat.isSelected());
    	filter.setDraw(draw.isSelected());
    	filter.setLeague(league.isSelected());
    	filter.setCup(cup.isSelected());
    	filter.setFriendly(friendly.isSelected());
    	filter.setQualifier(qualifier.isSelected());
    	filter.setTournament(tournament.isSelected());
    	filter.setMasters(masters.isSelected());
    	filter.setNumber(number.getValue());
    	filter.saveFilters();
    	
    }
    
    /**
     * Handle action events.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
    	setFilter();
    }

    private void jbInit() {
    	Filter filter = TeamAnalyzerPanel.filter;
        JPanel main = new ImagePanel();
        main.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));


        main.setLayout(new BorderLayout());
        setLayout(new BorderLayout());
        setOpaque(false);

        JPanel filters = new ImagePanel();

        filters.setLayout(new GridLayout(12, 2));
        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Home_Games"))); //$NON-NLS-1$
        homeGames.setSelected(filter.isHomeGames());
        homeGames.setOpaque(false);
        homeGames.addActionListener(this);
        filters.add(homeGames);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Away_Games"))); //$NON-NLS-1$
        awayGames.setSelected(filter.isAwayGames());
        awayGames.setOpaque(false);
        awayGames.addActionListener(this);
        filters.add(awayGames);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Win_Games"))); //$NON-NLS-1$
        win.setSelected(filter.isWin());
        win.setOpaque(false);
        win.addActionListener(this);
        filters.add(win);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Draw_Games"))); //$NON-NLS-1$
        draw.setSelected(filter.isDraw());
        draw.setOpaque(false);
        draw.addActionListener(this);
        filters.add(draw);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Defeat_Games"))); //$NON-NLS-1$
        defeat.setSelected(filter.isDefeat());
        defeat.setOpaque(false);
        defeat.addActionListener(this);
        filters.add(defeat);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.LeagueGame"))); //$NON-NLS-1$
        league.setSelected(filter.isLeague());
        league.setOpaque(false);
        league.addActionListener(this);
        filters.add(league);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.CupGame"))); //$NON-NLS-1$
        cup.setSelected(filter.isCup());
        cup.setOpaque(false);
        cup.addActionListener(this);
        filters.add(cup);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.FriendlyGame"))); //$NON-NLS-1$
        friendly.setSelected(filter.isFriendly());
        friendly.setOpaque(false);
        friendly.addActionListener(this);
        filters.add(friendly);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.QualifierGame"))); //$NON-NLS-1$
        qualifier.setSelected(filter.isQualifier());
        qualifier.setOpaque(false);
        qualifier.addActionListener(this);
        filters.add(qualifier);
        
        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.MastersGame"))); //$NON-NLS-1$
        masters.setSelected(filter.isQualifier());
        masters.setOpaque(false);
        masters.addActionListener(this);
        filters.add(masters);
        
        
        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.TournamentGame"))); //$NON-NLS-1$
        tournament.setSelected(filter.isTournament());
        tournament.setOpaque(false);
        tournament.addActionListener(this);
        filters.add(tournament);

        filters.add(new JLabel(HOVerwaltung.instance().getLanguageString("AutoFilterPanel.Max_Number"))); //$NON-NLS-1$
        number.setText(filter.getNumber() + "");
        number.addKeyListener(this);
        filters.add(number);

        main.add(filters, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(main);

        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPane);

        reload();
    }
	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		setFilter();
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}
