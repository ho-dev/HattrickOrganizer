package core.option;

import core.gui.comp.panel.ImagePanel;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.*;
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
    JTextArea  jl_Description;


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
        core.model.UserParameter.temp().ReleaseChannel = "Stable";
            jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_STABLE_desc"));}
        else if (jrb_Beta.isSelected()){
            core.model.UserParameter.temp().ReleaseChannel = "Beta";
            jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_BETA_desc"));}
        else{
            core.model.UserParameter.temp().ReleaseChannel = "Dev";
            jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_DEV_desc"));}
    }

    public void stateChanged(ChangeEvent arg0) {

    }

    private void initComponents() {
        setLayout(new GridLayout(6, 1, 0, 0));

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

        jl_Description = new JTextArea("", 5, 1);
        jl_Description.setLineWrap(true);
        jl_Description.setWrapStyleWord(true);
        jl_Description.setEditable(false);



        switch (core.model.UserParameter.temp().ReleaseChannel) {
            case "Beta":
                jrb_Beta.setSelected(true);
                jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_BETA_desc"));
                break;
            case "Dev":
                jrb_Dev.setSelected(true);
                jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_DEV_desc"));
                break;
            default:
                jl_Description.setText(core.model.HOVerwaltung.instance().getLanguageString("options.release_channels_STABLE_desc"));

        }

        //Register a listener for the radio buttons.
        jrb_Stable.addItemListener(this);
        jrb_Beta.addItemListener(this);
        jrb_Dev.addItemListener(this);

        add(jrb_Stable);
        add(jrb_Beta);
        add(jrb_Dev);

//        add(new JSeparator());

        add(jl_Description);


    }

}