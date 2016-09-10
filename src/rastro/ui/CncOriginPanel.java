package rastro.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CncOriginPanel extends BorderedTitledPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private JTextField positionX;
    private JTextField positionY;
    private JTextField originX;
    private JTextField originY;
    private JButton setZero;
    private JButton goZero;
    private JButton setOrigin;
    private JButton goOrigin;
    private float[] position;
    private float[] origin;
    private static final Dimension NUM_FIELD = new Dimension(50, 22);
    public CncOriginPanel() {
        super("CNC origin");
        positionX = new JTextField();
        positionX.setPreferredSize(NUM_FIELD);
        positionX.setEditable(false);
        positionY = new JTextField();
        positionY.setPreferredSize(NUM_FIELD);
        positionY.setEditable(false);
        originX = new JTextField();
        originX.setPreferredSize(NUM_FIELD);
        originX.setEditable(false);
        originY = new JTextField();
        originY.setPreferredSize(NUM_FIELD);
        originY.setEditable(false);
        
        setZero = new JButton("Set zero");
        JPanel panelSetZero = new JPanel();
        panelSetZero.add(setZero);        
        
        goZero = new JButton("Go zero");
        JPanel panelGoZero = new JPanel();
        panelGoZero.add(goZero);
        
        setOrigin = new JButton("Set origin");
        JPanel panelSetOrigin = new JPanel();
        panelSetOrigin.add(setOrigin);
        
        goOrigin = new JButton("Go origin");
        JPanel panelGoOrigin = new JPanel();
        panelGoOrigin.add(goOrigin);
        
        JPanel cellOriginX = new JPanel();
        cellOriginX.add(new JLabel("Orig X="));
        cellOriginX.add(originX);
        cellOriginX.add(new JLabel("mm"));        
        
        JPanel cellPositionX = new JPanel();
        cellPositionX.add(new JLabel("Pos X="));
        cellPositionX.add(positionX);
        cellPositionX.add(new JLabel("mm"));
        
        JPanel cellOriginY = new JPanel();
        cellOriginY.add(new JLabel("Orig Y="));
        cellOriginY.add(originY);
        cellOriginY.add(new JLabel("mm"));
        
        JPanel cellPositionY = new JPanel();        
        cellPositionY.add(new JLabel("Pos Y="));
        cellPositionY.add(positionY);
        cellPositionY.add(new JLabel("mm"));
        this.add(cellPositionX);
        this.add(panelGoZero);
        this.add(cellOriginX);
        this.add(panelGoOrigin);
        this.add(cellPositionY);
        this.add(panelSetZero);
        this.add(cellOriginY);
        this.add(panelSetOrigin);
        setPosition(new float[] {0.0f, 0.0f});
        setOrigin();        
    }
    
    public void setPosition(float[] coord) {
        position = coord.clone();
        positionX.setText(String.format("%4.1f", position[0]));
        positionY.setText(String.format("%4.1f", position[1]));
    }
    
    public void setOrigin() {
        origin = position.clone();
        originX.setText(String.format("%4.1f", origin[0]));
        originY.setText(String.format("%4.1f", origin[1]));
    }
    
    public float[] getOrigin() {
        return origin.clone();
    }
    
    @Override
    protected int getRackHeight() {
        return (int)(RACK_HEIGHT * 1.4f);
    }
    
    @Override
    protected void setLayout() {
        this.setLayout(new GridLayout(2, 4));
    }
}
