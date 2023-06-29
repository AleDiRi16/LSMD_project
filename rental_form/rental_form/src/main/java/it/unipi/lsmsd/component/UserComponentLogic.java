package it.unipi.lsmsd.component;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;
import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.entity.*;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.MongoDriver;
import it.unipi.lsmsd.util.Costant;
import it.unipi.lsmsd.util.Password;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.internal.InternalNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.regex;
import static com.mongodb.client.model.Projections.slice;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;
import static org.neo4j.driver.Values.parameters;

@Component
public class UserComponentLogic {
    @Autowired
    private MongoDriver mongoDriver;
    @Autowired
    private GraphDriver graphDriver;
    boolean isAdmin;

    //change with transaction util
    public boolean addUser(User user) {
        ClientSession session = mongoDriver.getSession();
        session.startTransaction();
        try {
            MongoCollection<Document> User = mongoDriver.getCollection("User");
            InsertOneResult result = User.insertOne(session, user.toDocument());
            // don't need admin account into graph db

        } catch (Exception e) {
            session.abortTransaction();
            return false;
        } finally {
            session.commitTransaction();
            session.close();
        }
        return true;
    }


    public UserAccessRequest getUser(String username, String password, boolean signup) throws Exception {
        MongoCollection<Document> Usercoll = mongoDriver.getCollection("User");
        Document document = Usercoll.find(eq("username", username)).first();
        if (document == null)
            return null;
        if (document != null & signup)
            return new UserAccessRequest(document.get("username").toString(), null, false);
        Password passwordObj = new Password();
        passwordObj.fromDocument(document);
        if (!passwordObj.checkPassword(password)) {
            throw new Exception("Password not correct");
        }
        isAdmin = (boolean) document.get("isAdmin");
        return new UserAccessRequest(document.get("username").toString(), password, isAdmin);
    }

    public Boolean getBusiness(String username, String password) {
        MongoCollection<Document> Bscoll = mongoDriver.getCollection("BusinessActivity");
        Document document = Bscoll.find(eq("username", username)).first();
        if (document == null)
            return null;
        Password passwordObj = new Password();
        passwordObj.fromDocument(document);
        if (!passwordObj.checkPassword(password)) {
            return false;
        }
        return true;

    }

    public Boolean getUserByEmail(String email) {
        MongoCollection<Document> Usercoll = mongoDriver.getCollection("User");
        Document document = Usercoll.find(eq("email", email)).first();
        return document != null;

    }

