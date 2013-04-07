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

import com.mongodb.gridfs.GridFSDBFile;
import de.iew.raspimotion.domain.FileDescriptor;
import de.iew.raspimotion.persistence.FileDescriptorDao;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;

import java.io.InputStream;

/**
 * DAO implementation to access files stored in a Mongo DB instance.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 00:52
 */
public class MongoDbFileDaoImpl implements FileDescriptorDao {

    public FileDescriptor getFileLastCreated(String filename) throws Exception {
        Query query = new Query(Criteria.where("filename").is(filename)).with(new Sort(new Sort.Order(Sort.Direction.DESC, "uploadDate")));

        return this.mongoTemplate.findOne(query, MongoDbFile.class, "fs.files");
    }

    public InputStream openFileInputStream(FileDescriptor fd) {
        MongoDbFile mongoDbFile = (MongoDbFile) fd;

        GridFSDBFile gridFSDBFile = this.gridFsTemplate.findOne(new Query(Criteria.where("id").is(new ObjectId(mongoDbFile.getId()))));
        return gridFSDBFile.getInputStream();
    }

    // Setters ////////////////////////////////////////////////////////////////

    private MongoTemplate mongoTemplate;

    private GridFsTemplate gridFsTemplate;

    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void setGridFsTemplate(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

}
