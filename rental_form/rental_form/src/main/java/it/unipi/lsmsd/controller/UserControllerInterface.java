package it.unipi.lsmsd.controller;


import it.unipi.lsmsd.DTO.*;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;
import java.util.Map;

public interface UserControllerInterface {


     ResponseEntity<String> signUp(@RequestBody UserDTO userSignUp);
     ResponseEntity<GenericResponse> addReview(@RequestBody ReviewDTO reviewUser);
     ResponseEntity<GenericResponse> addReservation(@RequestBody RentingReservationDTO reservationUser);
     ResponseEntity<List<Document>> getReviews(@RequestParam String username, @RequestParam Integer page, @RequestParam Map<String,String> searchParam);

     ResponseEntity<List<Document>> getReservations(@RequestParam String username, @RequestParam Integer Page,@RequestParam Map<String,String> searchParam);

    ResponseEntity<List<String>>getFollowedList(@RequestParam String username,@RequestParam Integer page, @RequestParam Map<String,String> SearchParam);
     ResponseEntity<String> followUser(@RequestParam String username);

    ResponseEntity<String> unFollowUser(@RequestParam String username);

    ResponseEntity<List<String>> getFollowerList(@RequestParam String username, @RequestParam Integer page, @RequestParam Map<String, String>searchParam);

     ResponseEntity<String> removeReview(@RequestBody ReviewDTO reviewToRemove);
     ResponseEntity<String> removeReservation(@RequestBody RentingReservationDTO reservation);

     ResponseEntity<String> updateReservation(@RequestBody UpdateReservation reservationUser);
     ResponseEntity<String> updateReview(@RequestBody UpdateReview reviewUser);

     ResponseEntity<List<BusinessDTO>> rankingBusiness();
     ResponseEntity<List<VehicleDTO>> mostRentedVehicle(@RequestParam String name,@RequestParam Integer age);
     ResponseEntity<List<GenericPageDTO>>mostActiveUsers(@RequestParam  String username);
    ResponseEntity<GenericResponse> removeTicket(@RequestBody  UpdateNotificationRequest request);

    ResponseEntity<List<Document>> getBusinessActivityList(@RequestParam String businessActivityName, @RequestParam Integer page, @RequestParam Map<String,String> searchParam);
    public ResponseEntity<List<AnalyticDTO>> getRankingHistoricVehicles(@RequestParam Integer year);
    public ResponseEntity<List<UserDTO>>getMostFollowedUser(@RequestParam String username,@RequestParam Integer page,@RequestParam String direction);

    public ResponseEntity<List<BusinessDTO>>getRecommendedBusiness(@RequestParam String username,@RequestParam Integer page,@RequestParam String direction);


}

