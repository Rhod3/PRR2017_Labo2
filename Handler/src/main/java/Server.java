import utils.Constants;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


/**
 * BLABLA
 * - Structure de comment on gère les trucs (genre le common)
 *
 * - Où est la variable partagée
 * Nous avons décidé de placer la variable partagée dans le classe LamportHandler. Celle-ci est un int car nous voulions
 * lors de ce laboratoire faire les choses simplement et ne pas se compliquer la vie. Les taches pourront donc accéder
 * à cet entier et le modifier en proposant un nouvel entier.
 *
 * - Test faits et du sleep
 * Pour tester le bon fonctionnement de notre application, nous avons testé plusieurs scénarios:
 * Le premier est le nombre de sites différents fonctionnant en parallèle .Nous avons tester pour 1 à 3 sites. Dans tous
 * les cas, nous arrivions bien à récupérer la section critique et à modifier la valeur de la variable partagée. Nous avons
 * ensuite réussi à récupérer sa valeur depuis tous les sites. (elle est bien mise à jour dans chaque site.)
 * Nous avons ensuite fait en sorte que la section critique prenne du temps à être traitée. En effet, nous tenions à vérifier
 * que lorsqu'un site fait une requête alors qu'un autre site se trouve actuellement en section critique, qu'il attende
 * bien le FREE avant de se lancer dans la section critique. Pour prolonger la section critique, nous avons donc
 * "fait dormir " le thread pendant 10 secondes.
 *
 * Nous avons également pu tester que lorsqu'un site demande à modifier la variable, les autres sites peuvent continuer
 * sans problème à pouvoir simplement lire la variable commune.
 *
 * Nous avons finalement tester l'ordre des accès à la section critique (en utilisant 3 sites distincs). Le site 1 a
 * fait la demande d'accès et s'est retrouvé en section critique. Pendant qu'il est en section critique, le site 3 emet
 * une requête. Puis le site 2 fait de même. Les sites 2 et 3 sont donc en attente de la libération de la section critique
 * possédée par le site 1.
 * Une fois le site 1 sorti de la section critique, on a pu constater que le site 3 entre en section critique. L'ordre est
 * donc bien resepcté.
 *
 * - Comment on gère le nommage des machins RMI
 *
 * - Retour de quittance dans RMI
 * Compte tenu qu'en RMI chaque message à une réponse, nous avons décidé d'utiliser ceci pour, lors d'une requête, renvoyer
 * directement la quittance. Cela permet d'éviter de générer des messages supplémentaires et donc de surcharger le réseau
 * On peut également constater que le message de libération (FREE) aura forcément une réponse, bien que celle-ci est inutile
 * dans le protocle de Lamport.
 *
 * - Hypothèses: tous les gestionnaires sont lancés avant les clients, on connait tous les noms des machins RMI,
 *
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
