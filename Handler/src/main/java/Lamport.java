import remoteInterfaces.ILamport;
import utils.Message;
import utils.MessageFree;
import utils.MessageType;

import java.rmi.Naming;

public class Lamport {
    private MessageType[] fileMessage;
    private int[] fileTimeStamp;
    private int numberSite;
    private int me;
    private int logicalClock;
    private boolean csGranted;
    private int sharedValue;

    public Lamport(int numberSite, int me) {
        this.sharedValue = 0;
        this.numberSite = numberSite;
        this.me = me;
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

    public void receive(Message message) {
        MessageType messageType = message.getMessageType();
        int timeStamp = message.getTimeStamp();
        int sender = message.getSender();

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

                sharedValue = ((MessageFree) message).getNewSharedValue();
                break;
        }

        csGranted = fileMessage[me] == MessageType.REQUEST && permission(me);
    }

    public void send(MessageType messageType, int destination) {
        try {
            // Getting the registry
            // Registry registry = LocateRegistry.getRegistry(10999);

            // Looking up the registry for the remote object
            ILamport lamport = (ILamport) Naming.lookup("Handler" + destination);

            // Calling the remote method using the obtained object
            if (messageType == MessageType.FREE)
                lamport.receive(new MessageFree(messageType, logicalClock, me, sharedValue));
            else
                lamport.receive(new Message(messageType, logicalClock, me));

            // System.out.println("Remote method invoked");
        } catch (Exception e) {
            System.err.println("Handler exception: " + e.toString());
            e.printStackTrace();

        }
    }
}
