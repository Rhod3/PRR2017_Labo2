import remoteInterfaces.ILamport;
import utils.Message;
import utils.MessageType;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Lamport extends UnicastRemoteObject implements ILamport {

    private MessageType[] fileMessage;
    private int[] fileTimeStamp;
    private int numberSite;
    private int me;
    private int logicalClock;
    private boolean csGranted;

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

    public void demande() {
        fileMessage[me] = MessageType.REQUEST;
        fileTimeStamp[me] = ++logicalClock;
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                send(MessageType.REQUEST, j);
            }
        }

        csGranted = permission(me);
    }

    public void waitDuringCs() {

    }

    public void end() {
        fileMessage[me] = MessageType.FREE;
        fileTimeStamp[me] = logicalClock;

        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                send(MessageType.FREE, j);
            }
        }
        csGranted = false;
    }

    public void receive(MessageType messageType, int timeStamp, int sender) {
        logicalClock = Math.max(logicalClock, timeStamp) + 1;
        switch (messageType) {
            case REQUEST:
                fileMessage[sender] = MessageType.REQUEST;
                fileTimeStamp[sender] = timeStamp;

                send(MessageType.RECEIPT, sender);
                break;

            case RECEIPT:
                if (fileMessage[sender] != MessageType.REQUEST) {
                    fileMessage[sender] = MessageType.RECEIPT;
                    fileTimeStamp[sender] = timeStamp;
                }
                break;

            case FREE:
                fileMessage[sender] = MessageType.FREE;
                fileTimeStamp[sender] = timeStamp;

                break;
        }

        csGranted = fileMessage[me] == MessageType.REQUEST && permission(me);
    }

    public void send(MessageType message, int destination) {
        // ENVOIE((message, logicalClock, me), destination);
    }


    public void test() {
        System.out.println("Test Lamport");
    }

    public void receive(Message message) {

    }
}
