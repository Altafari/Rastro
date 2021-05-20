package rastro.controller.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;

import rastro.controller.*;
import rastro.controller.RastroConfigCommand.ScanMode;

public class RastroConfigCommandTest {

	private final int[] LINE_LEN = { 856, 352, 88, 11542 };
	private final int[] LINE_OFFSET = { 22, 1582, 7443, 8887 };
	private final int[] EXP_TIME = { 1506, 41, 4577, 6896 };
	private final ScanMode[] MODE = { ScanMode.PROGRESSIVE, ScanMode.ZIGZAG, ScanMode.ZIGZAG, ScanMode.PROGRESSIVE };
	private int i;

	@BeforeClass
	public static void runOnceBeforeClass() {
	}

	@Before
	public void runBeforeTestMethod() {
	}

	@Test
	public void sendLineShouldSendProperData() throws IOException {
		for (i = 0; i < MODE.length; i++) {
			RastroConfigCommand rc = new RastroConfigCommand(LINE_LEN[i]);
			rc.setOffset(LINE_OFFSET[i]);
			rc.setExpTime(EXP_TIME[i]);
			rc.setScanMode(MODE[i]);
			ICommCommand icc = rc;
			icc.invoke(new SerialPortMock() {
				@Override
				public void write(byte[] buffer) {
					assertEquals('C', buffer[0]);
					assertEquals('F', buffer[1]);
					assertEquals((byte) (LINE_LEN[i] & 0xFF), buffer[2]);
					assertEquals((byte) (LINE_LEN[i] >> 8), buffer[3]);
					assertEquals((byte) (LINE_OFFSET[i] & 0xFF), buffer[4]);
					assertEquals((byte) (LINE_OFFSET[i] >> 8), buffer[5]);
					assertEquals((byte) (EXP_TIME[i] & 0xFF), buffer[6]);
					assertEquals((byte) (EXP_TIME[i] >> 8), buffer[7]);
					assertEquals((byte) (MODE[i] == ScanMode.PROGRESSIVE ? 1 : 0), buffer[8]);
					assertEquals(11, buffer.length);
				}
			});
		}
	}
}