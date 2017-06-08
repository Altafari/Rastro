package rastro.model;

import java.util.Iterator;

import rastro.controller.ICommCommand;
import rastro.controller.ImageController;
import rastro.controller.RasterScanner;
import rastro.controller.RastroConfigCommand;
import rastro.controller.RastroConfigCommand.ScanMode;
import rastro.controller.RastroLineCommand;
import rastro.model.GrblSettings.GrblSetting;
import rastro.system.SystemManager;

public class ProgramTaskSettings {

    private final float HW_TIME_QUANT_MS = 5.0E-4f;
    private final float MS_TO_MIN = 6.0E4f;
    private final float SPM_X_BY_SENSOR = 1200.0f / 25.4f;
    private final int DEFAULT_OFFSET_CORRECTION = 1;

    private SystemManager sysMgr;
    private float spmX;
    private float spmY;
    private float[] imgDim;
    private int[] rSize;
    private int nSkip;
    private float beamW;
    private float beamH;
    private float expTime;
    private ScanMode scanMode = ScanMode.ZIGZAG;
    
    public ProgramTaskSettings(SystemManager sysManager, float beamWidth, float beamHeight, float expoTime) {
        sysMgr = sysManager;
        spmX = SPM_X_BY_SENSOR;
        spmY = sysMgr.getGrblSettings().getSettings().get(GrblSetting.STEP_PER_MM_Y);
        imgDim = sysMgr.getImageController().getDimensions();
        rSize = new int[] {Math.round(spmX * imgDim[0]), Math.round(spmY * imgDim[1])};
        nSkip = sysMgr.getProgramControlPanel().getLineSkip();
        beamW = beamWidth;
        beamH = beamHeight;
        expTime = expoTime;
    }
    
    public Iterator<boolean[]> getScannerIterator() {
        ImageController imCon = sysMgr.getImageController();
        ScanningShape scShape = new ScanningShape(beamW * spmX, beamH * spmY);
        RasterScanner rScanner = new RasterScanner(spmX, spmY, rSize, scShape.getShape(), nSkip);
        rScanner.loadImage(imCon);
        return rScanner.iterator();
    }
    
    public ICommCommand getConfigCommand() {
        RastroConfigCommand configCmd = new RastroConfigCommand(rSize[0]);
        configCmd.setExpTime(computeSinglePixelExposition());
        configCmd.setOffset(DEFAULT_OFFSET_CORRECTION);
        configCmd.setScanMode(scanMode);
        return configCmd;
    }
    
    public RastroLineCommand getLineCommand() {
        return new RastroLineCommand(rSize[0]);
    }
    
    public boolean isZigZag() {
        return scanMode == ScanMode.ZIGZAG;
    }

    public float getLineStep() {
        return nSkip / spmY;
    }
    
    public float getMaxFeedRate() { // Output: mm/s
        float maxHwRate = sysMgr.getGrblSettings().getSettings().get(GrblSetting.MAX_RATE_X);
        return Math.min(MS_TO_MIN / computeLinearExposition(), maxHwRate);
    }
    
    private int computeSinglePixelExposition() {    // Computes integer value for HW timer
        return Math.round(computeLinearExposition() / (HW_TIME_QUANT_MS * spmX));
    }
    
    private float computeLinearExposition() {   // expTime: ms/mm^2
        return expTime * getLineStep();         // Output: ms/mm linear exposition
    }
}
