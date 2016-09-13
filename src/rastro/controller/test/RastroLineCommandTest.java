package rastro.controller.test;

import static org.junit.Assert.*;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rastro.controller.*;
import rastro.controller.CommController.CommResult;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CommController.class})

public class RastroLineCommandTest {

    @Mock private CommController commCtrl;

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
        Mockito.when(commCtrl.write(Mockito.any(byte[].class))).thenAnswer(
                new Answer<CommResult>() {
                    @Override
                    public CommResult answer(InvocationOnMock invocation) throws Throwable {
                        byte[] buffer = invocation.getArgumentAt(0, byte[].class);                        
                        assertEquals('L', buffer[0]);
                        assertEquals('N', buffer[1]);                            
                        assertEquals(straightLine[2], buffer[2]);
                        assertEquals(straightLine[3], buffer[3]);
                        return CommResult.ok;
                    }
                });
        RastroLineCommand rc = new RastroLineCommand(line.length);
        //TODO: Change test
        //rc.sendLine(false, line);           
    }
    
    @Test
    public void sendInvertedLineShouldProvideProperlyPackagedLine() {
        final boolean[] line = {true, false, false, true, true, false, false, false, true, true};
        final byte[] invertedLine = {'L', 'N', (byte)0x63, (byte)0x02};
        Mockito.when(commCtrl.write(Mockito.any(byte[].class))).thenAnswer(
                new Answer<CommResult>() {
                    @Override
                    public CommResult answer(InvocationOnMock invocation) throws Throwable {
                        byte[] buffer = invocation.getArgumentAt(0, byte[].class);                        
                        assertEquals('L', buffer[0]);
                        assertEquals('N', buffer[1]);                            
                        assertEquals(invertedLine[2], buffer[2]);
                        assertEquals(invertedLine[3], buffer[3]);
                        return CommResult.ok;
                    }
                });
        RastroLineCommand rc = new RastroLineCommand(line.length, commCtrl);
        rc.sendLine(true, line);           
    }

    @Test
    public void sendLineShouldProvideBufferOfCorrectLength() {
        final boolean[] line = new boolean[128];
        Mockito.when(commCtrl.write(Mockito.any(byte[].class))).thenAnswer(
                new Answer<CommResult>() {
                    @Override
                    public CommResult answer(InvocationOnMock invocation) throws Throwable {
                        byte[] buffer = invocation.getArgumentAt(0, byte[].class);                        
                        assertEquals(20, buffer.length);
                        return CommResult.ok;
                    }
                });
        RastroLineCommand rc = new RastroLineCommand(line.length, commCtrl);
        rc.sendLine(false, line);           
    }
   
    @Test
    public void sendLineShouldProperlyComputeCrc() {
        final boolean[] line = {true,  false, false,  false, false, false, true,  false,
                                false, true,  false,  false, false, false, true,  false,
                                true,  true,  false,  false, false, false, true,  false };
        final byte crcHi = (byte)0xA3;
        final byte crcLo = (byte)0x27;
        Mockito.when(commCtrl.write(Mockito.any(byte[].class))).thenAnswer(
                new Answer<CommResult>() {
                    @Override
                    public CommResult answer(InvocationOnMock invocation) throws Throwable {
                        byte[] buffer = invocation.getArgumentAt(0, byte[].class);                        
                        assertEquals(crcHi, buffer[5]);
                        assertEquals(crcLo, buffer[6]);
                        return CommResult.ok;
                    }
                });
        RastroLineCommand rc = new RastroLineCommand(line.length, commCtrl);
        rc.sendLine(false, line);           
    }
}
