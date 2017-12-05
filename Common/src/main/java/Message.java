public enum Message {
    REQUEST("REQUEST"),
    RECEIPT("RECEIPT"),
    FREE("FREE");

    private String value;

    Message(String value){
        this.value = value;
    }

    public String toString(){
        return value;
    }
}
