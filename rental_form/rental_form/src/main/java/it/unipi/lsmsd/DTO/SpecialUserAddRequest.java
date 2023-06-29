package it.unipi.lsmsd.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import it.unipi.lsmsd.entity.BusinessActivity;
import it.unipi.lsmsd.entity.User;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SpecialUserAddRequest {

    BusinessActivity newBusinessActivity;

    User newAdmin;

    public SpecialUserAddRequest(){}

    public User getNewAdmin() {
        return this.newAdmin;
    }

    public void setNewAdmin(User newAdmin) {
        this.newAdmin = newAdmin;
    }

    public BusinessActivity getNewBusinessActivity() {
        return this.newBusinessActivity;
    }

    public void setNewBusinessActivity(BusinessActivity newBusinessActivity) {
        this.newBusinessActivity = newBusinessActivity;
    }
}
