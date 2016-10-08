package rastro.ui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rastro.model.GrblSettings;
import rastro.system.IStateListener;
import rastro.system.SystemManager;

public class ProgramControlPanel extends BorderedTitledPanel implements IStateListener {

    /**
     * 
     */
    
    private static final long serialVersionUID = 1L;
    
    private SystemManager sysMgr;
    private JButton startBtn;
    private JButton stopBtn;
    private JButton pauseBtn;
    private JSlider beamRad;;
    private final float[] radValues = {0.01f, 0.02f, 0.03f, 0.04f, 0.05f, 0.06f, 0.07f, 0.08f, 0.09f};
    private JSlider lineStep;
    private final int[] stepValues = {1, 2, 3, 4, 5, 6, 7, 8, 9};
    private JFormattedTextField overScan;
    private JFormattedTextField expTime;
    private JLabel lineStepDim;
    private JLabel lineBoundary;
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
    
    private final ChangeListener changeListener = new ChangeListener() {
        @Override
        public void stateChanged(ChangeEvent e) {
            sysMgr.notifyStateChanged();
        }
    };

    private final PropertyChangeListener propChangeListener = new PropertyChangeListener() {
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Object src = evt.getSource();
            if (src == overScan) {
                sysMgr.getProgramController().setOverScan(((Number) overScan.getValue()).floatValue());
            } else if (src == expTime) {
                sysMgr.getProgramController().setExpTime(((Number) expTime.getValue()).floatValue());
            }
            sysMgr.notifyStateChanged();
        }        
    };

    public ProgramControlPanel(SystemManager sysManager) {
        super("Program control");
        sysMgr = sysManager;
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
        beamRad.addChangeListener(changeListener);
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
        lineStep.addChangeListener(changeListener);
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
        
        
        lineStepDim = new JLabel();
        JPanel lineStepDimPanel = new JPanel();
        lineStepDimPanel.setAlignmentX(CENTER_ALIGNMENT);
        lineStepDimPanel.add(lineStepDim);
        
        lineBoundary = new JLabel();
        JPanel lineBoundaryPanel = new JPanel();
        lineBoundaryPanel.setAlignmentX(CENTER_ALIGNMENT);
        lineBoundaryPanel.add(lineBoundary);
        
        textFieldPanel.add(lineStepDimPanel);
        textFieldPanel.add(lineBoundaryPanel);        
        overScan = new JFormattedTextField(NumberFormat.getNumberInstance());
        overScan.setPreferredSize(DIM_TEXTFIELD);
        overScan.setValue(new Float(0.0f));
        overScan.addPropertyChangeListener(propChangeListener);
        expTime = new JFormattedTextField(NumberFormat.getNumberInstance());
        expTime.setPreferredSize(DIM_TEXTFIELD);
        expTime.setValue(new Float(0.0f));
        expTime.addPropertyChangeListener(propChangeListener);
        
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
    
    public float getBeamRadius() {
        return radValues[beamRad.getValue()];
    }
    
    public int getLineStep() {
        return stepValues[lineStep.getValue()];
    }

    private void updateLineDim(float dim) {
        lineStepDim.setText(String.format("Scan step: %1.3f mm", dim));
    }
    
    private void updateLineBoundary(float[] span) {
        lineBoundary.setText(String.format("Span: %3.1f~%3.1f mm", span[0], span[1]));
    }
    
    protected int getRackHeight() {
        return RACK_HEIGHT * 2;
    }

    @Override
    public void stateChanged() {
        float spmY = Float.POSITIVE_INFINITY;
        Map<GrblSettings.GrblSetting, Float> settings= sysMgr.getGrblSettings().getSettings();
        GrblSettings.GrblSetting key = GrblSettings.GrblSetting.STEP_PER_MM_Y;
        if (settings.containsKey(key)) {
            spmY = settings.get(key);
        }
        updateLineDim(getLineStep() / spmY);
        updateLineBoundary(sysMgr.getProgramController().getLineSpan());
    }
}