package rastro.ui;

import javax.swing.*;

import rastro.controller.CommController;
import rastro.controller.ImageController;

import java.awt.BorderLayout;
import java.awt.Container;

public class MainDialogScene {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Rastro v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(Container pane) {
        JPanel lineStartPanel = new JPanel();
        lineStartPanel.setLayout(new BoxLayout(lineStartPanel, BoxLayout.PAGE_AXIS));
        CommPanel grblComm = new CommPanel("GRBL", new String[] { "115200", "9600" }, new CommController());
        CommPanel rastroComm = new CommPanel("Rastro", new String[] { "115200" }, new CommController());
        ImageInfoPanel imageInfoPanel = new ImageInfoPanel();
        lineStartPanel.add(grblComm);
        lineStartPanel.add(rastroComm);
        lineStartPanel.add(new ImageControlPanel(new ImageController(imageInfoPanel)));
        lineStartPanel.add(imageInfoPanel);
        lineStartPanel.add(new CncOriginPanel());
        lineStartPanel.add(Box.createVerticalGlue());
        pane.add(lineStartPanel, BorderLayout.LINE_START);
        pane.add(new JPanel(), BorderLayout.LINE_END);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
