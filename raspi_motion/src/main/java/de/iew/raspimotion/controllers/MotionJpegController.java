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

import de.iew.raspimotion.domain.FileDescriptor;
import de.iew.raspimotion.persistence.FileDescriptorDao;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Klassenkommentar.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 03.04.13 - 21:13
 */
@Controller
public class MotionJpegController {

    private static final Log log = LogFactory.getLog(MotionJpegController.class);

    @RequestMapping(value = "image/{imagename:.+}")
    public void imageAction(
            HttpServletResponse response,
            @PathVariable String imagename
    ) throws Exception {
        // TODO imagename validieren
        FileDescriptor file = this.fileDao.getFileLastCreated(imagename);

        if (file == null) {
            throw new NoSuchElementException("Image was not found");
        }

        sendCachingHeaders(response);
        response.setContentType("image/jpeg");
        response.setContentLength(new Long(file.getFilesize()).intValue());

        sendImageAsJpeg(file, response);
    }

    @RequestMapping(value = "stream/{imagename:.+}")
    public void streamAction(
            HttpServletResponse response,
            @PathVariable String imagename
    ) throws Exception {
        FileDescriptor file = this.fileDao.getFileLastCreated(imagename);

        if (file == null) {
            throw new NoSuchElementException("Image was not found");
        }

        sendCachingHeaders(response);
        response.setContentType("multipart/x-mixed-replace;boundary=BOUNDARY");

        OutputStream out = response.getOutputStream();
        List<FileDescriptor> files = new ArrayList<FileDescriptor>();

        writeFrameBoundary(out);

        files.add(file);

        while (true) {
            this.fileDao.loadFilesCreatedAfter(file.getCreateDate(), imagename, files);
            for (FileDescriptor fileIterator : files) {
                writeFrame(out, fileIterator);

                response.flushBuffer();

                file = fileIterator;
            }
            files.clear();
        }
    }

    @ExceptionHandler
    public void onException(HttpServletResponse response, Exception e) {
        if (log.isErrorEnabled()) {
            log.error("Fehler in Controller", e);
        }
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    public void writeFrame(OutputStream out, FileDescriptor file) throws Exception {
        writeJpegFrameHeader(out, file);
        sendImageAsJpeg(out, file);
        writeCrLf(out);
        writeFrameBoundary(out);
    }

    public void writeFrameBoundary(OutputStream out) throws Exception {
        out.write("--BOUNDARY".getBytes());
        writeCrLf(out);
    }

    public void writeJpegFrameHeader(OutputStream out, FileDescriptor file) throws Exception {
        StringBuilder sb = new StringBuilder("Content-Type: image/jpeg");
        sb.append(getCrLf()).append("Content-Length: ").append(file.getFilesize())
                .append(getCrLf()).append(getCrLf());

        out.write(sb.toString().getBytes());
    }

    public void sendImageAsJpeg(FileDescriptor file, HttpServletResponse response) throws Exception {
        sendImageAsJpeg(response.getOutputStream(), file);
        response.flushBuffer();
    }

    public void sendImageAsJpeg(OutputStream out, FileDescriptor file) throws Exception {
        // TODO Content Type aus Mongo DB prüfen und ggfs Bild konvertieren

        InputStream in = this.fileDao.openFileInputStream(file);
        IOUtils.copy(in, out);
        in.close();
    }

    public void sendCachingHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, pre-check=0, post-check=0, max-age=0");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "Mon, 3 Jan 2000 12:34:56 GMT");
    }

    // Helpers ////////////////////////////////////////////////////////////////

    public void writeCrLf(OutputStream out) throws Exception {
        out.write(getCrLf().getBytes());
    }

    public String getCrLf() {
        return "\r\n";
    }

    // Spring und Dao Abhängigkeiten //////////////////////////////////////////

    private FileDescriptorDao fileDao;

    @Autowired
    public void setFileDao(FileDescriptorDao fileDao) {
        this.fileDao = fileDao;
    }
}
