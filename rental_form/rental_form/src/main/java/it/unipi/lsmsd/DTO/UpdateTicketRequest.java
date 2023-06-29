package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.util.Costant.*;
import it.unipi.lsmsd.entity.Ticket;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateTicketRequest {
    private String updateInitiator;
    private String title;
    private String description;

    private String response;

    private SupportStatus status;

    private TicketCategory category;

    private Ticket referenceTicket;

    public UpdateTicketRequest(){}

    public void setUpdateInitiator(String updateInitiator) {
        this.updateInitiator = updateInitiator;
    }

    public String getUpdateInitiator() {
        return this.updateInitiator;
    }

    public SupportStatus getStatus() {
        return this.status;
    }
    public void setStatus(SupportStatus status){
        this.status = status;
    }
    public String getTitle(){
        return this.title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setResponse(String response){
        this.response = response;
    }
    public String getResponse(){
        return this.response;
    }
    public void setCategory(TicketCategory category){
        this.category = category;
    }
    public TicketCategory getCategory(){
        return this.category;
    }

    public void setReferenceTicket(Ticket referenceTicket) {
        this.referenceTicket = referenceTicket;
    }
    public Ticket getReferenceTicket(){
        return this.referenceTicket;
    }
}
