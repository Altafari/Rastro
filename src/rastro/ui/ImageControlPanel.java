package rastro.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

import rastro.controller.ImageController;
import rastro.controller.ImageController.RasterResult;

public class ImageControlPanel extends BorderedTitledPanel {
    private static final long serialVersionUID = 1L;
    private JTextField fileName;
    private JButton btnBrowse;
    private JTextField imageDpi;
    private ImageController imCon;
    private JFileChooser fc;
    private final static int PADDING_SMALL = 5;
    private final static int PADDING_LARGE = 10;
    private final static Dimension DIM_FILE_FIELD = new Dimension(180, 22);
    private final static Dimension DIM_DPI_FIELD = new Dimension(40, 22);
    private final static String[] ACCEPTED_FILE_EXTENSIONS = {".jpg", ".jpeg", ".bmp", ".png", ".tiff"};
    
    private ActionListener actionListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent event) {
            
            switch (event.getActionCommand()) {
                case "browse":       
                    if (fc.showOpenDialog(ImageControlPanel.this) == JFileChooser.APPROVE_OPTION);
                    File imgFile = fc.getSelectedFile();
                    if (imCon.loadImage(imgFile) == RasterResult.ok) {
                        fileName.setText(imgFile.getName());
                    } else {
                        fileName.setText("");
                        JOptionPane.showMessageDialog(ImageControlPanel.this,
                                "Can't load image file: " + imgFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                break;
                default:
            }
        }       
    };
    
    ImageControlPanel(ImageController imgController) {
        super("Input image");
        fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                for (String ex : ACCEPTED_FILE_EXTENSIONS) {
                    if (fileName.endsWith(ex)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "Image files";
            }
            
        });
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        imCon = imgController;        
        fileName = new JTextField();
        fileName.setMaximumSize(DIM_FILE_FIELD);
        fileName.setEditable(false);
        btnBrowse = new JButton("Browse");
        btnBrowse.setActionCommand("browse");
        btnBrowse.addActionListener(actionListener);
        imageDpi = new JTextField();        
        imageDpi.setMaximumSize(DIM_DPI_FIELD);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(new JLabel("File:"));
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(fileName);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(btnBrowse);
        this.add(Box.createHorizontalStrut(PADDING_LARGE));
        this.add(new JLabel("Resolution:"));
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(imageDpi);
        this.add(Box.createHorizontalStrut(PADDING_SMALL));
        this.add(new JLabel("DPI"));
    }
}
