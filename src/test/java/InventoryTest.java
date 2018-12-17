
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.util.Vector;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory = Inventory.getInstance();
    private BookInventoryInfo[] books;



    @Before
    public void setUp() throws Exception {
        books = new BookInventoryInfo[10];
        for (int i = 0; i < books.length; i++) {
            books[i] = new BookInventoryInfo("book " + i ,(i%6)+1,i*8);
        }

    }

    @Test
    public void getInstance() {
        assertEquals("wrong instance of class inventory",inventory,Inventory.getInstance());
    }

    @Test
    public void load() {
        // check if is Empty - need to check if you load only once

        for (int i=0; i<books.length; i++) {
               assertEquals("book shouldn't be found",  OrderResult.NOT_IN_STOCK, inventory.take(books[i].getBookTitle()));
        }
        inventory.load(books);
        for (int i=0; i<books.length; i++) {
            assertEquals("book wasn't loaded",OrderResult.SUCCESSFULLY_TAKEN , inventory.take(books[i].getBookTitle()));
        }
    }

    @Test
    public void take() {
        inventory.load(books);
        for (int i = 0; i < books.length; i++) {
            String bookName = books[i].getBookTitle();
            assertEquals("book wasn't loaded", OrderResult.SUCCESSFULLY_TAKEN,(inventory.take(bookName)));

        }
    }

    @Test
    public void checkAvailabiltyAndGetPrice() {

        assertEquals("found a book that doesnt exist" , -1 , inventory.checkAvailabiltyAndGetPrice("book 0"));
        inventory.load(books);
        for (int i = 0; i < books.length; i++) {
            String bookName = books[i].getBookTitle();
            assertEquals("Availability or price of a book isn't correct",books[i].getPrice(),inventory.checkAvailabiltyAndGetPrice(bookName));
        }
    }
    @Test
    public void printInventoryToFile() {
    }


}