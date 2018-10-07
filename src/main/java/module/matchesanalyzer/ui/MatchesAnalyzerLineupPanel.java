package module.matchesanalyzer.ui;

import core.gui.theme.HOColorName;
import core.gui.theme.HOIconName;
import core.gui.theme.ThemeManager;
import module.matchesanalyzer.data.MatchesAnalyzerLineup;
import module.matchesanalyzer.data.MatchesAnalyzerPlayer;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;


public class MatchesAnalyzerLineupPanel extends JLabel implements Observer {
	private static final long serialVersionUID = 1L;

	private static final Color COLOR_PANEL_BORDERS = ThemeManager.getColor(HOColorName.MATCHESANALYZER_PANELS_BORDER);
	private static final Color COLOR_FIELD_LINES = ThemeManager.getColor(HOColorName.MATCHESANALYZER_FIELD_LINES);
	private static final Color COLOR_FIELD_GRASS = ThemeManager.getColor(HOColorName.MATCHESANALYZER_FILED_GRASS);

	private final static ImageIcon STAR = ThemeManager.getIcon(HOIconName.STAR);
	private final static ImageIcon ARROW_UP = ThemeManager.getIcon(HOIconName.MOVE_UP);
	private final static ImageIcon ARROW_DOWN = ThemeManager.getIcon(HOIconName.MOVE_DOWN);
	private final static ImageIcon ARROW_LEFT = ThemeManager.getIcon(HOIconName.MOVE_RIGHT);
	private final static ImageIcon ARROW_RIGHT = ThemeManager.getIcon(HOIconName.MOVE_LEFT);

	private static final int PANEL_BORDERS_WIDTH = 0;
	private static final int SOCCERFIELD_LINES_WIDTH = 2;
	private static final int SOCCERFIELD_INNER_LINES_XFACTOR = 5;
	private static final int SOCCERFIELD_INNER_LINES_YFACTOR = 3;
	private static final int SOCCERFIELD_OUTTER_LINES_FACTOR = 30;
	private static final int SOCCERFIELD_SMALL_CIRCLES_FACTOR = 50;
	private static final int SOCCERFIELD_MIDFIELD_CIRCLE_FACTOR = 3;
	private static final double SOCCERFIELD_ROTATION_ANGLE = 180.0d;

	private static final int PLAYER_FRAME_SEPARATOR = 3;
	private static final int PLAYER_FRAME_HPADDING = 2;
	private static final int PLAYER_FRAME_VPADDING = 2;
	private static final int PLAYERFRAME_NAME_MAX_LENGTH = 15;
	private static final int PLAYERFRAME_WIDTH_FACTOR = 90;
	private static final int PLAYERFRAME_HEIGHT_FACTOR = 135;
	private static final float PLAYERFRAME_FONTSIZE_FACTOR = 28.0f;

	// MatchesAnalyzerPlayer.Position maps position horizontally from right to left
	private static final int X_ZONES = MatchesAnalyzerPlayer.Position.WINGER.getyFactor() * 2 + 1;
	private static final int Y_ZONES = MatchesAnalyzerPlayer.Position.FORWARD.getxOffset() + 2;

	private final int orientation;
	private MatchesAnalyzerLineup lineup = null;

