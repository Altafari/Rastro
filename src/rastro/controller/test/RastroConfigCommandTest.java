package rastro.controller.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rastro.controller.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RastroCommand.class,
                 CommController.class})

public class RastroConfigCommandTest {
    
    private final int[] LINE_LEN = {856, 352, 88, 11542};
    private final int[] LINE_OFFSET = {22, 1582, 7443, 8887};
    private final int[] EXP_TIME = {1506, 41, 4577, 6896};
    private final boolean[] MODE = {false, false, true, false};
    private int i;
    
    private class MockStream extends OutputStream {

        @Override
        public void write(int b) throws IOException {         
        }
    }
    
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
            icc.sendData(new MockStream() {
                @Override
                public void write(byte[] buffer) {
                    assertEquals('C', buffer[0]);
                    assertEquals('F', buffer[1]);
                    assertEquals((byte) (LINE_LEN[i] &  0xFF), buffer[2]);
                    assertEquals((byte) (LINE_LEN[i] >> 8), buffer[3]);
                    assertEquals((byte) (LINE_OFFSET[i] &  0xFF), buffer[4]);
                    assertEquals((byte) (LINE_OFFSET[i] >> 8), buffer[5]);
                    assertEquals((byte) (EXP_TIME[i] &  0xFF), buffer[6]);
                    assertEquals((byte) (EXP_TIME[i] >> 8), buffer[7]);
                    assertEquals((byte) (MODE[i] ? 0 : 1), buffer[8]);
                    assertEquals(11, buffer.length);   
                }
            });
        }
    }
}