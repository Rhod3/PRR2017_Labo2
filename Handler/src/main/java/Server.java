import utils.Constants;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String args[]) {
        if ( args.length != 2 ) {
            System.out.println("Invalid arguments, you need to pass a site ID and the total number of site");
            System.exit(1);
        }
        int siteId = Integer.parseInt(args[0]);
        int port = Constants.DEFAULT_PORT;
        int numberOfSites = Integer.parseInt(args[1]);

        try
        {
            // Instantiating the implementation class
            Lamport lamport = new Lamport(siteId, numberOfSites);
            AppCom appCom = new AppCom(siteId);

            // Binding the remote object (stub) in the registry
            LocateRegistry.createRegistry(1099);

            String urlLamport = Constants.LOCALHOST_RMI_URL + port + "/Lamport" + siteId;
            String urlAppCom = Constants.LOCALHOST_RMI_URL + port + "/AppCom" + siteId;
            Naming.rebind(urlLamport, lamport);
            Naming.rebind(urlAppCom, appCom);

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
