import remoteInterfaces.IHandler;
import utils.Constants;
import utils.Message;
import utils.MessageFree;
import utils.MessageType;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe représentant un gestionnaire de variable partagée utilisant l'algorithme de LamportHandler pour gérer
 * l'exclusion mutuelle de l'accès à cette variable partagée.
 */
public class LamportHandler extends UnicastRemoteObject implements IHandler {

    // Tableau contenant les messages des différents sites
    private MessageType[] fileMessage;
    // Tableau contenant les estampilles des différents sites
    private long[] fileTimeStamp;
    // Le nombre total de site(s)
    private int numberSite;
    // ID du gestionnaire
    private int me;
    // Horloge logique du gestionnaire
    private long logicalClock;
    // Définit si on a l'accès à la section critique
    private boolean csGranted;
    // La valeur partagée entre les sites. Par simplification, nous avons choisi le type int. Idéalement,
    // on aurait pu définir un type de variable générique.
    private int sharedValue;

    // Permet de bloquer l'application tant que la section critique n'est pas disponible
    private boolean waiting = false;

    /**
     * Construit le gestionnaire de variable partagée utilisant LamportHandler
     * @param personalNumber La valeur de ce site
     * @param numberSite Le nombre de site total
     * @throws RemoteException
     */
    public LamportHandler(int personalNumber, int numberSite) throws RemoteException {
        super();
        this.numberSite = numberSite;
        this.me = personalNumber;
        this.logicalClock = 0;
        this.csGranted = false;
        fileMessage = new MessageType[numberSite];
        fileTimeStamp = new long[numberSite];
    }

