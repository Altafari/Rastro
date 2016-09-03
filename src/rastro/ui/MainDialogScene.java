package rastro.ui;

import javax.swing.*;
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
        pane.add(new CommPanel(), BorderLayout.LINE_START);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
