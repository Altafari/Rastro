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
		frame.setResizable(false);
	}

	private static void addComponentsToPane(Container pane) {
		SystemManager sysMgr = SystemManager.getInstance();
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.PAGE_AXIS));
        leftPanel.add(sysMgr.getGrblCommPanel());
        leftPanel.add(sysMgr.getRastroCommPanel());
        leftPanel.add(sysMgr.getImageControlPanel());
        leftPanel.add(sysMgr.getImageInfoPanel());
        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(sysMgr.getFlipMirroringPanel());
        rightPanel.add(sysMgr.getCncOriginPanel());
        rightPanel.add(sysMgr.getCncPositioningPanel());
        rightPanel.add(sysMgr.getProgramControlPanel());
        JPanel ctrlPanel = new JPanel();
        ctrlPanel.setLayout(new BoxLayout(ctrlPanel, BoxLayout.LINE_AXIS));
        ctrlPanel.add(leftPanel);
        ctrlPanel.add(rightPanel);
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