    /**
     * Vérifie si notre site à la permission d'entrer en section critique
     * @return Si le gestionnaire a l'accès à la variable partagée
     */
    private boolean permission() {
        boolean granted = true;
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                granted &= fileTimeStamp[me] < fileTimeStamp[j] || (fileTimeStamp[me] == fileTimeStamp[j] && me < j);

                if (!granted) {
                    break;
                }
            }
        }
        return granted;
    }

    /**
     * Demande la permission d'accéder à la section critique. Si la permission n'est pas instantanément attribuée
     * Il sera mis en attente et réveillé lorsque la permission aura été aquise.
     * @throws InterruptedException
     */
    synchronized private void demande() throws InterruptedException {
        fileMessage[me] = MessageType.REQUEST;
        fileTimeStamp[me] = ++logicalClock;
        System.out.println("LamportHandler.demande - Sending request and receiving receipt");

        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                /* Lorsqu'on envoie un message, on profite de RMI pour directement récupérer la quittance.
                 * On recupère donc la réponse et on appelle la fonction receive pour la traiter
                 */
                Message receipt = send(MessageType.REQUEST, j);
                receive(receipt);
            }
        }
        System.out.println("LamportHandler.demande - checking access");
        csGranted = permission();

        if (!csGranted) {
            System.out.println("Waiting for SC");
            waiting = true;
            wait(); // on est notifié et reveillé quand la section critique est accordée (dans la méthode receive)
        }
    }

    /**
     * Permet de relâcher la section critique ainsi que de notifier les autres sites que l'on a libéré la section critique
     * Permet également de transmettre la nouvelle valeur de la variable partagée aux autres sites.
     * @param newSharedValue La nouvelle valeur de la variable partagée
     */
    private void end(int newSharedValue) {
        fileMessage[me] = MessageType.FREE;
        fileTimeStamp[me] = logicalClock;

        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                send(MessageType.FREE, newSharedValue, j);
            }
        }
        csGranted = false;
    }

    /**
     * Permet de gérer le traitement de la recéption des messages, selon LamportHandler.
     * Si le message reçu est une requête, alors la méthode retournera le message de quittance.
     * @param message Le message reçu
     * @return Un message de quittance ou null.
     */
    public Message receive(Message message) {
        System.out.println(String.format("IHandler %d LamportHandler.receive - received message from %d", numberSite, message.getSender()));
        MessageType messageType = message.getMessageType();
        long timeStamp = message.getTimeStamp();
        int sender = message.getSender();

        Message messageRet = null;

        logicalClock = Math.max(logicalClock, timeStamp) + 1;

        switch (messageType) {
            case REQUEST:
                System.out.println("LamportHandler.receive - request");
                fileMessage[sender] = MessageType.REQUEST;
                fileTimeStamp[sender] = timeStamp;

                /* On crée le message de la quittance avant de le retourner. */
                messageRet = new Message(MessageType.RECEIPT, logicalClock, me);

                break;

            case RECEIPT:
                System.out.println("LamportHandler.receive - receipt");
                if (fileMessage[sender] != MessageType.REQUEST) {
                    fileMessage[sender] = MessageType.RECEIPT;
                    fileTimeStamp[sender] = timeStamp;
                }
                break;

            case FREE:
                System.out.println("LamportHandler.receive - free");
                fileMessage[sender] = MessageType.FREE;
                fileTimeStamp[sender] = timeStamp;

                /* Dans le cas d'un message de libération, le message contiendra en plus la nouvelle valeur de la
                 * variable partagée. */
                sharedValue = ((MessageFree) message).getNewSharedValue();
                break;
        }

        csGranted = fileMessage[me] == MessageType.REQUEST && permission();

        /* Si on a accès à la section critique et qu'on était en train d'attendre de la recevoir,
        on va réveille le thread pour que le client puisse finalement modifier la variable. */
        if (csGranted && waiting) {
            System.out.println("Entering the release of the lock");
            waiting = false;
            synchronized (this) {
                notify();
            }
        }

        return messageRet;
    }

    /**
     * Méthode privée qui permet de construire le bon message qui sera envoyé.
     *
     * @param messageType Le type du message
     * @param sharedValue La nouvelle valeur de la variable partagée
     * @param destination Le site de destination du message
     * @return Le message de réponse du site
     */
    private Message send(MessageType messageType, int sharedValue, int destination) {
        return send(new MessageFree(messageType, logicalClock, me, sharedValue), destination);
    }

    /**
     * Méthode privée qui permet de construire le bon message qui sera envoyé.
     *
     * @param messageType Le type du message
     * @param destination Le site de destination du message
     * @return Le message de réponse du site
     */
    private Message send(MessageType messageType, int destination) {
        return send(new Message(messageType, logicalClock, me), destination);
    }

    /**
     * Permet d'envoyer un message à un site
     *
     * @param message Le messasge à envoyer (Contient le type du message, l'estampille, l'envoyeur et éventuellement la
     *                nouvelle valeur de la variable partagée)
     * @param destination Le site de destination du message
     * @return Le message de réponse du site
     */
    private Message send(Message message, int destination) {
        try {
            // Looking up the registry for the remote object
            String urlLamport = Constants.LOCALHOST_RMI_URL + Constants.DEFAULT_PORT + "/Lamport" + destination;
            IHandler lamport = (IHandler) Naming.lookup(urlLamport);

            // Calling the remote method using the obtained object
            return lamport.receive(message);
        } catch (Exception e) {
            System.err.println("IHandler exception: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Méthode permettant de récupérer la valeur de la variable partagée
     * @return La valeur de la variable partagée
     * @throws RemoteException
     */
    public int getValue() throws RemoteException {
        System.out.println("Value got by App : " + sharedValue);
        return sharedValue;
    }

    /**
     * Modifie la valeur de la variable partagée (juste localement. le changement sera envoyé lors du FREE)
     * Une fois le changement effectué, on endort le thread pendant 10s pour avoir le temps de bien tester avec plusieurs sites
     *
     * @param value la nouvelle valeur de la variable partagée
     * @throws RemoteException
     */
    public void setValue(int value) throws RemoteException {
        try {
            demande();

            System.out.println("Value set to : " + value);
            this.sharedValue = value;
            Thread.sleep(10000);

            end(value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
