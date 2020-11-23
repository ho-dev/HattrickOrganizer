package module.statistics;

import core.util.chart.GraphDataModel;
import core.gui.theme.HOColorName;
import core.gui.theme.ThemeManager;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.text.NumberFormat;

import javax.swing.JPanel;

/**
 * Displays statistics in the form of a line chart
 */
public class StatistikPanel extends JPanel {

	private static final long serialVersionUID = -821935126572236002L;
	private boolean print;
	// Distance between the auxiliary lines
	private int SA = 50;
	private int SO = 25;
	private int SR = 25;
	private int SU = 50;
	private NumberFormat m_clYAchseFormat;
	private String xBezeichner = "";
	private String yBezeichner = "";
	private GraphDataModel[] m_clGraphDataModel;
	private String[] m_clYAchseBeschriftung;
	private boolean beschriftung;
	private boolean hilfslinien = true;
	private boolean m_bMaxMinBerechnen;
	private boolean dataBasedBoundaries = false;
	// Page distances to the start of the coordinate cross
	// Dynamic calculation
	private int SL = 60;

	public StatistikPanel(boolean maxminBerechnen) {
		m_bMaxMinBerechnen = maxminBerechnen;
		setDoubleBuffered(true);
	}

	/**
	 * Maximaler Konstruktor
	 * 
	 * @param hilfslinien
	 *            Vertikale und Horizontale Linien durch den Graphen
	 * @param yAchseBeschriftung
	 *            Wert, mit der die x-Achse zu zählen anfängt
	 * @param yAchseFormat
	 *            Der Wertabstand der x-Achse zwischen 2 Werten
	 * @param xBezeichner
	 *            Bezeichnung der x-Achse
	 * @param yBezeichner
	 *            Bezeichnung der y-Achse
	 * @param beschriftung
	 *            Beschriftung des Graphen
	 * @param hilfslinien
	 *            Farbe des Graphen
	 */
	public final void setAllValues(GraphDataModel[] models, String[] yAchseBeschriftung,
								   NumberFormat yAchseFormat, String xBezeichner, String yBezeichner,
								   boolean beschriftung, boolean hilfslinien) {
		this.m_clGraphDataModel = models;
		this.m_clYAchseBeschriftung = yAchseBeschriftung;
		this.xBezeichner = xBezeichner;
		this.yBezeichner = yBezeichner;
		this.beschriftung = beschriftung;
		this.hilfslinien = hilfslinien;
		this.m_clYAchseFormat = yAchseFormat;
		repaint();
	}

	/**
	 * Switching the graph labelling on or off
	 */
	public final void setLabelling(boolean beschriftung) {
		this.beschriftung = beschriftung;
		repaint();
	}

	/**
	 * Switching the guide lines on and off
	 */
	public final void setHelpLines(boolean hilfslinien) {
		this.hilfslinien = hilfslinien;
		repaint();
	}

	public final void setModel(GraphDataModel[] models) {
		m_clGraphDataModel = models;
		repaint();
	}

	public final GraphDataModel[] getModel() {
		return m_clGraphDataModel;
	}

    /**
     * Setter for property dataBasedBoundaries.
     *
     * @param value New value of property dataBasedBoundaries.
     */
    public final void setDataBasedBoundaries(boolean value) {
        dataBasedBoundaries = value;
    }

	/**
	 * Ein bestimmtes Model holen
	 */
	public final GraphDataModel getModel(String name) {
		for (int i = 0; (m_clGraphDataModel != null) && (m_clGraphDataModel.length > i); i++) {
			if (m_clGraphDataModel[i].getName().equals(name)) {
				return m_clGraphDataModel[i];
			}
		}

		return null;
	}

	/**
	 * Einen bestimmten Graf sichtbar/unsichtbar machen
	 */
	public final void setShow(String name, boolean show) {
		if (m_clGraphDataModel != null){
			for (int i = 0; i <= m_clGraphDataModel.length; i++) {
				if ((m_clGraphDataModel[i] != null) && (m_clGraphDataModel[i].getName().equals(name))) {
					m_clGraphDataModel[i].setShow(show);
					break;
				}
			}
		}
		repaint();
	}

	@Override
	public final void paint(Graphics g) {
		update(g);
	}

	@Override
	public final void print(Graphics g) {
		update(g);
	}

