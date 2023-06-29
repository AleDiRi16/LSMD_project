package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleInsertResponse {
    private String answer;
    private String insertedVehicle;
    public VehicleInsertResponse(){}

    public String getInsertedVehicle() {
        return this.insertedVehicle;
    }

    public void setInsertedVehicle(String insertedVehicle) {
        this.insertedVehicle = insertedVehicle;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
