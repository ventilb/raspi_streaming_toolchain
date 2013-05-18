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
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

/**
 * A simple Java app to download image files from a Mongo DB instance.
 * <p>
 * The images can be filtered and grouped by date.
 * </p>
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:16
 */
public class Main {

    private String mongohost = "localhost";

    private int mongoport = 27017;

    private String mongodb = "test";

    private String imageFileNamePrefix = "image-";

    private String imageOutputDir;

    private OUTPUT_OPTION groupingOutputOption = OUTPUT_OPTION.ALL;

    private int zeroFill = 8;

    private Properties queryFilters;

    public void run() {
        try {
            this.imageOutputDir = System.getProperty("java.io.tmpdir");

            File outputBase = new File(this.imageOutputDir);
            testAndCreateDirectory(outputBase);

            MongoClient mongoClient = new MongoClient(this.mongohost, this.mongoport);

            DB db = mongoClient.getDB(this.mongodb);

            GridFS gridFS = new GridFS(db);

            DBCursor cursor = gridFS.getFileList(this.queryFromOptions()).sort(new BasicDBObject("uploadDate", -1));

            int imageNumber = 0;
            while (cursor.hasNext()) {
                DBObject fileObject = cursor.next();

                GridFSDBFile file = gridFS.find((ObjectId) fileObject.get("_id"));
                printGridFSDBFile(file);

                if (writeImageFile(outputBase, imageNumber, file)) {
                    imageNumber++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean writeImageFile(File outputBase, int imageNum, GridFSDBFile gridFSDBFile) throws Exception {
        switch (this.groupingOutputOption) {
            case ALL:
                return writeAllGroupingOptionImageFile(outputBase, imageNum, gridFSDBFile);
            case GROUP_BY_DAY:
                return writeGroupByDayGroupingOptionImageFile(outputBase, imageNum, gridFSDBFile);
            default:
                System.err.println("Invalid groupingOutputOption " + this.groupingOutputOption);
                return false;
        }
    }

    public boolean writeAllGroupingOptionImageFile(File outputBase, int imageNum, GridFSDBFile gridFSDBFile) throws Exception {
        File outFile = new File(outputBase, this.imageFileNamePrefix + sequenceNumber(imageNum) + ".jpg");

        return encodeImage(outFile, gridFSDBFile);
    }

    public boolean writeGroupByDayGroupingOptionImageFile(File outputBase, int imageNum, GridFSDBFile gridFSDBFile) throws Exception {
        String dayFolder = dayOfYearPatternFromUploadDate(gridFSDBFile);

        File outDir = new File(outputBase, dayFolder);
        testAndCreateDirectory(outDir);

        File outFile = new File(outDir, this.imageFileNamePrefix + sequenceNumber(imageNum) + ".jpg");

        return encodeImage(outFile, gridFSDBFile);
    }

    public boolean encodeImage(File outFile, GridFSDBFile gridFSDBFile) throws Exception {
        try {
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(outFile);

                BufferedImage bufferedImage = ImageIO.read(gridFSDBFile.getInputStream());
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);

                JPEGEncodeParam jpegEncodeParam = encoder.getDefaultJPEGEncodeParam(bufferedImage);
                jpegEncodeParam.setQuality(1f, false);
                encoder.setJPEGEncodeParam(jpegEncodeParam);
                encoder.encode(bufferedImage);
                return true;
            } finally {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        sb.append("\n           mongohost: ").append(this.mongohost)
                .append("\n           mongoport: ").append(this.mongoport)
                .append("\n             mongodb: ").append(this.mongodb)
                .append("\n imageFileNamePrefix: ").append(this.imageFileNamePrefix)
                .append("\n      imageOutputDir: ").append(this.imageOutputDir)
                .append("\ngroupingOutputOption: ").append(this.groupingOutputOption)
                .append("\n            zeroFill: ").append(this.zeroFill);

        if (this.queryFilters != null) {
            sb.append("\n        queryFilters:");
            for (Object key : this.queryFilters.keySet()) {
                sb.append("\n                      ").append(key).append(" = ").append(this.queryFilters.get(key));
            }
        }

        System.out.println(sb.toString());
    }

    protected String dayOfYearPattern(Calendar calendar) {
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);

        String dayString = StringUtils.leftPad(String.valueOf(day), 2, '0');
        String monthString = StringUtils.leftPad(String.valueOf(month), 2, '0');

        return year + "-" + monthString + "-" + dayString;
    }

    protected String dayOfYearPatternFromUploadDate(GridFSDBFile gridFSDBFile) {
        Date uploadDate = (Date) gridFSDBFile.get("uploadDate");
        System.out.println("Upload date " + uploadDate);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(uploadDate);

        return dayOfYearPattern(calendar);
    }

    protected String sequenceNumber(int sequenceNumber) {
        String s = String.valueOf(sequenceNumber);
        return StringUtils.leftPad(s, 8, '0');
    }

    protected void testAndCreateDirectory(File fileToTest) throws IOException {
        if (fileToTest.exists()) {
            if (!fileToTest.canWrite() || !fileToTest.isDirectory()) {
                throw new IOException("The file " + fileToTest + " exists but is not readable or not a directory");
            }
        } else {
            if (!fileToTest.mkdir()) {
                throw new IOException("Cannot create directory " + fileToTest);
            }
        }
    }

    protected DBObject queryFromOptions() throws Exception {
        BasicDBObject dbObject = null;
        if (this.queryFilters != null) {
            if (QUERY_OPTION.FROM.available(this.queryFilters)
                    && QUERY_OPTION.TO.available(this.queryFilters)) {
                Date fromDate = parseDateString(this.queryFilters.getProperty(QUERY_OPTION.FROM.name()));
                Date toDate = parseDateString(this.queryFilters.getProperty(QUERY_OPTION.TO.name()));

                dbObject = new BasicDBObject();
                dbObject.put("uploadDate", BasicDBObjectBuilder.start("$gte", dayBegin(fromDate)).add("$lte", dayEnd(toDate)).get());
            } else if (QUERY_OPTION.FROM.available(this.queryFilters)) {
                Date fromDate = parseDateString(QUERY_OPTION.FROM.getValue(this.queryFilters));

                dbObject = new BasicDBObject();
                dbObject.put("uploadDate", BasicDBObjectBuilder.start("$gte", dayBegin(fromDate)).get());
            } else if (QUERY_OPTION.TO.available(this.queryFilters)) {
                Date toDate = parseDateString(QUERY_OPTION.TO.getValue(this.queryFilters));

                dbObject = new BasicDBObject();
                dbObject.put("uploadDate", BasicDBObjectBuilder.start("$lte", dayEnd(toDate)).get());
            } else if (QUERY_OPTION.EXACT.available(this.queryFilters)) {
                Date exactDate = parseDateString(QUERY_OPTION.EXACT.getValue(this.queryFilters));

                dbObject = new BasicDBObject();
                dbObject.put("uploadDate", BasicDBObjectBuilder.start("$gte", dayBegin(exactDate)).add("$lte", dayEnd(exactDate)).get());
            }
        }
        return dbObject;
    }

    protected Date parseDateString(String dateString) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.parse(dateString);
    }

    protected Date dayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    protected Date dayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
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

    public void setImageFileNamePrefix(String imageFileNamePrefix) {
        this.imageFileNamePrefix = imageFileNamePrefix;
    }

    public void setImageOutputDir(String imageOutputDir) {
        this.imageOutputDir = imageOutputDir;
    }

    public void setGroupingOutputOption(OUTPUT_OPTION outputOption) {
        this.groupingOutputOption = outputOption;
    }

    public void setZeroFill(int zeroFill) {
        this.zeroFill = zeroFill;
    }

    public void setQueryFilter(Properties queryFilters) {
        this.queryFilters = queryFilters;
    }

    public enum OUTPUT_OPTION {
        ALL,
        GROUP_BY_DAY;
    }

    public enum QUERY_OPTION {
        FROM,
        TO,
        EXACT;

        public boolean available(Properties properties) {
            return properties.containsKey(this.name());
        }

        public String getValue(Properties properties) {
            return properties.getProperty(this.name());
        }
    }

    public static void main(String[] argv) {
        Options options = setupOptions();
        try {
            CommandLine cmd = parseOptions(options, argv);

            printHelp(options);

            if (cmd.hasOption("h")) {
                printHelp(options);
                return;
            }

            Main main = new Main();

            if (cmd.hasOption("o")) {
                main.setImageOutputDir(cmd.getOptionValue("o"));
            }

            if (cmd.hasOption("q")) {
                Properties properties = cmd.getOptionProperties("q");
                main.setQueryFilter(properties);
            }

            if (cmd.hasOption("mh")) {
                main.setMongohost(cmd.getOptionValue("mh"));
            }

            if (cmd.hasOption("mp")) {
                main.setMongoport(Integer.parseInt(cmd.getOptionValue("mp")));
            }

            if (cmd.hasOption("md")) {
                main.setMongodb(cmd.getOptionValue("md"));
            }

            if (cmd.hasOption("g")) {
                main.setGroupingOutputOption(OUTPUT_OPTION.valueOf(cmd.getOptionValue("g")));
            }

            if (cmd.hasOption("p")) {
                main.setImageFileNamePrefix(cmd.getOptionValue("p"));
            }
            main.printConfiguration();
            main.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static void printHelp(Options options) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(120, "Main", "", options, "", true);
    }

    protected static Options setupOptions() {
        Options options = new Options();

        Option imageOutputDirOption = new Option("o", "outputDir", true, "The base directory to write the images to (Default: system temp directory)");
        imageOutputDirOption.setArgName("outputDir");

        Option imageFilenamePrefixOption = new Option("p", "filenamePrefix", true, "The basename/prefix for each image (Default: image-)");
        imageFilenamePrefixOption.setArgName("filenamePrefix");

        Option groupingOutputOption = new Option("g", "groupingOutput", true, "Grouping option for the images; valid values are ALL, GROUP_BY_DAY (Default: ALL)");
        groupingOutputOption.setArgName("groupingOutput");
        groupingOutputOption.setType(OUTPUT_OPTION.class);

        Option queryOption = new Option("q", "query", true, "Query filters for the mongo db queries; valid values for 'query' are FROM, TO and EXACT (Default: no specific query)");
        queryOption.setArgs(2);
        queryOption.setArgName("query=date");
        queryOption.setValueSeparator('=');

        Option databaseOption = new Option("md", "database", true, "The mongo db database (Default: test)");
        databaseOption.setArgName("database");

        Option mongodbHostOption = new Option("mh", "hostname", true, "The hostname of the mongo db instance (Default: localhost)");
        mongodbHostOption.setArgName("hostname");

        Option mongodbPortOption = new Option("mp", "port", true, "The port of the mongo db instance (Default: 27017)");
        mongodbPortOption.setArgName("port");
        mongodbPortOption.setType(Integer.TYPE);

        Option printHelpOption = new Option("h", "help", false, "Print help");

        options.addOption(imageOutputDirOption);
        options.addOption(imageFilenamePrefixOption);
        options.addOption(groupingOutputOption);
        options.addOption(queryOption);
        options.addOption(databaseOption);
        options.addOption(mongodbHostOption);
        options.addOption(mongodbPortOption);

        options.addOption(printHelpOption);

        return options;
    }

    protected static CommandLine parseOptions(Options options, String[] argv) throws Exception {
        CommandLineParser commandLineParser = new GnuParser();
        return commandLineParser.parse(options, argv);
    }
}



