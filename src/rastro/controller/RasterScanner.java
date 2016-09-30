package rastro.controller;

import java.util.Iterator;

public class RasterScanner implements Iterable<boolean[]> {

    private float spmX;
    private float spmY;
    private int width;
    private int height;
    private int nLine;
    private boolean[][] buffer;
    private boolean[][] shape;
    private int vPadding;
    private int hPadding;
    private int lnStep;
    private ImageController imCon;

    public RasterScanner(float stepPermmX, float stepPermmY, float sizeXmm, float sizeYmm,
            boolean[][] scShape, int lineStep) {
        spmX = stepPermmX;
        spmY = stepPermmY;
        width = (int)Math.round(sizeXmm * spmX);
        height = (int)Math.round(sizeYmm * spmY);
        shape = scShape;
        vPadding = (shape.length - 1) / 2; // both scannerShape dimensions must be odd
        hPadding = (shape[0].length - 1) / 2;
        lnStep = lineStep;
        buffer = new boolean[shape.length][width + 2 * hPadding];
    }

    public void loadImage(ImageController imgController) {
        imCon = imgController;
        preFillBuffer();
    }

    public int getLineLength() {
        return width;
    }

    private void preFillBuffer() {
        nLine = -vPadding;
        while(nLine <= vPadding) {
            advanceBuffer();
        }
    }

    private void fillLine(boolean[] row, int y) {
        float xInc = 1.0f / spmX;
        float yInc = 1.0f / spmY;
        for (int x = 0; x < row.length; x++) {
            row[x] = imCon.isBlack((x - hPadding) * xInc, y * yInc);
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
        boolean[] res = new boolean[width]; // When shape "true" pixel intersects with image "true", then isOn false
        for (int x = 0; x < width; x++) {
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
                return nLine <= height + vPadding;
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
