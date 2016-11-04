package rastro.controller;

import java.io.IOException;
import java.io.OutputStream;

public class RastroConfigCommand extends RastroCommand {
    
    public enum ScanMode {ZIGZAG, PROGRESSIVE}
    private static final byte[] CFG_HDR = {'C', 'F'};
    private static final int CFG_FIELDS_LEN = 7;    
    private short lnOffset;
    private short expTime;
    private byte mode;
    
    public RastroConfigCommand(int lineLength) {
        super(lineLength);
        int buffLength = CFG_HDR.length + CFG_FIELDS_LEN + CRC_LEN; 
        txBuffer = new byte[buffLength];
    }

    public void setOffset(int lineOffset) {
        lnOffset = (short) lineOffset;
    }
    
    public void setExpTime(int expositionTime) {
        expTime = (short) expositionTime;
    }
    
    public void setScanMode(ScanMode scanMode) {
        mode = (scanMode == ScanMode.PROGRESSIVE)? (byte)1 : 0;
    }
    
    private int putShort(short x, int offset) {
        txBuffer[offset] = (byte)(x & 0xFF);
        txBuffer[offset + 1] = (byte)(x >> 8);
        return offset + 2;
    }
    
    @Override
    public boolean sendData(OutputStream os) throws IOException {
        putHeader(CFG_HDR);
        int offset = CFG_HDR.length;
        offset = putShort((short) lineLen, offset);
        offset = putShort((short) lnOffset, offset);
        offset = putShort((short) expTime, offset);
        txBuffer[offset] = mode;
        computeCRC16(txBuffer);
        os.write(txBuffer);        
        return true;
    }
}
