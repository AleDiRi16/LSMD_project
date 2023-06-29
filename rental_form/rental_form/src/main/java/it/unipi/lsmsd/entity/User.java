package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.util.Password;
import org.springframework.data.mongodb.core.mapping.Document;


import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "User")
public class User {

    private Object id_;
    private Boolean isAdmin;
    @JsonIgnore
    private Credential credential;
    private String occupation;
    private String firstName;
    private String lastName;
    private String username;
    private Password password;
    private String address;
    private String email;
    private String phoneNumber;
    //  @JsonFormat(pattern = "yyyy-MM-dd")
    private Date dateOfBirth;
    private List<Review> reviews ;
    private List<RentingReservation> rentingReservation;
    private List<Ticket> ticketList ;
    private List<Report> reportList;
    public User(){}


    public User (String firstName, String lastName, String username,
                 String address, String email, String phoneNumber, Date dateOfBirth,List<Review> reviews,
                 List<RentingReservation> rentingReservation,List<Ticket> ticketList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username= username;
        // this.password = password;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.reviews = reviews;
        this.rentingReservation = rentingReservation;
        this.ticketList = ticketList;

    }

    public User (String firstName, String lastName, String username, Password password,
                 String address, String email, String phoneNumber, Date dateOfBirth, String occupation,boolean isAdmin) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username= username;
        this.password=password;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;
        this.isAdmin=false;
    }

    public void setCredential(Credential credential) {
        this.credential = credential;
    }

    public Credential getCredential() {
        return this.credential;
    }

    public String getOccupation() {
        return this.occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setReportList(List<Report> reportList) {
        this.reportList = reportList;
    }

    public List<Report> getReportList() {
        return reportList;
    }

    public void setIsAdmin(Boolean admin) {
        this.isAdmin = admin;
    }

    public Boolean getIsAdmin() {
        return this.isAdmin;
    }

    public Object get_id() {
        return this.id_;
    }
    public void set_id(Object _id) {
        this.id_ = _id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    /*
    public Password getPassword() {
        return password;
    }
    public void setPassword(Password password) {
        this.password = password;
    }
    */

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Date getDateOfBirth() {
        return dateOfBirth;
    }
    public List<Review> getReviews() {
        return reviews;
    }
    public void setReviews(List<Review> reviewList) {
        this.reviews = reviewList;
    }
    public List<RentingReservation> getRentingReservation() {
        return rentingReservation;
    }
    public void setRentingReservation(List<RentingReservation> rentingReservationList) {
        this.rentingReservation = rentingReservationList;
    }
    public List<Ticket> getTicketList() {
        return this.ticketList;
    }
    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public org.bson.Document toDocument () {
        return new org.bson.Document()
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("username", username)
                .append("credential", password.toDocument())
                .append("address", address)
                .append("occupation",occupation)
                .append("email", email)
                .append("phoneNumber", phoneNumber)
                .append("dateOfBirth", dateOfBirth)
                .append("isAdmin",isAdmin);

    }

    public Password getPassword() {return this.password;}

    public void setPassword(Password password) {this.password = password;}
}