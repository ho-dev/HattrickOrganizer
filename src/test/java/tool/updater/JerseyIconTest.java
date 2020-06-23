package tool.updater;

import com.github.weisj.darklaf.LafManager;
import core.gui.theme.ImageUtilities;
import core.model.player.IMatchRoleID;

import javax.swing.*;
import java.awt.*;

public class JerseyIconTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            JFrame frame = new JFrame();

            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());

            Icon icon = ImageUtilities.getJerseyIcon(IMatchRoleID.keeper, (byte) 0, 49);
            JLabel label = new JLabel(ImageUtilities.getScaledIcon(icon, 100, 100));

            content.add(label);
            content.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

            frame.setContentPane(content);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setVisible(true);
        });
    }
}
