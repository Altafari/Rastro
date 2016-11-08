package rastro.controller;

import java.util.Iterator;
import java.util.Map;
import rastro.model.GrblSettings;
import rastro.model.GrblStatusMonitor;
import rastro.model.GrblStatusMonitor.IModeListener;
import rastro.model.ProgramTaskSettings;
import rastro.system.SystemManager;
import rastro.ui.ProgramControlPanel;

public class ProgramController {
    
    public enum Mode {STOP, RUN, PAUSE}
    private enum LineDir {FORWARD, BACK}
    private volatile Mode mode;
    private SystemManager sysMgr;
    private Runnable prog;
    private Thread progThread;
    
    public ProgramController(SystemManager sysManager) {
        mode = Mode.STOP;
        sysMgr = sysManager;
    }
    
    public synchronized void startProgram() {
        if (mode != Mode.STOP || !sysMgr.getImageController().isLoaded()) {
            return;
        }            
        mode = Mode.RUN;
        ProgramControlPanel pcp = sysMgr.getProgramControlPanel();
        final ProgramTaskSettings pts = new ProgramTaskSettings(sysMgr, pcp.getBeamRad(), pcp.getExpTime());
        final Iterator<boolean[]> lines = pts.getScannerIterator();
        final RastroLineCommand lineCommand = pts.getLineCommand();
        final float[] origin = sysMgr.getGrblController().getOrigin();
        final float[] xSpan = getLineSpan();
        final float yStep = pts.getLineStep();
        final float feedRate = pts.getMaxFeedRate();
        prog = new Runnable() {
            GrblStatusMonitor.Mode grblMode;
            LineDir lDir = LineDir.FORWARD;
            @Override
            public void run() {
                GrblController grblCtrl = sysMgr.getGrblController();
                CommController rastroCtrl = sysMgr.getRastroCommController();
                grblCtrl.setMode(GrblController.Mode.PROGRAM);
                rastroCtrl.sendCommand(pts.getConfigCommand());
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
                lineCommand.packLine(false, lines.next());
                rastroCtrl.sendCommand(lineCommand);
                float currentY = origin[1];
                grblCtrl.programMove(new float[] {xSpan[0], currentY}, false, 0.0f);
                while (mode == Mode.RUN || mode == Mode.PAUSE) {
                    while (mode == Mode.PAUSE) {
                        synchronized(this) {
                            try {
                                this.wait(100);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    synchronized (this) {
                    if (grblMode != GrblStatusMonitor.Mode.Idle) {
                            try {
                                this.wait();
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    }
                    if (lDir == LineDir.BACK || pts.isZigZag()) {
                        lineCommand.packLine(false, lines.next());
                        rastroCtrl.sendCommand(lineCommand);
                    }
                    if (lDir == LineDir.FORWARD) {                        
                        grblCtrl.programMove(new float[] {xSpan[1], currentY}, false, feedRate);
                        lDir = LineDir.BACK;
                    } else {                        
                        grblCtrl.programMove(new float[] {xSpan[0], currentY}, false, feedRate);
                        lDir = LineDir.FORWARD;
                    }
                    if (lines.hasNext()) {
                        if (lDir == LineDir.BACK || pts.isZigZag()) {
                            currentY += yStep;
                            grblCtrl.programMove(new float[] {0.0f, yStep}, true, 0.0f);
                        }
                    } else {
                        grblCtrl.programMove(origin, false, 0.0f);
                        mode = Mode.STOP;
                    }
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
        mode = Mode.STOP;
    }
    
    public void pauseProgram() {
        if (mode == Mode.RUN) {
            mode = Mode.PAUSE;
        } else if (mode == Mode.PAUSE) {
            mode = Mode.RUN;
        }
    }
    
    public float[] getLineSpan() {
        float overScan = sysMgr.getProgramControlPanel().getOverScan();
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
 /*
    private float computeMoveCompletionTime(float dist, float feedRate, float accRate) {
        float accTime = feedRate / accRate;
        float accDist = feedRate * 0.5f * accTime;
        if (accDist >= 0.5f * dist) {
            // No steady feed rate region
            return 2.0f * (float) Math.sqrt(dist / accRate);    // Divide and multiply by 2
        } else {
            // Acceleration completes
            return 2.0f * accTime + (dist - 2.0f * accDist) / feedRate;
        }
    }*/
}


