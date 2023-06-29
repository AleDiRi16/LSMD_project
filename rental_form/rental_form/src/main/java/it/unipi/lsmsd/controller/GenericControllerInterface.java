package it.unipi.lsmsd.controller;

import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.entity.RentingReservation;
import it.unipi.lsmsd.entity.Review;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface GenericControllerInterface {

     ResponseEntity<String> logout();
     ResponseEntity<ResponseRequest> login(@RequestBody UserAccessRequest requestUser);
     ResponseEntity<String> isLogged();
     ResponseEntity<GenericPageDTO> getProfile(@RequestParam String username);
     ResponseEntity<GenericResponse>updateNotification(@RequestBody UpdateNotificationRequest request);

     ResponseEntity<GenericResponse>sendNotification(@RequestBody SupportRequest request);
    ResponseEntity<List<Document>>getReport(@RequestParam String username, @RequestParam Integer page,@RequestParam Map<String,String> searchParam);
    ResponseEntity<List<Document>>getTicket(@RequestParam String username, @RequestParam Integer page,@RequestParam Map<String,String> searchParam);
}
