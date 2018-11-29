package bgu.spl.mics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletonHolder {
		private static MessageBusImpl MessegeBusInstance = new MessageBusImpl();
	}

	private ConcurrentHashMap<MicroService, Queue<Message>> QueueOfMicroTasks;
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<Event, Future> EventToFuture;

	private MessageBusImpl(){
	    QueueOfMicroTasks = new ConcurrentHashMap<>();
	    eventsSubscribers = new ConcurrentHashMap<>();
	    broadcastSubscribers = new ConcurrentHashMap<>();
	    EventToFuture = new ConcurrentHashMap<>();
    }

	public static MessageBus getInstance() {
		return SingletonHolder.MessegeBusInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (eventsSubscribers.contains(type)) {
			eventsSubscribers.get(type).add(m);
		}//check if event is already in hashmap
		else {
			eventsSubscribers.put(type, new LinkedBlockingQueue<>());
			eventsSubscribers.get(type).add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (broadcastSubscribers.contains(type)){
			broadcastSubscribers.get(type).add(m);
		}
		else {
			broadcastSubscribers.put(type, new Vector<>());
			broadcastSubscribers.get(type).add(m);
		}

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// check if this syntax is ok//
		Iterator<MicroService> iter = broadcastSubscribers.get(b).iterator();
		while (iter.hasNext()){
			QueueOfMicroTasks.get(iter).add(b);
			iter.next();
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {

		MicroService m = null;
		synchronized (eventsSubscribers.get(e)) {
			try {
				m = eventsSubscribers.get(e).take();
				eventsSubscribers.get(e).add(m);
			} catch (InterruptedException e1) {}
		}
		QueueOfMicroTasks.get(m).add(e);
		return null;
	}

	@Override
	public void register(MicroService m) {
		if (!QueueOfMicroTasks.contains(m))
		QueueOfMicroTasks.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		// TODO Auto-generated method stub
		return null;
	}

	

}
