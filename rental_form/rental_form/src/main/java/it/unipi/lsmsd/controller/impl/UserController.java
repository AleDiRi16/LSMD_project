package it.unipi.lsmsd.controller.impl;

import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.component.BusinessComponentLogic;
import it.unipi.lsmsd.component.UserComponentLogic;
import it.unipi.lsmsd.controller.UserControllerInterface;
import it.unipi.lsmsd.entity.*;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.NotificationManager;
import it.unipi.lsmsd.util.Password;
import it.unipi.lsmsd.util.SessionManagement;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
public class UserController implements UserControllerInterface {
    @Autowired
    SessionManagement session;
    @Autowired
    UserComponentLogic userService;
    @Autowired
    BusinessComponentLogic BService;
    @Autowired
    NotificationManager notificationManager;

    @Autowired
    GraphDriver test;

    @PostMapping("/signup")
    @Override
    public ResponseEntity<String> signUp(UserDTO UserSignUp) {
        UserAccessRequest User = null;
        try {
            User = userService.getUser(UserSignUp.getUsername(), UserSignUp.getPassword(),true);
        }catch (Exception e){

        }
        if (User != null)
            return new ResponseEntity<>("Username already in use", HttpStatus.BAD_REQUEST);
        boolean userEmail = userService.getUserByEmail(UserSignUp.getEmail());
        if (userEmail)
            return new ResponseEntity<>("Email already in use", HttpStatus.BAD_REQUEST);
        Password dbPassword = new Password(UserSignUp.getPassword());

        User user = new User(UserSignUp.getFirstName(), UserSignUp.getLastName(), UserSignUp.getUsername(),dbPassword, UserSignUp.getAddress(), UserSignUp.getEmail(), UserSignUp.getPhoneNumber(), UserSignUp.getDateOfBirth(),UserSignUp.getOccupation(),false);
        if (userService.addUser(user))
            return new ResponseEntity<>("Signup success", HttpStatus.OK);
        else return new ResponseEntity<>("User not inserted", HttpStatus.BAD_REQUEST);

    }

