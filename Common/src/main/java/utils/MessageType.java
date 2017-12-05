package utils;

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
