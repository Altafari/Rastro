package rastro.ui;

import javax.swing.Box;
import javax.swing.JLabel;

public class ImageInfoPanel extends BorderedTitledPanel {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private JLabel pixelSize;
    private JLabel dimensions;
    public ImageInfoPanel() {
        super("Image info");
        pixelSize = new JLabel();
        dimensions = new JLabel();
        this.add(Box.createHorizontalGlue());
        this.add(pixelSize);
        this.add(Box.createHorizontalGlue());
        this.add(dimensions);
        this.add(Box.createHorizontalGlue());
        resetImageInfo();
    }
    
    public void resetImageInfo() {
        setImageInfo(0, 0, 0.0f, 0.0f);
    }
    
    public void setImageInfo(int width, int height, float wMm, float hMm) {
        pixelSize.setText(String.format("Image size: W:%s H:%s", formatPx(width), formatPx(height)));
        dimensions.setText(String.format("Dimensions: W:%s H:%s", formatMm(wMm), formatMm(hMm)));
    }
    
    private String formatPx(int i) {
        return i == 0 ? "n/a" : String.format("%dpx", i);
    }
    
    private String formatMm(float f) {
        return f == 0.0f || Float.isInfinite(f) || Float.isNaN(f) ? "n/a" : String.format("%.1fmm", f);
    }
}
