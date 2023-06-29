package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.util.Costant.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Ticket {
    private String username;
    private String title;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
    private Date ticketDate;
    private String admin;
    private String description;

    private TicketCategory category;

    private String response;

    private SupportStatus status;

    private String ticketId;

    public Ticket(){}


    public Ticket(String title, String description, String response, SupportStatus status, TicketCategory category) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.response = response;
        this.status = status;
    }

    public String getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public void setTicketDate(Date ticketDate){
        this.ticketDate = ticketDate;
    }
    public Date getTicketDate(){
        return this.ticketDate;
    }
    public void setAdmin(String admin){
        this.admin = admin;
    }
    public String getAdmin(){
        return this.admin;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketCategory getCategory() {
        return this.category;
    }

    public void setCategory(TicketCategory category) {
        this.category = category;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public void setStatus(SupportStatus status){
        this.status = status;
    }
    public SupportStatus getStatus(){
        return this.status;
    }
    @Override
    public String toString() {
        return "Ticket{" +
                "title=" + title +
                ", description=" + description +
                ", category=" + category+
                ", response=" + response+
                '}';
    }
}
