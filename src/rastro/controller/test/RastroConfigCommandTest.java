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
@PrepareForTest({RastroCommand.class,
                 CommController.class})

public class RastroConfigCommandTest {

    @Mock private CommController commCtrl;

    @BeforeClass
    public static void runOnceBeforeClass() {            
    }

    @Before
    public void runBeforeTestMethod() {
    }

    @Test
    public void sendLineShouldSendProperData() {
        class TestConfig {
            private int x;
            public void set(int val) {
                x = val;
            }
            public int get() {
                return x;
            }
        }
        final TestConfig testConfig = new TestConfig();
        final int[] LINE_LEN = {856, 352, 88, 11542};
        final int[] LINE_OFFSET = {22, 1582, 7443, 8887};
        final int[] EXP_TIME = {1506, 41, 4577, 6896};
        final boolean[] MODE = {false, false, true, false};
        // TODO: change test
        Mockito.when(commCtrl.write(Mockito.any(byte[].class))).thenAnswer(
            new Answer<CommResult>() {
                @Override
                public CommResult answer(InvocationOnMock invocation) throws Throwable {
                    byte[] buffer = invocation.getArgumentAt(0, byte[].class);
                    int i = testConfig.get();
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
                    return CommResult.ok;
                }
            });
        for (int i = 0; i < MODE.length; i++) {
            testConfig.set(i);          
            RastroConfigCommand rc = new RastroConfigCommand(LINE_LEN[i]);
            rc.setOffset(LINE_OFFSET[i]);
            rc.setExpTime(EXP_TIME[i]);
            rc.setScanMode(MODE[i]);
            commCtrl.sendCommand(rc);
        }
    }
}
