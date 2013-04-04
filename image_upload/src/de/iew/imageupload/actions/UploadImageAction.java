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

import org.apache.commons.io.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Action to upload a file into a node JS instance over a TCP socket.
 * <p>
 * The intention is to test the node JS behaviour. The upload is not restricted to node JS. It's just a simple TCP
 * connection.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:42
 */
public class UploadImageAction extends AbstractAction {

    private File fileToUpload;

    public UploadImageAction() {
        putValue(NAME, "Upload");
        setEnabled(false);
    }

    public void actionPerformed(ActionEvent ev) {
        try {
            Socket socket = new Socket("squeeze", 8124);

            FileInputStream fin = new FileInputStream(this.fileToUpload);
            OutputStream out = socket.getOutputStream();

            IOUtils.copy(fin, out);

            out.flush();
            fin.close();
            out.close();

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setFileToUpload(File fileToUpload) {
        setEnabled(fileToUpload != null);

        this.fileToUpload = fileToUpload;
    }
}
