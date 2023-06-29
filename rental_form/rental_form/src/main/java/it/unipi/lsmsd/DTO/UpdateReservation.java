package it.unipi.lsmsd.DTO;

import it.unipi.lsmsd.entity.RentingReservation;

import java.util.Date;

public class UpdateReservation {
    private RentingReservation reservation;

    private Date startDate;

    private Date endDate;
    private String user;




    public UpdateReservation(Date startDate, RentingReservation reservation,
                             Date endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.reservation=reservation;

    }


    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate= startDate;
    }
    public Date getEndDate() {return endDate;}
    public void setEndDate(Date endDate) {this.endDate = endDate;}
    public RentingReservation getReservation() {
        return reservation;
    }
    public void setReservation(RentingReservation reservation) {
        this.reservation= reservation;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user= user;
    }
}