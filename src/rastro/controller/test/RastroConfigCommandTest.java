package rastro.controller.test;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rastro.controller.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RastroCommand.class,
                 CommController.class})

public class RastroConfigCommandTest {

    @BeforeClass
    public static void runOnceBeforeClass() {            
    }

    @Before
    public void runBeforeTestMethod() {
    }

    @Test
    public void sendLineShouldSendProperData() {
        final int[] LINE_LEN = {856, 352, 88, 11542};
        final int[] LINE_OFFSET = {22, 1582, 7443, 8887};
        final int[] EXP_TIME = {1506, 41, 4577, 6896};
        final boolean[] MODE = {false, false, true, false};
        for (int i = 0; i < MODE.length; i++) {
            RastroConfigCommand rc = new RastroConfigCommand(LINE_LEN[i]);
            rc.setOffset(LINE_OFFSET[i]);
            rc.setExpTime(EXP_TIME[i]);
            rc.setScanMode(MODE[i]);
            ICommCommand icc = rc;
            byte[] buffer = icc.getRequest();
            assertEquals('C', buffer[0]);
            assertEquals('F', buffer[1]);
            assertEquals((byte) (LINE_LEN[i] >> 8), buffer[2]);
            assertEquals((byte) (LINE_LEN[i] &  0xFF), buffer[3]);
            assertEquals((byte) (LINE_OFFSET[i] >> 8), buffer[4]);
            assertEquals((byte) (LINE_OFFSET[i] &  0xFF), buffer[5]);
            assertEquals((byte) (EXP_TIME[i] >> 8), buffer[6]);
            assertEquals((byte) (EXP_TIME[i] &  0xFF), buffer[7]);
            assertEquals((byte) (MODE[i] ? 0 : 1), buffer[8]);
            assertEquals(11, buffer.length);
        }
    }
}