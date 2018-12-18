package bgu.spl.mics.application;

import bgu.spl.mics.MySerializable;
import bgu.spl.mics.application.passiveObjects.*;

import javax.imageio.IIOException;
import javax.sql.rowset.Joinable;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;

/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */


public class BookStoreRunner {

    public static void main(String[] args) {

        try {
            JasonReader jasonReader = new JasonReader();
            jasonReader.ParseJson(args[0]);

            ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
            resourcesHolder.load(jasonReader.getVehiclesList());

            Inventory inventory = Inventory.getInstance();
            inventory.load(jasonReader.getBookList());

            MoneyRegister moneyRegister = MoneyRegister.getInstance();

            Services services = new Services(jasonReader.getSpeed(), jasonReader.getDuration(), jasonReader.getSelling(),
                                                jasonReader.getInventoryService(), jasonReader.getLogistics(),
                                                     jasonReader.getResourcesService(), jasonReader.getCustomerArray());
            services.initialServices();

            LinkedList<Thread> threads = services.getThreads();
            for (Thread thread : threads) {
                thread.join();
            }

            PrintToOutPutFiles(jasonReader.getCustomerArray(),inventory, moneyRegister, args[1], args[2], args[3], args[4]);
        } catch (IllegalArgumentException | InterruptedException ie) {}

    }

    //the first file is the output file for the customers HashMap the second is for the books HashMap object,
    //the third is for the list of order receipts,and the fourth is for the MoneyRegister object
    private static void PrintToOutPutFiles(Customer[] customers,Inventory inventory, MoneyRegister moneyRegister, String path1, String path2, String path3, String path4) {
        HashMap<Integer,Customer> customerHashMap=new HashMap<>();
        for(Customer customer : customers)
            customerHashMap.put(customer.getId(),customer);
        MySerializable ser_customers=new MySerializable(customerHashMap,path1);
        ser_customers.serializeObjToFile();
        try {
            inventory.printInventoryToFile(path2);
        } catch (IOException e) {}
        moneyRegister.printOrderReceipts(path3);

        MySerializable ser_moneyRegister = new MySerializable(MoneyRegister.getInstance(),path4);
        ser_moneyRegister.serializeObjToFile();
    }

}