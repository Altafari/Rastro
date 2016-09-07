package rastro.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;

public class CommPanel extends BorderedTitledPanel {

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

    CommPanel(String title, String[] allowedBaudRates, CommController controller) {
        super(title);
        cCon = controller;
        comPortName = new JComboBox<String>(CommController.getPortList());
        baudRate = new JComboBox<String>(allowedBaudRates);
        linkStatus = new JTextField();
        linkStatus.setEditable(false);
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
                        byte[] data = new byte[] { (byte) 'h', (byte) 'i' };
                        cCon.write(data);
                        byte[] rcv = new byte[2];
                        int cnt = cCon.read(rcv);
                        if (cnt == 2 && rcv[0] == (byte) 'h' && rcv[1] == (byte) 'i') {
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
