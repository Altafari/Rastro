package rastro.controller;

import rastro.controller.CommController.CommResult;

public class RastroCommand {
	
	private byte[] buffer;
	private static final byte[] LINE_HDR = {'L', 'N'};
	private static final int CRC_LEN = 2;
	private static final int CTRL_LEN = 12;
	
	public RastroCommand(int lineLength) {
		int buffSize = (int) Math.ceil((float)lineLength / 8.0f) + LINE_HDR.length + CRC_LEN;
		buffer = new byte[Math.max(buffSize, CTRL_LEN)];
	}
	
	public CommResult sendLine(boolean isInverted, boolean[] line) {
		putHeader(LINE_HDR);
		if (isInverted) {
			packInvertedLine(line, LINE_HDR.length);
		} else {
			packStraightLine(line, LINE_HDR.length);
		}
		computeCRC16(buffer.length - CRC_LEN);
		
		
		return CommResult.ok;
	}
	
	private void putHeader(byte[] hdr) {
		for (int i = 0; i < hdr.length; i++) {
			buffer[i] = hdr[i];
		}
	}
	
	private void packStraightLine(boolean[] line, int buffOffset) {
		for (int i = 0; i < line.length; i++) {
			int buffIdx = i >> 3;
			int bitShift = i & 7;
			if (bitShift == 0) {
				buffer[buffIdx + buffOffset] = line[i] ? (byte)1 : 0;
			} else {
				buffer[buffIdx + buffOffset] |= line[i] ? (byte)1 << bitShift : 0;
			}
		}
	}
	
	private void packInvertedLine(boolean[] line, int buffOffset) {
		for (int i = line.length - 1, j = 0; i >= 0; i--, j++) {
			int buffIdx = j >> 3;
			int bitShift = j & 7;
			if (bitShift == 0) {
				buffer[buffIdx + buffOffset] = line[i] ? (byte)1 : 0;
			} else {
				buffer[buffIdx + buffOffset] |= line[i] ? (byte)1 << bitShift : 0;
			}
		}
	}
	
	private void computeCRC16(int dataLen) {
		
	}
}
