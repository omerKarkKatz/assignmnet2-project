package bgu.spl.mics.application.passiveObjects;
import bgu.spl.mics.MySerializable;

import java.io.*;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister implements Serializable {
	
	/**
     * Retrieves the single instance of this class.
     */

	private static class SingletonMoneyRegisterHolder {
		private static MoneyRegister MoneyRegisterInstance = new MoneyRegister();
	}

	// fields
	private Vector<OrderReceipt> ordersInMoneyRegister;
	private AtomicInteger totalEarnings;

	public static MoneyRegister getInstance() {
		return SingletonMoneyRegisterHolder.MoneyRegisterInstance;
	}

	private MoneyRegister(){
		totalEarnings = new AtomicInteger();
		ordersInMoneyRegister = new Vector<>();
	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		// check with someone.
		ordersInMoneyRegister.add(r);
		totalEarnings.addAndGet(r.getPrice());
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings.get();
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.setAvailableAmountInCreditCard(c.getAvailableCreditAmount()-amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.. 
     */
	public void printOrderReceipts(String filename) {
		MySerializable mySerializable = new MySerializable(ordersInMoneyRegister, filename);
		mySerializable.serializeObjToFile();
	}
}
