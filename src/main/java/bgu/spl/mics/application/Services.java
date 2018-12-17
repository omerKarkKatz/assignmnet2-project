package bgu.spl.mics.application;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.services.*;

import java.util.LinkedList;
import java.util.concurrent.CountDownLatch;

public class Services {


    // Todo :: check witch field to add for the print methods;
    private int time;
    private int duration;
    private int selling;
    private int inventoryService;
    private int logistics;
    private int resourcesService;
    private Customer[] customers;
    private int numOfServices;
    private CountDownLatch countDownLatch;
    private LinkedList<MicroService> services;
    private LinkedList<Thread> threads;

    public Services(int time, int duration, int selling, int inventoryService, int logistics, int resourcesService, Customer[] customers) {
        this.time = time;
        this.duration = duration;
        this.selling = selling;
        this.inventoryService = inventoryService;
        this.logistics = logistics;
        this.resourcesService = resourcesService;
        this.customers = customers;
        numOfServices = selling + inventoryService + logistics + resourcesService;
        countDownLatch = new CountDownLatch(numOfServices);
    }

    public void initialServices() {
        for (int i = 1; i < selling; i++) {
            services.add(new SellingService(i, countDownLatch));
        }

        for (int i = 1; i < inventoryService; i++) {
            services.add(new InventoryService(i, countDownLatch));
        }

        for (int i = 1; i < logistics; i++) {
            services.add(new LogisticsService(i, countDownLatch));
        }

        for (int i = 1; i < resourcesService; i++) {
            services.add(new ResourceService(i, countDownLatch));
        }

        for (int i = 1; i < customers.length; i++) {
            services.add(new APIService(i, countDownLatch, customers[i]));
        }

        services.forEach(service->threads.add(new Thread(service)));

        threads.forEach(Thread::start);

        Thread timeThread = new Thread(new TimeService(time, duration));
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.out.println("Not all services intalized");
        }
        timeThread.start();
    }

    public LinkedList<MicroService> getMicroServices() {
        return services;
    }

    public LinkedList<Thread> getThreads() {
        return threads;
    }
}
