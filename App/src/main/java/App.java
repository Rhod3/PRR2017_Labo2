import remoteInterfaces.ILamport;
import utils.Constants;

import java.rmi.Naming;
import java.util.Scanner;

/**
 * La classe App représente une tâche applicative d'un site. Elle contient une méthode main() qui présente
 * à un utilisateur une GUI en ligne de commande. Les commandes possible sont "GET" et "SET", et elles permettent
 * respectivement de récupérer ou de modifier la valeur de la variable globale.
 */
public class App {

    public static void main(String[] args) {
        if ( args.length != 1 ) {
            System.out.println("Invalid argument, you need to pass a site ID");
            System.exit(1);
        }

        int siteId = Integer.parseInt(args[0]);

        try {
            // Le serveur est déjà lancé, donc l'object /Lamport+siteId est déjà disponible sur le rmiregistry
            String urlLamport = Constants.LOCALHOST_RMI_URL + Constants.DEFAULT_PORT + "/Lamport" + siteId;
            ILamport lamport = (ILamport) Naming.lookup(urlLamport);

            // Gestion de la GUI en ligne de commande
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
                        // Gérer un éventuel /n quand on a entré la nouvelle valeur
                        scanner.nextLine();
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

    /**
     * Méthode privée permettant d'afficher les commandes disponibles
     */
    private static void printInfo() {
        System.out.println();
        System.out.println("Please enter either :");
        System.out.println("SET to set a new shared value");
        System.out.println("GET to display the current shared value");
        System.out.print("> ");
    }
}
