package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.Vector;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */


public class BookStoreRunner {

    private static BookInventoryInfo[] bookList;
    private static DeliveryVehicle[] vehiclesList;
    private static int selling;
    private static int inventoryService;
    private static int logistics;
    private static int resourcesService;
    private static Customer [] CustomerArray;

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

            JsonObject services = jsonObject.getAsJsonObject("services");
            selling = services.get("selling").getAsInt();
            inventoryService = services.get("inventoryService").getAsInt();
            logistics = services.get("logistics").getAsInt();
            resourcesService = services.get("resourcesService").getAsInt();

            JsonArray customesJsonArray = services.getAsJsonArray("customers");
            CustomerArray = new Customer[customesJsonArray.size()];

            for(int i=0; i < customesJsonArray.size(); i++){
                JsonObject currCustomer = customesJsonArray.get(i).getAsJsonObject();
                int id = currCustomer.get("id").getAsInt();
                String name = currCustomer.get("name").getAsString();
                String address = currCustomer.get("address").getAsString();
                int distance = currCustomer.get("distance").getAsInt();
                JsonObject CurrCreditCard = currCustomer.getAsJsonObject("creditCard");
                int number = CurrCreditCard.get("number").getAsInt();
                int amount = CurrCreditCard.get("amount").getAsInt();
                JsonArray orderSchedulejsonArray = currCustomer.getAsJsonArray("orderSchedule");
                Vector<Pair<String,Integer>> orderSchedule = new Vector<>();
                for(int j=0 ; j < orderSchedulejsonArray.size(); j++){
                    JsonObject currOrder = orderSchedulejsonArray.get(j).getAsJsonObject();
                    Pair<String,Integer> title_tick = new Pair<>(currOrder.get("bookTitle").getAsString(), currOrder.get("tick").getAsInt());
                    orderSchedule.add(title_tick);
                }
                CustomerArray[i] = new Customer(id , name, address, distance, number, amount, orderSchedule);


            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
