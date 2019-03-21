package rastro.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import rastro.model.GrblSettings.GrblSetting;
import rastro.model.ICoordListener;
import rastro.system.SystemManager;

public class GrblController {
    
    public enum Mode { INIT, PROGRAM, JOGGING };
    private Mode mode;
    private SystemManager sysMgr;
    private float[] origin;
    private Set<ICoordListener> originListeners;
    private final float DEFAULT_ORIGIN_X = 30.6f;
    private final float DEFAULT_ORIGIN_Y = 4.5f;
    
    private class ControlCommand implements ICommCommand {
        
        private String command; 
        private byte[] rxBuffer;
        
        public ControlCommand(String cmdStr) {
            command = cmdStr;
            rxBuffer = new byte[16];
        }
        
        @Override
        public boolean sendData(OutputStream os) throws IOException {
            byte[] buff = command.getBytes("US-ASCII");
            os.write(buff);
            return true;
        }

        @Override
        public boolean receiveData(InputStream is) throws IOException {
            int bytesRead;
            StringBuilder sb = new StringBuilder();
            while (true) {
                bytesRead = is.read(rxBuffer);
                if (bytesRead > 0) {                   
                    sb.append(new String(Arrays.copyOfRange(rxBuffer, 0, bytesRead)));
                    if (rxBuffer[bytesRead - 1] == '\n') {
                        if (sb.indexOf("ok") != -1) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                } else {
                    return false;
                }                
            }      
        }

        @Override
        public int getTimeout() {
            return DEFAULT_TIMEOUT;
        }
        
    }
    
    public GrblController(SystemManager sysManager) {
        sysMgr = sysManager;
        mode = Mode.INIT;
        origin = new float[3];
        origin[0] = DEFAULT_ORIGIN_X;
        origin[1] = DEFAULT_ORIGIN_Y;
        originListeners = new HashSet<ICoordListener>();
    }

    public void setOrigin() {
        if (!isIdleJogging()) {
            return;
        }
        origin = sysMgr.getGrblStatusMonitor().getPosition();
        notifyListeners();
    }
    
    public void homingCycle() {
        ICommCommand homingCmd = new ControlCommand("$H\n");
        sysMgr.getGrblCommController().sendCommand(homingCmd);
        mode = Mode.JOGGING;
    }
    
    public void goOrigin() {
        if (!isIdleJogging()) {
            return;
        }
        float xOffset = sysMgr.getFlipMirroringPanel().getFlipOffset();
        joggingMove(new float[]{origin[0] + xOffset, origin[1], origin[2]}, false);
    }

    public void joggingMove(float[] pos, boolean isRelative) {
        if (mode != Mode.JOGGING) {
            return;
        } else {
            int pBuffState = sysMgr.getGrblStatusMonitor().getPlannerBufferState();
            if (pBuffState > 1) {
                return;
            }
            moveCmd(pos, isRelative, 0.0f);
            sysMgr.getGrblStatusMonitor().startMonitoringTask();
        }
    }
    
    public void programMove(float[] pos, boolean isRelative, float feedRate) {
        if (mode != Mode.PROGRAM) {
            return;
        } else {
           moveCmd(pos, isRelative, feedRate);
        }
    }

    private void moveCmd(float[] pos, boolean isRelative, float feedRate) {
        String pref;
        if (isRelative) {
            pref = "G91";
        } else {
            pref = "G90";
        }
        if (feedRate == 0) {
            feedRate = sysMgr.getGrblSettings().getSettings().get(GrblSetting.MAX_RATE_X);
        }
        String cmdStr = String.format("%s G1 X%f Y%f F%f\n", pref, pos[0], pos[1], feedRate);
        ICommCommand moveCmd = new ControlCommand(cmdStr);
        sysMgr.getGrblCommController().sendCommand(moveCmd);
    }
    
    public void addOriginListener(ICoordListener l) {
        originListeners.add(l);
    }

    public void removeOriginListener(ICoordListener l) {
        originListeners.remove(l);
    }
    
    public void setMode(Mode m) {
        mode = m;
    }
    
    public float[] getOrigin() {
        return origin.clone();
    }

    public void forceNotification() {
        notifyListeners();
    }

    private void notifyListeners() {
        for (ICoordListener l : originListeners) {
            l.onChange(origin);
        }
    }

    private boolean isIdleJogging() {
        return mode == Mode.JOGGING && sysMgr.getGrblStatusMonitor().getPlannerBufferState() == 0;
    }
}
