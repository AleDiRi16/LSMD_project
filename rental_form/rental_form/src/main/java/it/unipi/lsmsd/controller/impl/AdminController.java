package it.unipi.lsmsd.controller.impl;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.component.BusinessComponentLogic;
import it.unipi.lsmsd.component.UserComponentLogic;
import it.unipi.lsmsd.controller.AdminControllerInterface;
import it.unipi.lsmsd.entity.Report;
import it.unipi.lsmsd.entity.Ticket;
import it.unipi.lsmsd.component.AdminComponentLogic;
import it.unipi.lsmsd.service.MongoDriver;
import it.unipi.lsmsd.service.NotificationManager;

import it.unipi.lsmsd.util.Costant;
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
@RequestMapping("/admin")
public class AdminController implements AdminControllerInterface {
    @Autowired
    SessionManagement session;
    @Autowired
    AdminComponentLogic adminComponentLogic;
    @Autowired
    BusinessComponentLogic businessComponentLogic;
    @Autowired
    UserComponentLogic userComponentLogic;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    MongoDriver mongoDriver;

    @Override
    @GetMapping("/get_all_open")
    public ResponseEntity<List<SupportRequest>> getAllOpenNotifications(Integer page) {
        session=SessionManagement.getInstance();
        List<SupportRequest> openNotifications;
        ResponseEntity<List<SupportRequest>> response;
        if(session.getIsAdmin()==true) {
            openNotifications = notificationManager.getAll(page);
            response = new ResponseEntity<>(openNotifications, HttpStatus.OK);
          }
         else{
            openNotifications =null;
            response = new ResponseEntity<>(openNotifications, HttpStatus.FORBIDDEN);

        }
        return response;
    }
    @Override
    @PostMapping("/assign_open_notification")
    public ResponseEntity<GenericResponse> assignNotification(SupportRequest supportRequest) {
        GenericResponse response = notificationManager.dispatchSupportRequest(supportRequest);

        return response != null ? new ResponseEntity<>(response, HttpStatus.OK) : new ResponseEntity<>(new GenericResponse(), HttpStatus.OK);
    }

    @DeleteMapping("/remove_user")
    @Override
    public ResponseEntity<GenericResponse> banUser(String username) {
        boolean banResult = adminComponentLogic.removeUser(username);
        GenericResponse response = banResult ? new GenericResponse(String.format("%s is banned from the platform", username))
                : new GenericResponse(String.format("Impossible to remove %s, he/she may be already banned or you write an incorrect username", username));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/remove_business")
    @Override
    public ResponseEntity<GenericResponse> banBusiness(String name) {
        System.out.println(name);
        boolean banResult = adminComponentLogic.removeBusiness(name);
        GenericResponse response = banResult ? new GenericResponse(String.format("%s is banned from the platform", name))
                : new GenericResponse(String.format("Impossible to remove %s, he/she may be already banned or you write an incorrect username", name));
        return new ResponseEntity<>(response, HttpStatus.OK);

    }


}