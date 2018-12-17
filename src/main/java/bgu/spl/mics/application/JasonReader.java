package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Customer;
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

public class JasonReader {

    private static BookInventoryInfo[] bookList;
    private static DeliveryVehicle[] vehiclesList;
    private static int selling;
    private static int inventoryService;
    private static int logistics;
    private static int resourcesService;
    private static int speed;
    private static int duration;


    private static Customer[] CustomerArray;

    public JasonReader(){};

    public BookInventoryInfo[] getBookList() {
        return bookList;
    }

    public DeliveryVehicle[] getVehiclesList() {
        return vehiclesList;
    }

    public int getSelling() {
        return selling;
    }

    public int getInventoryService() {
        return inventoryService;
    }

    public int getLogistics() {
        return logistics;
    }

    public int getResourcesService() {
        return resourcesService;
    }

    public Customer[] getCustomerArray() {
        return CustomerArray;
    }

    public int getSpeed() { return speed; }

    public int getDuration() { return duration; }

    public void ParseJson(String Path) {
        Gson gson = new Gson();
        File jsonFile = Paths.get(Path).toFile();

        try {
            JsonObject jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

            // initialize BookInventoryInfo
            JsonArray BookInventoryInfoArray = jsonObject.getAsJsonArray("initialInventory");
            bookList = new BookInventoryInfo[BookInventoryInfoArray.size()];


            for (int i = 0; i < BookInventoryInfoArray.size(); i++) {
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

            for (int i = 0; i < JasonInnerVhiclesArray.size(); i++) {
                JsonObject cuurDeliveryVhicles = JasonInnerVhiclesArray.get(i).getAsJsonObject();
                int license = cuurDeliveryVhicles.get("license").getAsInt();
                int speed = cuurDeliveryVhicles.get("speed").getAsInt();
                vehiclesList[i] = new DeliveryVehicle(license, speed);
            }

            JsonObject services = jsonObject.getAsJsonObject("services");
            JsonObject time = services.getAsJsonObject("time");
            speed = time.get("speed").getAsInt();
            duration = time.get("duration").getAsInt();
            selling = services.get("selling").getAsInt();
            inventoryService = services.get("inventoryService").getAsInt();
            logistics = services.get("logistics").getAsInt();
            resourcesService = services.get("resourcesService").getAsInt();

            JsonArray customesJsonArray = services.getAsJsonArray("customers");
            CustomerArray = new Customer[customesJsonArray.size()];

            for (int i = 0; i < customesJsonArray.size(); i++) {
                JsonObject currCustomer = customesJsonArray.get(i).getAsJsonObject();
                // gets all the info on this customer
                int id = currCustomer.get("id").getAsInt();
                String name = currCustomer.get("name").getAsString();
                String address = currCustomer.get("address").getAsString();
                int distance = currCustomer.get("distance").getAsInt();
                JsonObject CurrCreditCard = currCustomer.getAsJsonObject("creditCard");
                int number = CurrCreditCard.get("number").getAsInt();
                int amount = CurrCreditCard.get("amount").getAsInt();

                //initelize a vector of orderSchedule that will be one of the customers fields
                JsonArray orderSchedulejsonArray = currCustomer.getAsJsonArray("orderSchedule");
                Vector<Pair<String, Integer>> orderSchedule = new Vector<>();
                for (int j = 0; j < orderSchedulejsonArray.size(); j++) {
                    JsonObject currOrder = orderSchedulejsonArray.get(j).getAsJsonObject();
                    Pair<String, Integer> title_tick = new Pair<>(currOrder.get("bookTitle").getAsString(), currOrder.get("tick").getAsInt());
                    orderSchedule.add(title_tick);
                }

                CustomerArray[i] = new Customer(id, name, address, distance, number, amount, orderSchedule);

            }
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}

