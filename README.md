Raspberry PI Toolchain
======================

Dieses Projekt stellt einige Tools bereit um einen Raspberry PI in eine
mobile Webcam zu verwandeln. Ziel ist, die aufgezeichneten Bilder an einen
Server zu senden, der sie interessierten Nutzern als Stream auf einer Webseite
bereitstellt. Sehr Hilfreich für den Einstieg war der
[Blog von Wolf Paulus] [3].

Als Technologie wird das sogenannte [Motion JPEG] [1] Verfahren eingesetzt. Die
Webcam zeichnet bei diesem Verfahren jedes Bild als JPG auf und liefert es an
die Anwendung aus. Die [mjpg-streamer] [2] Software auf dem Raspberry PI nimmt dieses
JPG entgegen und verschickt es üper TCP an einen node.js Server. Das Bild
wird vom node.js in einer Mongo DB Instanz abgelegt. Eine kleine Tomcat webapp
greift auf die Mongo DB zu und liefert die gespeicherten Bilder als
[Motion JPEG] [1] Stream an den Browser.

Notizen
-------
Sollte Mongo DB sehr viel Last verursachen, dann könnte ein fehlender Index
schuld sein. Für den Zugriff auf die Webcam Bilder ist folgender Index
notwendig.

    use <database name>
    db.fs.files.ensureIndex({filename: 1, uploadDate: -1})

Erstellt einen zusammengesetzten Index über den Dateinamen in aufsteigender
Ordnung und dem Upload-Datum in absteigender Sortierung.

Requirements
------------
*mjpg-streamer*

Die Paketabhängigkeiten werden wie folgt auf dem PI installiert.
sudo apt-get install imagemagick
sudo apt-get install git
sudo apt-get install subversion
sudo apt-get install libjpeg8-dev

Um mjpg-streamer zu kompilieren wird libjpeg-devel benötigt.

Um mjpg-streamer nach einem Neustart des Raspberry automatisch zu starten kann
die Runlevel Konfiguration wie folgt eingestellt werden.

    update-rc.d mjpg-streamer defaults

*node*
    npm install forever

    forever start -a -l /opt/imageupload/forever.log -o /opt/imageupload/imageupload_out.log -e /opt/imageupload/imageupload_err.log /opt/imageupload/imageupload.js localhost 27017 raspidb webcam.jpg 8124

[1]: http://de.wikipedia.org/wiki/Motion_JPEG                  "Motion JEPG"
[2]: http://sourceforge.net/projects/mjpg-streamer/            "mjpg-streamer Projektseite"
[3]: http://wolfpaulus.com/journal/embedded/raspberrypi_webcam "Streaming Your Webcam w/ Raspberry Pi"
