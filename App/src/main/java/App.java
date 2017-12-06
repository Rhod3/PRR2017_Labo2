import remoteInterfaces.IAppCom;
import remoteInterfaces.ILamport;
import utils.Constants;

import java.rmi.Naming;
import java.util.Scanner;

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

            Scanner sc = new Scanner(System.in);
            displayCommands();
            while (true) {
                String input = sc.nextLine();

                if (input.contains("set"))
                {
                    System.out.print("Please enter a new value: ");
                    appCom.setValue(sc.nextInt());
                    System.out.println("New value set !");
                }
                else if (input.contains("get")) {
                    System.out.println("Current global value : " + appCom.getValue());
                }
                displayCommands();
            }
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void displayCommands() {
        System.out.println("===== Commands ====");
        System.out.println("set : set new value");
        System.out.println("get : display value");
        System.out.print("> ");
    }
}
