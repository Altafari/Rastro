package rastro.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rastro.controller.ICommCommand;

public class GrblStatus {
    public enum Mode {Idle, Run, Hold, Door, Home, Alarm, Check};
    private Mode mode;
    private float[] mPos = {0.0f, 0.0f, 0.0f};
    private int pBuff;
    private int rxBuff;
    private static final byte repSetting = 11;
    private static final Pattern modePattern = Pattern.compile("^([A-Z][a-z]+)");
    private static final Pattern mPosPattern = 
            Pattern.compile("MPos:([0-9]+\\.[0-9]+),([0-9]+\\.[0-9]+),([0-9]+\\.[0-9]+)");
    private static final Pattern pBuffPattern = Pattern.compile("Buf:([0-9]+)");
    private static final Pattern rxBuffPattern = Pattern.compile("RX:([0-9]+)");
    
    private class ReadCommand implements ICommCommand {

        @Override
        public boolean sendData(OutputStream os) throws IOException {
            os.write('?');            
            return true;
        }

        @Override
        public boolean receiveData(InputStream is) throws IOException {
            boolean isStarted = false;
            int nRead;
            byte[] buff = new byte[1];
            StringBuilder sb = new StringBuilder();
            while(true) {
                nRead = is.read(buff);
                if (nRead == 0) {
                    return false;
                }
                if (isStarted) {
                    if (buff[0] == '\n') {
                        return parseString(sb.toString());
                    }
                    sb.append(new String(buff));
                } else {
                    if (buff[0] == '<') {
                        isStarted = true;
                    }
                }
            }
        }

        @Override
        public int getTimeout() {
            return DEFAULT_TIMEOUT;
        }
        
        private boolean parseString(String s) {
            try {
                if (!parseMode(s)) {
                    return false;
                }
                if (!parseMPos(s)) {
                    return false;
                }
                if (!parsePBuff(s)) {
                    return false;
                }
                if (!parseRxBuff(s)) {
                    return false;
                }
            } catch (IllegalArgumentException e) {
                return false;
            }
            return true;
        }
        
        private boolean parseMode(String s) {
            Matcher matcher = modePattern.matcher(s);
            if (!matcher.find()) {
                return false;
            }            
            mode = Mode.valueOf(matcher.group(1));            
            return true;
        }
        
        private boolean parseMPos(String s) {
            Matcher matcher = mPosPattern.matcher(s);
            if (!matcher.find()) {
                return false;
            }            
            for (int i = 0; i < mPos.length; i++) {
                mPos[i] = Float.parseFloat(matcher.group(i + 1));
            }
            return true;
        }
        
        private boolean parsePBuff(String s) {
            Matcher matcher = pBuffPattern.matcher(s);
            if (!matcher.find()) {
                return false;
            }
            pBuff = Integer.parseInt(matcher.group(1));
            return true;
        }
        
        private boolean parseRxBuff(String s) {
            Matcher matcher = rxBuffPattern.matcher(s);
            if (!matcher.find()) {
                return false;
            }
            rxBuff = Integer.parseInt(matcher.group(1));
            return true;
        }
    }
    
    public ICommCommand getReadStatusCommand() {
        return new ReadCommand();
    }
}
