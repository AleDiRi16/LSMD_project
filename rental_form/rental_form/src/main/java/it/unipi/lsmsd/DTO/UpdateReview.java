package it.unipi.lsmsd.DTO;

import it.unipi.lsmsd.entity.Review;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateReview {
    private String subject;
    private String review;
    private Integer rating;
    private Review referenceReview;
    private String username;



    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getSubject() {
        return subject;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getReview() {
        return review;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setReferenceReview(Review review) {
        this.referenceReview = review;
    }
    public Review getReferenceReview() {
        return this.referenceReview;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username= username;
    }


}
