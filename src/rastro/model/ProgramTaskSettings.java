package rastro.model;

import java.util.Iterator;

import rastro.controller.ICommCommand;
import rastro.controller.ImageController;
import rastro.controller.RasterScanner;
import rastro.controller.RastroConfigCommand;
import rastro.controller.RastroLineCommand;
import rastro.model.GrblSettings.GrblSetting;
import rastro.system.SystemManager;

public class ProgramTaskSettings {
    private SystemManager sysMgr;
    private float spmX;
    private float spmY;
    private float[] imgDim;
    private int[] rSize;
    private int nSkip;
    private float beamR;
    private int expTime;  
    
    public ProgramTaskSettings(SystemManager sysManager, float xSpan, float beamRad, int expoTime) {
        sysMgr = sysManager;
        spmX = sysMgr.getGrblSettings().getSettings().get(GrblSetting.STEP_PER_MM_X);
        spmY = sysMgr.getGrblSettings().getSettings().get(GrblSetting.STEP_PER_MM_Y);
        imgDim = sysMgr.getImageController().getDimensions();
        rSize = new int[] {Math.round(spmX * imgDim[0]), Math.round(spmY * imgDim[1])};        
        nSkip = sysMgr.getProgramControlPanel().getLineSkip();
        beamR = beamRad;
        expTime = expoTime;
    }
    
    public Iterator<boolean[]> getScannerIterator() {
        ImageController imCon = sysMgr.getImageController();
        ScanningShape scShape = new ScanningShape(beamR, spmX, spmY);
        RasterScanner rScanner = new RasterScanner(spmX, spmY, rSize, scShape.getShape(), nSkip);
        rScanner.loadImage(imCon);
        return rScanner.iterator();            
    }
    
    public ICommCommand getConfigCommand() {
        RastroConfigCommand configCmd = new RastroConfigCommand(rSize[0]);
        configCmd.setExpTime(expTime);
        configCmd.setOffset(0);
        configCmd.setScanMode(false);
        return configCmd;
    }
    
    public RastroLineCommand getLineCommand() {
        return new RastroLineCommand(rSize[0]);
    }
    
    public float getLineStep() {
        return spmY * nSkip;
    }
}
