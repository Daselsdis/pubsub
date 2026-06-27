// Clase que implementa la interfaz remota Subscriber
package broker;
import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import pubsub.Subscriber;
import pubsub.SubscriberCallback;
import pubsub.Event;

class SubscriberImpl extends UnicastRemoteObject implements Subscriber  {
    public static final long serialVersionUID=1234567890L;
    private UUID subUUID; // para facilitar depuración
    private transient PubSubImpl ps; // para acceder a funcionalidad del servicio general
    // para notificar al subscriptor de creación y destrucción de temas
    private transient SubscriberCallback scbk; 
    private final Set<String> subscribedTopics = new HashSet<>(); // Para sacar la lista de temas a los que esta suscrito un subscriptor
    private final Queue<Event> eventQueue = new LinkedList<>(); // Cola de eventos para este subscriptor
    private boolean active = true;// Variable para controlar si el subscriptor está activo o no

    public SubscriberImpl(PubSubImpl p, SubscriberCallback s) throws RemoteException {
        scbk = s;
        subUUID = UUID.randomUUID();
    ps = p;
    }
    
    public UUID getUUID() throws RemoteException {
        checkActive();
        return subUUID;
    }
    
    public int subscribe(String topic, boolean glob) throws RemoteException {
        checkActive();
        if (glob) {
            // Subscripción con patrón glob
            return subscribeGlob(topic);
        }
        
        // Subscripción normal (Fase 5)
        Topic t = ps.getTopic(topic);
        if (t == null) {
            return 0;
        }
        
        if (subscribedTopics.contains(topic)) {
            return 0;
        }
        
        t.addSubscriber(this);
        subscribedTopics.add(topic);
        return 1;
        }
    
    
    //Método para subscripcion con patron glob
     private int subscribeGlob(String pattern) throws RemoteException {
        checkActive();

        int count = 0;
        
        // Obtener todos los temas existentes
        Collection<String> allTopics = ps.topicList();
        
        // Crear el PathMatcher con el patrón glob
        java.nio.file.PathMatcher matcher = 
            java.nio.file.FileSystems.getDefault().getPathMatcher("glob:" + pattern);
        
        // Recorrer todos los temas y suscribir a los que coinciden
        for (String topic : allTopics) {
            // Convertir el tema a Path para poder compararlo
            java.nio.file.Path path = java.nio.file.Paths.get(topic);
            
            // Verificar si el tema coincide con el patrón
            if (matcher.matches(path)) {
                Topic t = ps.getTopic(topic);
                if (t != null && !subscribedTopics.contains(topic)) {
                    t.addSubscriber(this);
                    subscribedTopics.add(topic);
                    count++;
                }
            }
        }
        
        return count;
    }

    
    public Event getEvent() throws RemoteException {
        checkActive();

        return eventQueue.poll(); // Devuelve null si no hay eventos
    }
    
    public Collection<String> topicListBySubscriber() throws RemoteException {
        return new ArrayList<>(subscribedTopics);
    }
    
    public boolean unsubscribe(String topic) throws RemoteException {
        checkActive();

        // Obtener el tema
        Topic t = ps.getTopic(topic);
        
        // Verificar si el tema existe y si está suscrito a él
        if (t == null || !subscribedTopics.contains(topic)) {
            return false; // El tema no existe o no está suscrito
        }
        
        // Eliminar el subscriptor del tema
        t.removeSubscriber(this);
        
        // Eliminar el tema de la lista del subscriptor
        subscribedTopics.remove(topic);
        
        return true; // Baja exitosa
    }
    
    
    public void exit() throws RemoteException {
        checkActive();
        // Desuscribirse de todos los temas
        // Usamos una copia de la lista para evitar errores de concurrencia
        for (String topic : new ArrayList<>(subscribedTopics)) {
            Topic t = ps.getTopic(topic);
            if (t != null) {
                t.removeSubscriber(this);
            }
        }
        subscribedTopics.clear(); // Limpiar la lista de temas suscritos
        
        // Eliminar el subscriptor de la lista de subscriptores del sistema
        ps.removeSubscriber(this);
        
        // Marcar como inactivo
        active = false;
        
        // Limpiar la cola de eventos
        eventQueue.clear();
        
        try {
            UnicastRemoteObject.unexportObject(this, true);
        } catch (Exception e) {
            // Si hay error al unexportar, lo ignoramos
            System.err.println("Error al unexportar subscriptor: " + e.getMessage());
        }
    }
    
    // Método para verificar si el subscriptor está activo
    private void checkActive() throws NoSuchObjectException {
        if (!active) {
            throw new NoSuchObjectException("this subscriber has already finished");
        }
    }
    
    //Para notificar creación de tema
    public void notifyTopicAdded(String topic) throws RemoteException {
        if (scbk != null && active) {
            try {
                scbk.topicAdded(topic);
            } catch (RemoteException e) {
                // Subscriptor no accesible, relanzar para que el broker lo maneje
                throw e;
            }
        }
    }

    // Método para añadir un evento a la cola del subscriptor
    public void addEvent(Event ev) {
        if (active) {
        eventQueue.offer(ev);
        }
    }

}
