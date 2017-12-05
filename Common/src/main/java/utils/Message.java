package utils;

import java.io.Serializable;

public class Message implements Serializable {
    private MessageType messageType;
    private int timeStamp;
    private int sender;

    public Message() {
    }

    public Message(MessageType messageType, int timeStamp, int sender) {
        this.messageType = messageType;
        this.timeStamp = timeStamp;
        this.sender = sender;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    public int getSender() {
        return sender;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }
}
