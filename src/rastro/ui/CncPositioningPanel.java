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
import java.util.Hashtable;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.Timer;

public class CncPositioningPanel extends BorderedTitledPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final Dimension CTRL_PAD_SIZE = new Dimension(102, 102);
    private static final int KEY_REPEAT_INTERVAL_MS = 100;
    private static final int KEY_DELAY_INTERVAL_MS = 300;
    private enum ButtonDir {up, right, down, left};
    private JSlider slider;
    
    private ActionListener btnListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {            
            System.out.println(event.getActionCommand());
        }
        
    };
    
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
        private int keyCode;
        private Timer repeatTimer;
        
        public CenterButton() {
            this.setToolTipText("Click here, and use keyboard buttons to move laser");
            repeatTimer = new Timer(KEY_REPEAT_INTERVAL_MS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    onKeyPressed(keyCode);
                }                
            });
            repeatTimer.setInitialDelay(KEY_DELAY_INTERVAL_MS);
            this.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent event) {
                    switch (event.getKeyCode()) {
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_LEFT:
                        case KeyEvent.VK_RIGHT:
                        case KeyEvent.VK_DOWN:
                            keyCode = event.getKeyCode();
                            onKeyPressed(keyCode);
                            repeatTimer.restart();
                            break;
                        default:
                    }
                }

                @Override
                public void keyReleased(KeyEvent event) {
                    if (keyCode == event.getKeyCode()) {
                        repeatTimer.stop();
                    }
                }

                @Override
                public void keyTyped(KeyEvent event) {                    
                }
            });
        }
    }

    public CncPositioningPanel() {
        super("CNC Positioning");
        JPanel moveCtrl = new JPanel(new GridLayout(3, 3));
        moveCtrl.setPreferredSize(CTRL_PAD_SIZE);
        moveCtrl.setMaximumSize(CTRL_PAD_SIZE);
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.up));
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.left));
        moveCtrl.add(new CenterButton());
        moveCtrl.add(new ArrowButton(ButtonDir.right));
        moveCtrl.add(Box.createGlue());
        moveCtrl.add(new ArrowButton(ButtonDir.down));
        moveCtrl.add(Box.createGlue());
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
        this.add(Box.createHorizontalGlue());
        this.add(slider);
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
}
