package bgu.spl.mics.application;

import bgu.spl.mics.MySerializable;
import bgu.spl.mics.application.passiveObjects.*;

import javax.imageio.IIOException;
import javax.sql.rowset.Joinable;
import java.io.*;
import java.util.*;

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

            //  PrintToOutPutFiles(jasonReader.getCustomerArray(),inventory, moneyRegister, args[1], args[2], args[3], args[4]);


            HashMap<Integer, Customer> customerHashMap = new HashMap<>();
            for (Customer customer : jasonReader.getCustomerArray())
                customerHashMap.put(customer.getId(), customer);


            //for Yuval's tests
            int numOfTest = Integer.parseInt(args[0].replace(new File(args[0]).getParent(), "").replace("/", "").replace(".json", ""));
            String dir = new File(args[1]).getParent() + "/" + numOfTest + " - ";
            Customer[] customers1 = customerHashMap.values().toArray(new Customer[0]);
            Arrays.sort(customers1, Comparator.comparing(Customer::getName));
            String str_custs = customers2string(customers1);
            str_custs = str_custs.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
            Print(str_custs, dir + "Customers");

            String str_books = books2string(inventory.getbooks().values().toArray(new BookInventoryInfo[0]));
            str_books = str_books.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
            Print(str_books, dir + "Books");

            List<OrderReceipt> receipts_lst = MoneyRegister.getInstance().getOrderReceipts();
            receipts_lst.sort(Comparator.comparing(OrderReceipt::getOrderId));
            receipts_lst.sort(Comparator.comparing(OrderReceipt::getOrderTick));
            OrderReceipt[] receipts = receipts_lst.toArray(new OrderReceipt[0]);
            String str_receipts = receipts2string(receipts);
            str_receipts = str_receipts.replaceAll(", ", "\n---------------------------\n").replace("[", "").replace("]", "");
            Print(str_receipts, dir + "Receipts");

            Print(MoneyRegister.getInstance().getTotalEarnings() + "", dir + "Total");
        }
        catch (InterruptedException ie) {
        }

    }
public static String customers2string (Customer[]customers){
    String str = "";
    for (Customer customer : customers)
        str += customer2string(customer) + "\n---------------------------\n";
    return str;
}

    public static String customer2string (Customer customer){
        String str = "id    : " + customer.getId() + "\n";
        str += "name  : " + customer.getName() + "\n";
        str += "addr  : " + customer.getAddress() + "\n";
        str += "dist  : " + customer.getDistance() + "\n";
        str += "card  : " + customer.getCreditNumber() + "\n";
        str += "money : " + customer.getAvailableCreditAmount();
        return str;
    }

    public static String books2string (BookInventoryInfo[]books){
        String str = "";
        for (BookInventoryInfo book : books)
            str += book2string(book) + "\n---------------------------\n";
        return str;
    }

    public static String book2string (BookInventoryInfo book){
        String str = "";
        str += "title  : " + book.getBookTitle() + "\n";
        str += "amount : " + book.getAmountInInventory() + "\n";
        str += "price  : " + book.getPrice();
        return str;
    }


    public static String receipts2string (OrderReceipt[]receipts){
        String str = "";
        for (OrderReceipt receipt : receipts)
            str += receipt2string(receipt) + "\n---------------------------\n";
        return str;
    }
    public static String receipt2string (OrderReceipt receipt){
        String str = "";
        str += "customer   : " + receipt.getCustomerId() + "\n";
        str += "order tick : " + receipt.getOrderTick() + "\n";
        str += "id         : " + receipt.getOrderId() + "\n";
        str += "price      : " + receipt.getPrice() + "\n";
        str += "seller     : " + receipt.getSeller();
        return str;
    }

    public static void Print (String str, String filename){
        try {
            try (PrintStream out = new PrintStream(new FileOutputStream(filename))) {
                out.print(str);
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getClass().getSimpleName());
        }
    }}

    //the first file is the output file for the customers HashMap the second is for the books HashMap object,
    //the third is for the list of order receipts,and the fourth is for the MoneyRegister object
    /*private static void PrintToOutPutFiles(Customer[] customers,Inventory inventory, MoneyRegister moneyRegister, String path1, String path2, String path3, String path4) {
        HashMap<Integer, Customer> customerHashMap = new HashMap<>();
        for (Customer customer : customers)
            customerHashMap.put(customer.getId(), customer);
        //-------create output files
        try {
            print(path1, customerHashMap);
        } catch (Exception e) {
            throw new IllegalArgumentException("Can't create file, customers");
        }
        try {
            inventory.printInventoryToFile(path2);
            moneyRegister.printOrderReceipts(path3);
        } catch (IOException e) {
        }
        try {
            print(path4, MoneyRegister.getInstance());
        } catch (Exception e) {
        }
    }
        private static void print(String path, Object toPrint){
            FileOutputStream fileOutputStream;
            try {
            fileOutputStream = new FileOutputStream(path);
                ObjectOutputStream OutPutfile = new ObjectOutputStream(fileOutputStream);
                OutPutfile.writeObject(toPrint);
                OutPutfile.close();
                fileOutputStream.close();
            }
            catch (IOException e){ throw new IllegalArgumentException("Can't create file, customers");}
        }
    }*/

