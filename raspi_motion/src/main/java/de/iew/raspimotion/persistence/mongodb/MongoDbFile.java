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

package de.iew.raspimotion.persistence.mongodb;

import de.iew.raspimotion.domain.FileDescriptor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

/**
 * Implements the {@link FileDescriptor} interface to represent files stored in a Mongo DB instance.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 00:39
 */
public class MongoDbFile implements Serializable, FileDescriptor {

    @Id
    private String id;

    private String filename;

    private long length;

    private String contentType;

    // TODO Herausfinden wie man das Property Mapping beeinflussen kann => Siehe Converter Deklaration in XML
    // Wichtig: Dieses Feld muss uploadDate heißen. Sonst wird es nicht automatisch gemappt.
    private Date uploadDate;

    private String md5;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getFilesize() {
        return length;
    }

    public void setFilesize(long filesize) {
        this.length = filesize;
    }

    // Mongo DB Eigenschaft
    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public Date getCreateDate() {
        return this.uploadDate;
    }

    public void setCreateDate(Date createDate) {
        this.uploadDate = createDate;
    }

    // GridFS Kompatibilität; Man kann das sicher auch irgendwie durch den Konverter regeln.
    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
}
