package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalyticsRequest {
    private String command;
    private String businessUsername;

    private Date startDate;

    private Date endDate;

    public AnalyticsRequest(){}

    public String getCommand() {
        return this.command;
    }
    public void setCommand(String command){
        this.command = command;
    }

    public String getBusinessUsername() {
        return this.businessUsername;
    }

    public void setBusinessUsername(String businessUsername) {
        this.businessUsername = businessUsername;
    }

    public Date getEndDate() {
        return this.endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return this.startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}
