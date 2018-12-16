package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.services.*;

import java.util.concurrent.CountDownLatch;

public class Services {


    // Todo :: check witch field to add for the print methods;
    private TimeService time;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;
    private int numOfServices;
    private CountDownLatch countDownLatch;

    public Services(int selling, int inventoryService, int logistics, int resourcesService, Customer[] customers) {
        this.selling = selling;
        this.inventoryService = inventoryService;
        this.logistics = logistics;
        this.resourcesService = resourcesService;
        this.customers = customers;
        numOfServices = selling + inventoryService + logistics + resourcesService;
        countDownLatch = new CountDownLatch(numOfServices);
    }

    public void initialServices(){

        for(int i =0 ; i <= selling ; i++){
            Thread thread = new Thread(new SellingService(i , countDownLatch));
            thread.start();
        }

        for(int i =0 ; i <= inventoryService ; i++){
            Thread thread = new Thread(new InventoryService(i, countDownLatch));
            thread.start();
        }

        for(int i =0 ; i <= logistics ; i++){
            Thread thread = new Thread(new LogisticsService(i, countDownLatch));
            thread.start();
        }

        for(int i =0 ; i <= resourcesService ; i++){
            Thread thread = new Thread(new ResourceService(i, countDownLatch));
            thread.start();
        }

        for(int i =0 ; i <= customers.length ; i++){
            Thread thread = new Thread(new APIService(i, countDownLatch, customers[i]));
            thread.start();
        }




    }
}