	public MatchesAnalyzerLineupPanel(int orientation) {
		this.orientation = orientation;
		setBorder(BorderFactory.createLineBorder(COLOR_PANEL_BORDERS, PANEL_BORDERS_WIDTH));
		setOpaque(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Dimension size = getSize();
		int w = getSize().width;
		int h = getSize().height;

		Graphics2D g2d = (Graphics2D)g.create();

		// @todo this is static, optimize it!
		drawHalfSoccerField(g2d, 0, 0, w, h, orientation);

		if(lineup != null) {
			int xFactor = w / X_ZONES;
			int xExtra = w % X_ZONES + xFactor;

			int yFactor = h / Y_ZONES;
			int yExtra = h % Y_ZONES;

			Iterator<MatchesAnalyzerPlayer> it = lineup.iterator();
			while(it.hasNext()) {
				MatchesAnalyzerPlayer player = it.next();
				Point p = player.getPoint();
				int x = (orientation == MatchesAnalyzerPanel.BOTTOMUP ? p.y - 1 : X_ZONES - p.y);
				int y = (orientation == MatchesAnalyzerPanel.BOTTOMUP ? Y_ZONES - p.x - 2 : p.x);
				x = (x * xFactor + xExtra / 2) - 1;
				y = ((y + 1) * yFactor + yExtra / 2) - 1;
				drawPlayer(g2d, player, x, y);
			}
		}
		
		g2d.dispose();
	}

	// @todo it's time to rework the crappy code below!
	private void drawHalfSoccerField(Graphics g, int x, int y, int width, int height, int orientation) {
		Graphics2D g2d = (Graphics2D)g.create();

		// background
		g2d.setColor(COLOR_FIELD_GRASS);
		g2d.fillRect(x, y, width, height);

		y = (orientation == MatchesAnalyzerPanel.BOTTOMUP ? height - y : y);
		height = (orientation == MatchesAnalyzerPanel.BOTTOMUP ? -height : height);

		// border lines
		g2d.setStroke(new BasicStroke(SOCCERFIELD_LINES_WIDTH));
		g2d.setColor(COLOR_FIELD_LINES);
		int xMargin = width / SOCCERFIELD_OUTTER_LINES_FACTOR;
		int yMargin = height / SOCCERFIELD_OUTTER_LINES_FACTOR;
		g2d.drawLine(x + xMargin, y + yMargin, xMargin, height);				// left line
		g2d.drawLine(width - xMargin, y + yMargin, width - xMargin, height);	// right line
		g2d.drawLine(x + xMargin, y + yMargin, width - xMargin, y + yMargin);	// side line
		g2d.drawLine(x + xMargin, y + height, width - xMargin, y + height);		// middle line

		// goalkeeper lines
		int xFactor = width / SOCCERFIELD_INNER_LINES_XFACTOR;
		int yFactor = height / SOCCERFIELD_INNER_LINES_YFACTOR;

		// external
		g2d.drawLine(x + xMargin + xFactor, y + yMargin, x + xMargin + xFactor, y + yMargin + yFactor);					// left line
		g2d.drawLine(width - xMargin - xFactor, y + yMargin, width - xMargin - xFactor, y + yMargin + yFactor);			// right line
		g2d.drawLine(x + xMargin + xFactor, y + yMargin + yFactor, width - xMargin - xFactor, y + yMargin + yFactor);	// side line

		// freekick circle
		int wCircle = width / SOCCERFIELD_SMALL_CIRCLES_FACTOR;
		int hCircle = Math.abs(height / SOCCERFIELD_SMALL_CIRCLES_FACTOR);
		g2d.fillOval((width - wCircle) / 2, y + yMargin + 4 * yFactor / 5 - hCircle / 2, wCircle, hCircle);

		// internal
		int old_yF = yFactor;
		xFactor = 5 * xFactor / 3;
		yFactor = 3 * yFactor / 5;
		g2d.drawLine(x + xMargin + xFactor, y + yMargin, x + xMargin + xFactor, y + yMargin + yFactor);					// left line
		g2d.drawLine(width - xMargin - xFactor, y + yMargin, width - xMargin - xFactor, y + yMargin + yFactor);			// right line
		g2d.drawLine(x + xMargin + xFactor, y + yMargin + yFactor, width - xMargin - xFactor, y + yMargin + yFactor);	// side line

		// goalkeeper arc
		g2d.drawArc(x + xMargin + xFactor, y + yMargin + old_yF - Math.abs(old_yF / 4), width - 2 * xMargin - 2 * xFactor - x, Math.abs(old_yF / 2), 0, (orientation == MatchesAnalyzerPanel.BOTTOMUP ? 180 : -180));

		// small midfield circle
		g2d.fillOval((width - wCircle) / 2, y + height - hCircle / 2, wCircle, hCircle);

		// big midfield circle
		wCircle = width / SOCCERFIELD_MIDFIELD_CIRCLE_FACTOR;
		hCircle = Math.abs(height / SOCCERFIELD_MIDFIELD_CIRCLE_FACTOR);
		g2d.drawOval((width - wCircle) / 2, y + height - hCircle / 2, wCircle, hCircle);

		g2d.dispose();
	}

	// @todo it's time to rework the crappy code below!
	private void drawPlayer(Graphics g, MatchesAnalyzerPlayer player, int x, int y) {
		Graphics2D g2d = (Graphics2D)g.create();

		String name = player.getName();
		// @todo find the way to load the surname only!
		name = name.substring(name.indexOf(' ') + 1, name.length());
		int pos = 0;
		if((pos = name.indexOf(' ')) > 0) {
			name = name.substring(0, 1) + ". " + name.substring(name.indexOf(' ') + 1, name.length());
			if(name.length() > PLAYERFRAME_NAME_MAX_LENGTH) name = name.substring(0, PLAYERFRAME_NAME_MAX_LENGTH - 3).trim() + "...";
		}

		g2d.setFont(getFont().deriveFont(getSize().height / PLAYERFRAME_FONTSIZE_FACTOR));
		int w = getSize().width / X_ZONES * PLAYERFRAME_WIDTH_FACTOR / 100;
		int h = getSize().height / Y_ZONES * PLAYERFRAME_HEIGHT_FACTOR / 100;

		// frame background
		g2d.setColor(Color.WHITE);
		g2d.fillRect(x - w / 2 - PLAYER_FRAME_HPADDING, y - h / 2 - PLAYER_FRAME_VPADDING, w + 2 * PLAYER_FRAME_HPADDING, h + 2 * PLAYER_FRAME_VPADDING);

		// frame border
		g2d.setColor(Color.BLACK);
		g2d.drawRect(x - w / 2 - PLAYER_FRAME_HPADDING, y - h / 2 - PLAYER_FRAME_VPADDING, w + 2 * PLAYER_FRAME_HPADDING, h + 2 * PLAYER_FRAME_VPADDING);

		// name
		g2d.setColor(Color.BLACK);
		g2d.drawString(name, x - w / 2, y + h / 2);

		int xShift = PLAYER_FRAME_HPADDING / 2;
		double stars = player.getStars();
		if(Math.abs(stars) > 0.1) {
			// star icon
			xShift += STAR.getIconWidth();
			g2d.drawImage(STAR.getImage(), x + w / 2 - xShift, y - h / 2 + PLAYER_FRAME_VPADDING / 2, null);

			// star number
			g2d.setColor(Color.BLACK);
			g2d.setFont(getFont().deriveFont(Font.ITALIC | Font.BOLD));
			FontMetrics metric = getFontMetrics(getFont());
			String rating = String.valueOf(stars);
			int wStars = metric.stringWidth(rating);
			xShift += wStars + PLAYER_FRAME_SEPARATOR;
			g2d.drawString(rating, x + w / 2 - xShift, y + PLAYER_FRAME_VPADDING / 2);
		}

		// @todo sometime the player has no rating because of red cards. Are there other reasons?

		// player behavior
		ImageIcon icon = null;
		MatchesAnalyzerPlayer.Behavior behavior = player.getBehavior();
		if(behavior == MatchesAnalyzerPlayer.Behavior.DEFENSIVE) {
			icon = (orientation == 0 ? ARROW_UP : ARROW_DOWN);
		} else if(behavior == MatchesAnalyzerPlayer.Behavior.OFFENSIVE) {
			icon = (orientation == 0 ? ARROW_DOWN : ARROW_UP);
		} else if(behavior == MatchesAnalyzerPlayer.Behavior.TOWARDS_MIDDLE) {
			MatchesAnalyzerPlayer.Side side = player.getRole().getSide();
			if(side == MatchesAnalyzerPlayer.Side.LEFT) {
				icon = (orientation == 0 ? ARROW_RIGHT : ARROW_LEFT);
			} else if(side == MatchesAnalyzerPlayer.Side.RIGHT) {
				icon = (orientation == 0 ? ARROW_LEFT : ARROW_RIGHT);
			}
		} else if(behavior == MatchesAnalyzerPlayer.Behavior.TOWARDS_WING) {
			MatchesAnalyzerPlayer.Side side = player.getRole().getSide();
			if(side == MatchesAnalyzerPlayer.Side.LEFT) {
				icon = (orientation == 0 ? ARROW_LEFT : ARROW_RIGHT);
			} else if(side == MatchesAnalyzerPlayer.Side.RIGHT) {
				icon = (orientation == 0 ? ARROW_RIGHT : ARROW_LEFT);
			}
		}

		if(icon != null) {
			g2d.drawImage(icon.getImage(), x - w / 2 + PLAYER_FRAME_HPADDING, y - h / 2, null);
		}

		g2d.dispose();
	}

	@Override
	public void update(Observable source, Object arg) {
		lineup = (MatchesAnalyzerLineup)arg;
		repaint();
	}
}
