package rastro.system;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;
import rastro.controller.GrblController;
import rastro.controller.ImageController;
import rastro.controller.ProgramController;
import rastro.controller.RastroConfigCommand;
import rastro.model.GrblSettings;
import rastro.model.GrblStatusMonitor;
import rastro.ui.CncOriginPanel;
import rastro.ui.CncPositioningPanel;
import rastro.ui.CommPanel;
import rastro.ui.FlipMirroringPanel;
import rastro.ui.ImageControlPanel;
import rastro.ui.ImageInfoPanel;
import rastro.ui.ProgramControlPanel;
//import rastro.ui.MainDialog;

public class SystemManager {
	private static SystemManager sysMgr;

	private Set<IStateListener> stateListeners;
	// private MainDialog mainDlg;
	private CommController grblCommCtrl;
	private CommController rastroCommCtrl;
	private CommPanel grblCommPanel;
	private CommPanel rastroCommPanel;
	private CncOriginPanel cncOrigPanel;
	private CncPositioningPanel cncPosPanel;
	private GrblSettings grblSettings;
	private ImageInfoPanel imgInfoPanel;
	private ImageController imgCtrl;
	private ImageControlPanel imgCtrlPanel;
	private FlipMirroringPanel flipMirroringPanel;
	private ProgramControlPanel progCtrlPanel;
	private GrblStatusMonitor grblStatusMonitor;
	private GrblController grblController;
	private ProgramController progCtrl;

	private SystemManager() {
		stateListeners = new HashSet<IStateListener>();
		createSystemObjects();
		wireUpObservers();
		addStateListeners();
		stateListeners.add(progCtrlPanel);
	}

	private void createSystemObjects() {
		Callable<Boolean> grblTestCmd = new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return loadGrblSettings();
			}
		};
		Callable<Boolean> rastroTestCmd = new Callable<Boolean>() {
			@Override
			public Boolean call() {
				return checkRastroConnection();
			}
		};
		try {
			grblCommCtrl = new CommController();
			rastroCommCtrl = new CommController();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		grblCommPanel = new CommPanel("GRBL", new String[] { "115200", "9600" }, grblCommCtrl, grblTestCmd);
		rastroCommPanel = new CommPanel("Rastro", new String[] { "115200" }, rastroCommCtrl, rastroTestCmd);
		cncOrigPanel = new CncOriginPanel(this);
		cncPosPanel = new CncPositioningPanel(this);
		grblSettings = new GrblSettings();
		imgInfoPanel = new ImageInfoPanel(this);
		imgCtrl = new ImageController();
		imgCtrlPanel = new ImageControlPanel(this);
		progCtrlPanel = new ProgramControlPanel(this);
		grblStatusMonitor = new GrblStatusMonitor(this);
		grblController = new GrblController(this);
		progCtrl = new ProgramController(this);
		flipMirroringPanel = new FlipMirroringPanel(this);
	}

	private void wireUpObservers() {
		grblStatusMonitor.addPosListener(cncOrigPanel.getPositionListener());
		grblController.addOriginListener(cncOrigPanel.getOriginListener());
		grblStatusMonitor.forceNotification();
		grblController.forceNotification();
	}

	private void addStateListeners() {
		stateListeners.add(progCtrlPanel);
		stateListeners.add(imgInfoPanel);
		stateListeners.add(cncPosPanel);
		stateListeners.add(flipMirroringPanel);
	}

	private static synchronized void createNewInstance() {
		if (sysMgr == null) {
			sysMgr = new SystemManager();
		}
	}

	public static SystemManager getInstance() {
		if (sysMgr == null) {
			createNewInstance();
		}
		return sysMgr;
	}

	public CommPanel getGrblCommPanel() {
		return grblCommPanel;
	}

	public CommPanel getRastroCommPanel() {
		return rastroCommPanel;
	}

	public CncOriginPanel getCncOriginPanel() {
		return cncOrigPanel;
	}

	public ImageInfoPanel getImageInfoPanel() {
		return imgInfoPanel;
	}

	public ImageControlPanel getImageControlPanel() {
		return imgCtrlPanel;
	}

	public FlipMirroringPanel getFlipMirroringPanel() {
		return flipMirroringPanel;
	}

	public CncPositioningPanel getCncPositioningPanel() {
		return cncPosPanel;
	}

	public ProgramControlPanel getProgramControlPanel() {
		return progCtrlPanel;
	}

	public GrblStatusMonitor getGrblStatusMonitor() {
		return grblStatusMonitor;
	}

	public CommController getGrblCommController() {
		return grblCommCtrl;
	}

	public GrblController getGrblController() {
		return grblController;
	}

	public GrblSettings getGrblSettings() {
		return grblSettings;
	}

	public ImageController getImageController() {
		return imgCtrl;
	}

	public CommController getRastroCommController() {
		return rastroCommCtrl;
	}

	public ProgramController getProgramController() {
		return progCtrl;
	}

	/*
	 * public synchronized boolean isIdle() { return sysMode == SystemMode.IDLE; }
	 */

	public void notifyStateChanged() {
		for (IStateListener l : stateListeners) {
			l.stateChanged();
		}
	}

	public boolean loadGrblSettings() {
		if (grblCommCtrl.sendCommand(grblSettings.getLoadCommand()) == CommResult.ok) {
			notifyStateChanged();
			return true;
		}
		return false;
	}

	private boolean checkRastroConnection() {
		return rastroCommCtrl.sendCommand(new RastroConfigCommand(0)) == CommResult.ok;
	}
}
