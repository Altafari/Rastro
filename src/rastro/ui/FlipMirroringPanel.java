package rastro.ui;

import javax.swing.JButton;
import javax.swing.JTextField;

public class FlipMirroringPanel extends BorderedTitledPanel{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FlipMirroringPanel() {
        super("Board flip mirroring");
        JTextField boardWidth = new JTextField();
        JButton mirrorButton = new JButton();
        JTextField flipOffset = new JTextField();
    }

}