    public List<UserDTO> getMostFollowedUser(int page, String username, String direction) {
        int skip = page > 0 ? (page - 1) * 10 : 0;
        List<UserDTO> followerList = new ArrayList<>();
        try {
            Session session = graphDriver.getGraphDriver().session();
            String query = String.format("MATCH (user1:user)-[s:follow]->(friend:user)<-[r:follow]-(friend_friend:user)" +
                    " WHERE user1.username=$username" +
                    " RETURN friend as FRIEND, count(r)+1 as COUNTER" +
                    " ORDER BY COUNTER %s" +
                    " SKIP $skip" +
                    " LIMIT 10",direction);

            Result result = session.run(query, parameters("username", username, "direction", direction, "skip", skip));

            while (result.hasNext()) {
                UserDTO user = new UserDTO();
                result.next().asMap().forEach((key,value)->{

                    switch(key){
                        case "FRIEND"-> user.setUsername(((InternalNode) value).get("username").toString().replaceAll("\"",""));
                        case "COUNTER"-> user.setFollower(Integer.valueOf(value.toString()));
                    }
                });
                followerList.add(user);
            }
            session.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return followerList;
    }

    public List<BusinessDTO> getRecommendedBusinessActivity(int page, String username,String direction) {
        int skip = page > 0 ? (page - 1) * 10 : 0;
        List<BusinessDTO> businessActivityList = new ArrayList<>();
        try {
            Session session = graphDriver.getGraphDriver().session();
            String query = String.format("MATCH (user:user {username:$username})-[:follow]->(following:user)" +
                    " MATCH (following)-[review:Review]->(activity:BusinessActivity)" +
                    " WITH activity, toFloat(avg(review.rating)) as avg_rating, COUNT(DISTINCT following) as num_followers" +
                    " ORDER BY avg_rating %s" +
                    " SKIP $skip" +
                    " LIMIT 10" +
                    " RETURN activity.name as activity, avg_rating",direction);

            Result result = session.run(query, parameters("username", username, "skip", skip));

            while (result.hasNext()) {
                BusinessDTO activity = new BusinessDTO();
                result.next().asMap().forEach((key, value) -> {

                    switch (key) {
                        case "activity" -> activity.setName(value.toString());
                        case "avg_rating" -> activity.setRating((Double)(Math.round(Double.parseDouble(value.toString())*100.0)/100.0));
                    }

                });
                businessActivityList.add(activity);
            }
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return businessActivityList;

    }




    public List<Document> getReviews(String username, Integer page,String sortTarget,String direction,String searchText) {
        Bson sortFilter = direction.equalsIgnoreCase("asc")? ascending(String.format("reviews.%s",sortTarget)):descending(String.format("reviews.%s",sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        MongoCollection<Document> userColl = mongoDriver.getCollection("User");
        List<Document> response = new ArrayList<>();
        AggregateIterable<Document> aggregateResult = userColl.aggregate(Arrays.asList(
                Aggregates.match(eq("username",username)),
                Aggregates.unwind("$reviews"),
                Aggregates.match(or(regex("reviews.subject",searchText),regex("reviews.review",searchText),regex("reviews.businessActivity",searchText))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for(Document business : aggregateResult) {
            response.add((Document) business.get("reviews"));

            // response.add(business.getReviews().get(0));
        }
        return response;
    }

    public List<Document> getReservations(String username, Integer page, String sortTarget,String direction, String searchText ) {
        Bson sortFilter = direction.equalsIgnoreCase("asc")? ascending(String.format("rentingReservation.%s",sortTarget)):descending(String.format("rentingReservation.%s",sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        MongoCollection<Document> userColl = mongoDriver.getCollection("User");
        List<Document> response = new ArrayList<>();
        AggregateIterable<Document> aggregateResult = userColl.aggregate(Arrays.asList(
                Aggregates.match(eq("username",username)),
                Aggregates.unwind("$rentingReservation"),
                Aggregates.match(or(regex("rentingReservation.vehicle",searchText),regex("rentingReservation.businessActivity",searchText))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for(Document business : aggregateResult) {
            response.add((Document) business.get("rentingReservation"));

            // response.add(business.getReviews().get(0));
        }
        return response;

    }

    public List<String> GetFollowerList(int page, String username, String direction ,String searchText) {
        int skip = page > 0 ? (page - 1) * 20 : 0;
        List<String> follower = new ArrayList<>();
        try {
            Session session = graphDriver.getGraphDriver().session();
            String query = String.format("MATCH (user1:user)-[:follow]->(friend:user)" +
                    " WHERE user1.username=$username" +
                    " AND friend.username contains $searchText" +
                    " RETURN friend.username as FOLLOWER" +
                    " ORDER BY friend.username %s" +
                    " SKIP $skip" +
                    " LIMIT 20",direction);

            Result result = session.run(query,
                    parameters("username", username,"searchText",searchText, "skip", skip));

            while (result.hasNext()) {
                follower.add(result.next().get("FOLLOWER").asString());
            }
            session.close();
            return follower;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    public Boolean boolFollower(String username, String friend) {
        Boolean response=false;
        try {
            Session session = graphDriver.getGraphDriver().session();
            Result result = session.run("MATCH  (u1:user) WHERE u1.username = $name" +
                            " MATCH (u2:user) WHERE u2.username = $friend" +
                            " RETURN EXISTS ((u1)-[:follow]->(u2)) as boolExist",
                    parameters("name", username,"friend",friend));

            response = result.next().get("boolExist").asBoolean();
            session.close();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean FollowUser(String user, String friend) {

        boolean result = true;
        try {
            Session session = graphDriver.getGraphDriver().session();
            session.run("MERGE (uA:user { username : $user})"+
                            " MERGE (uB:user { username : $friend})" +
                            " MERGE (uA)-[:follow]->(uB)",
                    parameters("user", user, "friend", friend));
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public boolean UnfollowUser(String user, String friend) {

        boolean result = true;
        try {
            Session session = graphDriver.getGraphDriver().session();
            session.run("MATCH (u1:user) WHERE u1.username=$user" +
                            " MATCH (u2:user) WHERE u2.username=$friend" +
                            " MATCH (u1)-[r:follow]->(u2)" +
                            " DELETE r",
                    parameters("user", user, "friend", friend));
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    public GenericPageDTO getProfile(String username) {
        MongoCollection<User> Usercoll = mongoDriver.getUserCollection();
        User user = Usercoll.find(eq("username", username)).first();
        if (user == null) return null;
        GenericPageDTO userDTO = new GenericPageDTO(user.getFirstName(), user.getLastName(), user.getUsername(), user.getAddress(), user.getEmail(), user.getPhoneNumber(),user.getDateOfBirth(),user.getOccupation());
        return userDTO;
    }

    public List<BusinessDTO> RankingBusiness() {
        MongoCollection<Document> collection = mongoDriver.getCollection("BusinessActivity");
        List<BusinessDTO> bs = new ArrayList<>();
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$unwind", "$reviews"),
                new Document("$group",
                        new Document("_id",
                                new Document("BusinessActivity", "$name")
                                        .append("city", "$city"))
                                .append("avg_rating",
                                        new Document("$avg", "$reviews.rating"))),
                new Document("$sort",
                        new Document("avg_rating", -1L)),
                new Document("$limit", 5L)));
        try {
            MongoCursor<Document> iterator = result.iterator();
            while (iterator.hasNext()) {
                Document tmp = iterator.next();
                Document id = (Document) tmp.get("_id");
                BusinessDTO bs_new = new BusinessDTO(id.get("BusinessActivity").toString(), id.get("city").toString(), (Double) tmp.get("avg_rating"));
                Double rating = (double) Math.round((Double)bs_new.getRating() *10.0 )/10.0;
                bs_new.setRating(rating);
                bs.add(bs_new);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bs;
    }

    public List<VehicleDTO> MostRentedVehicle(String name,Integer age) {
        MongoCollection<Document> collection = mongoDriver.getCollection("BusinessActivity");
        List<VehicleDTO> vh = new ArrayList<>();
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$match",
                        new Document("name", name)),
                new Document("$unwind", "$rentingReservation"),
                new Document("$lookup",
                        new Document("from", "User")
                                .append("localField", "rentingReservation.user")
                                .append("foreignField", "username")
                                .append("as", "UserInfo")),
                new Document("$unwind", "$UserInfo"),
                new Document("$addFields",
                        new Document("age",
                                new Document("$subtract", Arrays.asList(new Document("$year", "$$NOW"),
                                        new Document("$year", "$UserInfo.dateOfBirth"))))),
                new Document("$match",
                        new Document("age",
                                new Document("$gt", age))),
                new Document("$unwind", "$vehicle"),
                new Document("$addFields",
                        new Document("isMatch",
                                new Document("$cond",
                                        new Document("if",
                                                new Document("$eq", Arrays.asList("$vehicle.id", "$rentingReservation.id")))
                                                .append("then", true)
                                                .append("else", false)))),
                new Document("$match",
                        new Document("isMatch", true)),
                new Document("$group",
                        new Document("_id",
                                new Document("Brand", "$vehicle.brand"))
                                .append("count",
                                        new Document("$sum", 1L))),
                new Document("$sort",
                        new Document("count", -1L)),
                new Document("$limit", 5L)));
        try {
            MongoCursor<Document> iterator = result.iterator();
            while (iterator.hasNext()) {
                Document tmp = iterator.next();
                Document id = (Document) tmp.get("_id");
                VehicleDTO vh_new = new VehicleDTO(id.get("Brand").toString(),Integer.valueOf(tmp.get("count").toString()));
                vh.add(vh_new);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return vh;

    }
    public List<AnalyticDTO> getRankingHistoricVehicles(Integer year) {
        MongoCollection<Document> collection = mongoDriver.getCollection("BusinessActivity");
        List<AnalyticDTO> vh = new ArrayList<>();
        AggregateIterable<Document> result = collection.aggregate(Arrays.asList(new Document("$unwind", "$rentingReservation"),
                new Document("$match",
                        new Document("rentingReservation.startDate",
                                new Document("$gte",
                                        new java.util.Date(1577836800000L))
                                        .append("$lt",
                                                new java.util.Date(1609459200000L)))),
                new Document("$unwind", "$vehicle"),
                new Document("$project",
                        new Document("businessActivity", "$name")
                                .append("rentingReservation", 1L)
                                .append("vehicle", 1L)
                                .append("compare",
                                        new Document("$eq", Arrays.asList("$rentingReservation.vehicle", "$vehicle.name")))),
                new Document("$match",
                        new Document("compare", true)
                                .append("vehicle.year",
                                        new Document("$lt", year))),
                new Document("$group",
                        new Document("_id", "$businessActivity")
                                .append("totalReservations",
                                        new Document("$sum", "$rentingReservation.price"))),
                new Document("$sort",
                        new Document("totalReservations", -1L))));

        try {
            MongoCursor<Document> iterator = result.iterator();
            while (iterator.hasNext()) {
                Document tmp = iterator.next();
                AnalyticDTO vh_new = new AnalyticDTO(tmp.get("_id").toString(), (Double) tmp.get("totalReservations"));
                vh.add(vh_new);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return vh;
    }
    public List<GenericPageDTO> MostActiveUsers(String username) {
        List<Record> list = new ArrayList<>();
        List<GenericPageDTO> users = new ArrayList<>();
        try {
            Session session = graphDriver.getGraphDriver().session();
            Result res = session.run
                         ("MATCH (user_target: user)-[follow:follow]->(user: user)" +
                            " MATCH (user: user)-[Review:Review]->(BusinessActivity:BusinessActivity)" +
                            " WHERE user_target.username=$username" +
                            " RETURN DISTINCT " +
                            " user.username AS FOLLOWER, " +
                            " (count(DISTINCT BusinessActivity)) AS REVIEW_N" +
                            " ORDER BY REVIEW_N DESC" +
                            " LIMIT 5",
                    parameters("username", username));
            while (res.hasNext()) {
                Record r = res.next();
                list.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        for (Record r : list) {
            String name = r.get("FOLLOWER").asString();
            users.add(new GenericPageDTO(name));
        }
        return users;
    }

    public void schedule_reservation(String name, String id, Boolean v) {
        MongoCollection<BusinessActivity> Bs = mongoDriver.getBusinessActivityCollection();
        System.out.println(name);
        System.out.println(id);
        System.out.println(v);
        Bson Filter = and(eq("name", name), eq("vehicle.identifier", id));
        Bson fieldToUpdate = Updates.set("vehicle.$.isAvailable", v);
        Bs.updateOne(Filter, fieldToUpdate);
        System.out.println("OK");

    }

    public List<String> GetFollowedList(int page, String username,String direction, String searchText) {
        int skip = page > 0 ? (page - 1) * 20 : 0;
        List<String> follower = new ArrayList<>();

        try {
            Session session = graphDriver.getGraphDriver().session();
            String query = String.format("MATCH (friend:user)-[:follow]->(user1:user)" +
                    " WHERE user1.username=$username" +
                    " AND friend.username contains $searchText" +
                    " RETURN friend.username as FOLLOWED" +
                    " ORDER BY friend.username %s" +
                    " SKIP $skip" +
                    " LIMIT 20",direction);
            Result result = session.run(query,
                    parameters("username",username,"searchText",searchText, "skip", skip));

            while (result.hasNext()) {

                follower.add(result.next().get("FOLLOWED").asString());
            }
            session.close();
            return follower;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}