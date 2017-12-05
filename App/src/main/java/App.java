import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;

import java.rmi.Naming;

public class App {

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

            System.out.println("Value before set: " + appCom.getValue());
            appCom.getCriticalSectionExclusion();
            appCom.setValue(2);
            appCom.releaseCriticalSectionExclusion();
            System.out.println("Value after set: " + appCom.getValue());
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
