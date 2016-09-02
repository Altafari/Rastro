package rastro.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;

public class CommSubPanel extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JComboBox<String> comPortName;
	private JComboBox<String> baudRate;
	private JTextField linkStatus;	
	private JButton btnOpen;
	private JButton btnTest;
	private CommController cCon;
	private static final int PADDING = 2;
	private static final int STATUS_FIELD_WIDTH = 8;
	CommSubPanel(String title, String[] allowedBaudRates, CommController controller) {
		cCon = controller;
		comPortName = new JComboBox<String>(CommController.getPortList());
		baudRate = new JComboBox<String>(allowedBaudRates);
		linkStatus = new JTextField(STATUS_FIELD_WIDTH);
		btnOpen = new JButton("Open");
		btnOpen.setActionCommand("open");
		btnOpen.addActionListener(actionListener);
		btnTest = new JButton("Test");
		btnTest.setActionCommand("test");
		btnTest.addActionListener(actionListener);
		this.add(comPortName);
		this.add(baudRate);
		this.add(linkStatus);
		this.add(btnOpen);
		this.add(btnTest);
		this.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING),
				BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(Color.gray), title)));
	}
	
	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {			
			switch (e.getActionCommand()) {
			case "open":
				cCon.setPortName((String) comPortName.getSelectedItem());
				cCon.setBaudRate(Integer.parseInt((String) baudRate.getSelectedItem()));
				if (cCon.openPort() == CommResult.ok) {
					linkStatus.setText("Status: ok");
				} else {
					linkStatus.setText("Error");
				}
			break;
			case "test":
				(new Thread(new Runnable() {
						public void run() {
							byte[] data = new byte[] {(byte)'h', (byte)'i'};				
							cCon.write(data);
							byte[] rcv = new byte[2];
							int cnt = cCon.read(rcv);
							if (cnt == 2 && rcv[0] == (byte)'h' && rcv[1] == (byte)'i') {
							linkStatus.setText("Test passed");
						}
					}
				})).start();				
			break;
			
			default:
			}
		}		
	};
	
}