	@Override
	public final void update(Graphics g) {
		if (g != null) {
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			final Color panelBackground = ThemeManager.getColor(HOColorName.STAT_PANEL_BG);
			final Color panelForeground = ThemeManager.getColor(HOColorName.STAT_PANEL_FG);
			final Color panelForegroundHelpingLines = ThemeManager.getColor(HOColorName.STAT_PANEL_FG_HELPING_LINES);

			// Initialization of the window
			final Rectangle r = getBounds();
			final int graph_width = r.width - 1;
			final int graph_height = r.height - 1;

			g.setColor(panelBackground);
			g.fillRect(1, 1, graph_width - 1, graph_height - 1);
			g.setColor(panelForeground);

			int fontSize = core.model.UserParameter.instance().schriftGroesse + 2;

			g.setFont(new Font("SansSerif", Font.BOLD, fontSize));
			g.drawString(yBezeichner, 8, 18);
			g.drawString(xBezeichner, graph_width - 150, graph_height - 8);

			// Highest value
			double max = 20;
			double min = 0;
			double max_without_factor;
			double min_without_factor;

			// Calculate MaxMin
			if (m_bMaxMinBerechnen) {
				max = maxFinder(true);
				min = minFinder(true);

				if(dataBasedBoundaries) {
					if (max < 0) {
						max *= 0.9;
					} else {
						max *= 1.1;
					  }

					if (min > 0) {
						min *= 0.9;
					} else {
						min *= 1.1;
					  }
				}
				else {
					if (max < 0) {
						max = 0;
					}

					if (min > 0) {
						min = 0;
					}
				}
			}

			max_without_factor = maxFinder(false);
			min_without_factor = minFinder(false);

			// Determine height of the x-axis
			final int xHoehe = (int) (((graph_height - SU - SO) / 2) + SO + ((max + min) * (((graph_height - SU - SO) / 2) / (max - min))));

			// ### SL dependent on max ###
			if ((m_clGraphDataModel != null) && (m_clGraphDataModel.length > 0)
					&& (m_clGraphDataModel[0] != null)) {
				// Draw in dimensions
				// Determine the amount of y-lines
				int f = 1;

				int yStriche = ((graph_height - SU - SO) / (SA / f));

				if (yStriche == 0) {
					yStriche = 1;
				}

				// Distance between the individual strokes
				double yAbstand = (((double) (graph_height - SU - SO)) / yStriche);
				int smallFontSize = core.model.UserParameter.instance().schriftGroesse;

				g.setFont(new Font("SansSerif", Font.BOLD, smallFontSize));

				// Calculate side distance
				SL = smallFontSize
						+ Math.max(
								((g.getFontMetrics().stringWidth(m_clYAchseFormat.format(max)) + 10)),
								((g.getFontMetrics().stringWidth(m_clYAchseFormat.format(min)) + 10)));

				for (int i = yStriche; i >= 0; i--) {
					// Draw lines y: Height - distance from the bottom edge -
					// multiple of the line spacing
					if (hilfslinien) {
						g.setColor(panelForegroundHelpingLines);
						g.drawLine(SL + 5, (int) (graph_height - SU - (yAbstand * i)), graph_width - SR,
								(int) (graph_height - SU - (yAbstand * i)));
					}

					g.setColor(panelForeground);
					g.drawLine(SL - 5, (int) (graph_height - SU - (yAbstand * i)), SL + 5,
							(int) (graph_height - SU - (yAbstand * i)));

					// Value per line
					final int ypos = (int) ((graph_height - SU + (smallFontSize / 2)) - (yAbstand * i));
					g.drawString(m_clYAchseFormat.format((((max - min) / (yStriche) * i) + min)),
							smallFontSize, ypos);
				}

				g.drawLine(SL, SO, SL, graph_height - SU);
				g.drawLine(SL, xHoehe, graph_width - SR, xHoehe);

				int fontWidth = 0;

				if (m_clGraphDataModel[0].getWerte().length > 0) {
					fontWidth = Math.max((g.getFontMetrics().stringWidth(m_clGraphDataModel[0].getFormat().format(max_without_factor)) + 10),
									((g.getFontMetrics().stringWidth(m_clGraphDataModel[0].getFormat().format(min_without_factor)) + 10)));
				}

				// Beschriftung XAchse
				showXAchseBeschriftung((Graphics2D) g, graph_width, graph_height);

				// Write label
				for (int i = 0; (m_clGraphDataModel != null) && (i < m_clGraphDataModel.length); i++) {
					if ((m_clGraphDataModel[i] != null) && m_clGraphDataModel[i].isShow()) {
						drawLine((Graphics2D) g, graph_width, graph_height, m_clGraphDataModel[i].getWerte(),
								m_clGraphDataModel[i].getFaktor(), max, min, beschriftung,
								m_clGraphDataModel[i].getColor(),
								m_clGraphDataModel[i].getFormat(), fontWidth);
					}
				}
			}

			// Ende Keine Werte
		}
	}

	// Findet den Maximalwert in einem double-Array
	private double maxFinder(boolean usefaktor) {
		double max;
		if(dataBasedBoundaries) max = Integer.MIN_VALUE;
		else max = 1;

		for (int i = 0; (m_clGraphDataModel != null) && (m_clGraphDataModel.length > i)
				&& (m_clGraphDataModel[i] != null) && (i < m_clGraphDataModel.length); i++) {
			if (m_clGraphDataModel[i].isShow()) {
				if (usefaktor) {
					if ((m_clGraphDataModel[i].getMaxValue() * m_clGraphDataModel[i].getFaktor()) > max) {
						max = m_clGraphDataModel[i].getMaxValue()
								* m_clGraphDataModel[i].getFaktor();
					}
				} else {
					if (m_clGraphDataModel[i].getMaxValue() > max) {
						max = m_clGraphDataModel[i].getMaxValue();
					}
				}
			}
		}

		return (max);
	}

