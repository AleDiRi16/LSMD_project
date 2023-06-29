package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import it.unipi.lsmsd.util.Password;

import java.util.Date;

public class GenericPageDTO {
    private String username;
    private String address;
    private String phoneNumber;
    private String email;
    private String city;
    private Password password;
    private String description;
    private String firstName;
    private String lastName;



    private String occupation;

    private Date dateOfBirth;

    public GenericPageDTO(String username, String address, String email, String phoneNumber, String city,String description) {
        this.username = username;
        this.city=city;
        this.description=description;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
    }
    public GenericPageDTO(String username) {
        this.username = username;
    }


    public GenericPageDTO(String firstName, String lastName, String username,
                          String address, String email, String phoneNumber, Date dateOfBirth,String occupation) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username= username;
        this.address = address;
        this.email =email;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.occupation = occupation;

    }

    public String getOccupation() {
        return this.occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getUsername() { return username;}
    public void setUsername(String username) {this.username = username;}
    public String getCity() { return city;}
    public void setCity(String city) {this.city = city;}
    public String getEmail() { return email;}
    public void setEmail(String email) {this.email = email;}
    public Password getPassword() {return password;}
    public void setPassword(Password password) { this.password = password;}
    public String getAddress() {return address;}
    public void setAddress(String address) { this.address = address;}
    public String getPhoneNumber() { return phoneNumber;}
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber;}
    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}
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
    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }




}
