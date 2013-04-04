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

package de.iew.imageupload;

import de.iew.imageupload.widgets.ContentPane;

import javax.swing.*;
import java.awt.*;

/**
 * Main class for the node JS image uploader.
 * <p>
 * The node JS image uploader is a simple Java application to read an image from the local filesystem and to upload it
 * into a running node JS instance.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:16
 */
public class Main {
    private ContentPane contentPane;

    public Main() {
        this.contentPane = new ContentPane();
    }

    public void startFrame() {
        JFrame frame = new JFrame("Image Uploader");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.setContentPane(this.contentPane.getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] argv) {
        Main main = new Main();
        main.startFrame();
    }
}



