// clase estática que contacta con el Registry para obtener la
// referencia remota al servicio

// DEBE RELLENAR EL MÉTODO init

package pubsubcln;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import pubsub.PubSub;

// no se puede instanciar ni derivar
public final class Client {
    private Client(){};
    static public PubSub init(String host, String port) throws RemoteException, NotBoundException {
        try {
            Registry reg = LocateRegistry.getRegistry(host, Integer.parseInt(port));

            PubSub objRem = (PubSub) reg.lookup("PubSub-broker_node");
            return objRem;
        } catch (RemoteException| NotBoundException e) {
            throw e;
        } catch (Exception e){ // error de otro tipo, es necesario? @da
            throw new RemoteException("Error en init client_node: " + e.getMessage(),e);
        }
    }
}
