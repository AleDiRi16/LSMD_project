package it.unipi.lsmsd.util;

import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmsd.entity.BusinessActivity;
import it.unipi.lsmsd.entity.Review;
import it.unipi.lsmsd.entity.User;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.MongoDriver;
import jakarta.ws.rs.client.Client;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.summary.ResultSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;

import static com.mongodb.client.model.Filters.eq;
import static org.neo4j.driver.Values.parameters;

@Component
public class TransactionUtil {

    @Autowired
    private MongoDriver mongoDriver;

    public TransactionUtil(){}

    public Boolean addBusinessTransaction(ClientSession mongoSession,BusinessActivity businessToInsert){
        mongoSession.startTransaction();
        try{
            MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
            InsertOneResult mongoTransactionResult = businessColl.insertOne(mongoSession,businessToInsert);
            if (mongoTransactionResult.getInsertedId() != null) {
                mongoSession.commitTransaction();
                return true;
            }
        }catch(Exception e){
            mongoSession.abortTransaction();
            return false;
        }
        return false;
    }
    //adding in calling method a try-with multiple resources or adding here
    public Boolean addUserTransaction(ClientSession mongoSession, User userToInsert){
        mongoSession.startTransaction();
        try {

            MongoCollection<User> userColl = mongoDriver.getUserCollection();
            InsertOneResult mongoTransactionResult = userColl.insertOne(mongoSession, userToInsert);
            if (mongoTransactionResult.getInsertedId() != null) {
                mongoSession.commitTransaction();
                return true;
            }
        } catch(Exception e){
            mongoSession.abortTransaction();
            return false;
        }
        return false;
    }
 //****CHECKED****
 public Boolean addReviewTransaction(ClientSession mongoSession, Session graphSession, Review reviewToInsert,String user){
     mongoSession.startTransaction();
     boolean response;
     Transaction graphTransaction = graphSession.beginTransaction();
     try {
         MongoCollection<User> userColl = mongoDriver.getUserCollection();
         MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();

         UpdateResult insertReviewUser = userColl.updateOne(mongoSession, Filters.eq("username",user), Updates.push("reviews",reviewToInsert));
         String business = reviewToInsert.getBusinessActivity();
         reviewToInsert.setBusinessActivity(null);
         reviewToInsert.setUsername(user);
         UpdateResult insertReviewBusiness = businessColl.updateOne(mongoSession, Filters.eq("name",business),Updates.push("reviews",reviewToInsert));

         ResultSummary graphResult = graphTransaction.run("MERGE (uA:user { username:$username})"+
                         "MERGE (uB: BusinessActivity { name:$business})"+
                         "MERGE (uA)-[r:Review {rating:$rating}]->(uB)",
                 parameters("username",user,"business",business,"rating",reviewToInsert.getRating())).consume();
         if(graphResult.counters().relationshipsCreated() == 1 && insertReviewBusiness.getModifiedCount() == 1 &&
                 insertReviewUser.getModifiedCount() ==1 ){
             mongoSession.commitTransaction();
             graphTransaction.commit();
             response = true;
         }
         else{
             mongoSession.abortTransaction();
             graphTransaction.rollback();
             response = false;
         }
     }catch(Exception e){
         mongoSession.abortTransaction();
         graphTransaction.rollback();
         response = false;
     }
     return response;
 }
    //****CHECKED****
    public boolean removeReviewTransaction(ClientSession mongoSession,Session graphSession,Review reviewToDelete,String user){
        boolean response;
        mongoSession.startTransaction();
        Transaction graphTransaction = graphSession.beginTransaction();
        try{
            String business = reviewToDelete.getBusinessActivity();
            //Instant utcTime = reviewToDelete.getDateOfReview().toInstant().atOffset(ZoneOffset.UTC).toInstant();
            MongoCollection<User> userColl = mongoDriver.getUserCollection();
            MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
            Bson businessFilter = eq("name", business);
            Bson userFilter= eq("username",user);
            Bson userDeleteReview = Updates.pull("reviews",reviewToDelete);
            UpdateResult userResult = userColl.updateOne(mongoSession,userFilter,userDeleteReview);
            reviewToDelete.setBusinessActivity(null);
            reviewToDelete.setUsername(user);
            Bson businessDeleteReview = Updates.pull("reviews",reviewToDelete);
           UpdateResult busResult = businessColl.updateOne(mongoSession,businessFilter,businessDeleteReview);
            System.out.println(userResult.getModifiedCount());
            System.out.println(busResult.getModifiedCount());

            graphTransaction.run("MATCH (uA:user) WHERE uA.username=$username" +
                            " MATCH (uB:BusinessActivity) WHERE uB.name=$business" +
                            " MATCH (uA)-[r:Review]->(uB) WHERE r.rating=$rating"+
                            " DELETE r",
                    parameters("username", user, "business", reviewToDelete.getBusinessActivity(),"rating",reviewToDelete.getRating()));
            mongoSession.commitTransaction();
            graphTransaction.commit();
            response = true;
        }catch(Exception e){
            mongoSession.abortTransaction();
            graphTransaction.rollback();
            return false;
        }

        return response;
    }

