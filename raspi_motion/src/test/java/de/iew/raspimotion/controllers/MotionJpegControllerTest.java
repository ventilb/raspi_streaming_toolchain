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

package de.iew.raspimotion.controllers;

import org.junit.Assert;

/**
 * JUnit test cases to test the {@link MotionJpegController} implementation.
 *
 * @author Manuel Schulze <mschulze@geneon.de>
 * @since 04.04.13 - 15:20
 */
public class MotionJpegControllerTest {

    @org.junit.Test
    public void testValidateImagename() throws Exception {
        MotionJpegController motionJpegController = new MotionJpegController();

        Assert.assertTrue(motionJpegController.validateImagename("webcam.jpg"));
        Assert.assertTrue(motionJpegController.validateImagename("uploaded_image.jpg"));
        Assert.assertTrue(motionJpegController.validateImagename("uploaded_image-1.jpg"));
        Assert.assertTrue(motionJpegController.validateImagename("1_webcam.jpg"));
        Assert.assertFalse(motionJpegController.validateImagename("1_webcam.png"));
        Assert.assertFalse(motionJpegController.validateImagename("1_\nwebcam.jpg"));
        Assert.assertFalse(motionJpegController.validateImagename("%$§!\"=/((webcam.jpg"));
        Assert.assertFalse(motionJpegController.validateImagename("../../webcam.jpg"));
        Assert.assertFalse(motionJpegController.validateImagename("/webcam.jpg"));
        Assert.assertFalse(motionJpegController.validateImagename("`cat /etc/passwd`"));
        Assert.assertFalse(motionJpegController.validateImagename("webcam"));
    }
}
