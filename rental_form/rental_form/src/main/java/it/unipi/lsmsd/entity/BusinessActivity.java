package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.util.Password;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Document(collection = "BusinessActivity")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessActivity {

        @BsonProperty(value = "name")
        private String name;
        @JsonIgnore
        private Credential credential;
        @BsonProperty(value = "username")
        private String username;
        @BsonProperty(value = "address")
        private String address;
        @BsonProperty(value = "phoneNumber")
        private String phoneNumber;
        @BsonProperty(value = "email")
        private String email;
        @BsonProperty(value = "city")
        private String city;
        @BsonProperty(value = "rating")
        private Double rating;
        //@BsonProperty(value = "password")
        private Password password;
        @BsonProperty(value = "description")
        private String description;

        private String image;
        @BsonProperty(value = "reviews")
        private List<Review> reviews;
        @BsonProperty(value = "rentingReservation")
        private List<RentingReservation> rentingReservation;
        @BsonProperty(value = "vehicle")
        private List<Vehicle> vehicle;
        @BsonProperty(value = "reportList")
        public List<Report> reportList;

        public BusinessActivity(){}
        public Credential getCredential() {
                return this.credential;
        }

        public String getImage() {
                return this.image;
        }

        public void setImage(String image) {
                this.image = image;
        }

        public void setCredential(Credential credential) {
                this.credential = credential;
        }

        public void setReportList(List<Report> reportList) {
                this.reportList = reportList;
        }

        public List<Report> getReportList() {
                return this.reportList;
        }

        public String getUsername() {
                return this.username;
        }

        public void setUsername(String username) {
                this.username = username;
        }

        public String getName() { return this.name;}
        public void setName(String name) {this.name = name;}
        public String getCity() { return this.city;}
        public void setCity(String city) {this.city = city;}
        public String getEmail() { return this.email;}
        public void setEmail(String email) {this.email = email;}
        public Password getPassword() {return this.password;}
        public void setPassword(Password password) { this.password = password;}
        public String getAddress() {return this.address;}
        public void setAddress(String address) { this.address = address;}
        public String getPhoneNumber() { return this.phoneNumber;}
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;}
        public String getDescription() {return this.description;}
        public void setDescription(String description) {this.description = description;}
        public List<Review> getReviews() {return this.reviews;}
        public void setReviews(List<Review> reviews) {this.reviews = reviews;}
        public List<Vehicle> getVehicle() {return this.vehicle;}
        public void setVehicle(List<Vehicle> vehicle) {this.vehicle = vehicle;}
        public List<RentingReservation> getRentingReservation() {return this.rentingReservation;}
        public void setRentingReservation(List<RentingReservation> rentingReservation) {this.rentingReservation = rentingReservation;}
        public Double getRating() { return this.rating;}
        public void setRating(Double rating) {this.rating = rating;}
}


