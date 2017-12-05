import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class Server {

    public static void main(String args[]) {
        if ( args.length != 2 ) {
            System.out.println("Invalid arguments, you need to pass a site number and the total number of site");
            System.exit(1);
        }

        try
        {
            // Instantiating the implementation class
            Lamport lamport = new Lamport(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
            AppCom appCom = new AppCom();


            // Binding the remote object (stub) in the registry
            LocateRegistry.createRegistry(1099);
            Naming.rebind("Lamport", lamport);
            Naming.rebind("AppCom", appCom);

            System.err.println("Server ready");
        }
        catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
