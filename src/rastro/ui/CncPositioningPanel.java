package rastro.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import rastro.model.GrblSettings.GrblSetting;
import rastro.system.IStateListener;
import rastro.system.SystemManager;

public class CncPositioningPanel extends BorderedTitledPanel implements IStateListener {

    /**
     * 
     */
    public enum CoordName { X, Y };
    private enum ButtonDir {UP, RIGHT, DOWN, LEFT};
    private static final long serialVersionUID = 1L;
    private static final Dimension CTRL_PAD_SIZE = new Dimension(102, 102);
    private static final Dimension LABEL_SIZE = new Dimension(64, 22);
    private ArrayList<ParamLabel> paramLabelList; 
    private JSlider slider;
    private float[] sliderMap = { 0.01f, 0.02f, 0.05f, 0.1f, 0.2f, 0.5f, 1.0f, 2.0f, 5.0f, 10.0f };
    private SystemManager sysMgr;
    
    private ActionListener btnListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {
            ArrowButton btn = (ArrowButton)event.getSource();
            onMoveCommand(btn.getDir());
        }
        
    };
    
    private class ParamLabel extends JLabel {
        
        /**
         * 
         */
        public final GrblSetting sId;
        private final CoordName coordName;        
        private static final long serialVersionUID = 1L;

        public ParamLabel(CoordName cn, GrblSetting settingId) {
            super();
            coordName = cn;
            sId = settingId;
            this.setPreferredSize(LABEL_SIZE);
            this.formatText(null);
        }

        private void formatText(Float val) {
            String strVal;
            if (val != null) {
                strVal = String.format("%4.2f", val);
            } else {
                strVal = "n/a";
            }
            this.setText(String.format("%s: %s", coordName.name(), strVal));
        }
    }
    
    private class ArrowButton extends JButton {
        /**
         * 
         */
        private ButtonDir btDir;
        private static final long serialVersionUID = 1L;
        ArrowButton(ButtonDir bDir) {
            super();
            btDir = bDir;
            BufferedImage img = null;
            try {
                ClassLoader cl = this.getClass().getClassLoader();
                img = ImageIO.read(cl.getResource("resources/arrow.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AffineTransform tr = new AffineTransform();
            tr.rotate(btDir.ordinal() * (Math.PI / 2.0), img.getWidth() / 2.0, img.getHeight() / 2.0);
            AffineTransformOp op = new AffineTransformOp(tr, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            this.setIcon(new ImageIcon(op.filter(img, null)));
            this.addActionListener(btnListener);
        }
        public ButtonDir getDir() {
            return btDir;
        }
    }
    
    private class CenterButton extends JButton {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        public CenterButton() {
            this.setToolTipText("Click here, and use keyboard buttons to move laser");
            this.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent event) {
                    int keyCode = event.getKeyCode();
                    switch (keyCode) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_KP_UP:
                            onMoveCommand(ButtonDir.UP);
                            break;
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_KP_LEFT:
                            onMoveCommand(ButtonDir.LEFT);
                            break;
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_KP_RIGHT:
                            onMoveCommand(ButtonDir.RIGHT);
                            break;
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_KP_DOWN:
                            onMoveCommand(ButtonDir.DOWN);
                            break;
                        case KeyEvent.VK_PAGE_UP:
                        case KeyEvent.VK_ADD:
                            onChangeStep(1);
                            break;
                        case KeyEvent.VK_PAGE_DOWN:
                        case KeyEvent.VK_SUBTRACT:
                            onChangeStep(-1);
                            break;
                        default:
                    }
                }
                @Override
                public void keyReleased(KeyEvent event) {
                }
                @Override
                public void keyTyped(KeyEvent event) {                    
                }
            });
        }
    }

    public CncPositioningPanel(SystemManager sysManager) {
        super("CNC Positioning");
        sysMgr = sysManager;
        paramLabelList = new ArrayList<ParamLabel>(8);
        paramLabelList.add(new ParamLabel(CoordName.X, GrblSetting.MAX_TRAVEL_X));
        paramLabelList.add(new ParamLabel(CoordName.Y, GrblSetting.MAX_TRAVEL_Y));
        paramLabelList.add(new ParamLabel(CoordName.X, GrblSetting.MAX_RATE_X));
        paramLabelList.add(new ParamLabel(CoordName.Y, GrblSetting.MAX_RATE_Y));
        paramLabelList.add(new ParamLabel(CoordName.X, GrblSetting.ACC_X));
        paramLabelList.add(new ParamLabel(CoordName.Y, GrblSetting.ACC_Y));
        paramLabelList.add(new ParamLabel(CoordName.X, GrblSetting.STEP_PER_MM_X));
        paramLabelList.add(new ParamLabel(CoordName.Y, GrblSetting.STEP_PER_MM_Y));
        Iterator<ParamLabel> it = paramLabelList.iterator(); 
        
        JPanel mvPar = new JPanel(new GridLayout(4, 3));
        mvPar.add(new JLabel("Max. travel"));
        mvPar.add(it.next());
        mvPar.add(it.next());
        mvPar.add(new JLabel("Max. speed"));
        mvPar.add(it.next());
        mvPar.add(it.next());
        mvPar.add(new JLabel("Acc. rate"));
        mvPar.add(it.next());
        mvPar.add(it.next());
        mvPar.add(new JLabel("Steps / mm"));
        mvPar.add(it.next());
        mvPar.add(it.next());

        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.PAGE_AXIS));
        slider = new JSlider(JSlider.VERTICAL, 0, 9, 6);
        slider.setMajorTickSpacing(3);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        Hashtable<Object, Object> labelTable = new Hashtable<Object, Object>();
        labelTable.put(0, new JLabel("0.01"));
        labelTable.put(3, new JLabel("0.1"));
        labelTable.put(6, new JLabel("1"));
        labelTable.put(9, new JLabel("10"));
        slider.setLabelTable(labelTable);
        slider.setAlignmentX(CENTER_ALIGNMENT);
        JLabel sliderLabel = new JLabel("Step, mm");
        sliderLabel.setAlignmentX(CENTER_ALIGNMENT);
        sliderPanel.add(sliderLabel);
        sliderPanel.add(Box.createVerticalStrut(PADDING_SMALL));
        sliderPanel.add(slider);
        
        JPanel moveCtrl = new JPanel(new GridLayout(3, 3));
        moveCtrl.setPreferredSize(CTRL_PAD_SIZE);
        moveCtrl.setMaximumSize(CTRL_PAD_SIZE);
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.UP));
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.LEFT));
        moveCtrl.add(new CenterButton());
        moveCtrl.add(new ArrowButton(ButtonDir.RIGHT));
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.DOWN));
        moveCtrl.add(Box.createGlue());

        this.add(mvPar);
        this.add(sliderPanel);
        this.add(Box.createHorizontalStrut(PADDING_LARGE));
        this.add(moveCtrl);
        this.add(Box.createHorizontalStrut(PADDING_LARGE));
    }

    @Override
    protected int getRackHeight() {
        return (int)(RACK_HEIGHT * 2.0f);
    }
    
    private void onMoveCommand(ButtonDir dir) {
        float step = sliderMap[slider.getValue()];        
        switch (dir) {
        case UP:
            sysMgr.getGrblController().joggingMove(new float[] {0.0f, step}, true);
            break;
        case DOWN:
            sysMgr.getGrblController().joggingMove(new float[] {0.0f, -step}, true);
            break;
        case LEFT:
            sysMgr.getGrblController().joggingMove(new float[] {-step, 0.0f}, true);
            break;
        case RIGHT:
            sysMgr.getGrblController().joggingMove(new float[] {step, 0.0f}, true);
            break;
        }
    }
    
    private void onChangeStep(int dir) {
        int currPos = slider.getValue();
        if (dir > 0) {
            if (currPos != slider.getMaximum()) {
                slider.setValue(currPos + 1);
            }
        } else {
            if (currPos != 0) {
                slider.setValue(currPos - 1);
            }
        }
    }

    @Override
    public void stateChanged() {
        Map<GrblSetting, Float> settings = sysMgr.getGrblSettings().getSettings();
        for (ParamLabel p : paramLabelList) {
            p.formatText(settings.get(p.sId));
        }
    }
}
