package rastro.ui;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

public class BorderedTitledPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int BORDER_PADDING = 2;
	private static final int RACK_WIDTH = 500;
	protected static final int RACK_HEIGHT = 70;
	protected static final int PADDING_SMALL = 5;
	protected static final int PADDING_LARGE = 10;

	public BorderedTitledPanel(String title) {
		setLayout();
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(BORDER_PADDING, BORDER_PADDING, BORDER_PADDING, BORDER_PADDING),
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), title)));
		Dimension dim = new Dimension(RACK_WIDTH, getRackHeight());
		this.setMinimumSize(dim);
		this.setPreferredSize(dim);
		this.setMaximumSize(dim);
	}

	protected int getRackHeight() {
		return RACK_HEIGHT;
	}

	protected void setLayout() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
	}
}
