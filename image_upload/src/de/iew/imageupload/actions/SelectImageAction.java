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

package de.iew.imageupload.actions;

import de.iew.imageupload.widgets.ContentPane;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Action to select a file from local filesystem.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:21
 */
public class SelectImageAction extends AbstractAction {

    private ContentPane contentPane;

    public SelectImageAction() {
        putValue(NAME, "Select Image");
    }

    public void actionPerformed(ActionEvent e) {
        JFileChooser jFileChooser = new JFileChooser();
        int returnVal = jFileChooser.showOpenDialog(this.contentPane.getContentPane());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = jFileChooser.getSelectedFile();
            this.contentPane.registerImageFile(file);
        }

    }

    public void setContentPane(ContentPane contentPane) {
        this.contentPane = contentPane;
    }
}
