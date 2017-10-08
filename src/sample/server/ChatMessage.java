package sample.server;

/*
* responsibilities:
* identify type of message
* manipulate chat messages
* find the name of the client
* make sure the name is less than 12 characters
* TODO make sure name consists of only letters, digits, ‘-‘ and ‘_’.
* marks message as VALID or not.
* TODO define what is VALID, different for each type of message.
* */

public class ChatMessage {

    private final String[] TYPES = {"JOIN", "DATA", "IMAV", "QUIT"};
    private boolean valid= false;
    private int type = -1;
    private String message;
    private String clientName;

    /**
     * the constructor receives a message as a parameter and detects the protocol's type of the message.
     * If the type is detected then the field valid is set to true.
     * If the messsage is type JOIN, then it also calls extractName(), that will check more in depth about
     * the name's validity.
     * @param message the raw protocol message.
     */
    public ChatMessage(String message) {
        this.message = message;
        int i = 0;
        for(String s: TYPES){
            if(message.startsWith(s)){
                type = i;
                valid=true;
            }
            i++;
        }
        if(type==0){ //type is JOIN
            extractName();
        }
    }

    /**
     * extracts the name from a JOIN message. if the format of the message is not valid, or if the name is longer
     * than 12 characters then the valid field is set to false.
     */
    private void extractName() {
        //check name validity
        int splitIndex = message.indexOf(",");
        if(splitIndex!=-1){
            String tempName= message.substring(5,splitIndex);
            if(tempName.length() < 13 && tempName.length()>0 && tempName.matches("[a-zA-z0-9_\\-]*")){
                clientName = tempName;
                //TODO check if the server IP and PORT in message are valid?
            }else {
                valid = false;
            }
        }else{
            valid = false;
        }
    }

    /**
     *
     * @return the raw message.
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @return the valid field value.
     */
    public boolean isValid() {
        return valid;
    }

    /**
     *
     * @return the type of the message, or if no type is detected the String "ERROR"
     */
    public String getType() {
        if(valid){
            return TYPES[type];
        }else{
            return "ERROR";
        }
    }

    /**
     *
     * @return the value of the clientName field
     */
    public String getClientName() {
        return clientName;
    }
}
