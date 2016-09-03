package rastro.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageController {
    public enum RasterResult {
        ok, error
    };

    public final float MM_PER_INCH = 25.4f;
    private BufferedImage img;
    private float pixelSize;
    private int width;
    private int height;

    ImageController(int dpiVal) {
        setDpi(dpiVal);
    }

    public RasterResult loadImage(String filename) {
        img = null;
        try {
            img = ImageIO.read(new File(filename));
        } catch (IOException e) {
            return RasterResult.error;
        }
        width = img.getWidth();
        height = img.getHeight();
        return RasterResult.ok;

    }

    public void setDpi(int dpiVal) {
        pixelSize = MM_PER_INCH / (float) dpiVal;
    }

    public boolean isBlack(float mmX, float mmY) {
        int x = Math.round(pixelSize * mmX);
        int y = Math.round(pixelSize * mmY);
        if (!isValidCoord(x, y)) {
            return false;
        }
        int color = img.getRGB(x, y) & 0x00FFFFFF;
        return color == 0;
    }

    private boolean isValidCoord(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
