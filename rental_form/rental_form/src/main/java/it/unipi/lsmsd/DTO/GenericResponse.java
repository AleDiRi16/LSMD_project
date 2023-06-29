package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericResponse {

    private String answer;

    public GenericResponse(){

    }
    public GenericResponse(String answer){
        this.answer = answer;
    }
    public String getAnswer(){
        return this.answer;
    }
    public void setAnswer(String answer){
        this.answer = answer;
    }
}
