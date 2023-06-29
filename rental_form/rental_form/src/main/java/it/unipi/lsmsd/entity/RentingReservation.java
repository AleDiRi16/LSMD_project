package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RentingReservation {

        @BsonProperty(value = "id")
        private Integer id;

        private String businessActivity;
        private String user;
        private String vehicle;

       // @JsonFormat(pattern = "yyyy-MM-dd",timezone = "UTC")
        private Date startDate;
        //@JsonFormat(pattern = "yyyy-MM-dd",timezone = "UTC")
        private Date endDate;
        private Double price;
        private String category;

        private String identifier;

        public RentingReservation(){}
        public RentingReservation (String businessActivity, String user, String vehicle, Integer id, String category, Date startDate,
                                   Date endDate, Double price,String identifier) {
            this.businessActivity = businessActivity;
            this.user = user;
            this.vehicle= vehicle;
            this.startDate = startDate;
            this.endDate = endDate;
            this.price =price;
            this.id=id;
            this.category=category;
            this.identifier=identifier;
        }

    public String getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Double getPrice() {
                return this.price;
            }
            public void setPrice(Double price) {
                this.price = price;
            }
            public Integer getId() {
            return this.id;
        }
            public void setId(Integer id) {
            this.id = id;
        }
            public String getCategory() {return this.category;}
            public void setCategory(String category) {
            this.category = category;
        }
            public String getBusinessActivity() {
                return this.businessActivity;
            }
            public void setBusinessActivity(String businessActivity) {
                this.businessActivity= businessActivity;
            }
            public String getUser() {
                return this.user;
            }
            public void setUser(String user) {
                this.user = user;
            }
            public String getVehicle() {
                return this.vehicle;
            }
            public void setVehicle(String vehicle) {
                this.vehicle = vehicle;
            }
            public Date getStartDate() {
                return this.startDate;
            }
            public void setStartDate(Date startDate) {
                this.startDate= startDate;
            }
            public Date getEndDate() {return this.endDate;}
            public void setEndDate(Date endDate) {this.endDate = endDate;}
            @Override
            public String toString() {
                return "RentingReservation{" +
                        " activity=" +  businessActivity +
                        ", user =" + user +
                        ", vehicle=" + vehicle +
                        ", startdate=" + startDate +
                        ", endDate=" + endDate +
                        ", identifier=" + identifier +
                        ", price=" + price +
                        '}';
            }
    }


