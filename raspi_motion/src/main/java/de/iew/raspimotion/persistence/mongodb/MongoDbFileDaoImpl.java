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

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import de.iew.raspimotion.domain.FileDescriptor;
import de.iew.raspimotion.persistence.FileDescriptorDao;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * DAO implementation to access files stored in a Mongo DB instance.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 04.04.13 - 00:52
 */
public class MongoDbFileDaoImpl implements FileDescriptorDao, ApplicationListener<ApplicationContextEvent>, DisposableBean {

    private MongoClient mongoClient;

    private DB mongoDb;

    private GridFS gridFS;

    private volatile boolean running = false;

    public FileDescriptor getFileLastCreated(String filename) throws Exception {
        MongoDbFile file = null;

        DBCursor cursor = this.gridFS.getFileList(new BasicDBObject("filename", filename)).sort(new BasicDBObject("uploadDate", -1)).limit(1);

        if (cursor.hasNext()) {
            DBObject fileObject = cursor.next();

            file = mapMongoDbFile(fileObject);
        }

        return file;
    }

    public void loadFilesCreatedAfter(Date after, String filename, List<FileDescriptor> files) throws Exception {
        BasicDBObject query = new BasicDBObject("filename", filename);
        query.put("uploadDate", new BasicDBObject("$gt", after));

        DBCursor cursor = this.gridFS.getFileList(query).sort(new BasicDBObject("uploadDate", 1));

        FileDescriptor file;
        while (cursor.hasNext()) {
            DBObject fileObject = cursor.next();

            file = mapMongoDbFile(fileObject);

            files.add(file);
        }
    }

    public InputStream openFileInputStream(FileDescriptor fd) {
        MongoDbFile mongoDbFile = (MongoDbFile) fd;

        GridFSDBFile gridFSDBFile = this.gridFS.find(new ObjectId(mongoDbFile.getId()));
        return gridFSDBFile.getInputStream();
    }

    public synchronized void onApplicationEvent(ApplicationContextEvent event) {
        try {
            if (event instanceof ContextStartedEvent
                    || event instanceof ContextRefreshedEvent) {
                if (!this.running) {
                    this.mongoClient = new MongoClient(this.mongohost, this.mongoport);
                    this.mongoDb = mongoClient.getDB(this.mongodb);
                    this.gridFS = new GridFS(this.mongoDb);

                    this.running = true;
                }
            } else if (event instanceof ContextStoppedEvent) {
                destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.running = false;
        }
    }

    public synchronized void destroy() throws Exception {
        if (this.running) {
            this.mongoClient.close();
        }
    }

    protected MongoDbFile mapMongoDbFile(DBObject dbObject) {
        MongoDbFile file = new MongoDbFile();
        file.setId((dbObject.get("_id")).toString());
        file.setCreateDate((Date) dbObject.get("uploadDate"));
        file.setFilename((String) dbObject.get("filename"));
        file.setContentType((String) dbObject.get("contentType"));
        file.setFilesize((Long) dbObject.get("length"));
        file.setMd5((String) dbObject.get("md5"));
        return file;
    }

    // Setters ////////////////////////////////////////////////////////////////

    private String mongohost;

    private int mongoport;

    private String mongodb;

    public void setMongohost(String mongohost) {
        this.mongohost = mongohost;
    }

    public void setMongoport(int mongoport) {
        this.mongoport = mongoport;
    }

    public void setMongodb(String mongodb) {
        this.mongodb = mongodb;
    }

}
