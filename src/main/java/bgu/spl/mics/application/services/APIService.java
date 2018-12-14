package bgu.spl.mics.application.services;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.*;
import jdk.internal.vm.compiler.collections.Pair;

import java.util.Vector;
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
public class APIService extends MicroService{

	private Customer customer;
	private AtomicInteger currentTick = new AtomicInteger(0);
	private Vector<Pair<String,Integer >> bookOrderByTick;

	// id num of this APIService the number of
	//TODO: add to the countractor a list of the books he wants to order and when he wants to order them
	// this list will need to be sorted by ticks/ check maybe this should be in a different place because
	// mybe a few api can handle a customer or an api is a customer.

	public APIService(int id , Customer customer) {
		super("APIService" + id);
		this.customer = customer;
	}

	@Override
	protected void initialize() {
		this.subscribeBroadcast(TickBroadcast.class , tickBrod -> currentTick.set(tickBrod.getCurrTick()));



	}

}
