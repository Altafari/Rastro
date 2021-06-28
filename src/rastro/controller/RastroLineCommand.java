package rastro.controller;

import java.io.IOException;

import rastro.comm.ISerialPort;

public class RastroLineCommand extends RastroCommand {

	private static final byte LINE_HDR[] = { 'L', 'N' };

	public RastroLineCommand(int lineLength) {
		super(lineLength);
		int buffSize = (lineLen + 7) / 8 + LINE_HDR.length + CRC_LEN;
		txBuffer = new byte[buffSize];
	}

	public void packLine(boolean isInverted, boolean[] line) {
		if (isInverted) {
			packInvertedLine(line, LINE_HDR.length);
		} else {
			packStraightLine(line, LINE_HDR.length);
		}
	}

	public void blindLine() {
		packStraightLine(new boolean[super.lineLen], LINE_HDR.length);
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

	@Override
	public boolean invoke(ISerialPort port) {
		putHeader(LINE_HDR);
		computeCRC16(txBuffer);
		try {
			port.write(txBuffer);
		} catch (IOException e) {
			return false;
		}
		return receiveAck(port);
	}
}
