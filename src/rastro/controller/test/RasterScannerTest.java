package rastro.controller.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.Mock;
import org.mockito.Mockito;

import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import rastro.controller.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RasterScanner.class,
                 ImageController.class})

public class RasterScannerTest {
    private static final boolean shape3dot[][] = {{false, false, false},
                                                  {false, true , false},
                                                  {false, false, false}};
    
    private static final boolean shape5dot[][] = {{false, false, false, false, false},
                                                  {false, false, false, false, false},
                                                  {false, false, true , false, false},
                                                  {false, false, false, false, false},
                                                  {false, false, false, false, false}};
    
    @Mock private ImageController imCon;

    @BeforeClass
    public static void runOnceBeforeClass() {
        
    }
    
    @Before
    public void runBeforeTestMethod() {
        Mockito.when(imCon.isBlack(Mockito.anyFloat(), Mockito.anyFloat())).thenReturn(true);
    }

    @Test
    public void scanAtSingleStepShouldReturnTheSameNumberOfLines() {
        RasterScanner rs = new RasterScanner(10, 10, 1, 1, shape3dot, 1);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        assertEquals(result.size(), 10);
    }
    

    @Test
    public void scanAtMultipleStepShouldReturnTheFractionNumberOfLines() {
        RasterScanner rs = new RasterScanner(16, 16, 1, 1, shape5dot, 4);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        assertEquals(result.size(), 4);
    }
    
    
    @Test
    public void scanAtMultipleStepShouldReturnLineOfProperLength() {
        RasterScanner rs = new RasterScanner(16, 16, 1, 1, shape5dot, 4);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        for(boolean[] b : result) {
            assertEquals(b.length, 16);
        }
    }
    
    @Test
    public void scanAtShouldReturnCorrectLines() {               
        Mockito.when(imCon.isBlack(Mockito.anyFloat(), Mockito.anyFloat()))
        .thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock inv) {
                float x = inv.getArgumentAt(0, float.class);
                float y = inv.getArgumentAt(0, float.class);
                //TODO here
                return true;
            }
        });
        RasterScanner rs = new RasterScanner(16, 16, 1, 1, shape3dot, 1);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        for(boolean[] b : result) {
            assertEquals(b.length, 16);
        }
    }
}
