package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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

    private static BookInventoryInfo[] bookList;
    private static DeliveryVehicle[] vehiclesList;

    public static void main(String[] args) {

        Gson gson = new Gson();
        File jsonFile = Paths.get(args[0]).toFile();

        try {
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class );

            // initialize BookInventoryInfo
            JsonArray BookInventoryInfoArray = jsonObject.getAsJsonArray("initialInventory");
            bookList = new BookInventoryInfo[BookInventoryInfoArray.size()];



            for(int i =0 ; i < BookInventoryInfoArray.size(); i++) {
                //takes the book obj
                JsonObject currBook = BookInventoryInfoArray.get(i).getAsJsonObject();

                String bookTitle = currBook.get("bookTitle").getAsString();
                int amount = currBook.get("amount").getAsInt();
                int price = currBook.get("price").getAsInt();
                bookList[i] = new BookInventoryInfo(bookTitle, amount, price);
            }

            // initial resources array
            JsonArray JasonOterVhiclesArray = jsonObject.getAsJsonArray("initialResources");
            JsonElement vhiclesElement = JasonOterVhiclesArray.get(0);
            JsonArray JasonInnerVhiclesArray = vhiclesElement.getAsJsonObject().getAsJsonArray("vehicles");
            vehiclesList = new DeliveryVehicle[JasonInnerVhiclesArray.size()];

            for (int i=0 ; i < JasonInnerVhiclesArray.size(); i ++){
                JsonObject cuurDeliveryVhicles = JasonInnerVhiclesArray.get(i).getAsJsonObject();
                int license = cuurDeliveryVhicles.get("license").getAsInt();
                int speed = cuurDeliveryVhicles.get("speed").getAsInt();
                vehiclesList[i] = new DeliveryVehicle(license,speed);
            }



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
