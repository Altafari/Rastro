package rastro.system;

import rastro.controller.CommController;
import rastro.controller.CommController.CommResult;
import rastro.controller.ImageController;
import rastro.model.CncSettings;
import rastro.ui.CncOriginPanel;
import rastro.ui.CncPositioningPanel;
import rastro.ui.CommPanel;
import rastro.ui.ImageControlPanel;
import rastro.ui.ImageInfoPanel;
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
    private CncSettings cncSettings;
    private ImageInfoPanel imgInfoPanel;
    private ImageController imgCtrl;
    private ImageControlPanel imgCtrlPanel;
        
    private SystemMode sysMode;
    
    private SystemManager() {
        sysMode = SystemMode.IDLE;
        //mainDlg = new MainDialog();
        grblCommCtrl = new CommController();
        rastroCommCtrl = new CommController();        
        grblCommPanel = new CommPanel("GRBL", new String[] { "115200", "9600" }, grblCommCtrl);
        rastroCommPanel = new CommPanel("Rastro", new String[] { "115200" }, rastroCommCtrl);
        cncOrigPanel = new CncOriginPanel();
        cncPosPanel = new CncPositioningPanel();
        cncSettings = new CncSettings();
        imgInfoPanel = new ImageInfoPanel();
        imgCtrl = new ImageController(imgInfoPanel);
        imgCtrlPanel = new ImageControlPanel(imgCtrl);        
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
    
    public synchronized boolean isIdle() {
        return sysMode == SystemMode.IDLE;
    }
    
    public void loadGrblSettings() {
        if (grblCommCtrl.sendCommand(cncSettings.getLoadCommand()) == CommResult.ok) {
            cncPosPanel.updateParams(cncSettings.getSettings());
        }
    }
}
