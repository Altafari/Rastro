package rastro.ui;

import javax.swing.*;

import rastro.system.SystemManager;
import java.awt.BorderLayout;
import java.awt.Container;

public class MainDialog {
    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Rastro v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
    }

    private static void addComponentsToPane(Container pane) {
        SystemManager sysMgr = SystemManager.getInstance();
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.PAGE_AXIS));
        ctrlPanel.add(sysMgr.getGrblCommPanel());
        ctrlPanel.add(sysMgr.getRastroCommPanel());
        ctrlPanel.add(sysMgr.getImageControlPanel());
        ctrlPanel.add(sysMgr.getImageInfoPanel());
        ctrlPanel.add(sysMgr.getCncOriginPanel());
        ctrlPanel.add(sysMgr.getCncPositioningPanel());
        ctrlPanel.add(Box.createVerticalGlue());
        pane.add(ctrlPanel, BorderLayout.LINE_START);        
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
