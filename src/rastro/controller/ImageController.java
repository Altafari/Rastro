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

    public ImageController() {
        img = null;
    }

    public RasterResult loadImage(File file) {
        img = null;
        width = 0;
        height = 0;
        if (file == null) {
            return RasterResult.error;
        }
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            return RasterResult.error;
        }
        if (img == null) {
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
        int x = (int)(mmX / pixelSize); // Actually cast is an implicit floor function
        int y = (height - 1) - (int)(mmY / pixelSize);
        if (!isValidCoord(x, y)) {
            return false;    // Avoid "border" effect
        }
        int color = img.getRGB(x, y) & 0x00FFFFFF;
        return color == 0;
    }
    
    public boolean isLoaded() {
        return img != null;
    }
    
    public float[] getDimensions() {
        return new float[] {width * pixelSize, height * pixelSize};
    }
    
    public int[] getSize() {
        return new int[] {width, height};
    }
    
    private boolean isValidCoord(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
