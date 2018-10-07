package module.matchesanalyzer.ui.table.cell;

import module.matchesanalyzer.ui.table.cell.content.MatchesAnalyzerCellContent;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;


public class MatchesAnalyzerCell extends JLabel {
	private static final long serialVersionUID = 1L;

	private final boolean selected;
	private final Font font;
	private final MatchesAnalyzerCellContent content;

	public MatchesAnalyzerCell(MatchesAnalyzerCellContent content, boolean selected) {
		this.content = content;
		this.selected = selected;
		this.font = getFont();

		setOpaque(true);
		setText(null);
		setToolTipText(null);
		setIcon(null);

		content.configure(this, selected);

		if(selected) setBackground(getBackground().darker());
		repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		content.paint(g, getSize(), content.getCellType().getStyle(), selected);
	}

}
