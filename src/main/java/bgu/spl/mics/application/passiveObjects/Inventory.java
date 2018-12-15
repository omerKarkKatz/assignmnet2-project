package bgu.spl.mics.application.passiveObjects;


import bgu.spl.mics.MySerializable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private static class SingletonHolder {
		private static Inventory inventoryInstance = new Inventory();

	}

	private ConcurrentHashMap <String,BookInventoryInfo> bookStock;
	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return SingletonHolder.inventoryInstance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[ ] inventory ) {
        for (BookInventoryInfo book: inventory) {
           bookStock.putIfAbsent(book.getBookTitle(), book);
        }
		
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take (String book) {
		synchronized (bookStock.get(book)){
			if (bookStock.containsKey(book)) {
				if (bookStock.get(book).getAmountInInventory() > 0) {
					bookStock.get(book).reduceAmount();
					return OrderResult.SUCCESSFULLY_TAKEN;
				} else
					return OrderResult.NOT_IN_STOCK;
			}
			return OrderResult.NOT_IN_STOCK;
		}
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
	    if (bookStock.containsKey(book) && bookStock.get(book).getAmountInInventory()>0)
	        return bookStock.get(book).getPrice();
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename) throws IOException {
			ConcurrentHashMap<String, Integer> bookTitle_Amount_ToPrint = new ConcurrentHashMap<>();
			for(String str : bookStock.keySet()){
				bookTitle_Amount_ToPrint.put(str , bookStock.get(str).getAmountInInventory());
			}
		MySerializable myInventorySerializable = new MySerializable(bookTitle_Amount_ToPrint, filename);
		myInventorySerializable.serializeObjToFile();
        }
	}

