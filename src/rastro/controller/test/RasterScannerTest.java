package rastro.controller.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
/*
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
    */
    @Test
    public void scanAtShouldReturnCorrectLinesWihtDotPattern() {
        final int hSize = 128;
        final int vSize = 64;
        final int stepSize = 1;
        Random rng = new Random();
        boolean[][] img = new boolean[vSize][hSize];
        int p = 0;
        for(boolean[] row: img) {
            p++;
            for(int i = 0; i < row.length; i++) {
                //row[i] = (p + i) % 2 == 0;
                row[i] = rng.nextBoolean();
            }
        }
        final boolean[][] testImage = img;
        Mockito.when(imCon.isBlack(Mockito.anyFloat(), Mockito.anyFloat()))
        .thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock inv) {
                float x = inv.getArgumentAt(0, float.class);
                float y = inv.getArgumentAt(1, float.class);
                int iX = Math.round(x * hSize);
                int iY = Math.round(y * vSize);
                if (iX >= 0 && iX < hSize && iY >= 0 && iY < vSize) {
                    return testImage[iY][iX];
                } else {
                    return false;                    
                }
            }
        });
        RasterScanner rs = new RasterScanner(hSize, vSize, 1, 1, shape5dot, stepSize);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        for(int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).length; j++) {
               /* System.out.print(!testImage[i * stepSize][j]);
                System.out.print("->");
                System.out.print(result.get(i)[j]);
                System.out.print("\t");*/
                assertEquals(result.get(i)[j], !testImage[i * stepSize][j]);
            }
            //System.out.println();
        }
    }
}
