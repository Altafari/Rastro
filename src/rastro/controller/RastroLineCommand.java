package rastro.controller;

import rastro.controller.CommController.CommResult;

public class RastroLineCommand extends RastroCommand {

    private static final byte LINE_HDR[] = {'L', 'N'};  
    public RastroLineCommand(int lineLength, CommController commController) {
        super(lineLength, commController);
        int buffSize = (int) Math.ceil((float) lineLen / 8.0f) + LINE_HDR.length + CRC_LEN;
        txBuffer = new byte[buffSize];
    }
    
    public CommResult sendLine(boolean isInverted, boolean[] line) {
        putHeader(LINE_HDR);
        if (isInverted) {
            packInvertedLine(line, LINE_HDR.length);
        } else {
            packStraightLine(line, LINE_HDR.length);
        }
        return send();
    }

    private void packStraightLine(boolean[] line, int buffOffset) {
        for (int i = 0; i < line.length; i++) {
            int buffIdx = i >> 3;
            int bitShift = i & 7;
            if (bitShift == 0) {
                txBuffer[buffIdx + buffOffset] = line[i] ? (byte) 1 : 0;
            } else {
                txBuffer[buffIdx + buffOffset] |= line[i] ? (byte) 1 << bitShift : 0;
            }
        }
    }

    private void packInvertedLine(boolean[] line, int buffOffset) {
        for (int i = line.length - 1, j = 0; i >= 0; i--, j++) {
            int buffIdx = j >> 3;
            int bitShift = j & 7;
            if (bitShift == 0) {
                txBuffer[buffIdx + buffOffset] = line[i] ? (byte) 1 : 0;
            } else {
                txBuffer[buffIdx + buffOffset] |= line[i] ? (byte) 1 << bitShift : 0;
            }
        }
    }

}