	// Findet den Minimalwert in einem double-Array
	private double minFinder(boolean usefaktor) {
		double min;
		if(dataBasedBoundaries) min = Integer.MAX_VALUE;
		else min = 0;

		for (int i = 0; (m_clGraphDataModel != null) && (m_clGraphDataModel.length > i)
				&& (m_clGraphDataModel[i] != null) && (i < m_clGraphDataModel.length); i++) {
			if (m_clGraphDataModel[i].isShow()) {
				if (usefaktor) {
					if ((m_clGraphDataModel[i].getMinValue() * m_clGraphDataModel[i].getFaktor()) < min) {
						min = m_clGraphDataModel[i].getMinValue()
								* m_clGraphDataModel[i].getFaktor();
					}
				} else {
					if (m_clGraphDataModel[i].getMinValue() < min) {
						min = m_clGraphDataModel[i].getMinValue();
					}
				}
			}
		}

		return (min);
	}

	// Drawing a line chart
	private void showXAchseBeschriftung(Graphics2D g, int b, int h) {
		if (m_clYAchseBeschriftung.length > 0) {
			final int schriftbreite = (int) ((g.getFontMetrics().stringWidth(
					m_clYAchseBeschriftung[0]) + 10) * 1.5);

			// int x1;
			int x2;
			// int y1;
			int y2;
			int mengeBeschriftung = ((b - SL - SR) / schriftbreite);

			if (mengeBeschriftung == 0) {
				mengeBeschriftung = 1;
			}

			int abstandBeschriftung = (m_clYAchseBeschriftung.length / mengeBeschriftung);

			if (abstandBeschriftung == 0) {
				abstandBeschriftung = 1;
			}

			y2 = this.getHeight() - SU + 25;

			final Color foregroundColor = ThemeManager.getColor(HOColorName.STAT_PANEL_FG);

			for (int i = 0; i < m_clYAchseBeschriftung.length; i++) {
				y2 = this.getHeight() - SU + 15;
				x2 = (int) ((((double) (b - SL - SR)) / (m_clYAchseBeschriftung.length) * (m_clYAchseBeschriftung.length
						- i - 1)) + SL);

				g.setColor(foregroundColor);
				if ((i % abstandBeschriftung) == 0) {
//					if (hilfslinien) {
//						g.setColor(Color.lightGray);
//						g.drawLine(x2, this.getHeight() - SU, x2, SO);
//					}

					g.setColor(foregroundColor);


					final int xpos = x2
							- (g.getFontMetrics().stringWidth(m_clYAchseBeschriftung[i]) / 2);
					final int ypos = y2 + g.getFont().getSize();
					g.drawString(m_clYAchseBeschriftung[i], xpos, ypos);
					g.drawLine(x2, this.getHeight() - SU - 8, x2, this.getHeight() - SU + 10);
				}
			}
		}
	}

	// Drawing a line chart
	private void drawLine(Graphics2D g, int b, int h, double[] values, double faktor, double max,
						  double min, boolean isLabeled, Color color, java.text.NumberFormat format,
						  int fontWidth) {
		if (values.length > 0) {
			int x1;
			int x2;
			int y1;
			int y2;
			int lengthLabel = ((b - SL - SR) / fontWidth) - 1;

			if (lengthLabel == 0) {
				lengthLabel = 1;
			}

			int abstandBeschriftung = (values.length / lengthLabel);

			if (abstandBeschriftung == 0) {
				abstandBeschriftung = 1;
			}

			y2 = (int) ((h - SU - ((h - SU - SO) / (max - min) * ((values[values.length - 1] * faktor) - min))));
			x2 = SL;

			for (int i = 1; i < values.length; i++) {
				x1 = x2;
				y1 = y2;
				y2 = (int) ((h - SU - ((h - SU - SO) / (max - min) * ((values[values.length - i - 1] * faktor) - min))));
				x2 = (int) ((((double) (b - SL - SR)) / (values.length) * (i)) + SL);
				g.setColor(color);
				g.drawLine(x1, y1, x2, y2);
				g.drawLine(x1, y1 + 1, x2, y2 + 1);
				if (isLabeled && ((i % abstandBeschriftung) == 0)) {
					g.setColor(ThemeManager.getColor(HOColorName.STAT_PANEL_FG));

					final int xpos = x2
							- (g.getFontMetrics().stringWidth(
									format.format(values[values.length - i - 1])) / 2);
					final int ypos = y2 - (g.getFont().getSize() / 2);
					g.drawString(format.format(values[values.length - i - 1]), xpos, ypos);
				}
			}
		}
	}
}
