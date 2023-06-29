package it.unipi.lsmsd.controller.impl;

import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.component.AdminComponentLogic;
import it.unipi.lsmsd.component.BusinessComponentLogic;
import it.unipi.lsmsd.component.UserComponentLogic;
import it.unipi.lsmsd.controller.GenericControllerInterface;
import it.unipi.lsmsd.entity.Review;
import it.unipi.lsmsd.service.NotificationManager;
import it.unipi.lsmsd.util.Costant;
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
@SessionAttributes("userlog")
public class GenericController implements GenericControllerInterface {
    @Autowired
    SessionManagement session;
    @Autowired
    NotificationManager notificationManager;
    @Autowired
    UserComponentLogic userService;
    @Autowired
    BusinessComponentLogic BService;
    @Autowired
    AdminComponentLogic adminService;
    UserAccessRequest user;
    GenericPageDTO loadPage;


    @PostMapping("/login")
    public ResponseEntity<ResponseRequest> login(UserAccessRequest requestUser) {
        session=SessionManagement.getInstance();
        ResponseRequest responsereq;

        if(session.getLogUser() != null)
        {   responsereq=new ResponseRequest("User Already logged");

            return new ResponseEntity<>(responsereq,HttpStatus.BAD_REQUEST);
        }
        if (requestUser.getUsername().contains(Costant.business_patt)) {

            Boolean response =  userService.getBusiness(requestUser.getUsername(), requestUser.getPassword());
            if (response == null){
                responsereq=new ResponseRequest("Business username not found");
                return new ResponseEntity<>(responsereq,HttpStatus.BAD_REQUEST);
            } else if (response == false ) {
                responsereq=new ResponseRequest("Password not correct");
                return new ResponseEntity<>(responsereq,HttpStatus.UNAUTHORIZED);
            } else{
                session.setLogUser(requestUser.getUsername());
                session.setIsAdmin(false);
            }
        }
        else {
            try {
                user = userService.getUser(requestUser.getUsername(), requestUser.getPassword(),false);
                if(user == null){
                    responsereq=new ResponseRequest("User not found");
                    return new ResponseEntity<>(responsereq,HttpStatus.BAD_REQUEST);
                }

                session.setLogUser(user.getUsername());
                session.setIsAdmin(user.getIsAdmin());
            }catch(Exception e){
                responsereq=new ResponseRequest(e.getMessage());
                return new ResponseEntity<>(responsereq,HttpStatus.UNAUTHORIZED);
            }
        }

        ResponseRequest response=new ResponseRequest("Login success",session.getIsAdmin());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/isLogged")
    public ResponseEntity<String> isLogged() {
        session=SessionManagement.getInstance();
        if(session.getLogUser() == null)
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        else
            return new ResponseEntity<>("OK", HttpStatus.OK);
    }


    @GetMapping("/userProfile")
    public ResponseEntity<GenericPageDTO> getProfile(String username) {

        if (username.contains(Costant.business_patt)) {
            System.out.println(username.contains(Costant.business_patt));
            loadPage = BService.getProfile(username);
        } else {
            System.out.println("profilo"+session.getLogUser());
            loadPage = userService.getProfile(username);
        }
        if (loadPage != null)
            return new ResponseEntity<>(loadPage, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }
    @GetMapping("/logout")
    public ResponseEntity<String> logout() {
        session=SessionManagement.getInstance();
        System.out.println(session.getLogUser());
       session.setLogUser(null);
       session.setIsAdmin(false);
        return new ResponseEntity<>("Logout", HttpStatus.OK);
    }

    @PostMapping("/sendNotification")
    public ResponseEntity<GenericResponse> sendNotification(SupportRequest request){
        ResponseEntity<GenericResponse> response = null;
        if(!request.getUsername().contains("@Business") && request.getReport()!=null){
            response =  new ResponseEntity<>(new GenericResponse("You can't perform this action"),HttpStatus.UNAUTHORIZED);
        }else{
            response = new ResponseEntity<>(notificationManager.addToNotification(request),HttpStatus.OK);
        }
        return response;
    }

    //it can be used by any kind of user
    @PatchMapping("/updateNotification")
    public ResponseEntity<GenericResponse> updateNotification(UpdateNotificationRequest request){
        GenericResponse response = notificationManager.update(request);
        return response!=null ? new ResponseEntity<>(response,HttpStatus.OK) :
                new ResponseEntity<>(new GenericResponse("Ops something wrong just Happen retry later"),HttpStatus.BAD_REQUEST);
    }

    @Override
    @GetMapping("/get_report")
    public ResponseEntity<List<Document>>getReport(String username,Integer page, Map<String,String> searchParam){
        List<Document> reports;
        ResponseEntity<List<Document>> response;
        reports = notificationManager.getAllReport(page, username, searchParam.get("sortTarget"),searchParam.get("direction"),searchParam.get("searchText"));
        response = new ResponseEntity<>(reports,HttpStatus.OK);

        return response;
    }
    @GetMapping("/get_ticket")
    @Override
    public ResponseEntity<List<Document>>getTicket(String username,Integer page, Map<String,String> searchParam){
        List<Document> tickets;
        ResponseEntity<List<Document>> response;
        tickets = notificationManager.getAllTicket(page,username,searchParam.get("sortTarget"),searchParam.get("direction"),searchParam.get("searchText"));
        response = new ResponseEntity<>(tickets,HttpStatus.OK);

        return response;
    }

}