package it.unipi.lsmsd.DTO;

import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.util.Costant;

public class UpdateReportRequest {
    private String title;
    private String description;
    private Costant.SupportStatus status;

    private Report referenceReport;

    public Costant.SupportStatus getStatus() {
        return this.status;
    }

    public void setStatus(Costant.SupportStatus status) {
        this.status = status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public Report getReferenceReport() {
        return this.referenceReport;
    }

    public void setReferenceReport(Report referenceReport) {
        this.referenceReport = referenceReport;
    }
}
