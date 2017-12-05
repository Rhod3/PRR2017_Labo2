import remoteInterfaces.Hello;
import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Tache {

    public static void main(String[] args) {
        try {
            // Getting the registry
            // Registry registry = LocateRegistry.getRegistry(10999);

            // Looking up the registry for the remote object
            IAppCom appCom = (IAppCom) Naming.lookup("AppCom");
            ILamport lamport = (ILamport) Naming.lookup("Lamport");

            // Calling the remote method using the obtained object
            appCom.test();
            lamport.test();

            // System.out.println("Remote method invoked");
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
