package it.unipi.lsmsd.component;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.SchedulerConfig;
import it.unipi.lsmsd.entity.*;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.MongoDriver;
import it.unipi.lsmsd.util.TransactionUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.driver.Session;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.summary.ResultSummary;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.slice;
import static com.mongodb.client.model.Sorts.*;
import static org.neo4j.driver.Values.parameters;

@Component
public class BusinessComponentLogic {
    @Autowired
    MongoDriver mongoDriver;
    @Autowired
    private SchedulerConfig schedulerConfig;
    @Autowired
    private GraphDriver graphDriver;
    @Autowired
    private TransactionUtil transactionUtil;
    public String insertVehicle(VehicleInsertRequest vehicleToInsert){
        vehicleToInsert.getVehicleToInsert().setVehicleIdentifier(new ObjectId().toHexString());
        MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
        Bson businessFilter = eq("username",vehicleToInsert.getBusinessUsername());
        Bson update = Updates.push("vehicle",vehicleToInsert.getVehicleToInsert());
        UpdateResult result = businessColl.updateMany(businessFilter,update);

        return String.format("%s with identifier %s",vehicleToInsert.getVehicleToInsert().getName(),vehicleToInsert.getVehicleToInsert().getVehicleIdentifier());
    }
    public List<Document> getAllVehicle(String username, Integer page,String sortTarget, String direction, String searchText ) {
        Bson sortFilter = direction.equalsIgnoreCase("asc")? ascending(String.format("vehicle.%s",sortTarget)):descending(String.format("vehicle.%s",sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        MongoCollection<Document> businessColl = mongoDriver.getCollection("BusinessActivity");
        List<Document> response = new ArrayList<>();
        AggregateIterable<Document> aggregateResult = businessColl.aggregate(Arrays.asList(
                Aggregates.match(eq("name",username)),
                Aggregates.unwind("$vehicle"),
                Aggregates.match(or(regex("vehicle.name",searchText,"i"),regex("vehicle.brand",searchText,"i"))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for(Document business : aggregateResult) {
            response.add((Document) business.get("vehicle"));
            // response.add(business.getReviews().get(0));
        }
        return response;
    }

    public VehicleUpdateResponse removeOne(VehicleUpdateRequest vehicleToDelete){
        VehicleUpdateResponse response = new VehicleUpdateResponse();
        if(vehicleToDelete.getCommand().equalsIgnoreCase("DELETE")){
           MongoCollection<BusinessActivity> businessColl =  mongoDriver.getBusinessActivityCollection();
           Bson filter = and(eq("username",vehicleToDelete.getBusinessUsername()));
           UpdateResult result = businessColl.updateOne(filter,Updates.pull("vehicle",vehicleToDelete.getVehicleToUpdate()));
           response.setAnswer(result.getModifiedCount()!= 0 ? "Your vehicle has been removed from the platform" : "TRY AGAIN, NO VEHICLE HAS BENN REMOVED");
           response.setNumberOfUpdate((int) result.getModifiedCount());
        }
        else{
            response.setAnswer("WRONG COMMAND");
        }
        return response;
    }

    public VehicleUpdateResponse updateOne(VehicleUpdateRequest vehicleToUpdate){
        VehicleUpdateResponse response = new VehicleUpdateResponse();
        if(vehicleToUpdate.getCommand().equalsIgnoreCase("UPDATE")) {
            Field[] fieldToUpdate = vehicleToUpdate.getClass().getDeclaredFields();
            ArrayList<Bson> updates = new ArrayList<>();
            for (Iterator<Field> iterator = Arrays.stream(fieldToUpdate).iterator(); iterator.hasNext(); ) {
                Field field = iterator.next();
                switch (field.getName()) {
                    case "automaticTrasmission" :
                        if(vehicleToUpdate.getAutomaticTrasmission()!=null){
                            updates.add(Updates.set("vehicle.$.automaticTrasmission",vehicleToUpdate.getAutomaticTrasmission()));
                        }
                        break;
                    case "isAvailable":
                        if(vehicleToUpdate.getIsAvailable()!=null){
                            updates.add(Updates.set("vehicle.$.isAvailable",vehicleToUpdate.getIsAvailable()));
                        }
                        break;
                    case "price":
                        if(vehicleToUpdate.getPrice()!=null){
                            updates.add(Updates.set("vehicle.$.price",vehicleToUpdate.getPrice()));
                        }
                        break;
                    case "brand":
                        if(vehicleToUpdate.getBrand()!=null){
                            updates.add(Updates.set("vehicle.$.brand",vehicleToUpdate.getBrand()));
                        }
                        break;
                    case "name":
                        if(vehicleToUpdate.getName()!=null){
                            updates.add(Updates.set("vehicle.$.name",vehicleToUpdate.getName()));
                        }
                        break;
                    case "year":
                        if(vehicleToUpdate.getYear()!=null){
                            updates.add(Updates.set("vehicle.$.year",vehicleToUpdate.getYear()));
                        }
                        break;
                }

            }
                //using the business username with the @BUSINESS tag instead of the business activity name, so in this way we can perform
                //a control on the access privileges in the controller , without querying the db
            Bson queryFilter = and(eq("username",vehicleToUpdate.getBusinessUsername()),
                    eq("vehicle.name",vehicleToUpdate.getVehicleToUpdate().getName()),
                    eq("vehicle.brand",vehicleToUpdate.getVehicleToUpdate().getBrand()),
                    eq("vehicle.vehicleIdentifier",vehicleToUpdate.getVehicleToUpdate().getVehicleIdentifier()));
           MongoCollection<BusinessActivity>businessColl = mongoDriver.getBusinessActivityCollection();
           UpdateResult updateResult = businessColl.updateOne(queryFilter,Updates.combine(updates));
           response.setAnswer(updateResult.getModifiedCount() == 1 ? "Your vehicle has been updated" : "No update has been performed, probably you've inserted the same value");
        }else{
            response.setAnswer("Missing UPDATE command in your request");

        }


        return response;
    }
    public List<Document> getRentedVehicleByDate(AnalyticsRequest request,Integer page){
        int pageToSkip = page > 0 ? (page-1) * 10 : 0;
        MongoCollection<Document> businessColl = mongoDriver.getCollection("BusinessActivity");
        ArrayList<Document> elements = new ArrayList<>();
        Instant utcStart = request.getStartDate().toInstant().atOffset(ZoneOffset.UTC).toInstant();
        Instant utcEnd = request.getEndDate().toInstant().atOffset(ZoneOffset.UTC).toInstant();
        AggregateIterable<Document> iterable = businessColl.aggregate(Arrays.asList(
                new Document("$match",
                        new Document("username",request.getBusinessUsername())),
                new Document("$unwind",
                        new Document("path","$rentingReservation")),
                new Document("$match",
                        new Document("rentingReservation.startDate",
                                new Document("$lte",utcEnd)
                                        .append("$gte",utcStart)
                        )),
                new Document("$group",
                        new Document("_id",
                                new Document("vehicle","$rentingReservation.vehicle"))
                        .append("count",
                                new Document("$sum",1))
                        ),
                new Document("$sort",
                        new Document("count",-1)),
                new Document("$skip",pageToSkip),
                new Document("$limit",10)

        ));
        for (Document businessActivity : iterable) {
            elements.add(businessActivity);
        }
        return elements;
    }
    public List<Document> getMostRentedVehicleByWorker(AnalyticsRequest request){
        MongoCollection<Document> businessColl = mongoDriver.getCollection("BusinessActivity");
        ArrayList<Document> elements = new ArrayList<>();

        AggregateIterable<Document> iterable = businessColl.aggregate(Arrays.asList(
                Aggregates.match(eq("username",request.getBusinessUsername())),
                new Document("$unwind","$rentingReservation"),
                new Document("$lookup",
                        new Document("from","User")
                                .append("localField","rentingReservation.user")
                                .append("foreignField","username")
                                .append("as","join")
                        ),
                new Document("$replaceRoot",
                        new Document("newRoot",
                                new Document("$mergeObjects",
                                        Arrays.asList(new Document("$arrayElemAt",
                                                Arrays.asList("$join",0)),
                                                "$$ROOT")))),
               new Document("$project",
                       new Document("join",0)),
                new Document("$group",
                        new Document("_id",
                               new Document("vehicle","$rentingReservation.vehicle")
                                       .append("occupation","$occupation"))
                                .append("count",
                                        new Document("$sum",1))),

               Aggregates.sort(descending("count")),
                new Document("$group",
                        new Document("_id",
                                new Document("occupation","$_id.occupation"))
                                .append("vehicle",new Document("$first", "$_id.vehicle"))
                                .append("count",
                                         new Document("$max","$count"))),
                new Document("$sort",
                        new Document("count",-1))
        )).allowDiskUse(true);

        for (Document businessActivity : iterable) {
            elements.add(businessActivity);
        }
        return elements;
    }

    public boolean AddReview(ReviewDTO reviewUser) {
        Boolean response;

        try(Session graphSession = graphDriver.getGraphDriver().session();
            ClientSession mongoSession= mongoDriver.getSession()){
            response =transactionUtil.addReviewTransaction(mongoSession,graphSession,reviewUser.getReview(),reviewUser.getUsername());
        }
            return response;
    }

    public boolean AddRentingReservation(RentingReservationDTO reservationUser) {
        ClientSession session= mongoDriver.getSession();
        MongoCollection<BusinessActivity> BsColl = mongoDriver.getBusinessActivityCollection();
        MongoCollection<User> Usercoll = mongoDriver.getUserCollection();
        RentingReservation reservation = reservationUser.getRentingReservation();
        String user = reservation.getUser();
        String Business = reservationUser.getRentingReservation().getBusinessActivity();
        Double price=reservation.getPrice();
        price=(price * (reservation.getEndDate().getTime()- reservation.getStartDate().getTime()))/(1000*60*60*24);
        System.out.println(price);
        if(price == 0) price = reservation.getPrice();
        price = Math.round( price *10.0)/10.0;
        reservation.setPrice(price);
        TransactionBody<Boolean> transaction = () -> {
            Bson userFilter = Filters.eq("username", user);
            reservation.setUser(null);
            Bson userUpdate = Updates.push("rentingReservation", reservation);
            Usercoll.updateOne(session, userFilter, userUpdate);
            reservation.setUser(user);
            reservation.setBusinessActivity(null);
            Bson filter1 = Filters.eq("name", Business);
            Bson setUpdate = Updates.push("rentingReservation", reservation);
            BsColl.updateOne(session, filter1, setUpdate);
            return true;
        };
        try{
            session.withTransaction(transaction);
        }catch(RuntimeException e){
            System.out.println("no");
            session.abortTransaction();
            session.close();
            return false;
        }
        finally {
           // isAvailable(reservationUser.getRentingReservation().getStartDate(),reservationUser.getRentingReservation().getIdentifier(), business,false);
           // isAvailable(reservationUser.getRentingReservation().getEndDate(),reservationUser.getRentingReservation().getIdentifier(), business,true);
            session.close();
        }
        return true;
    }
    
    public boolean reservationValidity(RentingReservation rentingReservation) {
        System.out.println("ciao");
        MongoCollection<Document> BsColl = mongoDriver.getCollection(("BusinessActivity"));
        AggregateIterable<Document> aggregateResult = BsColl.aggregate(Arrays.asList(
                Aggregates.match(eq("name", rentingReservation.getBusinessActivity())),
                Aggregates.unwind("$rentingReservation"),
                Aggregates.match(and(eq("rentingReservation.identifier", rentingReservation.getIdentifier()),
               or(and(lt("rentingReservation.startDate", rentingReservation.getStartDate()),gt("rentingReservation.endDate",rentingReservation.getEndDate())),and(eq("rentingReservation.startDate", rentingReservation.getStartDate()),eq("rentingReservation.endDate",rentingReservation.getEndDate())),and(gt("rentingReservation.startDate", rentingReservation.getStartDate()),lt("rentingReservation.startDate",rentingReservation.getEndDate())),and(gt("rentingReservation.endDate",rentingReservation.getStartDate()),lt("rentingReservation.endDate",rentingReservation.getEndDate())))))));
        return aggregateResult.first() == null;
    }

    public void isAvailable(Date date,String id,String name, boolean v) {
        try {
            System.out.println(date);
            JobDetail job = JobBuilder.newJob(MyJob.class).withIdentity(id,date.toString()).build();
            job.getJobDataMap().put("name", name);
            job.getJobDataMap().put("id" ,id);
            job.getJobDataMap().put("v",v);
            Trigger trigger = TriggerBuilder.newTrigger().withIdentity(id,date.toString()).startAt(date).build();
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        }catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    public List<Document> getReviews(String name, Integer page, String sortTarget, String direction,String searchText) {
        Bson sortFilter = direction.equalsIgnoreCase("asc")? ascending(String.format("reviews.%s",sortTarget)):descending(String.format("reviews.%s",sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        MongoCollection<Document> businessColl = mongoDriver.getCollection("BusinessActivity");
        List<Document> response = new ArrayList<>();
        AggregateIterable<Document> aggregateResult = businessColl.aggregate(Arrays.asList(
            Aggregates.match(eq("name",name)),
                Aggregates.unwind("$reviews"),
                Aggregates.match(or(regex("reviews.subject",searchText,"i"),regex("reviews.review",searchText,"i"),regex("reviews.username",searchText,"i"))),
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

    //Renting reservation called by the business activity itself, so it use its own access username in the form <username>@BUSINESS
    public List<Document> getReservations(String username, Integer page,String sortTarget,String direction,String searchText) {
        Bson sortFilter = direction.equalsIgnoreCase("asc")? ascending(String.format("rentingReservation.%s",sortTarget)):descending(String.format("rentingReservation.%s",sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        MongoCollection<Document> businessColl = mongoDriver.getCollection("BusinessActivity");
        List<Document> response = new ArrayList<>();
        AggregateIterable<Document> aggregateResult = businessColl.aggregate(Arrays.asList(
                Aggregates.match(eq("username",username)),
                Aggregates.unwind("$rentingReservation"),
                Aggregates.match(or(regex("rentingReservation.vehicle",searchText),regex("rentingReservation.user",searchText))),
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
    //should we use the business username instead of the business name?
    public GenericPageDTO getProfile(String username) {
        MongoCollection<BusinessActivity> BColl = mongoDriver.getBusinessActivityCollection();
        BusinessActivity Bs = BColl.find(eq("username",username)).first();
        if (Bs == null)
            return null;
        GenericPageDTO BsDTO = new GenericPageDTO(Bs.getName(), Bs.getAddress(), Bs.getEmail(), Bs.getPhoneNumber(), Bs.getCity(), Bs.getDescription());
        return BsDTO;
    }

    public boolean RemoveReservation(RentingReservationDTO rentingReservation){
        ClientSession session= mongoDriver.getSession();
        MongoCollection<BusinessActivity> BsColl = mongoDriver.getBusinessActivityCollection();
        MongoCollection<User> Usercoll = mongoDriver.getUserCollection();
        RentingReservation reservation = rentingReservation.getRentingReservation();
        TransactionBody<Boolean> transaction = () -> {
            Bson filter = (eq("name", rentingReservation.getActivity()));
            Bson filter1=(eq("username",rentingReservation.getUsername()));
            reservation.setUser(null);
            Bson delete = Updates.pull("rentingReservation",reservation);
            Usercoll.updateOne(session,filter1, delete);
            reservation.setBusinessActivity(null);
            reservation.setUser(rentingReservation.getUsername());
            Bson delete1 = Updates.pull("rentingReservation",reservation);
            BsColl.updateOne(session,filter, delete1);
            return true;
        };
        try{
            session.withTransaction(transaction);
        }catch(RuntimeException e){
            session.close();
            return false;
        } finally {
            remove_schedule(rentingReservation.getRentingReservation().getIdentifier(),rentingReservation.getRentingReservation().getStartDate(),rentingReservation.getRentingReservation().getEndDate());
            session.close();
        }
        return true;
    }

    public void remove_schedule(String id,Date start,Date end) {
        try{
            Scheduler scheduler = schedulerConfig.schedulerFactoryBean().getScheduler();
            scheduler.deleteJob(new JobKey(id,start.toString()));
            scheduler.deleteJob(new JobKey(id,end.toString()));
            TriggerKey triggerKey = new TriggerKey(id,start.toString());
            TriggerKey triggerKey1 = new TriggerKey(id,end.toString());
            scheduler.unscheduleJob(triggerKey);
            scheduler.unscheduleJob(triggerKey1);

        } catch (IOException | SchedulerException e) {
            e.printStackTrace();
        }
    }

    public boolean removeReview(ReviewDTO review) {

        boolean response;
        try( ClientSession mongoSession = mongoDriver.getSession();
             Session graphSession = graphDriver.getGraphDriver().session()){
            response = transactionUtil.removeReviewTransaction(mongoSession,graphSession,review.getReview(),review.getUsername());
        }
        return response;
    }

    public boolean UpdateReview(UpdateReview reviewUser) {
        MongoCollection<User> userColl = mongoDriver.getUserCollection();
        MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
        ClientSession session = mongoDriver.getSession();
        TransactionBody<Boolean> transaction = () -> {
            String graphQuery=null;
            Field[] field = reviewUser.getClass().getDeclaredFields();
            ArrayList<Bson> updates = new ArrayList<>();
            for(Iterator<Field> iterator = Arrays.stream(field).iterator(); iterator.hasNext();){
                Field element = iterator.next();
                switch (element.getName()){
                    case "review":
                        if(reviewUser.getReview() != null){
                            System.out.println(reviewUser.getReview());
                            Bson updateReview = Updates.set("reviews.$.review",reviewUser.getReview());
                            updates.add(updateReview);
                        }
                        break;
                    case "subject":
                        if(reviewUser.getSubject()!=null){
                            System.out.println(reviewUser.getSubject());
                            Bson updateSubject = Updates.set("reviews.$.subject",reviewUser.getSubject());
                            updates.add(updateSubject);
                        }
                        break;
                    case "rating":
                        if(reviewUser.getRating()!=null){
                            System.out.println(reviewUser.getSubject());
                            Bson updateRating = Updates.set("reviews.$.rating",reviewUser.getRating());
                            updates.add(updateRating);

                        }
                        break;

                }
            }
            Bson UserFilter = and(eq("username",reviewUser.getUsername()),eq("reviews.dateOfReview",reviewUser.getReferenceReview().getDateOfReview()),eq("reviews.businessActivity",reviewUser.getReferenceReview().getBusinessActivity()));
            System.out.println("ok");
            Bson BusinessFilter = and(eq("name",reviewUser.getReferenceReview().getBusinessActivity()),eq("reviews.dateOfReview",reviewUser.getReferenceReview().getDateOfReview()),eq("reviews.username",reviewUser.getUsername()));
            UpdateResult userResult = userColl.updateOne(session,UserFilter,Updates.combine(updates));
            UpdateResult businessResult = businessColl.updateOne(session,BusinessFilter,Updates.combine(updates));
            if(reviewUser.getRating()!=null){
                Session graphSession = graphDriver.getGraphDriver().session();
                    Transaction graphTransaction = graphSession.beginTransaction();
                   ResultSummary summary = graphTransaction.run("MATCH (au:user)-[r:review]->(ab:BusinessActivity) where" +
                                    " au.username=$username and ab.name=$businessName and r.rating=$rating set r.rating=$newRating",
                            parameters("username",reviewUser.getUsername(),"businessName",reviewUser.getReferenceReview().getBusinessActivity(),
                                    "rating",reviewUser.getReferenceReview().getRating(),"newRating",reviewUser.getRating())).consume();

                return true;
            }else {
                throw new RuntimeException();
            }
        };
        try{
            Boolean response = session.withTransaction(transaction);
           return response;
        }catch(RuntimeException e){

            return false;
        }
    }

    public boolean UpdateReservation(UpdateReservation reservationUser) {
        Boolean response;
        Double priceup=reservationUser.getReservation().getPrice();
        Double pricegg=(priceup / ((reservationUser.getReservation().getEndDate().getTime()- reservationUser.getReservation().getStartDate().getTime())/(1000*60*60*24)));


        priceup=(pricegg * ((reservationUser.getEndDate().getTime()- reservationUser.getStartDate().getTime())/(1000*60*60*24)));
        if(priceup == 0) priceup = pricegg;
        priceup = Math.round( priceup *10.0)/10.0;
        System.out.println("priceup");
        System.out.println(priceup);
        MongoCollection<User> userColl = mongoDriver.getUserCollection();
        MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
        ClientSession session = mongoDriver.getSession();
        Double finalPriceup = priceup;
        ArrayList<Bson> updates = new ArrayList<>();
        if (reservationUser.getStartDate() != null) {
            System.out.println(reservationUser.getEndDate());
            Bson updatesDate = Updates.set("rentingReservation.$.startDate", reservationUser.getStartDate());
            updates.add(updatesDate);
        }
        if (reservationUser.getEndDate() != null) {
            System.out.println(reservationUser.getEndDate());
            Bson updateEdate = Updates.set("rentingReservation.$.endDate", reservationUser.getEndDate());
            updates.add(updateEdate);
        }
        Bson priceUpdate=Updates.set("rentingReservation.$.price", finalPriceup);
        updates.add(priceUpdate);
        TransactionBody<Boolean> transaction = () -> {
            Bson UserFilter = and(eq("username",reservationUser.getUser()),eq("rentingReservation.identifier",reservationUser.getReservation().getIdentifier()),eq("rentingReservation.businessActivity",reservationUser.getReservation().getBusinessActivity()),eq("rentingReservation.startDate",reservationUser.getReservation().getStartDate()),eq("rentingReservation.endDate",reservationUser.getReservation().getEndDate()));
            UpdateResult s= userColl.updateOne(session,UserFilter,Updates.combine(updates));
            Bson BusinessFilter = (and(eq("name",reservationUser.getReservation().getBusinessActivity()),eq("rentingReservation.identifier",reservationUser.getReservation().getIdentifier()),eq("rentingReservation.user",reservationUser.getUser()),eq("rentingReservation.startDate",reservationUser.getReservation().getStartDate()),eq("rentingReservation.endDate",reservationUser.getReservation().getEndDate())));
            System.out.println(reservationUser.getReservation().getBusinessActivity());
            System.out.println(reservationUser.getUser());
            System.out.println(reservationUser.getReservation().getIdentifier());
            System.out.println(reservationUser.getReservation().getStartDate());
            System.out.println(reservationUser.getReservation().getEndDate());
            UpdateResult busResult=businessColl.updateOne(session,BusinessFilter,Updates.combine(updates));
            System.out.println(busResult.getModifiedCount());
            System.out.println(s.getModifiedCount());
            return true;
        };
        try{
            response= session.withTransaction(transaction);
            return response;
        }catch(RuntimeException e){
            System.out.println("rtrto");
        }
        return false;
    }

    public List<Document> getBusinessActivity(String businessName, int page, String sortTarget, String direction) {
        Bson sortFilter = direction.equalsIgnoreCase("asc") ? ascending(String.format("%s", sortTarget)) : descending(String.format("%s", sortTarget));

        int skip = page > 0 ? (page - 1) * 10 : 0;
        List<Document> doc = new ArrayList<>();
        MongoCollection<Document> dbCollection = mongoDriver.getCollection("BusinessActivity");
        AggregateIterable<org.bson.Document> aggregate = dbCollection.aggregate(Arrays.asList(
                Aggregates.match(regex("name", businessName, "i")),
                Aggregates.project(Projections.fields(Projections.exclude("_id"),Projections.include("name", "image", "address", "phoneNumber", "email", "city", "description"),Projections.computed("rating", new Document("$avg", "$reviews.rating")))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for (Document business : aggregate) {
            Double rating = (double) Math.round((Double) business.get("rating") *10.0)/10.0;
            business.put("rating",rating);
            doc.add(business);
        }
        return doc;
    }

    public Boolean addNewBusinessActivity(BusinessActivity businessActivity){
        boolean result;
        try(ClientSession mongoSession = mongoDriver.getSession();

        ){
            result = transactionUtil.addBusinessTransaction(mongoSession,businessActivity);
        }

        return result;
    }

}


