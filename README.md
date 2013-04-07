Raspberry PI Toolchain
======================

Dieses Projekt stellt einige Tools bereit um einen Raspberry PI in eine
mobile Webcam zu verwandeln. Ziel ist, die aufgezeichneten Bilder an einen
Server zu senden, der sie interessierten Nutzern als Stream auf einer Webseite
bereitstellt.

Als Technologie wird das sogenannte Motion JPG Verfahren eingesetzt. Die Webcam
zeichnet bei diesem Verfahren jedes Bild als JPG. Der Raspberry PI nimmt dieses
JPG entgegen und verschickt es per TCP an einen node.js Server. Das Bild wird
vom node.js in einer Mongo DB Instanz abgelegt. Eine kleine Tomcat webapp
greift auf die Mongo DB zu und liefert die gespeicherten Bilder als Motion JPG
Stream an den Browser.

Einige Notizen f�r den Anfang.
Sollte Mongo DB sehr viel Last verursachen, dann k�nnte ein fehlender Index
schuld sein. F�r den Zugriff auf die Webcam Bilder ist folgender Index
notwendig.

    use <database name>
    db.fs.files.ensureIndex({filename: 1, uploadDate: -1})

Erstellt einen zusammengesetzten Index �ber den Dateinamen in aufsteigender
Ordnung und dem Upload-Datum in absteigender Sortierung.