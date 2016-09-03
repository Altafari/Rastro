package rastro.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import rastro.controller.CommController;

public class CommPanel extends JPanel {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    private CommSubPanel grblComm;
    private CommSubPanel rastroComm;

    CommPanel() {
        grblComm = new CommSubPanel("GRBL", new String[] { "115200", "9600" }, new CommController());

        rastroComm = new CommSubPanel("Rastro", new String[] { "115200" }, new CommController());
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(grblComm);
        this.add(rastroComm);
    }

}
