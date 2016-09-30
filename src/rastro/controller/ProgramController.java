package rastro.controller;

import rastro.system.SystemManager;

public class ProgramController {
    
    public enum Mode {IDLE, RUN, PAUSE}
    private Mode mode;
    private SystemManager sysMgr;
    
    public ProgramController(SystemManager sysManager) {
        mode = Mode.IDLE;
        sysMgr = sysManager;
    }
    
    public void startProgram() {
        
        // TODO: pull settings - spmX and spmY
        // pull settings - scanning shape radius
        // pull settings - working area size -> image size
    }
    
    public void stopProgram() {
        
    }
    
    public void pauseProgram() {
        
    }
}


