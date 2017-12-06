import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;
import utils.Constants;

import java.rmi.Naming;

public class App {

    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.out.println("Invalid argument, you need to pass a site ID");
            System.exit(1);
        }

        int siteId = Integer.parseInt(args[0]);
        int port = Constants.DEFAULT_PORT;

        try {
            // Getting the registry
            // Registry registry = LocateRegistry.getRegistry(1099);

            // Looking up the registry for the remote object
            String urlAppCom = Constants.LOCALHOST_RMI_URL + port + "/AppCom" + siteId;
            IAppCom appCom = (IAppCom) Naming.lookup(urlAppCom);

            String urlLamport = Constants.LOCALHOST_RMI_URL + port + "/Lamport" + siteId;
            ILamport lamport = (ILamport) Naming.lookup(urlLamport);

            // Calling the remote method using the obtained object
            appCom.test();
            lamport.test();

            System.out.println(String.format("App %d, Value before set: %s", siteId, appCom.getValue()));
            appCom.getCriticalSectionExclusion();
            appCom.setValue(2);
            appCom.releaseCriticalSectionExclusion();
            System.out.println(String.format("App %d, Value after set: %s", siteId, appCom.getValue()));
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
