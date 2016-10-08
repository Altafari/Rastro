package rastro.controller;

import java.util.Map;

import rastro.model.GrblSettings.GrblSetting;
import rastro.model.GrblSettings;
import rastro.model.GrblStatusMonitor;
import rastro.model.GrblStatusMonitor.IModeListener;
import rastro.system.SystemManager;

public class ProgramController {
    
    public enum Mode {IDLE, RUN, PAUSE}
    private enum LineDir {FORWARD, BACK};
    private Mode mode;
    private SystemManager sysMgr;
    private Runnable prog;
    private float overScan;
    private float expTime;
    private float beamR;
    private Thread progThread;
    
    public ProgramController(SystemManager sysManager) {
        mode = Mode.IDLE;
        sysMgr = sysManager;
    }
    
    public synchronized void startProgram() {
        if (mode != Mode.IDLE) { // || !sysMgr.getImageController().isLoaded()) {
            return;
        }            
        mode = Mode.RUN;
        Map<GrblSetting, Float> settings = sysMgr.getGrblSettings().getSettings();
        final float[] orig = sysMgr.getGrblController().getOrigin();
        float[] imDim = sysMgr.getImageController().getDimensions();
        final float spmY = settings.get(GrblSetting.STEP_PER_MM_Y);
        final float[] line = getLineSpan();
        final float lineStep = sysMgr.getProgramControlPanel().getLineStep() / spmY;
        final float maxY = orig[1] + imDim[1];
        // pull settings - scanning shape radius
        prog = new Runnable() {
            GrblStatusMonitor.Mode grblMode;
            LineDir lDir = LineDir.FORWARD;
            @Override
            public void run() {
                GrblController grblCtrl = sysMgr.getGrblController();
                CommController rastroCtrl = sysMgr.getRastroCommController();
                grblCtrl.setMode(GrblController.Mode.PROGRAM);
                IModeListener modeListener = new IModeListener() {
                    @Override
                    public void onChange(GrblStatusMonitor.Mode mode) {
                        if (mode == GrblStatusMonitor.Mode.Idle) {
                            synchronized (prog) {
                                grblMode = mode;
                                prog.notify();
                            }
                        }
                    }
                };
                sysMgr.getGrblStatusMonitor().addModeListener(modeListener);
                sysMgr.getGrblStatusMonitor().startMonitoringTask();
                float currentY = orig[1];
                grblCtrl.programMove(new float[] {line[0], currentY}, false);
                while (mode == Mode.RUN) {
                    synchronized (this) {
                    if (grblMode != GrblStatusMonitor.Mode.Idle) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }                    
                    if (lDir == LineDir.FORWARD) {                        
                        grblCtrl.programMove(new float[] {line[1], currentY}, false);
                        lDir = LineDir.BACK;
                    } else {                        
                        grblCtrl.programMove(new float[] {line[0], currentY}, false);
                        lDir = LineDir.FORWARD;
                    }
                    currentY += lineStep;
                    if (currentY > maxY) {
                        grblCtrl.programMove(orig, false);
                        mode = Mode.IDLE;
                    } else {
                        grblCtrl.programMove(new float[] {0.0f, lineStep}, true);
                    }
                    //TODO: add delay
                    grblMode = null;
                    sysMgr.getGrblStatusMonitor().startMonitoringTask();
                }
                sysMgr.getGrblStatusMonitor().removeModeListener(modeListener);
                grblCtrl.setMode(GrblController.Mode.JOGGING);                
            }
        };
        progThread = new Thread(prog);
        progThread.start();
    }
    
    public void stopProgram() {
        
    }
    
    public void pauseProgram() {
        
    }
    
    public void setOverScan(float val) {
        overScan = val;
    }
    
    public float[] getLineSpan() {
        float[] origin = sysMgr.getGrblController().getOrigin();
        float[] imgDim = sysMgr.getImageController().getDimensions();
        float maxTravelX = 0.0f;
        Map<GrblSettings.GrblSetting, Float> settings = sysMgr.getGrblSettings().getSettings();
        GrblSettings.GrblSetting key = GrblSettings.GrblSetting.MAX_TRAVEL_X;
        if (settings.containsKey(key)) {
            maxTravelX = settings.get(key);
        }
        float[] lineSpan = new float[2];
        lineSpan[0] = Math.max(0.0f, origin[0] - overScan);
        lineSpan[1] = Math.min(maxTravelX, imgDim[0] + origin[0] + overScan);
        return lineSpan;
    }
    
    public void setExpTime(float val) {
        expTime = val;
    }
    
    private float computeMoveCompletionTime(float dist, float feedRate, float accRate) {
        
        return 0.0f;
    }
    
    private void schedulePeriodicWithDelay(int delay, int interval) {
        
    }
}


