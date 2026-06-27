package interfaces;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.Serializable;

public class Titular implements Serializable {
     public static final long serialVersionUID=1234567890L;
     private String nombre;
     private String iD;
     public Titular(String n, String i) {
         nombre = n;
         iD = i;
     }
     public String obtenerNombre() {
         return nombre;
     }
     public String obtenerID() {
         return iD;
     }
     public String toString() {
         return nombre + " | " + iD;
     }
}
