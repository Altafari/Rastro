package rastro.ui;

import static rastro.ui.GlobalUiDimensions.RACK_SIZE;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class BorderedTitledPanel extends JPanel {
    /**
     * 
     */ 
    private static final long serialVersionUID = 1L;
    private static int PADDING = 2;
    
    public BorderedTitledPanel(String title) {
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING),
                        BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.gray), title)));
        Dimension dim = getDimension();
        this.setMinimumSize(dim);
        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
    }
    
    protected Dimension getDimension() {
        return  RACK_SIZE.dim;
    }
}
