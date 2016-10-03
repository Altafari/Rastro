package rastro.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;

public class ProgramControlPanel extends BorderedTitledPanel {

    /**
     * 
     */
    
    private static final long serialVersionUID = 1L;
    
    private JButton startBtn;
    private JButton stopBtn;
    private JButton pauseBtn;
    private JSlider beamRad;;
    private final float[] radValues = {0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.09f};
    private JSlider lineStep;
    private final int[] stepValues = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private JTextField overScan;
    private JTextField expTime;
    private final Dimension DIM_TEXTFIELD = new Dimension(50, 22);
    private final Dimension DIM_BUTTON = new Dimension(80, 22);
    
    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
         /*   switch (event.getActionCommand()) {
            case "start":
                break;
            case "stop":
                break;
            case "pause":
                break;
            default:
            }*/
        }  
    };
    
    public ProgramControlPanel() {
        super("Program control");
        startBtn = new JButton("Start");
        startBtn.setActionCommand("start");
        startBtn.addActionListener(actionListener);
        startBtn.setMaximumSize(DIM_BUTTON);
        stopBtn = new JButton("Stop");
        stopBtn.setActionCommand("stop");
        stopBtn.addActionListener(actionListener);
        stopBtn.setMaximumSize(DIM_BUTTON);
        pauseBtn = new JButton("Pause");
        pauseBtn.setActionCommand("pause");
        pauseBtn.addActionListener(actionListener);
        pauseBtn.setMaximumSize(DIM_BUTTON);

        JPanel radSliderPanel = new JPanel();
        radSliderPanel.setLayout(new BoxLayout(radSliderPanel, BoxLayout.PAGE_AXIS));
        beamRad = new JSlider(JSlider.VERTICAL, 0, 8, 4);
        beamRad.setMajorTickSpacing(4);
        beamRad.setMinorTickSpacing(1);
        beamRad.setPaintTicks(true);
        beamRad.setPaintLabels(true);
        Hashtable<Object, Object> radLabels = new Hashtable<Object, Object>();
        radLabels.put(0, new JLabel("0.01"));
        radLabels.put(4, new JLabel("0.05"));
        radLabels.put(8, new JLabel("0.09"));
        beamRad.setLabelTable(radLabels);
        beamRad.setAlignmentX(CENTER_ALIGNMENT);
        JLabel radSliderLabel = new JLabel("Beam radius");
        radSliderLabel.setAlignmentX(CENTER_ALIGNMENT);
        radSliderPanel.add(radSliderLabel);
        radSliderPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        radSliderPanel.add(beamRad);
        
        JPanel stepSliderPanel = new JPanel();
        stepSliderPanel.setLayout(new BoxLayout(stepSliderPanel, BoxLayout.PAGE_AXIS));
        lineStep = new JSlider(JSlider.VERTICAL, 0, 8, 3);
        lineStep.setMajorTickSpacing(4);
        lineStep.setMinorTickSpacing(1);
        lineStep.setPaintTicks(true);
        lineStep.setPaintLabels(true);
        Hashtable<Object, Object> stepLabels = new Hashtable<Object, Object>();
        stepLabels.put(0, new JLabel("1"));
        stepLabels.put(4, new JLabel("5"));
        stepLabels.put(8, new JLabel("9"));
        lineStep.setLabelTable(stepLabels);
        lineStep.setAlignmentX(CENTER_ALIGNMENT);
        JLabel stepSliderLabel = new JLabel("Line step");
        stepSliderLabel.setAlignmentX(CENTER_ALIGNMENT);
        stepSliderPanel.add(stepSliderLabel);
        stepSliderPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        stepSliderPanel.add(lineStep);
                
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.PAGE_AXIS));
        buttonPanel.add(startBtn);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(pauseBtn);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(stopBtn);
        buttonPanel.setMaximumSize(new Dimension(80, 100));
        
        JPanel textFieldPanel = new JPanel();        
        textFieldPanel.setLayout(new GridLayout(4, 1));        
        
        textFieldPanel.add(Box.createVerticalGlue());
        textFieldPanel.add(Box.createVerticalGlue());
        
        overScan = new JTextField();
        overScan.setPreferredSize(DIM_TEXTFIELD);
        expTime = new JTextField();
        expTime.setPreferredSize(DIM_TEXTFIELD);        
        
        JPanel overScanPanel = new JPanel();
        overScanPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        overScanPanel.add(new JLabel("Overscan, mm"));
        overScanPanel.add(overScan);
        textFieldPanel.add(overScanPanel);

        JPanel expTimePanel = new JPanel();
        expTimePanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
        expTimePanel.add(new JLabel("Exposition, ms"));
        expTimePanel.add(expTime);
        textFieldPanel.add(expTimePanel);

        this.add(textFieldPanel);
        this.add(Box.createHorizontalGlue());
        this.add(stepSliderPanel);
        this.add(radSliderPanel);
        this.add(Box.createHorizontalGlue());
        this.add(buttonPanel);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
    }
    
    protected int getRackHeight() {
        return RACK_HEIGHT * 2;
    }

}
