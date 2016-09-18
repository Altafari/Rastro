package rastro.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;
import rastro.system.SystemManager;

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
    private static final Dimension DIM_PORT_FIELD = new Dimension(130, 22);
    private static final Dimension DIM_BAUD_FIELD = new Dimension(80, 22);
    private static final Dimension DIM_LINK_FIELD = new Dimension(40, 22);

    public CommPanel(String title, String[] allowedBaudRates, CommController controller) {
        super(title);
        cCon = controller;
        comPortName = new JComboBox<String>(CommController.getPortList());
        comPortName.setMinimumSize(DIM_PORT_FIELD);
        comPortName.setPreferredSize(DIM_PORT_FIELD);
        comPortName.setMaximumSize(DIM_PORT_FIELD);
        baudRate = new JComboBox<String>(allowedBaudRates);
        baudRate.setMinimumSize(DIM_BAUD_FIELD);
        baudRate.setPreferredSize(DIM_BAUD_FIELD);
        baudRate.setMaximumSize(DIM_BAUD_FIELD);
        linkStatus = new JTextField();
        linkStatus.setEditable(false);
        linkStatus.setMinimumSize(DIM_LINK_FIELD);
        linkStatus.setPreferredSize(DIM_LINK_FIELD);
        linkStatus.setMaximumSize(DIM_LINK_FIELD);
        btnOpen = new JButton("Open");
        btnOpen.setActionCommand("open");
        btnOpen.addActionListener(actionListener);
        btnTest = new JButton("Test");
        btnTest.setActionCommand("test");
        btnTest.addActionListener(actionListener);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(comPortName);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(baudRate);
        this.add(Box.createHorizontalStrut(PADDING_LARGE));
        this.add(new JLabel("Status:"));
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(linkStatus);
        this.add(Box.createHorizontalStrut(PADDING_LARGE));
        this.add(btnOpen);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
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
                        SystemManager.getInstance().loadGrblSettings();
                    }
                })).start();
                break;
            default:
            }
        }
    };

}
