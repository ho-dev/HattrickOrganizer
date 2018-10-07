package core.module.config;

import core.gui.comp.panel.ImagePanel;
import core.model.HOVerwaltung;
import core.module.IModule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class ModuleConfigDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = -6012059855852713150L;
	private IModule module;
	JButton okButton;
//	JButton cancelButton;

	public ModuleConfigDialog(JDialog owner, IModule module){
		super(owner,module.getDescription());
		this.module = module;
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		initialize();
	}


	private void initialize() {
		setSize(300,500);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(new JScrollPane(module.createConfigPanel()),BorderLayout.CENTER);
		getContentPane().add(createButtons(),BorderLayout.SOUTH);
	}


	@Override
	public void setSize(int width, int height) {
	   super.setSize(width, height);

	   Dimension screenSize = getParent().getSize();
	   int x = (screenSize.width - getWidth()) / 2;
	   int y = (screenSize.height - getHeight()) / 2;

	   setLocation(getParent().getX()+x, getParent().getY()+y);
	}


    private JPanel createButtons() {
        JPanel buttonPanel = new ImagePanel();
        ((FlowLayout) buttonPanel.getLayout()).setAlignment(FlowLayout.CENTER);

        okButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.save"));
        okButton.addActionListener(this);

//        cancelButton = new JButton(HOVerwaltung.instance().getLanguageString("ls.button.cancel"));
//        cancelButton.addActionListener(this);

        buttonPanel.add(okButton);
       // buttonPanel.add(cancelButton);
        return buttonPanel;
    }


	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == okButton){
			ModuleConfig.instance().save();
			dispose();
		}

	}
}
