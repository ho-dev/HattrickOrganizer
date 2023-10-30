package module.playerOverview;

import com.github.weisj.darklaf.LafManager;
import core.db.DBManager;
import core.db.user.UserManager;
import core.gui.theme.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.util.Map;

public class RemoveGruppenPanelTest {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.install();

            setEnvVariable("AppData",
                    new File("").getAbsoluteFile().getParentFile().getAbsolutePath());

            UserManager.INSTANCE.getCurrentUser();
            DBManager.INSTANCE.loadUserParameter();
            ThemeManager.instance().setCurrentTheme();
            JFrame frame = new JFrame();

            JPanel content = new JPanel();
            content.setLayout(new BorderLayout());

            RemoveGruppenPanel panel = new RemoveGruppenPanel(null);
            content.add(panel, BorderLayout.CENTER);

            frame.setContentPane(content);
            frame.pack();

            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.setVisible(true);
        });
    }

    private static void setEnvVariable(String name, String value) {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(name, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}