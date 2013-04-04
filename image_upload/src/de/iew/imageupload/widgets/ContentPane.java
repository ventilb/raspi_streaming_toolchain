/*
 * Copyright 2012-2013 Manuel Schulze <manuel_schulze@i-entwicklung.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.iew.imageupload.widgets;

import de.iew.imageupload.actions.SelectImageAction;
import de.iew.imageupload.actions.UploadImageAction;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;

/**
 * Implements the main UI panel for the image uploader.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:18
 */
public class ContentPane {

    private JPanel contentPane;
    private JButton selectFileButton;
    private JPanel imageGridPane;
    private JButton uploadButton;

    private final ImageMouseClickHandler imageMouseClickHandler = new ImageMouseClickHandler();

    private JLabel selectedImage;

    private UploadImageAction uploadImageAction;

    public void registerImageFile(File file) {
        try {
            ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());
            JLabel imageLabel = new JLabel(imageIcon, JLabel.CENTER);
            imageLabel.setPreferredSize(new Dimension(100, 100));
            imageLabel.setSize(new Dimension(100, 100));
            imageLabel.addMouseListener(this.imageMouseClickHandler);
            imageLabel.setBackground(Color.WHITE);
            imageLabel.setBorder(new EmptyBorder(10, 10, 10, 10));
            imageLabel.putClientProperty("Image File", file);


            this.imageGridPane.add(imageLabel);
            this.imageGridPane.revalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JComponent getContentPane() {
        return this.contentPane;
    }

    private void createUIComponents() {
        SelectImageAction selectImageAction = new SelectImageAction();
        selectImageAction.setContentPane(this);
        this.selectFileButton = new JButton(selectImageAction);

        this.uploadImageAction = new UploadImageAction();
        this.uploadButton = new JButton(this.uploadImageAction);
    }

    protected class ImageMouseClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (selectedImage != null) {
                selectedImage.setBorder(new EmptyBorder(10, 10, 10, 10));
                selectedImage = null;
                uploadImageAction.setFileToUpload(null);
            }

            JLabel imageLabel = (JLabel) e.getSource();
            imageLabel.setBorder(new CompoundBorder(new LineBorder(Color.BLACK, 1), new EmptyBorder(10, 10, 10, 10)));
            selectedImage = imageLabel;
            uploadImageAction.setFileToUpload((File) imageLabel.getClientProperty("Image File"));
        }
    }
}
