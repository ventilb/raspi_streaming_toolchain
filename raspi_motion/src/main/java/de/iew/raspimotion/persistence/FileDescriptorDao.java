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

package de.iew.raspimotion.persistence;

import de.iew.raspimotion.domain.FileDescriptor;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Specifies an interface to implement a DAO layer for domain model based file systems.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 00:44
 */
public interface FileDescriptorDao {

    public FileDescriptor getFileLastCreated(String filename) throws Exception;

    public InputStream openFileInputStream(FileDescriptor fd);
}
