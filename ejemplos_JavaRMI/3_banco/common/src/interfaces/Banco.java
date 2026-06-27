package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Banco extends Remote {
    Cuenta crearCuenta(Titular t) throws RemoteException;
    List<Cuenta> obtenerCuentas() throws RemoteException;
}
