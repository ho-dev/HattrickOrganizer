package core.playeravatars;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JProgressBar;


public class Test {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JProgressBar bar = new JProgressBar();
        bar.setEnabled(true);

        bar.setBackground(Color.YELLOW);
        bar.setForeground(Color.GREEN);

        bar.setStringPainted(true);
        bar.setString("2000 g");
        bar.setValue(65);
        frame.setLayout(new BorderLayout());
        frame.add(bar, BorderLayout.CENTER);
        frame.setSize(500, 400);
        frame.setVisible(true);
    }


}
