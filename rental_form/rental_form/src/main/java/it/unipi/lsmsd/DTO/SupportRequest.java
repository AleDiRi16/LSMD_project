package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.entity.Ticket;
import it.unipi.lsmsd.util.Costant.*;
import org.bson.BsonType;
import org.bson.codecs.ObjectIdCodec;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.codecs.pojo.annotations.BsonRepresentation;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.MongoId;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupportRequest {


    @JsonIgnore
    private ObjectId id;
    private Ticket ticket;
    private Report report;
    private String username;
    private String applicant;
    private RequestCategory requestCategory;
    public SupportRequest(){}


    public String getApplicant() {
        return this.applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }


    public ObjectId getId() {
        return this.id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setTicket(Ticket ticket){
        this.ticket = ticket;
    }
    public Ticket getTicket(){
        return this.ticket;
    }
    public void setReport(Report report){
        this.report = report;
    }
    public Report getReport(){
        return this.report;
    }
    public void setRequestCategory(RequestCategory requestCategory){
        this.requestCategory = requestCategory;
    }
    public RequestCategory getRequestCategory(){
        return this.requestCategory;
    }



}
