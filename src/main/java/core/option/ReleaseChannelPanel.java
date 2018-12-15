package core.option;



import core.gui.comp.panel.ImagePanel;

import java.awt.GridLayout;
import java.awt.Font;

import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;


/**
 * Radio buttons in Release Channel Panel  (preferences)
 */
public final class ReleaseChannelPanel extends ImagePanel
        implements javax.swing.event.ChangeListener, java.awt.event.ItemListener
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final long serialVersionUID = 1L;
    JRadioButton jrb_Stable;
    JRadioButton jrb_Beta;
    JRadioButton jrb_Dev;


    /**
     * Creates a new Release Channel Panel object.
     */
    public ReleaseChannelPanel() {
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    public final void itemStateChanged(java.awt.event.ItemEvent itemEvent) {
        // TODO: manage saving into DB
        if (jrb_Stable.isSelected()){
        core.model.UserParameter.temp().ReleaseChannel = "Stable";}
        else if (jrb_Beta.isSelected()){
            core.model.UserParameter.temp().ReleaseChannel = "Beta";}
        else{
            core.model.UserParameter.temp().ReleaseChannel = "Dev";}
    }

    public void stateChanged(ChangeEvent arg0) {

    }

    private void initComponents() {
        setLayout(new GridLayout(10, 1, 4, 4));

        JLabel jlQuestion = new JLabel(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_pleaseSelect"));
        Font f = jlQuestion.getFont();
        jlQuestion.setFont(f.deriveFont(f.getStyle() ^ Font.BOLD));
        add(jlQuestion);

        jrb_Stable = new JRadioButton("Stable", true);
        jrb_Beta = new JRadioButton("Beta", false);
        jrb_Dev = new JRadioButton("Dev", false);

        ButtonGroup jbg_buttonGroup = new ButtonGroup();
        jbg_buttonGroup.add(jrb_Stable);
        jbg_buttonGroup.add(jrb_Beta);
        jbg_buttonGroup.add(jrb_Dev);


        String sReleaseChannel = core.model.UserParameter.temp().ReleaseChannel;

        switch (core.model.UserParameter.temp().ReleaseChannel) {
            case "Beta":
                jrb_Beta.setSelected(true);
                break;
            case "Dev":
                jrb_Dev.setSelected(true);
                break;
        }

        //Register a listener for the radio buttons.
        jrb_Stable.addItemListener(this);
        jrb_Beta.addItemListener(this);
        jrb_Dev.addItemListener(this);

        add(jrb_Stable);
        add(jrb_Beta);
        add(jrb_Dev);


    }

}