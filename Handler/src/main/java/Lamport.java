public class Lamport {
    private static int id = 0;
    private Message[] fileMessage;
    private int[] fileTimeStamp;
    private int numberSite;
    private int me;
    private int logicalClock;
    private boolean csGranted;

    public Lamport(int numberSite) {
        this.numberSite = numberSite;
        this.me = id++;
        this.logicalClock = 0;
        this.csGranted = false;
        fileMessage = new Message[numberSite];
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
        fileMessage[me] = Message.REQUEST;
        fileTimeStamp[me] = ++logicalClock;
        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                send(Message.REQUEST, j);
            }
        }

        csGranted = permission(me);
    }

    public void waitDuringCs() {

    }

    public void end() {
        fileMessage[me] = Message.FREE;
        fileTimeStamp[me] = logicalClock;

        for (int j = 0; j < numberSite; ++j) {
            if (j != me) {
                send(Message.FREE, j);
            }
        }
        csGranted = false;
    }

    public void receive(Message messageType, int timeStamp, int sender) {
        logicalClock = Math.max(logicalClock, timeStamp) + 1;
        switch (messageType) {
            case REQUEST:
                fileMessage[sender] = Message.REQUEST;
                fileTimeStamp[sender] = timeStamp;

                send(Message.RECEIPT, sender);
                break;

            case RECEIPT:
                if (fileMessage[sender] != Message.REQUEST) {
                    fileMessage[sender] = Message.RECEIPT;
                    fileTimeStamp[sender] = timeStamp;
                }
                break;

            case FREE:
                fileMessage[sender] = Message.FREE;
                fileTimeStamp[sender] = timeStamp;

                break;
        }

        csGranted = fileMessage[me] == Message.REQUEST && permission(me);
    }

    public void send(Message message, int destination) {
        // ENVOIE((message, logicalClock, me), destination);
    }


}
