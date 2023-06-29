package it.unipi.lsmsd.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Review {
    private String id_;
    private String subject;
    private String review;
    private Integer rating;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "UTC")
    private Date dateOfReview;
    private String username;
    private String businessActivity;

    public Review(){}
    public Review (String subject, String review, Integer rating, Date dateOfReview,
                 String username, String businessActivity) {

        this.subject = subject;
        this.username= username;
        this.rating = rating;
        this.review=review;
        this.dateOfReview = dateOfReview;
        this.businessActivity =businessActivity;
    }
    public Review (String subject, String review, Integer rating,String businessActivity, Date dateOfReview) {

        this.subject = subject;
        this.rating = rating;
        this.dateOfReview = dateOfReview;
        this.businessActivity =businessActivity;
    }
    public String getId_() { return id_; }
    public void setId_(String id_) {this.id_ = id_;}
    public void setSubject(String subject) {
        this.subject= subject;
    }
    public String getSubject() {
        return subject;}
    public void setReview(String review) {
        this.review = review;
    }
    public String getReview() {
        return review;
    }
    public void setRating(Integer rating) {
        this.rating= rating;
    }
    public Integer getRating() {
        return rating;
    }
    public void setDateOfReview(Date dateOfReview) {
        this.dateOfReview= dateOfReview;
    }
    public Date getDateOfReview() {
        return dateOfReview;
    }
    public void setUsername(String username) {
        this.username= username;
    }
    public String getUsername() {
        return username;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity= businessActivity;
    }
    public String getBusinessActivity() {
        return businessActivity;
    }
    @Override
    public String toString() {
        return "Review{" +
                "subject=" + subject +
                ", review=" + review +
                ", rating=" + rating +
                ", username =" + username +
                ", businessActivity=" + businessActivity +
                ", dateofreview=" + dateOfReview +
                '}';
    }
}
