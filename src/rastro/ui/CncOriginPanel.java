package rastro.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import rastro.model.ICoordListener;
import rastro.system.SystemManager;

public class CncOriginPanel extends BorderedTitledPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SystemManager sysMgr;
	private JTextField positionX;
	private JTextField positionY;
	private JTextField originX;
	private JTextField originY;
	private JButton goZero;
	private JButton homingCycle;
	private JButton setOrigin;
	private JButton goOrigin;
	private static final Dimension NUM_FIELD = new Dimension(55, 22);
	private ICoordListener posListener;
	private ICoordListener originListener;
	private ActionListener actionListener;

	public CncOriginPanel(SystemManager systemMgr) {
		super("CNC origin");
		sysMgr = systemMgr;
		positionX = new JTextField();
		positionX.setPreferredSize(NUM_FIELD);
		positionX.setEditable(false);
		positionY = new JTextField();
		positionY.setPreferredSize(NUM_FIELD);
		positionY.setEditable(false);
		originX = new JTextField();
		originX.setPreferredSize(NUM_FIELD);
		originX.setEditable(false);
		originY = new JTextField();
		originY.setPreferredSize(NUM_FIELD);
		originY.setEditable(false);

		goZero = new JButton("Go zero");
		JPanel panelGoZero = new JPanel();
		panelGoZero.add(goZero);

		homingCycle = new JButton("Homing");
		JPanel panelHoming = new JPanel();
		panelHoming.add(homingCycle);

		setOrigin = new JButton("Set origin");
		JPanel panelSetOrigin = new JPanel();
		panelSetOrigin.add(setOrigin);

		goOrigin = new JButton("Go origin");
		JPanel panelGoOrigin = new JPanel();
		panelGoOrigin.add(goOrigin);

		JPanel cellOriginX = new JPanel();
		cellOriginX.add(new JLabel("Orig X="));
		cellOriginX.add(originX);
		cellOriginX.add(new JLabel("mm"));

		JPanel cellPositionX = new JPanel();
		cellPositionX.add(new JLabel("Pos X="));
		cellPositionX.add(positionX);
		cellPositionX.add(new JLabel("mm"));

		JPanel cellOriginY = new JPanel();
		cellOriginY.add(new JLabel("Orig Y="));
		cellOriginY.add(originY);
		cellOriginY.add(new JLabel("mm"));

		JPanel cellPositionY = new JPanel();
		cellPositionY.add(new JLabel("Pos Y="));
		cellPositionY.add(positionY);
		cellPositionY.add(new JLabel("mm"));
		this.add(cellPositionX);
		this.add(panelGoZero);
		this.add(cellOriginX);
		this.add(panelGoOrigin);
		this.add(cellPositionY);
		this.add(panelHoming);
		this.add(cellOriginY);
		this.add(panelSetOrigin);
		posListener = new ICoordListener() {
			@Override
			public void onChange(float[] pos) {
				positionX.setText(String.format("%4.2f", pos[0]));
				positionY.setText(String.format("%4.2f", pos[1]));
			}
		};
		originListener = new ICoordListener() {
			@Override
			public void onChange(float[] pos) {
				originX.setText(String.format("%4.2f", pos[0]));
				originY.setText(String.format("%4.2f", pos[1]));
			}
		};
		actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String command = event.getActionCommand();
				switch (command) {
				case "homingCycle":
					sysMgr.getGrblController().homingCycle();
					break;
				case "goZero":
					sysMgr.getGrblController().joggingMove(new float[] { 0.0f, 0.0f, 0.0f }, false);
					break;
				case "goOrigin":
					sysMgr.getGrblController().goOrigin();
					break;
				case "setOrigin":
					sysMgr.getGrblController().setOrigin();
					break;
				default:
				}
				sysMgr.notifyStateChanged();
			}
		};
		homingCycle.setActionCommand("homingCycle");
		homingCycle.addActionListener(actionListener);
		goZero.setActionCommand("goZero");
		goZero.addActionListener(actionListener);
		setOrigin.setActionCommand("setOrigin");
		setOrigin.addActionListener(actionListener);
		goOrigin.setActionCommand("goOrigin");
		goOrigin.addActionListener(actionListener);
	}

	public ICoordListener getPositionListener() {
		return posListener;
	}

	public ICoordListener getOriginListener() {
		return originListener;
	}

	@Override
	protected int getRackHeight() {
		return (int) (RACK_HEIGHT * 1.4f);
	}

	@Override
	protected void setLayout() {
		this.setLayout(new GridLayout(2, 4));
	}
}
