/**
 * PRR Laboratoire 2 - Basile Châtillon & Nicolas Rod
 *
 * === Structure ===
 * Notre laboratoire est structuré en 3 modules:
 * - un module App, qui représente une tâche applicative
 * - un module Handler, qui représente le gestionnaire de variable globale
 * - un module Common, qui contient tout les éléments commun au module App et Handler
 * Ces 3 modules sont nécessaires pour exécuter un site du système réparti.
 *
 * Afin de mettre en place le système réparti à N sites, il faut procéder de la manière suivant:
 * - Lancer les N Server avec comme paramètres [ID du site] [N]
 * - Lancer les N App avec comme paramètre [ID du site]
 * Sachant que les ID de site sont des entiers allant de 0 à N-1.
 *
 * === Commentaires ===
 * Dans notre laboratoire, la gestion des URL des différents RMI registry est la suivante: chaque Server va mettre
 * à disposition un object LamportHandler à l'adresse rmi://localhost:1099/LamportX , où X est l'ID du site
 * que l'on souhaite joindre. Ainsi, notre système réparti est uniquement utilisable en local sur une seule machine
 * en l'état: il faudrait changer la gestion des adresses RMI pour que l'on puisse indiquer à chaque site ou se
 * situe les autres sites (par exemple à l'aide d'un fichier de configuration externe) que l'on donnerait à
 * chaque site à leur démarrage.
 *
 * Nous avons décidé de placer la variable partagée dans la classe LamportHandler. Pour ce laboratoire, cette
 * variable est de type int pour rester simple. Idéalement, le type aurait pu être générique.
 * Les tâches applicatives peuvent donc accéder à cet variable et la modifier en proposant une nouvelle valeur.
 * Par ailleurs, la variable partagée est par défaut initialisé à 0.
 *
 * Pour tester le bon fonctionnement de notre application, nous avons testé plusieurs scénarios:
 * Le premier est le nombre de sites différents fonctionnant en parallèles. Nous avons tester pour 1 à 3 sites.
 * Dans tous les cas, nous arrivions bien à récupérer l'accès à la section critique et à modifier la valeur
 * de la variable partagée. Nous avons de plus vérifier que la variable récupérée par les autres sites après
 * modification soit bien la nouvelle valeur (elle est donc bien mise à jour dans chaque site).
 * Nous avons ensuite fait en sorte que la section critique prenne du temps à être traitée. En effet, nous tenions
 * à vérifier que lorsqu'un site fait une requête alors qu'un autre site se trouve actuellement en section critique,
 * qu'il attende bien la libération avant de se lancer dans la section critique. Pour prolonger la section critique,
 * nous avons donc "fait dormir" le thread pendant 10 secondes.
 *
 * Nous avons également pu tester que lorsqu'un site demande à modifier la variable, les autres sites peuvent
 * continuer sans problème à pouvoir lire la variable partagée.
 *
 * Nous avons finalement tester l'ordre des accès à la section critique (en utilisant 3 sites distincs). Le site 1 a
 * fait la demande d'accès et s'est retrouvé en section critique. Pendant qu'il est en section critique, le site 3 émet
 * une requête. Puis le site 2 fait de même. Les sites 2 et 3 sont donc en attente de la libération de la section
 * critique par le site 1.
 * Une fois le site 1 sorti de la section critique, on a pu constater que le site 3 entre en section critique. L'ordre
 * est donc bien resepcté.
 *
 * Etant donnée qu'en RMI chaque message à une réponse, nous avons décidé d'utiliser celle-ci pour, lors d'une requête
 * de section critique, renvoyer directement la quittance dans la réponse du message. Cela permet d'éviter de générer
 * des messages supplémentaires et donc de surcharger le réseau.
 * A noter également que le message de libération (FREE) aura forcément une réponse, bien que celle-ci est inutile
 * dans le protocole de Lamport.
 *
 */

import utils.Constants;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;


/**
 * La méthode main() de la classe Server va créer l'objet gérant la variable
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
