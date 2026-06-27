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
    private final Set<Subscriber> subscribers = new HashSet<>(); // Lista de subscriptores

    public PubSubImpl() throws RemoteException {
    }
    
    public int getVersion() throws RemoteException { // ya programada
        return version;
    }
    
    public synchronized boolean createTopic(String topic) throws RemoteException {

    if(topics.containsKey(topic)){
        return false;
    }
    topics.put(topic, new Topic());
    
    // Notificar a todos los subscriptores interesados
    for (Subscriber sub : subscribers) {
        try {
            ((SubscriberImpl) sub).notifyTopicAdded(topic);
        } catch (NoSuchObjectException e) {
            // El subscriptor ya no existe, eliminarlo de la lista
            System.err.println("Subscriptor ya no existe, eliminando: " + e.getMessage());
             subscribers.remove(sub); //Creo que podriamos quitarlos si no existe
        } catch (RemoteException e) {
            // Subscriptor no accesible, continuar con los demás
            System.err.println("Error notificando a subscriptor: " + e.getMessage());
        }
    }
    return true;
}

    public synchronized Collection<String> topicList() throws RemoteException {
        return new ArrayList<>(topics.keySet());
    }
    
    public synchronized boolean publish(Event ev) throws RemoteException {
        String topicName = ev.getTopic();
        Topic topic = topics.get(topicName);
        if (topic == null) {
            return false;
        }

        topic.addEvent(ev);

        for (Subscriber sub : topic.getSubscribers()) {
            try {
                ((SubscriberImpl) sub).addEvent(ev);
            } catch (Exception e) {
                //Si el subscriptor no es accesible, continua con los demás
                System.err.println("Error añadiendo evento a subscriptor: " + e.getMessage());

            }
        }

        return true;
    }
    
    public synchronized Event consumeEvent(String topic) throws RemoteException {
        Topic t = topics.get(topic);
        if (t == null) {
            throw new NoSuchObjectException("topic does not exist");
        }
        return t.consumeEvent();
    }
    
    // Método para dar de alta un subscriptor
    public synchronized Subscriber initSubscriber(SubscriberCallback c) throws RemoteException {
        SubscriberImpl sub = new SubscriberImpl(this, c);
        subscribers.add(sub);
        return sub;
    }
    
    // Método para listar subscriptores
    public synchronized Collection<Subscriber> subscriberList() throws RemoteException {
        return new ArrayList<>(subscribers);
    }
    
    public synchronized Collection<Subscriber> subscriberListByTopic(String topic) throws RemoteException {
        Topic t = topics.get(topic);
        if (t == null) {
            return null; 
        }
        return t.getSubscribers();
    }// Se implementa en la fase 5
    
    
    public synchronized boolean deleteTopic(String topic) throws RemoteException {
        return false; // Se implementará en la fase 10
    }
    
    // Método para eliminar un subscriptor de la lista
    public void removeSubscriber(Subscriber sub) {
        subscribers.remove(sub);
    }

    // Método para obtener un tema
    public synchronized Topic getTopic(String topic) {
        return topics.get(topic);
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

