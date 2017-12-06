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
            // Looking up the registry for the remote object
            String urlLamport = Constants.LOCALHOST_RMI_URL + port + "/Lamport" + siteId;
            ILamport lamport = (ILamport) Naming.lookup(urlLamport);

            // Calling the remote method using the obtained object
            lamport.test();

            Scanner scanner = new Scanner(System.in);
            while (true) {
                printInfo();
                String input = scanner.nextLine();
                input = input.toLowerCase();

                if (input.contains("set"))
                {
                    System.out.print("Enter a new value: ");
                    lamport.setValue(scanner.nextInt());
                    System.out.println("New value set !");
                    if (scanner.hasNextLine()){
                        scanner.nextLine(); // To flush the last /n
                    }
                }
                else if (input.contains("get")) {
                    System.out.println("Current global value : " + lamport.getValue());
                }
            }
        }
        catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private static void printInfo() {
        System.out.println();
        System.out.println("Please enter either :");
        System.out.println("SET to set a new shared value");
        System.out.println("GET to display the current shared value");
        System.out.print("> ");
    }
}
