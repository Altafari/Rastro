package rastro.ui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;

import rastro.controller.ImageController;
import static rastro.ui.GlobalUiDimensions.*;

public class ImageControlPanel extends BorderedTitledPanel {
    private static final long serialVersionUID = 1L;
    private JTextField fileName;
    private JButton btnBrowse;
    private JTextField imageDpi;
    private ImageController imCon;
    
    ImageControlPanel(ImageController imgController) {
        super("Input image");
        imCon = imgController;        
        fileName = new JTextField();
        fileName.setPreferredSize(IMAGE_FILE_FIELD.dim);
        fileName.setEditable(false);
        btnBrowse = new JButton("Browse");
        btnBrowse.setActionCommand("Browse");
        //btnBrowse.addActionListener(actionListener);
        imageDpi = new JTextField();
        imageDpi.setPreferredSize(IMAGE_DPI_FIELD.dim);
        this.add(new JLabel("File:"));
        this.add(fileName);
        this.add(btnBrowse);
        this.add(new JLabel("Resolution:"));
        this.add(imageDpi);
        this.add(new JLabel("DPI"));
    }
}
