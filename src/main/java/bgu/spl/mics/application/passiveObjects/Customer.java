package bgu.spl.mics.application.passiveObjects;

import javafx.util.Pair;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {

	private int id;
	private String name;
	private String address;
	private int distance;
	private Vector receipts;
	private int creditCardNumber;
	private int availableAmountInCreditCard;
	private final transient Object moneyLock = new Object();
	private Vector<Pair<String,Integer>> OrderSchedule;



	public Customer(int id, String name, String address, int distance, int creditCardNumber, int availableAmountInCreditCard, Vector<Pair<String,Integer>> orderSchedule){
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		receipts = new Vector();
		this.creditCardNumber = creditCardNumber;
		this.availableAmountInCreditCard = availableAmountInCreditCard;
		this.OrderSchedule = orderSchedule;
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}


	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public Vector<Pair<String,Integer>> getOrderSchedule() {
		return OrderSchedule;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return availableAmountInCreditCard;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCardNumber;
	}

	public void setAvailableAmountInCreditCard(int availableAmountInCreditCard) {
		this.availableAmountInCreditCard = availableAmountInCreditCard;
	}

	public Vector getCustomerReceiptList() {
		return receipts;
	}

	public Object getMoneyLock() {
		return moneyLock;
	}
}
