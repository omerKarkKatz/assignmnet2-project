package bgu.spl.mics.application;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {

        Gson gson = new Gson();
        File jsonFile = Paths.get(args[0]).toFile();

        try {
            JsonObject jasonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class );


            JsonArray BookInventoryInfoArray = jasonObject.getAsJsonArray("initialInventory");
            JsonObject currBook = BookInventoryInfoArray.get(0).getAsJsonObject();
            String bookTitle = currBook.get("bookTitle").getAsString();
            System.out.println(bookTitle);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
