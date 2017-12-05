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
        try
        {
            // System.setSecurityManager(new SecurityManager());

            // Instantiating the implementation class
            ImplExample obj = new ImplExample();

            // Binding the remote object (stub) in the registry
            LocateRegistry.createRegistry(1099);
            Naming.rebind("Hello", obj);

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
