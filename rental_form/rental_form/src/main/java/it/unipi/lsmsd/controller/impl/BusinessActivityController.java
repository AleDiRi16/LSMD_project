package it.unipi.lsmsd.controller.impl;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;

import com.mongodb.client.model.Projections;
import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.component.BusinessComponentLogic;
import it.unipi.lsmsd.controller.BusinessActivityControllerInterface;
import it.unipi.lsmsd.entity.*;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.MongoDriver;
import it.unipi.lsmsd.service.NotificationManager;
import it.unipi.lsmsd.util.SessionManagement;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.summary.ResultSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static org.neo4j.driver.Values.parameters;

@RestController
@RequestMapping("/business")
public class BusinessActivityController implements BusinessActivityControllerInterface {
    @Autowired
    SessionManagement session;
    @Autowired
    BusinessComponentLogic componentLogic;
    @Autowired
    MongoDriver mongoDriver;
    @Autowired
    NotificationManager notificationManager;

    @Autowired
    GraphDriver graphDriver;

    @PostMapping("/add_vehicle")
    @Override
    public ResponseEntity<VehicleInsertResponse> addVehicle(VehicleInsertRequest request) {
        session=SessionManagement.getInstance();
        if (session.getLogUser() == null || !session.getLogUser().equals(request.getBusinessUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        VehicleInsertResponse response = new VehicleInsertResponse();
        HttpStatus statusCode;
        if (!request.getBusinessUsername().isEmpty() && request.getVehicleToInsert()!=null) {

            String result = componentLogic.insertVehicle(request);
            if (result != null) {
                response.setAnswer("You inserted the following vehicle");
                response.setInsertedVehicle(result);
                statusCode = HttpStatus.OK;
            } else {
                response.setAnswer("A problem occurred during your insert request, please try again later");
                statusCode = HttpStatus.NO_CONTENT;
            }
        } else {
            response.setAnswer("A Problem Occurred with your vehicle insert request. HINT: CHECK if you inserted all the data");
            statusCode = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, statusCode);
    }

    @Override
    @DeleteMapping("/remove_vehicle")
    public ResponseEntity<VehicleUpdateResponse> removeVehicle(VehicleUpdateRequest request) {
        session=SessionManagement.getInstance();
        if (session.getLogUser() == null || !session.getLogUser().equals(request.getBusinessUsername()))
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        ResponseEntity<VehicleUpdateResponse> response;
        if (request.getBusinessUsername().contains("@Business")) {
            response = new ResponseEntity<>(componentLogic.removeOne(request), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new VehicleUpdateResponse("You don't have the necessary permission"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    @Override
    @PatchMapping("/update_vehicle")
    public ResponseEntity<VehicleUpdateResponse> updateVehicle(VehicleUpdateRequest vehicle) {
        session=SessionManagement.getInstance();
        if (session.getLogUser() == null || !session.getLogUser().equals(vehicle.getBusinessUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        ResponseEntity<VehicleUpdateResponse> response;
        if (vehicle.getBusinessUsername().contains("@Business")) {
            response = new ResponseEntity<>(componentLogic.updateOne(vehicle), HttpStatus.OK);
        } else {
            response = new ResponseEntity<>(new VehicleUpdateResponse("You don't have the necessary permission"), HttpStatus.UNAUTHORIZED);
        }
        return response;
    }

    @Override
    @GetMapping("/all_business_vehicle")
    public ResponseEntity<List<Document>> getAllVehicle(String businessName, Integer page, Map<String, String> searchParam) {

        List<Document> vehicleList = componentLogic.getAllVehicle(businessName, page, searchParam.get("sortTarget"), searchParam.get("direction"),searchParam.get("searchText"));

        return vehicleList != null ? new ResponseEntity<>(vehicleList, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/remove_report")
    @Override
    public ResponseEntity<GenericResponse> removeReport(UpdateNotificationRequest request) {
        session=SessionManagement.getInstance();
        if (session.getLogUser()== null || !session.getLogUser().equals(request.getUpdateInitiator()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        GenericResponse response = new GenericResponse();
        if (request.getCommand().equalsIgnoreCase("DELETE")) {
            response = notificationManager.update(request);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/analytics")
    @Override
    public ResponseEntity<List<Document>> getAnalytics(@RequestBody AnalyticsRequest request) {
        /*session=SessionManagement.getInstance();
        if (session.getLogUser() == null || !session.getLogUser().equals(request.getBusinessUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        */List<Document> response = null;
        if (request.getCommand().equalsIgnoreCase("VEHICLE_BY_WORKER")) {
            response = componentLogic.getMostRentedVehicleByWorker(request);
        } else if (request.getCommand().equalsIgnoreCase("VEHICLE_BY_DATE")) {
            response = componentLogic.getRentedVehicleByDate(request, 1);
        }
        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Override
    @GetMapping("/getReviews")
    //searchParam is a map which contains the sortTarget and direction query param
    public ResponseEntity<List<Document>> getReviews(String businessActivityName, Integer page, Map<String, String> searchParam) {
        ResponseEntity<List<Document>> response;
        response = new ResponseEntity<>(componentLogic.getReviews(businessActivityName, page, searchParam.get("sortTarget"), searchParam.get("direction"),searchParam.get("searchText")), HttpStatus.OK);

        return response;
    }

    @Override
    @GetMapping("/get_renting_reservation")
    public ResponseEntity<List<Document>> getRentingReservation(String username, Integer page, Map<String, String> searchParam) {
        session=SessionManagement.getInstance();
        System.out.println(session.getLogUser());
        if(session.getLogUser() == null || !session.getLogUser().equals(username))
             return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        List<Document> response;
        response = componentLogic.getReservations(username, page, searchParam.get("sortTarget"), searchParam.get("direction"), searchParam.get("searchText"));

        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
}




