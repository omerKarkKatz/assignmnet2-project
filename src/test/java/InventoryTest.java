
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.Before;
import org.junit.Test;

import java.awt.print.Book;
import java.util.Vector;

import static org.junit.Assert.*;

public class InventoryTest {

    private Inventory inventory = Inventory.getInstance();
    private BookInventoryInfo[] books = new BookInventoryInfo[10];
    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < books.length; i++) {
            books[i] = new BookInventoryInfo("book ",i%5,i*8);
        }
    }

    @Test
    public void getInstance() {
        assertEquals("wrong instance of class inventory",inventory,Inventory.getInstance());
    }

    @Test
    public void load() {
        for (int i=0; i<books.length; i++) {
               assertNull("book shouldn't be found",inventory.take(books[i].getBookTitle()));
        }
        inventory.load(books);
        for (int i=0; i<books.length; i++) {
            assertEquals("book wasn't loaded",books[i].getBookTitle(),inventory.take(books[i].getBookTitle()));
        }
    }

    @Test
    public void take() {
        inventory.load(books);
        for (int i = 0; i < books.length; i++) {
            assertEquals("book wasn't loaded",books[i].getBookTitle(),inventory.take(books[i].getBookTitle()));
        }

    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        inventory.load(books);
        for (int i = 0; i < books.length; i++) {
            String bookName = books[i].getBookTitle();
            assertEquals("Availability or price of a book isn't correct",books[i].getPrice(),inventory.checkAvailabiltyAndGetPrice(bookName));

        }
    }
    @Test
    public void printInventoryToFile() {
    }

    @Test
    public void isHappy() {

    }
}