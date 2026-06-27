// clase estática que contacta con el Registry para dar de alta el servicio
package broker;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import pubsub.PubSub;

// no se puede instanciar ni derivar
public final class Server {
    private Server(){};
    static void init(PubSub pubsub, String port) throws RemoteException {
        try {

        Registry reg = LocateRegistry.getRegistry(Integer.parseInt(port));

        reg.rebind("PubSub-broker_node", pubsub);

        } catch ( Exception e){
            throw new RemoteException("Error en init servidor nodo broker", e);
        }
    }
}
