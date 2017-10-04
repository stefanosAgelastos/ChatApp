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

    public ChatMessage(String message) {
        this.message = message;
        int i = 0;
        for(String s: TYPES){
            if(message.startsWith(s)){
                type = i;
            }
            i++;
            analyseMessage();
        }
    }

    private void analyseMessage() {

        if(type == 0){
            //check name validity
            int splitIndex = message.indexOf(",");
            String tempName= message.substring(5,splitIndex);
            if(tempName.length() <= 12){
                valid = true;
                //TODO check for unsuitable characters
                clientName = tempName;
                //TODO check if the server IP and PORT in message are valid
            }
        }else if(type == 1){
        //to implement later
        }
    }

    public String getMessage() {
        return message;
    }

    public boolean isValid() {
        return valid;
    }

    public String getType() {
        return TYPES[type];
    }

    public String getClientName() {
        return clientName;
    }
}
