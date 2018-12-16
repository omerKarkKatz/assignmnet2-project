package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.*;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */


public class BookStoreRunner {

    public static void main(String[] args) {

        JasonReader jasonReader = new JasonReader();
        jasonReader.ParseJson(args[0]);

        ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
        resourcesHolder.load(jasonReader.getVehiclesList());

        Inventory inventory = Inventory.getInstance();
        inventory.load(jasonReader.getBookList());


        Services services = new Services(jasonReader.getSelling(), jasonReader.getInventoryService(), jasonReader.getLogistics(),
                                                jasonReader.getResourcesService(), jasonReader.getCustomerArray());



        // TODO: JOIN BEFORE PRINT.


    }
}