package utils;

/**
 * Sous-classe de message utilisé uniquement pour représenter un message de libération.
 * Cette implémentation de message diffère de la spécification standard de RMI puisqu'elle comprend un attribut
 * représentant la nouvelle valeur de la variable partagée (pour que les gestionnaires recevant ce message puisse
 * mettre à jour leur copie locale de la variable partagée.
 */
public class MessageFree extends Message {
    private int newSharedValue;

    public MessageFree() {}

    /**
     * Constructeur
     * @param messageType Type du message
     * @param timeStamp Estampille du message
     * @param sender Envoyeur
     * @param newSharedValue Nouvelle valeur de la variable partagée
     */
    public MessageFree(MessageType messageType, int timeStamp, int sender, int newSharedValue) {
        super(messageType, timeStamp, sender);
        this.newSharedValue = newSharedValue;
    }

    /**
     * Récupère la nouvelle valeur de la variable partagée
     * @return Valeur de la variable partagée
     */
    public int getNewSharedValue() {
        return newSharedValue;
    }

    /**
     * Définit la valeur de la variable partagée
     * @param newSharedValue Nouvelle valeur de la variable partagée
     */
    public void setNewSharedValue(int newSharedValue) {
        this.newSharedValue = newSharedValue;
    }
}
