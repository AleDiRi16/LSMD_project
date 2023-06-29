package it.unipi.lsmsd.controller;

import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.entity.RentingReservation;
import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.entity.Review;
import it.unipi.lsmsd.entity.Vehicle;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface BusinessActivityControllerInterface {

     ResponseEntity<VehicleInsertResponse> addVehicle(@RequestBody VehicleInsertRequest vehicle);
     ResponseEntity<VehicleUpdateResponse> removeVehicle(@RequestBody VehicleUpdateRequest vehicle);
     ResponseEntity<VehicleUpdateResponse> updateVehicle(@RequestBody VehicleUpdateRequest vehicle);
     ResponseEntity<List<Document>> getAllVehicle(@RequestParam String businessName,@RequestParam Integer page, @RequestParam Map<String,String>searchParam);
    ResponseEntity<List<Document>> getAnalytics(@RequestBody AnalyticsRequest request);
    ResponseEntity<List<Document>> getReviews(@RequestParam String businessActivityName, @RequestParam Integer page,@RequestParam Map<String,String> searchParam);
    ResponseEntity<GenericResponse> removeReport(@RequestBody UpdateNotificationRequest request);
    ResponseEntity<List<Document>>getRentingReservation(String username,@RequestParam Integer page,@RequestParam Map<String,String>searchParam);
}
