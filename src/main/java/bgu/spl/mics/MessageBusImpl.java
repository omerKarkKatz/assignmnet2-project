package bgu.spl.mics;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletonHolder {
		private static MessageBusImpl MessegeBusInstance = new MessageBusImpl();
	}

	private ConcurrentHashMap<MicroService, LinkedBlockingQueue<Message>> QueueOfMicroTasks;
	private ConcurrentHashMap<Class<? extends Event>, LinkedBlockingQueue<MicroService>> eventsSubscribers;
	private ConcurrentHashMap<Class<? extends Broadcast>, Vector<MicroService>> broadcastSubscribers;
	private ConcurrentHashMap<Event, Future> EventToFuture;
	private ConcurrentHashMap<MicroService, Vector<Class<? extends Message>>> messagesOfMicroToDelete;
	private Object lockSendTask;
	private ReadWriteLock broadCastLock;

	private MessageBusImpl(){
	    QueueOfMicroTasks = new ConcurrentHashMap<>();
	    eventsSubscribers = new ConcurrentHashMap<>();
	    broadcastSubscribers = new ConcurrentHashMap<>();
	    EventToFuture = new ConcurrentHashMap<>();
		messagesOfMicroToDelete = new ConcurrentHashMap<>();
		lockSendTask = new Object();
		broadCastLock = new ReentrantReadWriteLock(true);

    }

	public static MessageBus getInstance() {
		return SingletonHolder.MessegeBusInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// check if to sync these 2 lines.
			messagesOfMicroToDelete.putIfAbsent(m, new Vector<>());
			messagesOfMicroToDelete.get(m).add(type);

			eventsSubscribers.putIfAbsent(type, new LinkedBlockingQueue<>());
			eventsSubscribers.get(type).add(m);

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

			messagesOfMicroToDelete.putIfAbsent(m, new Vector<>());
			messagesOfMicroToDelete.get(m).add(type);

			//check if this method should be synchronized
			broadcastSubscribers.putIfAbsent(type, new Vector<>());
			broadcastSubscribers.get(type).add(m);

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// find the future object related to event e, resolve the future obj(future method)
		EventToFuture.get(e).resolve(result);
		// checks if to delete the event from the hash or we need it for log.
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		Iterator<MicroService> iter;
		Vector<MicroService> tmp1=broadcastSubscribers.get(b.getClass());
		if (tmp1 == null) {
			System.out.println("noo handler for this Broadcast");
		}
		else{
			iter = tmp1.iterator();
			broadCastLock.readLock().lock();
			try {
				while (iter.hasNext())
					QueueOfMicroTasks.get(iter.next()).add(b);
			}finally {
				broadCastLock.readLock().unlock();
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> futureEvent = new Future<T>();
		EventToFuture.putIfAbsent(e, futureEvent);
		MicroService m = null;
		if(eventsSubscribers.containsKey(e.getClass())) {
			synchronized (eventsSubscribers.get(e.getClass())) {
				m = eventsSubscribers.get(e.getClass()).poll();
				// checks if there is a micro service which can handle this.
				if (m == null)
					return null;
					// moves the micro to the end of the queue (round robin manner), adds message to the microMessageQueue.
				else {
					eventsSubscribers.get(e.getClass()).add(m);
					synchronized (lockSendTask) {
						QueueOfMicroTasks.get(m).add(e);
					}
				}
			}
		}
		else{// meaning there is no eventType equals to e
		    futureEvent = null;
        }
			return futureEvent;
	}

	@Override
	public void register(MicroService m) {
		QueueOfMicroTasks.putIfAbsent(m, new LinkedBlockingQueue<>());
	}


	@Override
	public void unregister(MicroService m) {
		// check if clear is sync.
		QueueOfMicroTasks.get(m).clear();
		QueueOfMicroTasks.remove(m);
		if (messagesOfMicroToDelete.containsKey(m)) {
			broadCastLock.writeLock().lock();
			try {
				Iterator<Class<? extends Message>> iter = messagesOfMicroToDelete.get(m).iterator();
				while (iter.hasNext()) {
					Class<? extends Message> messageIter = iter.next();
					if (messageIter.getClass().isInstance(Event.class))
						eventsSubscribers.get(messageIter).remove(m);
					else
						broadcastSubscribers.get(messageIter).remove(m);
				}
			}finally {
				broadCastLock.writeLock().unlock();
			}
		}
		m.terminate();
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException, IllegalStateException {
			if (!QueueOfMicroTasks.containsKey(m))
				throw new IllegalStateException();
			else
				return QueueOfMicroTasks.get(m).take();
	}
}
