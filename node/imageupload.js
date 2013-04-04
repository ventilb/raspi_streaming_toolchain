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

/**
 * A node JS application which opens a TCP socket to read images and to store these images into a Mongo DB instance.
 *
 * @author Manuel Schulze <manuel_schulze@i-entwicklung.de>
 * @since 01.04.13 - 15:59
 */

var net = require('net');
var fs = require('fs');


var Db = require('mongodb').Db,
    MongoClient = require('mongodb').MongoClient,
    Server = require('mongodb').Server,
    ReplSetServers = require('mongodb').ReplSetServers,
    ObjectID = require('mongodb').ObjectID,
    Binary = require('mongodb').Binary,
    GridStore = require('mongodb').GridStore,
    Code = require('mongodb').Code,
    BSON = require('mongodb').pure().BSON,
    assert = require('assert');


var mongohost = getArg(2, 'localhost');
var mongoport = getArg(3, 27017);
var mongodb = getArg(4, 'test');
var imagename = getArg(5, 'uploaded_image.jpg');
var tcpport = getArg(6, 8124);

if (!mongohost || !mongoport || !mongodb || !imagename || !tcpport) {
    printUsage();
    process.exit(1);
}
/**
 * Performs cleanup after this instance of node.js terminates.
 */
var shutdownHook = function () {
    mongoDbHandle.close();
    console.log('MongoDB disconnected');
}

console.log('Using:\nmongohost: ' + mongohost + '\nmongoport: ' + mongoport + '\n  mongodb: ' + mongodb + '\nimagename: ' + imagename + '\n  tcpport: ' + tcpport);

// Erstelle Verbindung zur Mongo DB
var mongoDbHandle = new Db(mongodb, new Server(mongohost, mongoport, {}), {safe: false});

mongoDbHandle.open(function (err, db) {
    if (err == null) {
        console.log('MongoDB connected');
        mongoDbHandle = db;
    } else {
        printMongoError(err);
        process.exit(1);
    }
});

// Node JS exit hook; Close connection to Mongo DB
process.on('exit', shutdownHook);

process.on('SIGINT', function () {
    console.log('Received SIGINT. This node.js instance will exit gracefully now.');
    process.exit(0);
})

// TCP Server erstellen
var server = net.createServer(function (/* connection */ connection) {
    console.log('Client connected');

    handleImageUpload(mongoDbHandle, connection, imagename);
});
server.listen(tcpport, function () {
    console.log('Server listening on TCP port ' + tcpport);
});

function handleImageUpload(/* Db */ db, /* connection */ connection, /* String */ imagename) {
    var gridStoreOptions = {
        content_type: 'image/jpeg'
    };

    var gridStore = new GridStore(db, new ObjectID(), imagename, "w", gridStoreOptions);
    gridStore.open(function (err, gridStore) {
        if (err == null) {
            console.log('GridStore object successfully created');

            connection.on('data', function (data) {
                gridStore.write(new Buffer(data, 'utf-8'), function (err, gridStore) {
                    if (err != null) {
                        printMongoError(err);
                    }
                });
            });

            connection.on('end', function () {
                gridStore.close(function (err, gridStore) {
                    if (err == null) {
                        // Bild wurde ordentlich erstellt; Jetzt ließe es sich als aktuellstes Bild markieren
                        console.log('GridStore object successfully closed');
                    } else {
                        printMongoError(err);
                    }
                });

            });
        } else {
            printMongoError(err);
        }

    });
}

function printMongoError(/**/ error) {
    console.log('A Mongo DB error occurred', error);
}

/**
 * Liefert Werte von der Kommandozeile.
 *
 * @param argIndex Index des Programmarguments.
 * @param defaultValue Default Wert
 * @return {*}
 */
function getArg(argIndex, defaultValue) {
    if (process.argv.length > argIndex) {
        return process.argv[argIndex];
    } else {
        return defaultValue;
    }
}

/**
 * Prints usage information.
 */
function printUsage() {
    console.log('Usage: node imageupload.js <mongohost> <mongoport> <mongodb> <imagefilename> <tcpport>');
}
