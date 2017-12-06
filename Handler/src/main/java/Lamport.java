import remoteInterfaces.ILamport;
import utils.Constants;
import utils.Message;
import utils.MessageFree;
import utils.MessageType;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Lamport extends UnicastRemoteObject implements ILamport {

    private MessageType[] fileMessage;
    private int[] fileTimeStamp;
    private int numberSite;
    private int me;
    private int logicalClock;
    private boolean csGranted;
    private int sharedValue;

    private boolean waiting = false;


    public Lamport(int personalNumber, int numberSite) throws RemoteException {
        super();
        this.numberSite = numberSite;
        this.me = personalNumber;
        this.logicalClock = 0;
        this.csGranted = false;
        fileMessage = new MessageType[numberSite];
        fileTimeStamp = new int[numberSite];
    }

    public boolean permission(int me) {
        boolean granted = true;
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                granted &= fileTimeStamp[me] < fileTimeStamp[j] || (fileTimeStamp[me] == fileTimeStamp[j] && me < j);

                if (!granted)
                    break;
            }
        }
        return granted;
    }

    synchronized public void demande() throws InterruptedException {
        fileMessage[me] = MessageType.REQUEST;
        fileTimeStamp[me] = ++logicalClock;
        System.out.println("Lamport.demande - envoie des messages et récupération des receipt");
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                Message receipt = send(MessageType.REQUEST, j);
                receive(receipt);
            }
        }
        System.out.println("Lamport.demande - récupération de la permission");
        csGranted = permission(me);

        if (!csGranted) {
            System.out.println("Waiting for SC");
            waiting = true;
            wait(); // We are notified by the receive method, where csGranted is set to true.
        }
    }

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

                sharedValue = ((MessageFree) message).getNewSharedValue();
                break;
        }

        csGranted = fileMessage[me] == MessageType.REQUEST && permission(me);

        if (csGranted && waiting) {
            System.out.println("Entering the release of lock");
            waiting = false;
            csGranted = true;
            synchronized (this) {
                notify();
            }
        }

        return messageRet;
    }

    private Message send(MessageType messageType, int sharedValue, int destination) {
        return send(new MessageFree(messageType, logicalClock, me, sharedValue), destination);
    }

    private Message send(MessageType messageType, int destination) {
        return send(new Message(messageType, logicalClock, me), destination);
    }

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

    public int getValue() throws RemoteException {
        System.out.println("Value got by App : " + sharedValue);
        return sharedValue;
    }

    public void setValue(int value) throws RemoteException {
        try {
            demande();

            System.out.println("Value set to : " + value);
            this.sharedValue = value;

            end(value);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
