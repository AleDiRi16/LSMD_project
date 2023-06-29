package it.unipi.lsmsd.entity;

import it.unipi.lsmsd.util.Costant;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.conversions.Bson;

public class Vehicle {

   @BsonProperty(value = "id")
    private Integer id;
    @BsonProperty(value="automaticTrasmission")
    private Boolean automaticTrasmission;
    @BsonProperty(value="brand")
    private String brand;
    @BsonProperty(value="name")
    private String name;
    @BsonProperty(value="year")
    private Integer year;
    @BsonProperty(value="price")
    private Float price;
    @BsonProperty(value="category")
    private Costant.VehicleCategory category;
    @BsonProperty(value="isAvailable")
    private Boolean isAvailable;
    @BsonProperty(value="vehicleIdentifier")
    private String vehicleIdentifier;

    private String image;
    public Vehicle(){}

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return this.image;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }

    public void setVehicleIdentifier(String vehicleIdentifier) {
        this.vehicleIdentifier = vehicleIdentifier;
    }

    public String getVehicleIdentifier() {
        return this.vehicleIdentifier;
    }

    public Boolean getAutomaticTrasmission() {
        return this.automaticTrasmission;
    }

    public void setAutomaticTrasmission(Boolean automaticTrasmission) {
        this.automaticTrasmission = automaticTrasmission;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsAvailable(){
        return this.isAvailable;
    }
    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Integer getYear() {
        return this.year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getBrand() {
        return this.brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Costant.VehicleCategory getCategory() {
        return this.category;
    }

    public void setCategory(Costant.VehicleCategory category) {
        this.category = category;
    }

}
