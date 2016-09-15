package rastro.controller.test;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rastro.controller.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommController.class})

public class RastroLineCommandTest {

    @BeforeClass
    public static void runOnceBeforeClass() {            
    }

    @Before
    public void runBeforeTestMethod() {
    }

    @Test
    public void sendStraightLineShouldProvideProperlyPackagedLine() {
        final boolean[] line = {true, false, false, true, true, false, false, false, true, true};
        final byte[] straightLine = {'L', 'N', (byte)0x19, (byte)0x03};
        RastroLineCommand rc = new RastroLineCommand(line.length);
        ICommCommand icc = rc;
        rc.packLine(false, line);
        byte[] buffer = icc.getRequest();
        assertEquals('L', buffer[0]);
        assertEquals('N', buffer[1]);
        assertEquals(straightLine[2], buffer[2]);
        assertEquals(straightLine[3], buffer[3]);
    }

    @Test
    public void sendInvertedLineShouldProvideProperlyPackagedLine() {
        final boolean[] line = {true, false, false, true, true, false, false, false, true, true};
        final byte[] invertedLine = {'L', 'N', (byte)0x63, (byte)0x02};
        RastroLineCommand rc = new RastroLineCommand(line.length);
        rc.packLine(true, line);
        ICommCommand icc = rc;
        byte[] buffer = icc.getRequest();
        assertEquals('L', buffer[0]);
        assertEquals('N', buffer[1]);
        assertEquals(invertedLine[2], buffer[2]);
        assertEquals(invertedLine[3], buffer[3]);
    }

    @Test
    public void sendLineShouldProvideBufferOfCorrectLength() {
        final boolean[] line = new boolean[128];
        RastroLineCommand rc = new RastroLineCommand(line.length);
        rc.packLine(false, line);
        ICommCommand icc = rc;
        byte[] buffer = icc.getRequest();
        assertEquals(20, buffer.length);
    }
   
    @Test
    public void sendLineShouldProperlyComputeCrc() {
        final boolean[] line = {true,  false, false,  false, false, false, true,  false,
                                false, true,  false,  false, false, false, true,  false,
                                true,  true,  false,  false, false, false, true,  false };
        final byte crcHi = (byte)0xA3;
        final byte crcLo = (byte)0x27;
        RastroLineCommand rc = new RastroLineCommand(line.length);
        rc.packLine(false, line);
        ICommCommand icc = rc;
        byte buffer[] = icc.getRequest();
        assertEquals(crcHi, buffer[5]);
        assertEquals(crcLo, buffer[6]);
    }

    @Test
    public void commCommandShouldReturnDefaultTimeout() {
        ICommCommand icc = new RastroLineCommand(10);
        assertEquals(ICommCommand.DEFAULT_TIMEOUT, icc.getTimeout());
    }

    @Test
    public void commResponseBufferSizeMustBeCorrect() {
        ICommCommand icc = new RastroLineCommand(10);
        assertEquals(3, icc.getResponseBufer().length);
    }

    @Test
    public void commResponseShoudReturnFalseIfIncorectNumberOfBytesReceived() {
        ICommCommand icc = new RastroLineCommand(10);
        assertEquals(false, icc.parseResponse(2));
    }

    @Test
    public void commResponseShoudReturnTrueIfResponseIsAck() {
        ICommCommand icc = new RastroLineCommand(10);
        final byte[] ack = {'A', 'C', 'K'};
        byte[] buffer = icc.getResponseBufer();
        for (int i = 0; i < ack.length; i++) {
            buffer[i] = ack[i];
        }
        assertEquals(true, icc.parseResponse(3));
    }

    @Test
    public void commResponseShoudReturnFalseIfResponseIsNak() {
        ICommCommand icc = new RastroLineCommand(10);
        final byte[] ack = {'N', 'A', 'K'};
        byte[] buffer = icc.getResponseBufer();
        for (int i = 0; i < ack.length; i++) {
            buffer[i] = ack[i];
        }
        assertEquals(false, icc.parseResponse(3));
    }
}
