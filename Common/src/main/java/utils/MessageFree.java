package utils;

public class MessageFree extends Message {
    private int newSharedValue;

    public MessageFree() {
    }

    public MessageFree(MessageType messageType, int timeStamp, int sender, int newSharedValue) {
        super(messageType, timeStamp, sender);
        this.newSharedValue = newSharedValue;
    }

    public int getNewSharedValue() {
        return newSharedValue;
    }

    public void setNewSharedValue(int newSharedValue) {
        this.newSharedValue = newSharedValue;
    }
}
