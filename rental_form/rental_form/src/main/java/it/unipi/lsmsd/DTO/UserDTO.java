package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.RentingReservation;
import it.unipi.lsmsd.entity.Review;
import it.unipi.lsmsd.entity.Ticket;

import java.util.Date;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private String _id;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private String address;
    private String email;
    private String phoneNumber;
    private String occupation;

    private Integer follower;

    private Date dateOfBirth;
    private List<Review> reviews;
    private List<RentingReservation> rentingReservation;
    private List<Ticket> ticketList ;

    public UserDTO(){}
    public UserDTO(String firstName, String lastName, String username, String password,
                 String address, String email, String phoneNumber, Date dateOfBirth,List<Review> reviewList,
                 List<RentingReservation> rentingReservationList,List<Ticket> ticketList) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username= username;
        this.password = password;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.reviews = reviewList;
        this.rentingReservation = rentingReservationList;
        this.ticketList = ticketList;
    }

    public UserDTO(String firstName, String lastName, String username, String password,
                 String address, String email, String phoneNumber, Date dateOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username= username;
        this.password = password;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getOccupation() {
        return this.occupation;
    }

    public Integer getFollower() {
        return this.follower;
    }

    public void setFollower(Integer follower) {
        this.follower = follower;
    }

    public String get_id() {
        return this._id;
    }
    public void set_id(String _id) {
        this._id = _id;
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
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
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
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
    public List<RentingReservation> getRentingReservation() {
        return rentingReservation;
    }
    public void setRentingReservation(List<RentingReservation> rentingReservation) {
        this.rentingReservation = rentingReservation;}
    public List<Ticket> getTicketList() {
        return this.ticketList;
    }
    public void setTicketList(List<Ticket> ticketList) {
        this.ticketList = ticketList;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
