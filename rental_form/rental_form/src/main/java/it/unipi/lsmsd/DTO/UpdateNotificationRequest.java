package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.entity.Ticket;
import it.unipi.lsmsd.util.Costant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateNotificationRequest {
    private String title;
    private String description;
    private Costant.SupportStatus status;
    private String updateInitiator; //who initialize the update process and could be either user,admin and business
    private String response;
    private Costant.TicketCategory category;
    private Ticket referenceTicket;
    private Report referenceReport;
    private String command;

    public UpdateNotificationRequest(){}
    public void setCommand(String command) {
        this.command = command;
    }

    public String getCommand() {
        return this.command;
    }

    public void setUpdateInitiator(String updateInitiator) {
        this.updateInitiator = updateInitiator;
    }

    public String getUpdateInitiator() {
        return this.updateInitiator;
    }

    public Costant.SupportStatus getStatus() {
        return this.status;
    }
    public void setStatus(Costant.SupportStatus status){
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
    public void setCategory(Costant.TicketCategory category){
        this.category = category;
    }
    public Costant.TicketCategory getCategory(){
        return this.category;
    }

    public void setReferenceTicket(Ticket referenceTicket) {
        this.referenceTicket = referenceTicket;
    }
    public Ticket getReferenceTicket(){
        return this.referenceTicket;
    }

    public Report getReferenceReport() {
        return this.referenceReport;
    }

    public void setReferenceReport(Report referenceReport) {
        this.referenceReport = referenceReport;
    }

}
