package rastro.ui;

import javax.swing.Box;
import javax.swing.JLabel;

import rastro.system.IStateListener;
import rastro.system.SystemManager;

public class ImageInfoPanel extends BorderedTitledPanel implements IStateListener {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private SystemManager sysMgr;
    private JLabel pixelSize;
    private JLabel dimensions;
    
    public ImageInfoPanel(SystemManager sysManager) {
        super("Image info");
        sysMgr = sysManager;
        pixelSize = new JLabel();
        dimensions = new JLabel();
        this.add(Box.createHorizontalGlue());
        this.add(pixelSize);
        this.add(Box.createHorizontalGlue());
        this.add(dimensions);
        this.add(Box.createHorizontalGlue());
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
    
    @Override
    protected int getRackHeight() {
        return (int)(RACK_HEIGHT * 0.8f);
    }

    @Override
    public void stateChanged() {
        float[] dim = sysMgr.getImageController().getDimensions();
        int[] pxSz = sysMgr.getImageController().getSize();
        pixelSize.setText(String.format("Image size: W:%s H:%s", formatPx(pxSz[0]), formatPx(pxSz[1])));
        dimensions.setText(String.format("Dimensions: W:%s H:%s", formatMm(dim[0]), formatMm(dim[1])));
    }
}
