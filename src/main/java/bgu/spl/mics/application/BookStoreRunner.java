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

        MoneyRegister moneyRegister = MoneyRegister.getInstance();

        Services services = new Services(jasonReader.getSelling(), jasonReader.getInventoryService(), jasonReader.getLogistics(),
                                                jasonReader.getResourcesService(), jasonReader.getCustomerArray());
        services.initialServices();

        // TODO: JOIN BEFORE PRINT.

        PrintToOutPutFiles(inventory, moneyRegister , args[1], args[2], args[3], args[4] );





    }
    //the first file is the output file for the customers HashMap the second is for the books HashMap object,
    //the third is for the list of order receipts,and the fourth is for the MoneyRegister object
    private static void PrintToOutPutFiles(Inventory inventory , MoneyRegister moneyRegister , String path1, String path2, String path3, String path4){
        inventory.printInventoryToFile(path2);
        moneyRegister.printOrderReceipts();

    }
}