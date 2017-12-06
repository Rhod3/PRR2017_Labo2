package utils;

import java.io.Serializable;

/**
 * Classe représentant un message. Ce message sera transmis entre à travers RMI entre différents gestionnaires
 * de variable partagée afin de coordonner l'accès à l'exclusion mutuelle.
 */
public class Message implements Serializable {
    // Type du message
    private MessageType messageType;
    // Estampille du message
    private int timeStamp;
    // Envoyeur du message
    private int sender;

    public Message() {}

    /**
     * Constructeur
     * @param messageType Type du message
     * @param timeStamp Estampille du message
     * @param sender ID envoyeur
     */
    public Message(MessageType messageType, int timeStamp, int sender) {
        this.messageType = messageType;
        this.timeStamp = timeStamp;
        this.sender = sender;
    }

    /**
     * Récupère le type du message
     * @return Le type du message
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * Définit le type du message
     * @param messageType Le type du message
     */
    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    /**
     * Récupère l'estampille du message
     * @return L'estampille du message
     */
    public int getTimeStamp() {
        return timeStamp;
    }

    /**
     * Définit l'estampille du message
     * @param timeStamp L'estampille
     */
    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Récupère l'envoyeur du message
     * @return L'ID de l'envoyeur du message
     */
    public int getSender() {
        return sender;
    }

    /**
     * Définit l'envoyeur du message
     * @param sender L'ID de l'envoyeur du message
     */
    public void setSender(int sender) {
        this.sender = sender;
    }
}
