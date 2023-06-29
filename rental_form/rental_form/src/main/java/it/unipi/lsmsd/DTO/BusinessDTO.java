package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.RentingReservation;
import it.unipi.lsmsd.entity.Review;
import it.unipi.lsmsd.util.Password;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessDTO {
    private String name;
    private String city;
    private Double rating;


    public BusinessDTO(String name,String city,Double rating) {
        this.name = name;
        this.city=city;
        this.rating=rating;

    }

    public BusinessDTO() {}


    public Double getRating() {
        return this.rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getName() { return name;}
    public void setName(String name) {this.name = name;}
    public String getCity() { return city;}
    public void setCity(String city) {this.city = city;}



}
