package bgu.spl.mics;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MySerializable {

    Object objectToSerialize;
    String filename;

    public MySerializable(Object obj, String fileName) {
        this.objectToSerialize = obj;
        this.filename = fileName;
    }

    public void serializeObjToFile() {
        try (FileOutputStream file = new FileOutputStream(filename) ; ObjectOutputStream out = new ObjectOutputStream(file)) {
            out.writeObject(objectToSerialize);
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }
}

