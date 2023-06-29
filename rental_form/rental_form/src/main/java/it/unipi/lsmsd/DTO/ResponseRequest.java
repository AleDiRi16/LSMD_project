package it.unipi.lsmsd.DTO;

public class ResponseRequest {

    private String answer;
    private boolean isAdmin;

    public ResponseRequest(){
    }

    public ResponseRequest(String answer, boolean isAdmin){
        this.answer = answer;
        this.isAdmin=isAdmin;
    }
    public ResponseRequest(String answer){
        this.answer = answer;
    }

    public String getAnswer(){
        return this.answer;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }
    public boolean getIsAdmin() {return this.isAdmin;}
    public void setAdmin(boolean admin) {isAdmin = admin;}
}
