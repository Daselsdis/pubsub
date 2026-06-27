// Servidor que implementa la interfaz remota PubSub
package broker;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import pubsub.Event;
import pubsub.PubSub;
import pubsub.Subscriber;
import pubsub.SubscriberCallback;

class PubSubImpl extends UnicastRemoteObject implements PubSub  {
    public static final long serialVersionUID=1234567890L;

    // Elijo hashmap xq string-clase
    private final HashMap<String, Topic> topics = new HashMap<String, Topic>();

    public PubSubImpl() throws RemoteException {
    }
    public int getVersion() throws RemoteException { // ya programada
        return version;
    }
    public synchronized boolean createTopic(String topic) throws RemoteException {
        if(topics.containsKey(topic)){
            return false; //ya está el topic
        }
        topics.put(topic, new Topic());
        return true;
    }
    public synchronized Collection<String> topicList() throws RemoteException {
        return new ArrayList<>(topics.keySet());
    }
    public synchronized boolean publish(Event ev) throws RemoteException {
        return false;
    }
    public synchronized Event consumeEvent(String topic) throws RemoteException {
        return null;
    }
    public synchronized Subscriber initSubscriber(SubscriberCallback c) throws RemoteException {
        return null;
    }
    public synchronized Collection<Subscriber> subscriberList() throws RemoteException {
        return null;
    }
    public synchronized Collection<Subscriber> subscriberListByTopic(String topic) throws RemoteException {
        return null;
    }
    public synchronized boolean deleteTopic(String topic) throws RemoteException {
       return false;
    }
    static public void main (String args[])  {
        if (args.length!=1) {
            System.err.println("Usage: PubSubImpl registryPortNumber");
            return;
        }
        try {
            PubSub ps = new PubSubImpl();
            Server.init(ps, args[0]);
        }
        catch (Exception e) {
            System.err.println("PubSubImpl exception: " + e.toString());
            System.exit(1);
        }
    }
}