    public boolean removeUserTransaction(ClientSession mongoSession, Session graphSession, String userToRemove){
        boolean response;
        mongoSession.startTransaction();
        Transaction graphTransaction = graphSession.beginTransaction();
        try{
            MongoCollection<User> userColl = mongoDriver.getUserCollection();
            Bson userFilter = eq("username",userToRemove);
            String  graphQuery = "MATCH (uA:user) WHERE uA.username=$username DETACH DELETE uA";
            DeleteResult mongoDeleteResult = userColl.deleteOne(mongoSession,userFilter);
            ResultSummary graphDeleteResult = graphTransaction.run(graphQuery,parameters("username",userToRemove)).consume();
            if(graphDeleteResult.counters().nodesDeleted()!=1 && mongoDeleteResult.getDeletedCount()!=1){
                System.out.print(graphDeleteResult.counters().nodesDeleted());
                System.out.println(mongoDeleteResult.getDeletedCount());
                mongoSession.abortTransaction();
                graphTransaction.rollback();
                response=false;
            }else {
                System.out.println(mongoDeleteResult.getDeletedCount());
                System.out.print(graphDeleteResult.counters().nodesDeleted());
                mongoSession.commitTransaction();
                graphTransaction.commit();
                response = true;
            }
        }catch(Exception e){
            mongoSession.abortTransaction();
            graphTransaction.rollback();
            response=false;

        }
        return response;
    }
    public boolean removeBusinessTransaction(ClientSession mongoSession, Session graphSession, String BsToRemove){
        boolean response;
        mongoSession.startTransaction();
        Transaction graphTransaction = graphSession.beginTransaction();
        try{
            MongoCollection<BusinessActivity> userColl = mongoDriver.getBusinessActivityCollection();
            Bson BsFilter = eq("name",BsToRemove);
            String  graphQuery = "MATCH (bA:BusinessActivity) WHERE bA.name=$name DETACH DELETE bA";
            DeleteResult mongoDeleteResult = userColl.deleteOne(mongoSession,BsFilter);
            ResultSummary graphDeleteResult = graphTransaction.run(graphQuery,parameters("name",BsToRemove)).consume();
            if(graphDeleteResult.counters().nodesDeleted()!=1 && mongoDeleteResult.getDeletedCount()!=1){
                System.out.print(graphDeleteResult.counters().nodesDeleted());
                mongoSession.abortTransaction();
                graphTransaction.rollback();
                response=false;
            }else {
                mongoSession.commitTransaction();
                graphTransaction.commit();
                response = true;
            }
        }catch(Exception e){
            mongoSession.abortTransaction();
            graphTransaction.rollback();
            response=false;

        }
        return response;
    }

}
