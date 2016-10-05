package rastro.system;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;
import rastro.controller.GrblController;
import rastro.controller.ImageController;
import rastro.controller.ProgramController;
import rastro.model.GrblSettings;
import rastro.model.GrblStatusMonitor;
import rastro.ui.CncOriginPanel;
import rastro.ui.CncPositioningPanel;
import rastro.ui.CommPanel;
import rastro.ui.ImageControlPanel;
import rastro.ui.ImageInfoPanel;
import rastro.ui.ProgramControlPanel;
//import rastro.ui.MainDialog;

public class SystemManager {
    public enum SystemMode {IDLE, RUNNING};    
    private static SystemManager sysMgr;
    
    //private MainDialog mainDlg;
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
    private ProgramControlPanel progCtrlPanel;
    private GrblStatusMonitor grblStatusMonitor;
    private GrblController grblController;
    private ProgramController progCtrl;
        
    //private SystemMode sysMode;
    
    private SystemManager() {
        createSystemObjects();
        wireUpObservers();
    }
    
    private void createSystemObjects() {
   //     sysMode = SystemMode.IDLE;
        //mainDlg = new MainDialog();
        grblCommCtrl = new CommController();
        rastroCommCtrl = new CommController();        
        grblCommPanel = new CommPanel("GRBL", new String[] { "115200", "9600" }, grblCommCtrl);
        rastroCommPanel = new CommPanel("Rastro", new String[] { "115200" }, rastroCommCtrl);
        cncOrigPanel = new CncOriginPanel(this);
        cncPosPanel = new CncPositioningPanel(this);
        grblSettings = new GrblSettings();
        imgInfoPanel = new ImageInfoPanel();
        imgCtrl = new ImageController(imgInfoPanel);
        imgCtrlPanel = new ImageControlPanel(imgCtrl);
        progCtrlPanel = new ProgramControlPanel(this);
        grblStatusMonitor = new GrblStatusMonitor(this);
        grblController = new GrblController(this);
        progCtrl = new ProgramController(this);
    }
    
    private void wireUpObservers() {
        grblStatusMonitor.addPosListener(cncOrigPanel.getPositionListener());
        grblController.addOriginListener(cncOrigPanel.getOriginListener());
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
    
    /*public synchronized boolean isIdle() {
        return sysMode == SystemMode.IDLE;
    }*/
    
    public void loadGrblSettings() {
        if (grblCommCtrl.sendCommand(grblSettings.getLoadCommand()) == CommResult.ok) {
            cncPosPanel.updateParams(grblSettings.getSettings());            
            //CommResult result = grblCommCtrl.sendCommand(grblStatusMonitor.getReadStatusCommand());
        }
    }
}
