package utils;

/**
 * Enum représentant les différents types de messages utilisés dans l'algorithme de Lamport
 */
public enum MessageType {
    REQUEST("REQUEST"),
    RECEIPT("RECEIPT"),
    FREE("FREE");

    private String value;

    MessageType(String value){
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
