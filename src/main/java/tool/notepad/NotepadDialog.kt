package tool.notepad;

import core.gui.comp.panel.ImagePanel;
import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import core.model.HOVerwaltung;
import core.util.HOLogger;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class NotepadDialog extends JDialog implements ActionListener{

	private static final long serialVersionUID = 7998858836260564800L;
	
	private final int dialogWidth = 480;
	private final int dialogHeight = 320;
	private final JTextArea textArea = new JTextArea();
	private File file = null;
	private final JButton cmdSave	 = new JButton(ThemeManager.getIcon(HOIconName.DISK));
	
	
	public NotepadDialog(JFrame owner, String title){
		super(owner,title);
		initialize();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	private void initialize(){
		file = new File(HOVerwaltung.instance().getModel().getBasics().getTeamId()+"_note.txt");
		readFile();
        int with = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
                                            .getWidth();
        int height = (int) GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds()
	                                              .getHeight();
	    setLocation((with - dialogWidth) / 2, (height - dialogHeight) / 2);
	    setSize(dialogWidth, dialogHeight);

		cmdSave.setPreferredSize(new Dimension(25,25));
		cmdSave.setBackground(ThemeManager.getColor(HOColorName.BUTTON_BG));
		cmdSave.addActionListener( this );
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(getButtonPanel(), BorderLayout.NORTH);
		getContentPane().add(new JScrollPane(textArea),BorderLayout.CENTER);
	}
	
	private JPanel getButtonPanel(){
		JPanel panel = new ImagePanel();
		panel.setLayout(new FlowLayout(FlowLayout.LEFT));
		panel.add(cmdSave);
		return panel;
	}

	public void actionPerformed(ActionEvent arg0) {
		if(arg0.getSource() == cmdSave){
			write();
		}
		
	}
	
	protected void readFile(){
        try {
        	if(!file.exists())
        		return;
            BufferedReader in = new BufferedReader(new FileReader(file));
            StringBuffer txt = new StringBuffer();
            String line;
            while((line = in.readLine()) != null){
                txt.append(line+"\n");
            }
            textArea.setText(txt.toString());
        } catch (Exception e) {
            HOLogger.instance().log(getClass(),e);
        }
    }

	private void write(){
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(textArea.getText());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception innerEx) {
            HOLogger.instance().log(getClass(),innerEx);
        }
	}
}
