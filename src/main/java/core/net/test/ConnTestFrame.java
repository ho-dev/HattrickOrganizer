package core.net.test;

import core.HO;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Visual part of the connectiontest.
 *
 * @author aik
 */
public class ConnTestFrame extends JFrame {

	private static final long serialVersionUID = 9043187286506794399L;
	private ConnTest conntest = null;
	private JTextArea log = null;

	/**
	 * Constructor.
	 */
	public ConnTestFrame(ConnTest conntest) {
		this.conntest = conntest;
		Dimension dim = new Dimension(450, 350);
		setSize(dim);
		buildGui();
	}

	/**
	 * Build the GUI.
	 */
	private void buildGui() {
		setTitle("HO! Connection Test");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.setSize(450, 300);
		JPanel topPanel = createTopPanel();
		contentPane.add(topPanel, "North");
		JComponent mainPanel = createMainPanel();
		contentPane.add(mainPanel, "Center");
		JPanel controlPanel = createControlPane();
		contentPane.add(controlPanel, "South");
		Image image = null;
		try {
			if (HO.isDevelopment()) {
				image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/gui/bilder/Logo-16px_dev.png"));
			}
			else if (HO.isBeta()) {
				image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/gui/bilder/Logo-16px_beta.png"));
			}
			else {
				image = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("/gui/bilder/Logo-16px_stable.png"));
			}

		} catch (Exception e) {
			System.out.println("Error loading icon: " + e.getMessage());
		}
		if (image != null) setIconImage(image);
	}

	/**
	 * Create the center panel.
	 * @return the created panel
	 */
	private JComponent createMainPanel() {
		//JPanel tmpPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane();
		log = new JTextArea("");
		float newSize = log.getFont().getSize2D()-1f;
		log.setFont(log.getFont().deriveFont(newSize));
		scrollPane.getViewport().add(log);
		//return tmpPanel;
		return scrollPane;
	}

	private JPanel createTopPanel() {
		JPanel tmpPanel = new JPanel();
		JLabel label = new JLabel("HO! Connection Test (version " + ConnTest.VERSION + ")");
		label.setFont(label.getFont().deriveFont(label.getFont().getSize2D()+3f));
		tmpPanel.add(label);
        return tmpPanel;
	}


	/**
	 * Build the control pane in the bottom
	 * @param contentPane the new panel is added to this pane
	 */
	private JPanel createControlPane() {
		JPanel ret = new JPanel();
		JButton startBtn = new JButton("Start");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("START");
				conntest.start(log);
			}
		});
		JButton exitBtn = new JButton("Exit");
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("EXIT");
				System.exit(0);
			}
		});
		ret.add(startBtn);
		ret.add(exitBtn);
		return ret;
	}

	/**
	 * React on action performed
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("actionPerformed TODO" + e);
	}
}
