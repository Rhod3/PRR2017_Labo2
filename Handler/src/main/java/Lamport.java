import remoteInterfaces.ILamport;
import utils.Constants;
import utils.Message;
import utils.MessageFree;
import utils.MessageType;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Lamport extends UnicastRemoteObject implements ILamport {

    private MessageType[] fileMessage; // tableau contenant les messages des différents sites
    private int[] fileTimeStamp; // tableau contenant les estampilles des différents sites
    private int numberSite; // le nombre de site
    private int me; // notre valeur
    private int logicalClock; // notre horloge logique
    private boolean csGranted; // Définit si on a l'accès à la section critique
    private int sharedValue; // la valeur partagée entre les sites.

    private boolean waiting = false; // Permet de bloquer l'application tant que la section critique n'est pas disponible

    /**
     * Construit le protocle de Lamport
     * @param personalNumber La valeur de ce site
     * @param numberSite Le nombre de site totale
     * @throws RemoteException
     */
    public Lamport(int personalNumber, int numberSite) throws RemoteException {
        super();
        this.numberSite = numberSite;
        this.me = personalNumber;
        this.logicalClock = 0;
        this.csGranted = false;
        fileMessage = new MessageType[numberSite];
        fileTimeStamp = new int[numberSite];
    }

    /**
     * Vérifie si notre site à la permission d'entrer en section critique
     * @return
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
    synchronized public void demande() throws InterruptedException {
        fileMessage[me] = MessageType.REQUEST;
        fileTimeStamp[me] = ++logicalClock;
        System.out.println("Lamport.demande - envoie des messages et récupération des receipt");
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                /* Lorsqu'on envoie un message, on profite de RMI pour directement récupérer la quitance.
                 * On recupère donc la réponse et on appelle la fonction receive pour la traiter
                 */
                Message receipt = send(MessageType.REQUEST, j);
                receive(receipt);
            }
        }
        System.out.println("Lamport.demande - récupération de la permission");
        csGranted = permission();

        if (!csGranted) {
            System.out.println("Waiting for SC");
            waiting = true;
            wait(); // on est notifié et reveillé quand la section critique est accordée (dans la méthode receive)
        }
    }

    /**
     * Permet de relacher la section critique ainsi que de notifier les autres sites que l'on a libéré la section critique
     * Permet également de transmettre la nouvelle valeur de la variable partagée aux autres sites.
     * @param newSharedValue La nouvelle valeur de la variable partagée
     */
    public void end(int newSharedValue) {
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
     * Permet de géré le traitement de la récéption des messages, selon Lamport.
     * Si le message reçu est une requête, alors la méthode retournera le message de quitance.
     * @param message Le message reçu
     * @return Un message de quitance ou null.
     */
    public Message receive(Message message) {
        System.out.println("Lamport.receive - un message a été recu de " + message.getSender());
        MessageType messageType = message.getMessageType();
        int timeStamp = message.getTimeStamp();
        int sender = message.getSender();

        Message messageRet = null;

        logicalClock = Math.max(logicalClock, timeStamp) + 1;

        switch (messageType) {
            case REQUEST:
                System.out.println("Lamport.receive - request");
                fileMessage[sender] = MessageType.REQUEST;
                fileTimeStamp[sender] = timeStamp;

                /* On crée le message de la quitance avant de le retourner. */
                messageRet = new Message(MessageType.RECEIPT, logicalClock, me);

                break;

            case RECEIPT:
                System.out.println("Lamport.receive - receipt");
                if (fileMessage[sender] != MessageType.REQUEST) {
                    fileMessage[sender] = MessageType.RECEIPT;
                    fileTimeStamp[sender] = timeStamp;
                }
                break;

            case FREE:
                System.out.println("Lamport.receive - free");
                fileMessage[sender] = MessageType.FREE;
                fileTimeStamp[sender] = timeStamp;

                /* Dans le cas d'un message de libération, le message contiendra en plus la nouveelle valeur de la
                 * variable partagée. */
                sharedValue = ((MessageFree) message).getNewSharedValue();
                break;
        }

        csGranted = fileMessage[me] == MessageType.REQUEST && permission();

        /* Si on a accès à la section critique et qu'on était en train d'attendre de la recevoir. On va réveiller le thread
         * Pour que le client puisse finalement modifier la variable. */
        if (csGranted && waiting) {
            System.out.println("Entering the release of lock");
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
     * @param messageType Le type du messasge
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
     * @param messageType Le type du messasge
     * @param destination Le site de destination du message
     * @return Le message de réponse du site
     */
    private Message send(MessageType messageType, int destination) {
        return send(new Message(messageType, logicalClock, me), destination);
    }

    /**
     * Permet d'envoyer un message à un site
     *
     * @param message Le messasge à envoyerm (Contient le type du message, l'estampille, l'envoyeur et éventuellement la
     *                nouvelle valeur de la variable partagée)
     * @param destination Le site de destination du message
     * @return Le message de réponse du site
     */
    private Message send(Message message, int destination) {
        try {
            // Looking up the registry for the remote object
            String urlLamport = Constants.LOCALHOST_RMI_URL + Constants.DEFAULT_PORT + "/Lamport" + destination;
            ILamport lamport = (ILamport) Naming.lookup(urlLamport);

            // Calling the remote method using the obtained object
            return lamport.receive(message);
        } catch (Exception e) {
            System.err.println("Handler exception: " + e.toString());
            e.printStackTrace();
        }

        return null;
    }

    public void test() {
        System.out.println("Test Lamport");
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
