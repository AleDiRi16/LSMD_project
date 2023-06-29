package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.Review;

import java.util.Date;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewDTO {

    private String username;
    private String businessActivity;
    private Review review;

    public Review getReview() {
        return this.review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getBusinessActivity() {
        return this.businessActivity;
    }

    public void setBusinessActivity(String businessActivity) {
        this.businessActivity = businessActivity;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }
}
