package rastro.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
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
    private int dpi = 900;
    private static final int MIN_DPI = 75;
    private static final int MAX_DPI = 2400;
    private static final Dimension DIM_FILE_FIELD = new Dimension(180, 22);
    private static final Dimension DIM_DPI_FIELD = new Dimension(40, 22);
    private static final String[] ACCEPTED_FILE_EXTENSIONS = {".jpg", ".jpeg", ".bmp", ".png", ".tiff"};
    
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
                        if (imgFile != null) {
                            JOptionPane.showMessageDialog(ImageControlPanel.this,
                                "Can't load image file: " + imgFile.getName(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                break;
                case "dpi":
                    onDpiChange();                  
                break;
                default:
            }
        }       
    };    
    
    public ImageControlPanel(ImageController imgController) {
        super("Input image");
        fc = new JFileChooser();
        fc.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File file) {
                String fileName = file.getName();
                if (file.isDirectory()) {
                    return true;
                }
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
        imageDpi.setActionCommand("dpi");
        imageDpi.addActionListener(actionListener);
        imageDpi.addFocusListener(new FocusListener() {

            @Override
            public void focusGained(FocusEvent event) {                
            }

            @Override
            public void focusLost(FocusEvent event) {
                onDpiChange();
            }
            
        });
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
        onDpiChange();
    }
    
    private void onDpiChange() {
        int val = 0;
        try {
            val = Integer.parseInt(imageDpi.getText());            
        } catch (NumberFormatException e) { }
        if (val >= MIN_DPI && val <= MAX_DPI) {
            dpi = val;
        }
        imageDpi.setText(Integer.toString(dpi));
        imCon.setDpi(dpi);
    }
}
