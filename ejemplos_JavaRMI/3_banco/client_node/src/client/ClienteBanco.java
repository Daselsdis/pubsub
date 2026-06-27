package client;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import interfaces.Banco;
import interfaces.Cuenta;
import interfaces.Titular;

class ClienteBanco {
    static public void main (String args[]) {
        if (args.length!=4) {
            System.err.println("Uso: ClienteBanco hostregistro numPuertoRegistro nombreTitular IDTitular");
            return;
        }
        try {
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            Banco srv = (Banco) registry.lookup("Banco");

            Titular tit = new Titular(args[2], args[3]);
            Cuenta c = srv.crearCuenta(tit);
            c.operacion(30);

            List <Cuenta> l;
            l = srv.obtenerCuentas();
            for (Cuenta i: l) {
                Titular t = i.obtenerTitular();
                System.out.println(t + ": " +i.obtenerSaldo());
            }

            c.operacion(-5);

            l = srv.obtenerCuentas();
            for (Cuenta i: l)
                System.out.println(i.obtenerTitular() + ": " +i.obtenerSaldo());
        }
        catch (Exception e) {
            System.err.println("Excepcion en ClienteBanco:");
            e.printStackTrace();
        }
    }
}
