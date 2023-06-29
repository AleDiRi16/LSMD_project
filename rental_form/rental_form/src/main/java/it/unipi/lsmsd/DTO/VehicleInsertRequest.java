package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.Vehicle;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleInsertRequest {
    private String businessUsername;
    private Vehicle vehicleToInsert;
    public VehicleInsertRequest(){}

    public Vehicle getVehicleToInsert() {
        return this.vehicleToInsert;
    }

    public void setVehicleToInsert(Vehicle vehicleToInsert) {
        this.vehicleToInsert = vehicleToInsert;
    }

    public void setBusinessUsername(String businessUsername) {
        this.businessUsername = businessUsername;
    }

    public String getBusinessUsername() {
        return this.businessUsername;
    }
}
