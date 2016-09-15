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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

public class CncPositioningPanel extends BorderedTitledPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Dimension CTRL_PAD_SIZE = new Dimension(102, 102);
    private static final Dimension LABEL_SIZE = new Dimension(64, 22);
    private ArrayList<ParamLabel> paramLabelList; 
    private enum ButtonDir {UP, RIGHT, DOWN, LEFT};
    public enum CoordName {X, Y};
    private JSlider slider;
    
    private ActionListener btnListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {            
            System.out.println(event.getActionCommand());
        }
        
    };
    
    private class ParamLabel extends JLabel {
        
        /**
         * 
         */
        private final CoordName coordName;
        private final int cmdNo;
        private static final long serialVersionUID = 1L;

        public ParamLabel(CoordName cn, int commandNo) {
            super();
            coordName = cn;
            cmdNo = commandNo;
            this.setPreferredSize(LABEL_SIZE);
            formatText(300.0f);
        }
        
        public void updateText(InputStream is, OutputStream os) {
            
        }
        
        private void formatText(float val) {
            this.setText(String.format("%s: %4.2f", coordName.name(), val));
        }
        
    }
    
    private class ArrowButton extends JButton {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        ArrowButton(ButtonDir bd) {
            super();
            BufferedImage img = null;
            try {
                img = ImageIO.read(new File("resources/arrow.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            AffineTransform tr = new AffineTransform();
            tr.rotate(bd.ordinal() * (Math.PI / 2.0), img.getWidth() / 2.0, img.getHeight() / 2.0);
            AffineTransformOp op = new AffineTransformOp(tr, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            this.setIcon(new ImageIcon(op.filter(img, null)));
            this.setActionCommand(bd.name());
            this.addActionListener(btnListener);
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
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_DOWN:
                            onKeyPressed(keyCode);
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

    public CncPositioningPanel() {
        super("CNC Positioning");
        paramLabelList = new ArrayList<ParamLabel>(8);
        paramLabelList.add(new ParamLabel(CoordName.X, 33));
        paramLabelList.add(new ParamLabel(CoordName.Y, 34));
        paramLabelList.add(new ParamLabel(CoordName.X, 33));
        paramLabelList.add(new ParamLabel(CoordName.Y, 34));
        paramLabelList.add(new ParamLabel(CoordName.X, 33));
        paramLabelList.add(new ParamLabel(CoordName.Y, 34));
        paramLabelList.add(new ParamLabel(CoordName.X, 33));
        paramLabelList.add(new ParamLabel(CoordName.Y, 34));
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
    
    private void onKeyPressed(int keyCode) {
        System.out.println(keyCode);
    }
    
    private void updateParams(InputStream is, OutputStream os) {
        for (ParamLabel pl : paramLabelList) {
           pl.updateText(is, os);
        }
    }
}
