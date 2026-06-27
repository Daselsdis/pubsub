package server;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import interfaces.Cuenta;
import interfaces.Titular;

class CuentaImpl extends UnicastRemoteObject implements Cuenta {
    public static final long serialVersionUID=1234567890L;
    private Titular tit;
    private float saldo = 0;
    CuentaImpl(Titular t) throws RemoteException {
        tit = t;
    }
    public Titular obtenerTitular() throws RemoteException {
        return tit;
    }
    public float obtenerSaldo() throws RemoteException {
        return saldo;
    }
    public float operacion(float valor) throws RemoteException {
        saldo += valor;
        return saldo;
    }
}
