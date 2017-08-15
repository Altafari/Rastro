package rastro.ui;

import java.awt.Dimension;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FlipMirroringPanel extends BorderedTitledPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final Dimension DIM_TEXTFIELD = new Dimension(54, 22);
    private final Dimension DIM_BUTTON = new Dimension(80, 22);

    public FlipMirroringPanel() {
        super("Board flip horizontal mirroring");
        JFormattedTextField boardWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
        boardWidth.setPreferredSize(DIM_TEXTFIELD);
        boardWidth.setValue(new Float(15.0f));
        JButton mirrorButton = new JButton("Flip");
        mirrorButton.setPreferredSize(DIM_BUTTON);
        JFormattedTextField flipOffset = new JFormattedTextField(NumberFormat.getNumberInstance());
        flipOffset.setPreferredSize(DIM_TEXTFIELD);
        flipOffset.setValue(new Float(15.0f));
        flipOffset.setEditable(false);
        JTextField status = new JTextField();
        status.setPreferredSize(DIM_TEXTFIELD);
        status.setText("Bottom");
        status.setEditable(false);
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(new JLabel("Board width"));
        controlsPanel.add(boardWidth);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(new JLabel("Origin offset"));
        controlsPanel.add(flipOffset);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(status);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(mirrorButton);
        this.add(controlsPanel);
    }
    @Override
    protected int getRackHeight() {
        return (int)(RACK_HEIGHT * 0.85f);
    }
}
