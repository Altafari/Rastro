package rastro.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rastro.system.SystemManager;

public class FlipMirroringPanel extends BorderedTitledPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public enum FlipPosition { TOP, BOTTOM }; 

    private final Dimension DIM_TEXTFIELD = new Dimension(54, 22);
    private final Dimension DIM_BUTTON = new Dimension(80, 22);
    private final String STR_TOP = "Top";
    private final String STR_BOTTOM = "Bottom";
    
    private SystemManager sysMgr;
    private JTextField statusWindow;
    private JFormattedTextField flipOffsetWindow;
    private JFormattedTextField boardWidth;
    private FlipPosition flipPosition;
    private float flipOffset;
    
    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            sysMgr.notifyStateChanged();
        }
    };
    
    private final ActionListener flipListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (flipPosition.equals(FlipPosition.TOP)) {
                flipPosition = FlipPosition.BOTTOM;
                statusWindow.setText(STR_BOTTOM);
                onStateChanged();
            } else {
                flipPosition = FlipPosition.TOP;
                statusWindow.setText(STR_TOP);
                onStateChanged();
            }
        }  
    };

    public FlipMirroringPanel(SystemManager sysManager) {
        super("Board flip horizontal mirroring");
        sysMgr = sysManager;
        flipPosition = FlipPosition.TOP;
        boardWidth = new JFormattedTextField(NumberFormat.getNumberInstance());
        boardWidth.setPreferredSize(DIM_TEXTFIELD);
        boardWidth.setValue(new Float(15.0f));
        JButton flipButton = new JButton("Flip");
        flipButton.setPreferredSize(DIM_BUTTON);
        flipButton.addActionListener(flipListener);
        flipOffsetWindow = new JFormattedTextField(NumberFormat.getNumberInstance());
        flipOffsetWindow.setPreferredSize(DIM_TEXTFIELD);
        flipOffsetWindow.setValue(new Float(15.0f));
        flipOffsetWindow.setEditable(false);
        statusWindow = new JTextField();
        statusWindow.setPreferredSize(DIM_TEXTFIELD);
        statusWindow.setText(STR_TOP);
        statusWindow.setEditable(false);
        JPanel controlsPanel = new JPanel();
        controlsPanel.add(new JLabel("Board width"));
        controlsPanel.add(boardWidth);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(new JLabel("Origin offset"));
        controlsPanel.add(flipOffsetWindow);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(statusWindow);
        controlsPanel.add(Box.createHorizontalStrut(PADDING_SMALL));
        controlsPanel.add(flipButton);
        this.add(controlsPanel);
    }

    @Override
    protected int getRackHeight() {
        return (int)(RACK_HEIGHT * 0.85f);
    }

    private void onStateChanged() {
        float offset;
        if (flipPosition.equals(FlipPosition.TOP)) {
            offset = 0;
            flipOffsetWindow.setValue(offset);
        } else {
            offset = ((Number)boardWidth.getValue()).floatValue() - sysMgr.getImageController().getDimensions()[0];            
            flipOffsetWindow.setValue(offset);
        }
    }
}
