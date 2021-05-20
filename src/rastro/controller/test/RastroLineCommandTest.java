package rastro.controller.test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.*;

import rastro.controller.*;

public class RastroLineCommandTest {

	@BeforeClass
	public static void runOnceBeforeClass() {
	}

	@Before
	public void runBeforeTestMethod() {
	}

	@Test
	public void sendStraightLineShouldProvideProperlyPackagedLine() throws IOException {
		final boolean[] line = { true, false, false, true, true, false, false, false, true, true };
		final byte[] straightLine = { 'L', 'N', (byte) 0x19, (byte) 0x03 };
		RastroLineCommand rc = new RastroLineCommand(line.length);
		ICommCommand icc = rc;
		rc.packLine(false, line);
		icc.invoke(new SerialPortMock() {
			@Override
			public void write(byte[] buffer) {
				assertEquals('L', buffer[0]);
				assertEquals('N', buffer[1]);
				assertEquals(straightLine[2], buffer[2]);
				assertEquals(straightLine[3], buffer[3]);
			}
		});
	}

	@Test
	public void sendInvertedLineShouldProvideProperlyPackagedLine() throws IOException {
		final boolean[] line = { true, false, false, true, true, false, false, false, true, true };
		final byte[] invertedLine = { 'L', 'N', (byte) 0x63, (byte) 0x02 };
		RastroLineCommand rc = new RastroLineCommand(line.length);
		rc.packLine(true, line);
		ICommCommand icc = rc;
		icc.invoke(new SerialPortMock() {
			@Override
			public void write(byte[] buffer) {
				assertEquals('L', buffer[0]);
				assertEquals('N', buffer[1]);
				assertEquals(invertedLine[2], buffer[2]);
				assertEquals(invertedLine[3], buffer[3]);
			}
		});
	}

	@Test
	public void sendLineShouldProvideBufferOfCorrectLength() throws IOException {
		final boolean[] line = new boolean[128];
		RastroLineCommand rc = new RastroLineCommand(line.length);
		rc.packLine(false, line);
		ICommCommand icc = rc;
		icc.invoke(new SerialPortMock() {
			@Override
			public void write(byte[] buffer) {
				assertEquals(20, buffer.length);
			}
		});
	}

	@Test
	public void sendLineShouldProperlyComputeCrc() throws IOException {
		final boolean[] line = { true, false, false, false, false, false, true, false, false, true, false, false, false,
				false, true, false, true, true, false, false, false, false, true, false };
		final byte crcHi = (byte) 0xA3;
		final byte crcLo = (byte) 0x27;
		RastroLineCommand rc = new RastroLineCommand(line.length);
		rc.packLine(false, line);
		ICommCommand icc = rc;
		icc.invoke(new SerialPortMock() {
			@Override
			public void write(byte[] buffer) {
				assertEquals(crcLo, buffer[5]);
				assertEquals(crcHi, buffer[6]);
			}
		});
	}
}
