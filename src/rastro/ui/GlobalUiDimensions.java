package rastro.ui;

import java.awt.Dimension;

public enum GlobalUiDimensions {
    IMAGE_FILE_FIELD(180, 18),
    IMAGE_DPI_FIELD(40, 18),
    LINE_START_PANEL(500, 200),
    RACK_SIZE(500, 70);
    
    public final Dimension dim;
    
    GlobalUiDimensions(int w, int h) {
        dim = new Dimension(w, h);
    }
}
