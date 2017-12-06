import utils.Constants;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


/**
 * BLABLA
 * - Structure de comment on gère les trucs (genre le common)
 * - OU est la shared valeur
 * - Test faits et du sleep
 * - Comment on gère le nommage des machins RMI
 * - Reetour de quittance dans RMI
 * - Hypothèses: tous les gestionnaires sont lancés avant les clients, on connait tous les noms des machins RMI,
 */


/**
 * Classe représentant le gestionnaire de variable partagée. Sa méthode main() va créer l'objet gérant la variable
 * globale avant de le mettre a disposition des autres gestionnaires à l'aide d'un registre RMI.
 */
public class Server {

    public static void main(String args[]) {
        if ( args.length != 2 ) {
            System.out.println("Invalid arguments, you need to pass a site ID and the total number of site");
            System.exit(1);
        }

        // Récupération de l'ID du site et du nombre total de site
        int siteId = Integer.parseInt(args[0]);
        int numberOfSites = Integer.parseInt(args[1]);

        try
        {
            boolean remoteObjectBound = false;

            /**
             * Dans cette boucle, on va essayer de bind notre objet avec l'adresse RMI standard prévue pour ce site.
             * Si l'on arrive pas à contacter le registre RMI, on en crée un sur le port 1099 (défaut) et on réessaie
             * le bind. Cela implique qu'en localhost, tous les gestionnaires communiqueront à travers le même
             * registre RMI.
             * Sur une implémentation sur des sites distants, les gestionnaires créeront chacun leur registre RMI. Cela
             * impliquera néanmoins de redéfinir la gestion des adresses URL de registre RMI, par exemple avec un
             * fichier de config externe permettant de définir facilement les adresses de chacun des sites, plutôt que
             * de le considérer comme étant constant.
             */
            while (!remoteObjectBound) {
                try {
                    String urlLamport = Constants.LOCALHOST_RMI_URL + Constants.DEFAULT_PORT + "/Lamport" + siteId;
                    LamportHandler lamport = new LamportHandler(siteId, numberOfSites);
                    Naming.rebind(urlLamport, lamport);

                    remoteObjectBound = true;
                }
                catch (RemoteException e) {
                    System.out.println("No RMI Registry running ! Creating...");
                    LocateRegistry.createRegistry(1099);
                }
                catch (Exception e) {
                    e.printStackTrace();
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
