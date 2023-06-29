package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.RentingReservation;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentingReservationDTO {
    //Business name
    private String activity;
    //user or business username, and it depends on who is going to send this DTO
    private String username;
    private RentingReservation rentingReservation;

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public RentingReservation getRentingReservation() {
        return this.rentingReservation;
    }

    public void setRentingReservation(RentingReservation rentingReservation) {
        this.rentingReservation = rentingReservation;
    }

    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
