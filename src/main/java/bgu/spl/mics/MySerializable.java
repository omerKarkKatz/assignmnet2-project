package bgu.spl.mics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MySerializable {

    Object objecttoSerialize;
    String filename;

    public MySerializable(Object obj, String fileName) {
        this.objecttoSerialize = obj;
        this.filename = fileName;
    }

    public void serializeObjToFile() {
        try (FileOutputStream file = new FileOutputStream(filename) ; ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(objecttoSerialize);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}

