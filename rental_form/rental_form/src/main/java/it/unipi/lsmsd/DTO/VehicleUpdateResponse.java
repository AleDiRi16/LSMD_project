package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VehicleUpdateResponse {
    private String answer;
    private Integer NumberOfUpdate;

    public VehicleUpdateResponse(){}
    public VehicleUpdateResponse(String answer){
        this.answer = answer;
    }
    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getNumberOfUpdate() {
        return this.NumberOfUpdate;
    }

    public void setNumberOfUpdate(Integer numberOfUpdate) {
        this.NumberOfUpdate = numberOfUpdate;
    }
}
