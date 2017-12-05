import remoteInterfaces.Hello;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server extends ImplExample {

    protected Server() throws RemoteException {
    }

    public static void main(String args[]) {
        if ( args.length != 2 ) {
            System.out.println("Invalid arguments, you need to pass a site number and the total number of site");
            return;
        }

        try
        {
            // System.setSecurityManager(new SecurityManager());

            // Instantiating the implementation class
            ImplExample obj = new ImplExample();

            Lamport lamport = new Lamport(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            AppCom appCom = new AppCom();


            // Binding the remote object (stub) in the registry
            LocateRegistry.createRegistry(1099);
            Naming.rebind("Lamport", lamport);
            Naming.rebind("AppCom", appCom);
            Naming.rebind("Hello", obj);

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
