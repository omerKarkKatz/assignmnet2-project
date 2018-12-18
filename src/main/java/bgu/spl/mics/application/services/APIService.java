package bgu.spl.mics.application.services;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TerminationBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import javafx.util.Pair;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService {

	private Customer customer;
	private AtomicInteger currentTick = new AtomicInteger(0);
	private ConcurrentHashMap<Integer, Vector<String>> bookToOrderInCurrTick;
	private CountDownLatch countDownLatch;
	//TODO: think where are we sapoused to recive the orderRecite or null.

	public APIService(int id, CountDownLatch countDownLatch,  Customer customer) {
		super("APIService " + id);
		this.countDownLatch = countDownLatch;
		this.customer = customer;
		this.bookToOrderInCurrTick = ConstructHashMap(customer.getOrderSchedule());
	}


	@Override
	protected void initialize() {
		System.out.println("started: " + this.getName());
		this.subscribeBroadcast(TickBroadcast.class, tickBrod -> {
			currentTick.set(tickBrod.getCurrTick());
			if (bookToOrderInCurrTick.containsKey(currentTick.get())) {
				List<String> orderNow = bookToOrderInCurrTick.get(currentTick.get());
				for (String bookTitle : orderNow) {
					sendEvent(new BookOrderEvent(customer, bookTitle, currentTick.get()));
				}
			}
		});
		subscribeBroadcast(TerminationBroadcast.class, closingStore -> terminate());
		countDownLatch.countDown();
	}

	private ConcurrentHashMap<Integer,Vector<String>> ConstructHashMap(List<Pair<String, Integer>> bookTick) {
		ConcurrentHashMap<Integer,Vector<String>> OrderByTick = new ConcurrentHashMap<Integer, Vector<String>>();
		for (Pair<String, Integer> pair : bookTick) {
			OrderByTick.putIfAbsent(pair.getValue(),new Vector<String>());
			OrderByTick.get(pair.getValue()).add(pair.getKey());
		}
		return OrderByTick;
	}
}
