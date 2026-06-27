// clase Topic
package broker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import pubsub.Event;
import pubsub.Subscriber;

    class Topic {
    private final Queue<Event> eventQueue = new LinkedList<>();
    private final Set<Subscriber> subscribers = new HashSet<>();
    public Topic() {
    }
        public synchronized void addEvent(Event ev) {
        eventQueue.offer(ev);
    }
    
    public synchronized Event consumeEvent() {
        return eventQueue.poll();
    }
    
    public synchronized void addSubscriber(Subscriber sub) {
        subscribers.add(sub);
    }
    
    public synchronized void removeSubscriber(Subscriber sub) {
        subscribers.remove(sub);
    }
    
    public synchronized Collection<Subscriber> getSubscribers() {
        return new ArrayList<>(subscribers);
    }
    
    public synchronized boolean hasSubscriber(Subscriber sub) {
        return subscribers.contains(sub);
    }

}
