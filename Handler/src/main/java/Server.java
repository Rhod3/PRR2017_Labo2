import utils.Constants;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Locale;

public class Server {

    public static void main(String args[]) {
        if ( args.length != 2 ) {
            System.out.println("Invalid arguments, you need to pass a site ID and the total number of site");
            System.exit(1);
        }

        int siteId = Integer.parseInt(args[0]);
        int port = Constants.DEFAULT_PORT;
        int numberOfSites = Integer.parseInt(args[1]);
        boolean remoteObjectBound = false;

        try
        {
            // Instantiating the implementation class
            Lamport lamport = new Lamport(siteId, numberOfSites);

            // Binding the remote object (stub) in the registry
            while (!remoteObjectBound) {
                try {
                    // Registry registry = LocateRegistry.getRegistry(1099);
                    String urlLamport = Constants.LOCALHOST_RMI_URL + port + "/Lamport" + siteId;
                    Naming.rebind(urlLamport, lamport);

                    remoteObjectBound = true;
                }
                catch (Exception e) {
                    System.out.println("No RMI Registry running ! Creating...");
                    LocateRegistry.createRegistry(1099);
                }
            }

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
