package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Cuenta extends Remote {
    Titular obtenerTitular() throws RemoteException;
    float obtenerSaldo() throws RemoteException;
    float operacion(float valor) throws RemoteException;
}
