package rastro.controller;

import java.util.Map;

import rastro.model.GrblSettings.GrblSetting;
import rastro.model.GrblStatusMonitor;
import rastro.model.GrblStatusMonitor.IModeListener;
import rastro.system.SystemManager;

public class ProgramController {
    
    public enum Mode {IDLE, RUN, PAUSE}
    private enum LineDir {FORWARD, BACK};
    private Mode mode;
    private SystemManager sysMgr;
    private Runnable prog;
    private float overrun;
    private float beamR;
    
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
        float spmX = settings.get(GrblSetting.STEP_PER_MM_X);
        float spmY = settings.get(GrblSetting.STEP_PER_MM_Y);
        // pull settings - scanning shape radius
        // pull settings - working area size -> image size
        prog = new Runnable() {
            GrblStatusMonitor.Mode grblMode;
            LineDir lDir = LineDir.FORWARD;
            @Override
            public void run() {
                float lineLen = 100.0f; // TODO: Stub
                float lineStep = 1.0f;
                int lCount = 0;
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
                        grblCtrl.programMove(new float[] {lineLen, 0.0f}, true);
                        lDir = LineDir.BACK;
                    } else {                        
                        grblCtrl.programMove(new float[] {-lineLen, 0.0f}, true);
                        lDir = LineDir.FORWARD;
                    }
                    grblCtrl.programMove(new float[] {0.0f, lineStep}, true);
                    if (++lCount > 10) {
                        mode = Mode.IDLE;
                    }
                    //TODO: add delay
                    grblMode = null;
                    sysMgr.getGrblStatusMonitor().startMonitoringTask();
                }
                sysMgr.getGrblStatusMonitor().removeModeListener(modeListener);
                grblCtrl.setMode(GrblController.Mode.JOGGING);                
            }
        };
        (new Thread(prog)).start();
    }
    
    public void stopProgram() {
        
    }
    
    public void pauseProgram() {
        
    }
    
    private float computeMoveCompletionTime(float dist, float feedRate, float accRate) {
        
        return 0.0f;
    }
    
    private void schedulePeriodicWithDelay(int delay, int interval) {
        
    }
}


