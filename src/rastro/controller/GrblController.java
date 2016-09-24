package rastro.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import rastro.system.SystemManager;

public class GrblController {
    
    public enum Mode { PROGRAM, JOGGING };
    private Mode mode;
    private SystemManager sysMgr;
    
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
        mode = Mode.JOGGING;
    }
    
    public void joggingMove(float offsetX, float offsetY) {
        if (mode != Mode.JOGGING) {
            return;
        } else {
            int pBuffState = sysMgr.getGrblStatusMonitor().getPlannerBufferState();
            if (pBuffState > 1) {   // TODO: this staff doesn't work now, implement monitor thread
                return;
            }
            String cmdStr = String.format("G91 G0 X%f Y%f\n", offsetX, offsetY);
            ICommCommand moveCmd = new ControlCommand(cmdStr);
            sysMgr.getGrblCommController().sendCommand(moveCmd);
        }
    }
    
    public void programMove(float offsetX, float offsetY) {
        
    }
}