    @PostMapping("/addReview")
    @Override
    public ResponseEntity<GenericResponse> addReview(ReviewDTO reviewUser) {
        session=SessionManagement.getInstance();
        System.out.print(session.getLogUser());
        if(session.getLogUser() == null || !session.getLogUser().equals(reviewUser.getUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        boolean result = BService.AddReview(reviewUser);
        if (result)
            return new ResponseEntity<>(new GenericResponse("Review inserted"), HttpStatus.OK);
        return new ResponseEntity<>(new GenericResponse("Review not inserted"), HttpStatus.OK);
    }

    @PostMapping("/followUser")
    @Override
    public ResponseEntity<String> followUser(String username) {
        boolean result;
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        result=userService.boolFollower(session.getLogUser(),username);
        if(result) {
            return new ResponseEntity<>("User already followed", HttpStatus.BAD_REQUEST);
        }
        result = userService.FollowUser(session.getLogUser(), username);
        if (result)
            return new ResponseEntity<>("NEW FOLLOWER ADDED", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/unfollowUser")
    @Override
    public ResponseEntity<String> unFollowUser(String username) {
        Boolean result;
        session=SessionManagement.getInstance();
        System.out.println(session.getLogUser());
        if(session.getLogUser() == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        result=userService.boolFollower(session.getLogUser(),username);
        if(!result) {
            return new ResponseEntity<>("User not followed", HttpStatus.BAD_REQUEST);
        }
        result = userService.UnfollowUser(session.getLogUser(), username);
        if (result)
            return new ResponseEntity<>("USER UNFOLLOWED", HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getFollowerList")
    @Override
    public ResponseEntity<List<String>>getFollowerList(String username,Integer page,Map<String, String> searchParam) {
        List<String> follower = userService.GetFollowerList(page,username,searchParam.get("direction"),searchParam.get("searchText"));
        if (follower!= null)
            return new ResponseEntity<>(follower,HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/getFollowedList")
    @Override
    public ResponseEntity<List<String>>getFollowedList(String username,Integer page, Map<String, String>searchParam) {
        List<String> follower = userService.GetFollowedList(page,username,searchParam.get("direction"),searchParam.get("searchText"));
        if (follower!= null)
            return new ResponseEntity<>(follower,HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @Override
    @GetMapping("/getReviews")
    public ResponseEntity<List<Document>>getReviews(String username, Integer page, Map<String,String> searchParam){
        List<Document> ReviewU = userService.getReviews(username, page,searchParam.get("sortTarget"),searchParam.get("direction"),searchParam.get("searchText"));
        if (ReviewU != null)
            return new ResponseEntity<>(ReviewU, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getReservations")
    @Override
    public ResponseEntity<List<Document>> getReservations(String username, Integer Page,Map<String,String> searchParam) {
        session=SessionManagement.getInstance();
        System.out.print("ok"+session.getLogUser());
        if(session.getLogUser() == null || !session.getLogUser().equals(username))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        List<Document> reservationList = userService.getReservations(username, Page, searchParam.get("sortTarget"),searchParam.get("direction"),searchParam.get("searchText"));
        if (reservationList != null) {
            return new ResponseEntity<>(reservationList, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

 @PostMapping("/addReservation")
    @Override
    public ResponseEntity<GenericResponse> addReservation(RentingReservationDTO reservationUser){
   //  session=SessionManagement.getInstance();
   //  if(session.getLogUser() == null || !session.getLogUser().equals(reservationUser.getUsername()))
    //        return new ResponseEntity<>(new GenericResponse("Login required to book for a rent"),HttpStatus.FORBIDDEN);
        boolean result;
        result=BService.reservationValidity(reservationUser.getRentingReservation());
        if(!result)
            return new ResponseEntity<>(new GenericResponse("VEHICLE NOT AVAILABLE ON THESE DATES,CHOOSE OTHERS"), HttpStatus.BAD_REQUEST);
        result=BService.AddRentingReservation(reservationUser);
        if (result)
            return new ResponseEntity<>(new GenericResponse("RESERVATION ADDED"), HttpStatus.OK);
        return new ResponseEntity<>(new GenericResponse("RESERVATION NOT ADDED"), HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/removeReview")
    @Override
    public ResponseEntity<String> removeReview(ReviewDTO reviewToRemove) {
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null || !session.getLogUser().equals(reviewToRemove.getUsername()))
             return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        boolean result = BService.removeReview(reviewToRemove);
        if (result)
            return new ResponseEntity<>("Review removed", HttpStatus.OK);
        return new ResponseEntity<>("Review not removed", HttpStatus.BAD_REQUEST);
    }
    @PostMapping("/removeReservation")
    @Override
    public ResponseEntity<String> removeReservation( RentingReservationDTO reservation) {
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null || !session.getLogUser().equals(reservation.getUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        boolean result = BService.RemoveReservation(reservation);
        if (result)
            return new ResponseEntity<>("Reservation removed", HttpStatus.OK);
        return new ResponseEntity<>("Reservation not removed", HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/updateReview")
    @Override
    public ResponseEntity<String> updateReview(UpdateReview reviewUser) {
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null || !session.getLogUser().equals(reviewUser.getUsername()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        boolean result = BService.UpdateReview(reviewUser);
        if (result)
            return new ResponseEntity<>("Review modified", HttpStatus.OK);
        return new ResponseEntity<>("Review not modified", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/updateReservation")
    @Override
    public ResponseEntity<String> updateReservation( UpdateReservation reservationUser) {
        RentingReservation rs = new RentingReservation(reservationUser.getReservation().getBusinessActivity(), reservationUser.getUser(), reservationUser.getReservation().getVehicle(), reservationUser.getReservation().getId(), reservationUser.getReservation().getCategory(), reservationUser.getStartDate(), reservationUser.getEndDate(), reservationUser.getReservation().getPrice(), reservationUser.getReservation().getIdentifier());
        boolean result;
        result=BService.reservationValidity(rs);
        if(!result) {
            return new ResponseEntity<>("VEHICLE NOT AVAILABLE ON THESE DATES,CHOOSE OTHERS", HttpStatus.BAD_REQUEST);
        }
         result = BService.UpdateReservation(reservationUser);
        if (result)
            return new ResponseEntity<>("Reservation modified", HttpStatus.OK);
        return new ResponseEntity<>("Reservation not modified,vehicle not available in these date", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getRankingBusiness")
    @Override
    public ResponseEntity<List<BusinessDTO>> rankingBusiness() {
        List<BusinessDTO> bs = userService.RankingBusiness();
        if (bs== null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(bs,HttpStatus.OK);
    }
    @GetMapping("/getMostRentedVehicle")
    @Override
    public ResponseEntity<List<VehicleDTO>> mostRentedVehicle(String name,Integer age) {
        List<VehicleDTO> result = userService.MostRentedVehicle(name,age);
        if (result== null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
    @GetMapping("/getRankingHistoricVehicles")
    @Override
    public ResponseEntity<List<AnalyticDTO>> getRankingHistoricVehicles(Integer year) {
        List<AnalyticDTO> result = userService.getRankingHistoricVehicles(year);
        if (result== null) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @GetMapping("/getMostActiveUsers")
    @Override
    public ResponseEntity<List<GenericPageDTO>> mostActiveUsers(String username) {
        List<GenericPageDTO> result = userService.MostActiveUsers(username);
        if (result== null)
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

    @DeleteMapping("/remove_ticket")
    @Override
    public ResponseEntity<GenericResponse> removeTicket(UpdateNotificationRequest request){
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null || !session.getLogUser().equals(request.getUpdateInitiator()))
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        GenericResponse response = new GenericResponse();
        if(request.getCommand().equalsIgnoreCase("DELETE")) {
            response = notificationManager.update(request);
        }else{
            response.setAnswer("No delete command found");
        }
        return new ResponseEntity<>(response,HttpStatus.OK) ;
    }


    @GetMapping("/get_business_activity")
    public ResponseEntity<List<Document>> getBusinessActivityList(String businessActivityName, Integer page, Map<String,String> searchParam){

        List<Document> result = BService.getBusinessActivity(businessActivityName,page,searchParam.get("sortTarget"),searchParam.get("direction"));

        return new ResponseEntity<>(result,HttpStatus.OK);
    }


    @Override
    @GetMapping("/get_most_followed_user")
    public ResponseEntity<List<UserDTO>>getMostFollowedUser(String username, Integer page, String direction){

        return new ResponseEntity<>(userService.getMostFollowedUser(page, username, direction),HttpStatus.OK);
    }

    @Override
    @GetMapping("/get_recommended_business_activity")
    public ResponseEntity<List<BusinessDTO>>getRecommendedBusiness(String username, Integer page, String direction){

        return new ResponseEntity<>(userService.getRecommendedBusinessActivity(page, username,direction),HttpStatus.OK);
    }


}
