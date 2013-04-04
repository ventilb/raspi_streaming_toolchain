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

package de.iew.imageread;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;

import java.io.FileOutputStream;

/**
 * A simple Java app to download an image file from a Mongo DB instance.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:16
 */
public class Main {

    private String mongohost = "localhost";

    private int mongoport = 27017;

    private String mongodb = "test";

    private String imagename = "uploaded_image.jpg";

    private String saveImagename = "/tmp/test.jpg";

    public void run() {
        try {
            MongoClient mongoClient = new MongoClient(this.mongohost, this.mongoport);

            DB db = mongoClient.getDB(this.mongodb);

            GridFS gridFS = new GridFS(db);

            DBCursor cursor = gridFS.getFileList().sort(new BasicDBObject("uploadDate", -1)).limit(1);

            if (cursor.hasNext()) {
                DBObject fileObject = cursor.next();

                GridFSDBFile file = gridFS.find((ObjectId) fileObject.get("_id"));
                printGridFSDBFile(file);

                FileOutputStream fos = new FileOutputStream(this.saveImagename);
                IOUtils.copy(file.getInputStream(), fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printGridFSDBFile(GridFSDBFile gridFSDBFile) {
        /*
        { "_id" : ObjectId("515c7864e4e08c240700099b"), "filename" : "webcam.jpg", "contentType" : "binary/octet-stream", "length" : 23950, "chunkSize" : 262144, "uploadDate" : ISODate("2013-04-03T18:43:48.704Z"), "aliases" : null, "metadata" : null, "md5" : "4d58ff711c0128110fcf7fe94beed4c1" }
         */
        StringBuilder sb = new StringBuilder("GridFSDBFile properties:");
        sb.append("\n         id: ").append(gridFSDBFile.get("_id"))
                .append("\n   filename: ").append(gridFSDBFile.get("filename"))
                .append("\ncontentType: ").append(gridFSDBFile.get("contentType"))
                .append("\n     length: ").append(gridFSDBFile.get("length"))
                .append("\n  chunkSize: ").append(gridFSDBFile.get("chunkSize"))
                .append("\n uploadDate: ").append(gridFSDBFile.get("uploadDate"))
                .append("\n    aliases: ").append(gridFSDBFile.get("aliases"))
                .append("\n   metadata: ").append(gridFSDBFile.get("metadata"))
                .append("\n        md5: ").append(gridFSDBFile.get("md5"));
        System.out.println(sb.toString());
    }

    public void printConfiguration() {
        StringBuilder sb = new StringBuilder("Using configuration:");
        sb.append("\n    mongohost: ").append(this.mongohost)
                .append("\n    mongoport: ").append(this.mongoport)
                .append("\n      mongodb: ").append(this.mongodb)
                .append("\n    imagename: ").append(this.imagename)
                .append("\nsaveImagename: ").append(this.saveImagename);
        System.out.println(sb.toString());
    }

    public void setMongohost(String mongohost) {
        this.mongohost = mongohost;
    }

    public void setMongoport(int mongoport) {
        this.mongoport = mongoport;
    }

    public void setMongodb(String mongodb) {
        this.mongodb = mongodb;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public void setSaveImagename(String saveImagename) {
        this.saveImagename = saveImagename;
    }

    public static void main(String[] argv) {
        Main main = new Main();

        switch (argv.length) {
            case 5:
                main.setMongoport(Integer.parseInt(argv[4]));
            case 4:
                main.setMongohost(argv[3]);
            case 3:
                main.setMongodb(argv[2]);
            case 2:
                main.setImagename(argv[1]);
            case 1:
                main.setSaveImagename(argv[0]);
        }

        main.printConfiguration();
        main.run();
    }
}



