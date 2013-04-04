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

package de.iew.raspimotion.domain;

import java.util.Date;

/**
 * Specifies an interface to implement domain model based filesystems.
 * <p>
 * We describe an interface because file descriptors are very dependent on the backend. A file descriptor for Mongo DB
 * is different than a file descriptor in Hibernate.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 02:00
 */
public interface FileDescriptor {
    /**
     * Gets filename.
     *
     * @return the filename
     */
    public String getFilename();

    /**
     * Sets filename.
     *
     * @param filename the filename
     */
    public void setFilename(String filename);

    /**
     * Gets content type.
     *
     * @return the content type
     */
    public String getContentType();

    /**
     * Sets content type.
     *
     * @param contentType the content type
     */
    public void setContentType(String contentType);

    /**
     * Gets filesize.
     *
     * @return the filesize
     */
    public long getFilesize();

    /**
     * Sets filesize.
     *
     * @param filesize the filesize
     */
    public void setFilesize(long filesize);

    /**
     * Gets create date.
     *
     * @return the create date
     */
    public Date getCreateDate();

    /**
     * Sets create date.
     *
     * @param createDate the create date
     */
    public void setCreateDate(Date createDate);

    /**
     * Gets md 5.
     *
     * @return the md 5
     */
    public String getMd5();

    /**
     * Sets md 5.
     *
     * @param md5 the md 5
     */
    public void setMd5(String md5);
}
