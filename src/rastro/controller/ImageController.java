package rastro.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import rastro.ui.ImageInfoPanel;

public class ImageController {
    public enum RasterResult {
        ok, error
    };

    public final float MM_PER_INCH = 25.4f;
    private BufferedImage img;
    private ImageInfoPanel infoPanel;
    private float pixelSize;
    private int width;
    private int height;

    public ImageController(ImageInfoPanel imageInfoPanel) {
        img = null;
        infoPanel = imageInfoPanel;
    }

    public RasterResult loadImage(File file) {
        img = null;
        if (file == null) {
            return RasterResult.error;
        }
        try {
            img = ImageIO.read(file);
        } catch (IOException e) {
            infoPanel.resetImageInfo();
            return RasterResult.error;
        }
        if (img == null) {
            infoPanel.resetImageInfo();
            return RasterResult.error;
        }
        width = img.getWidth();
        height = img.getHeight();
        infoPanel.setImageInfo(width, height, width * pixelSize, height * pixelSize);
        return RasterResult.ok;

    }

    public void setDpi(int dpiVal) {
        pixelSize = MM_PER_INCH / (float) dpiVal;
        infoPanel.setImageInfo(width, height, width * pixelSize, height * pixelSize);
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
    
    public boolean isLoaded() {
        return img != null;
    }
    
    public float[] getDimensions() {
        return new float[] {width * pixelSize, height * pixelSize};
    }
    
    private boolean isValidCoord(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
