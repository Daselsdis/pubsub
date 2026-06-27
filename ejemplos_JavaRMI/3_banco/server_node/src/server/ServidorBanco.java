package server;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.LinkedList;
import interfaces.Banco;
import interfaces.Cuenta;
import interfaces.Titular;

class ServidorBanco extends UnicastRemoteObject implements Banco {
    public static final long serialVersionUID=1234567890L;
    LinkedList<Cuenta> l;
    ServidorBanco() throws RemoteException {
        l = new LinkedList<Cuenta>();
    }
    public Cuenta crearCuenta(Titular t) throws RemoteException {
        Cuenta c = new CuentaImpl(t);
        l.add(c);
        return c;
    }
    public List<Cuenta> obtenerCuentas() throws RemoteException {
       return l;
    }
    static public void main (String args[]) {
       if (args.length!=1) {
            System.err.println("Uso: ServidorBanco numPuertoRegistro");
            return;
        }
        try {
            ServidorBanco srv = new ServidorBanco();
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(args[0]));
            registry.rebind("Banco", srv);
        }
        catch (Exception e) {
            System.err.println("Excepcion en ServidorBanco:");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
