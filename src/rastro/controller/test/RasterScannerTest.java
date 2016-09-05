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
    
    private static final boolean shape3diamond[][] = {{false, true, false},
                                                     {true,  true, true},
                                                     {false, true, false}};
    
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
        assertEquals(10, result.size());
    }
    
    @Test
    public void scanWithDiamondShouldReturnProperImage() {
        final int hSize = 8;
        final int vSize = 5;
        final boolean[][] testImage = {{false, false, false, false, false, false, false, false},
                                       {false, false, false, false, false, false, false, true},
                                       {false, false, false, true,  false, false, false, false},
                                       {false, false, false, false, false, false, false, false},
                                       {true,  false, false, false, false, false, false, false}};
        
        final boolean[][] expectedImage = {{true,  true,  true,  true,  true,  true,  true,  false},
                                           {true,  true,  true,  false, true,  true,  false, false},
                                           {true,  true,  false, false, false, true,  true,  false},
                                           {false, true,  true,  false,  true, true,  true,  true },
                                           {false, false, true,  true,  true,  true,  true,  true }};
        
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
        RasterScanner rs = new RasterScanner(hSize, vSize, 1, 1, shape3diamond, 1);
        rs.loadImage(imCon);
        Iterator<boolean[]> it = rs.iterator();
        ArrayList<boolean[]> result = new ArrayList<boolean[]>();
        while (it.hasNext()) {
            result.add(it.next());
        }
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(i).length; j++) {
                assertEquals(expectedImage[i][j], result.get(i)[j]);
            }
        }
    }

    @Test
    public void scanAtMultipleStepShouldReturnTheFractionNumberOfLines() {
        for (int stepSize = 1; stepSize < 9; stepSize++) {
            RasterScanner rs = new RasterScanner(16, 16, 1, 1, shape5dot, stepSize);
            rs.loadImage(imCon);
            Iterator<boolean[]> it = rs.iterator();
            ArrayList<boolean[]> result = new ArrayList<boolean[]>();
            while (it.hasNext()) {
                result.add(it.next());
            }
            assertEquals((int)Math.ceil(16.0f / stepSize), result.size());
        }
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
            assertEquals(16, b.length);
        }
    }

    @Test
    public void scanAtShouldReturnCorrectLinesWihtDotPattern() {
        final int hSize = 64;
        final int vSize = 32;
        Random rng = new Random();
        for (int stepSize = 1; stepSize < 10; stepSize++) {        
            boolean[][] img = new boolean[vSize][hSize];
            for(boolean[] row: img) {
                for(int i = 0; i < row.length; i++) {
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
                    assertEquals(!testImage[i * stepSize][j], result.get(i)[j]);
                }
            }
        }
    }
}
