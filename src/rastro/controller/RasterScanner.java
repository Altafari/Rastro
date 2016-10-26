package rastro.controller;

import java.util.Iterator;

public class RasterScanner implements Iterable<boolean[]> {

    private float incX;
    private float incY;
    private int[] rSize;
    private int nLine;
    private boolean[][] buffer;
    private boolean[][] shape;
    private int vPadding;
    private int hPadding;
    private int lnStep;
    private ImageController imCon;

    public RasterScanner(float stepPermmX, float stepPermmY, int[] rasterSize,
            boolean[][] scShape, int lineStep) {
        incX = 1.0f / stepPermmX;
        incY = 1.0f / stepPermmY;
        rSize = rasterSize;
        shape = scShape;
        vPadding = (shape.length - 1) / 2; // both scannerShape dimensions must be odd
        hPadding = (shape[0].length - 1) / 2;
        lnStep = lineStep;
        buffer = new boolean[shape.length][rSize[0] + 2 * hPadding];
    }

    public void loadImage(ImageController imgController) {
        imCon = imgController;
        preFillBuffer();
    }

    private void preFillBuffer() {
        nLine = -vPadding;
        while(nLine <= vPadding) {
            advanceBuffer();
        }
    }

    private void fillLine(boolean[] row, int y) {
        for (int x = 0; x < row.length; x++) {
            row[x] = imCon.isBlack((x - hPadding) * incX, y * incY);
        }
    }

    private void advanceBuffer() {
        boolean[] firstRow = buffer[0];
        for (int i = 0; i < buffer.length - 1; i++) {
            buffer[i] = buffer[i + 1];
        }
        fillLine(firstRow, nLine++);
        buffer[buffer.length - 1] = firstRow;        
    }

    private boolean[] scanLine() {
        boolean[] res = new boolean[rSize[0]]; // When shape "true" pixel intersects with image "true", then isOn false
        for (int x = 0; x < rSize[0]; x++) {
            boolean isOn = true;
            for (int i = 0; i <= vPadding * 2; i++) {
                for (int j = 0; j <= hPadding * 2; j++) {
                    isOn &= !(shape[i][j] && buffer[i][j + x]);
                } // Loop is not interrupted for uniform performance
            }
            res[x] = isOn;
        }
        return res;
    }

    @Override
    public Iterator<boolean[]> iterator() {
        return new Iterator<boolean[]>() {

            @Override
            public boolean hasNext() {
                return nLine <= rSize[1] + vPadding;
            }

            @Override
            public boolean[] next() {
                boolean[] line = scanLine();
                for (int i = 0; i < lnStep; i++) {
                    advanceBuffer();
                }
                return line;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
