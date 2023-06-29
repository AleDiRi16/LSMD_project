package it.unipi.lsmsd.controller;

import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.entity.Ticket;
import jakarta.servlet.http.HttpSession;
import org.bson.Document;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

public interface AdminControllerInterface {

     ResponseEntity<List<SupportRequest>> getAllOpenNotifications(@RequestParam Integer page);
     ResponseEntity<GenericResponse> assignNotification(@RequestBody SupportRequest supportRequest);

     ResponseEntity<GenericResponse> banUser(@RequestBody String username);

    ResponseEntity<GenericResponse> banBusiness(@RequestBody String name);
}
