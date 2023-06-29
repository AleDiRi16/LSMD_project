package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.util.Costant. *;

import java.util.Date;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class Report {

    private String businessActivityName;
    private String title;
    private String description;
    private SupportStatus status;
    private String admin;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "UTC")
    private Date dateOfReport;
    private Review review;
    private String reportId;
    private String response;
    public Report(){}

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getAdmin() {
        return this.admin;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public SupportStatus getStatus() {
        return this.status;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public void setStatus(SupportStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Report(String businessActivityName, Review review){
        this.businessActivityName = businessActivityName;
        this.review = review;
    }
    public void setDateOfReport(Date dateOfReport){
        this.dateOfReport = dateOfReport;
    }
    public Date getDateOfReport(){
        return this.dateOfReport;
    }
    public void setBusinessActivityName(String businessActivityName){
        this.businessActivityName = businessActivityName;
    }
    public void setReview(Review review){
        this.review = review;
    }
    public String getBusinessActivityName(){
        return this.businessActivityName;
    }
    public Review getReview() {
        return this.review;
    }
}
