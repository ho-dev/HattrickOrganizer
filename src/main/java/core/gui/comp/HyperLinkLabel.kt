package core.gui.comp;

import core.gui.Credits;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import core.util.BrowserLauncher;
import core.util.HOLogger;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JLabel;

public class HyperLinkLabel extends JLabel {

	private static final Color LINK_COLOR = ThemeManager.getColor(HOColorName.LINK_LABEL_FG);
	private String url;

	public HyperLinkLabel() {
		init();
	}

	public HyperLinkLabel(String text, String url) {
		this();
		this.url = url;
		setText(text);
	}

	public HyperLinkLabel( String url) {
		this();
		this.url = url;
		setText(url);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private void init() {
		Map<TextAttribute, Object> map = new HashMap<>();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		Font font = getFont().deriveFont(map);
		setFont(font);
		setForeground(LINK_COLOR);

		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					BrowserLauncher.openURL(HyperLinkLabel.this.url);
				} catch (Exception ex) {
					HOLogger.instance().log(Credits.class, ex);
				}
			}
		});
	}

}
