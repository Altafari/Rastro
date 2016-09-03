package rastro.controller;

import java.util.Iterator;

public class RasterScanner implements Iterable<boolean[]> {

    private int spmX;
    private int spmY;
    private int width;
    private int height;
    private int nLine;
    private boolean[][] buffer;
    private boolean[][] shape;
    private int vPadding;
    private int hPadding;
    private ImageController imCon;

    public RasterScanner(int stepPermmX, int stepPermmY, int sizeXmm, int sizeYmm, boolean[][] scannerShape) {
        spmX = stepPermmX;
        spmY = stepPermmY;
        width = sizeXmm * spmX;
        height = sizeYmm * spmY;
        shape = scannerShape;
        vPadding = (shape.length - 1) / 2; // both scannerShape dimensions must
                                           // be odd
        hPadding = (shape[0].length - 1) / 2;
        buffer = new boolean[shape.length][width + 2 * hPadding];
    }

    public void loadImage(ImageController imgController) {
        nLine = 0;
        imCon = imgController;
        preFillBuffer();
    }

    public int getLineLength() {
        return width;
    }

    private void preFillBuffer() {
        int y = -1 - vPadding; // Actual y position is -1 for convenience
        for (boolean[] row : this.buffer) {
            fillLine(row, y);
            y++;
        }
    }

    private void fillLine(boolean[] row, int y) {
        float xInc = 1.0f / spmX;
        float yInc = 1.0f / spmY;
        for (int x = 0; x < width; x++) {
            row[x] = imCon.isBlack((x - hPadding) * xInc, y * yInc);
        }
    }

    private void advanceBuffer() {
        for (int i = 0; i < buffer.length - 1; i++) {
            buffer[i] = buffer[i + 1];
        }
        fillLine(buffer[buffer.length - 1], nLine + vPadding);
        nLine++;
    }

    private boolean[] scanLine() {
        boolean[] res = new boolean[width]; // When shape "true" pixel
                                            // intersects with image "true",
                                            // then isOn false
        for (int x = 0; x < width; x++) {
            boolean isOn = true;
            for (int i = -vPadding; i <= vPadding; i++) {
                for (int j = -hPadding; j <= hPadding; j++) {
                    isOn &= !(shape[i + vPadding][j + hPadding] && buffer[i][j + x]);
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
                return nLine < height;
            }

            @Override
            public boolean[] next() {
                advanceBuffer();
                return scanLine();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
