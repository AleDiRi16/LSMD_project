package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.Vehicle;

import java.util.Map;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleUpdateRequest {
    private Boolean automaticTrasmission;
    private Boolean isAvailable;
    private Float price;
    private String brand;
    private String name;
    private Integer year;
    private String command;
    private Vehicle vehicleToUpdate;
    private String businessUsername;
    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    public Boolean getAutomaticTrasmission() {
        return this.automaticTrasmission;
    }

    public void setAutomaticTrasmission(Boolean automaticTrasmission) {
        this.automaticTrasmission = automaticTrasmission;
    }
    public Boolean getIsAvailable(){
        return this.isAvailable;
    }
    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public VehicleUpdateRequest(){}

    public String getBusinessUsername() {
        return this.businessUsername;
    }

    public void setBusinessUsername(String businessUsername) {
        this.businessUsername = businessUsername;
    }

    public String getCommand() {
        return this.command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Vehicle getVehicleToUpdate() {
        return this.vehicleToUpdate;
    }

    public void setVehicleToUpdate(Vehicle vehicleToUpdate) {
        this.vehicleToUpdate = vehicleToUpdate;
    }
}
