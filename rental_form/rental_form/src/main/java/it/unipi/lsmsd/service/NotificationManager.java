package it.unipi.lsmsd.service;

import com.mongodb.client.*;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import it.unipi.lsmsd.DTO.*;
import it.unipi.lsmsd.entity.*;
import it.unipi.lsmsd.util.Costant;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.neo4j.cypher.internal.util.Cost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.include;
import static com.mongodb.client.model.Projections.slice;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.descending;

@Service
public class NotificationManager {
    Logger logger = LoggerFactory.getLogger(MongoDriver.class);
    private static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private MongoDriver mongoDriver;
    public NotificationManager(){

    }
    public GenericResponse addToNotification(SupportRequest supportRequest){
        ClientSession session = mongoDriver.getSession();
        GenericResponse response = new GenericResponse();
        GenericResponse finalResponse = response;
        TransactionBody<GenericResponse> transaction = () -> {
            MongoCollection<SupportRequest> openSupport = mongoDriver.getSupportCollection();
            UpdateResult updateResult;
                if(supportRequest.getRequestCategory() == Costant.RequestCategory.TICKET){
                    try {
                        supportRequest.getTicket().setTicketDate(sf.parse(sf.format(new Date() )));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    supportRequest.setId(new ObjectId());
                    supportRequest.getTicket().setTicketId(supportRequest.getId().toHexString());
                    supportRequest.setApplicant(supportRequest.getUsername());
                    supportRequest.setUsername(null);
                    InsertOneResult result = openSupport.insertOne(session,supportRequest);
                    MongoCollection<User> userColl = mongoDriver.getUserCollection();
                    Ticket ticket = supportRequest.getTicket();
                    ticket.setStatus(Costant.SupportStatus.OPEN);
                    updateResult =userColl.updateOne(session,eq("username",supportRequest.getApplicant()),Updates.push("ticketList",ticket));
                    finalResponse.setAnswer(String.format("Ticket n°: %s has been inserted",ticket.getTicketId()));

                }else if(supportRequest.getRequestCategory() == Costant.RequestCategory.REPORT){
                    MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();

                    try {
                        supportRequest.getReport().setDateOfReport(sf.parse(sf.format(new Date() )));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    supportRequest.setId(new ObjectId());
                    supportRequest.getReport().setReportId(supportRequest.getId().toHexString());
                    supportRequest.setApplicant(supportRequest.getUsername());
                    supportRequest.setUsername(null);
                    InsertOneResult result = openSupport.insertOne(session,supportRequest);
                    Report report = supportRequest.getReport();
                    report.setStatus(Costant.SupportStatus.OPEN);

                    updateResult = businessColl.updateOne(eq("username",supportRequest.getApplicant()),Updates.push("reportList",supportRequest.getReport()));
                    finalResponse.setAnswer(String.format("Report n°: %s has been inserted",report.getReportId()));
                }
               return finalResponse;

            };
        try{
          response = session.withTransaction(transaction);

        }catch(RuntimeException e){

            response.setAnswer("ERROR Try again later");
            logger.error(e.getMessage());
        }
        finally {
            session.close();
        }
        return response;
    }
    public GenericResponse dispatchSupportRequest(SupportRequest supportRequest) {
        ClientSession session = mongoDriver.getSession();
        MongoCollection<SupportRequest> openSupport = mongoDriver.getSupportCollection();
        MongoCollection<User> userColl = mongoDriver.getUserCollection();
        GenericResponse result = new GenericResponse();
        TransactionBody<GenericResponse> transaction = () -> {
            Bson filters;
            GenericResponse response = new GenericResponse();
            if (supportRequest.getRequestCategory() == Costant.RequestCategory.TICKET) {
                filters = and(eq("_id", new ObjectId(supportRequest.getTicket().getTicketId())),eq("applicant",supportRequest.getApplicant()));
                DeleteResult document = openSupport.deleteOne(filters);

                Ticket ticket = supportRequest.getTicket();
                Instant utcTime = ticket.getTicketDate().toInstant().atOffset(ZoneOffset.UTC).toInstant();
                Bson ticketFilters = and(eq("username", supportRequest.getApplicant()), eq("ticketList.ticketId", ticket.getTicketId()),eq("ticketList.ticketDate",utcTime));
                ticket.setStatus(Costant.SupportStatus.TAKEN);
                userColl.updateOne(ticketFilters, Updates.combine(Updates.set("ticketList.$.admin", supportRequest.getUsername()), Updates.set("ticketList.$.status", ticket.getStatus())));
                ticket.setUsername(supportRequest.getApplicant());
                userColl.updateOne(and(eq("username", supportRequest.getUsername()),eq("isAdmin",true)), Updates.push("ticketList", supportRequest.getTicket()));
                response.setAnswer(String.format("TICKET %s has been correctly dispatched",ticket.getTicketId()));

            } else if (supportRequest.getRequestCategory() == Costant.RequestCategory.REPORT) {
                filters =and(eq("_id", new ObjectId(supportRequest.getReport().getReportId())),eq("applicant",supportRequest.getApplicant()));
                DeleteResult document = openSupport.deleteOne(filters);

                MongoCollection<BusinessActivity> businessColl = mongoDriver.getBusinessActivityCollection();
                Report report = supportRequest.getReport();
                Instant utcTime = report.getDateOfReport().toInstant().atOffset(ZoneOffset.UTC).toInstant();
                Bson reportFilters = and(eq("username", supportRequest.getApplicant()), eq("reportList.reportId", report.getReportId()),eq("reportList.dateOfReport",utcTime));
                report.setStatus(Costant.SupportStatus.TAKEN);
                businessColl.updateOne(reportFilters, Updates.combine(Updates.set("reportList.$.admin", supportRequest.getUsername()), Updates.set("reportList.$.status", report.getStatus())));
                report.setBusinessActivityName(supportRequest.getApplicant());
                userColl.updateOne(and(eq("username", supportRequest.getUsername()),eq("isAdmin",true)), Updates.push("reportList", supportRequest.getReport()));
                response.setAnswer(String.format("REPORT %s has been correctly dispatched",report.getReportId()));
                }
            return response;

            };
        try{
            result = session.withTransaction(transaction);
        }catch(RuntimeException e){
            result.setAnswer("ERROR Try again later");
            logger.error(e.toString());
        }
        finally {
            session.close();
        }

        return result;
    }
    public List<SupportRequest> getAll(int page){
        int limit = 10;
        int pageToSkip = page > 0 ? (page-1)*10 : 0;
        ArrayList<SupportRequest> requests = new ArrayList<>();
        MongoCollection<SupportRequest> supportColl = mongoDriver.getSupportCollection();
        FindIterable<SupportRequest> iter = supportColl.find().skip(pageToSkip).limit(limit);
        for (SupportRequest supportRequest :iter ) {

            requests.add(supportRequest);
        }
        return requests;

    }
    public List<Document> getAllTicket(int page,String username,String sortTarget,String direction,String searchText){
        Bson sortFilter = direction.equalsIgnoreCase("asc") ? ascending(String.format("ticketList.%s", sortTarget)) : descending(String.format("ticketList.%s", sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        List<Document> reports = new ArrayList<>();
        MongoCollection<Document>userColl = mongoDriver.getCollection("User");

        AggregateIterable<Document> aggregateResult = userColl.aggregate(Arrays.asList(
                Aggregates.match(eq("username", username)),
                Aggregates.unwind("$ticketList"),
                Aggregates.match((regex("ticketList.title",searchText))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for (Document report : aggregateResult) {
            reports.add((Document) report.get("ticketList"));

        }
        return reports;
    }

    public List<Document>getAllReport(int page, String username,String sortTarget,String direction,String searchText) {
        Bson sortFilter = direction.equalsIgnoreCase("asc") ? ascending(String.format("reportList.%s", sortTarget)) : descending(String.format("reportList.%s", sortTarget));
        int skip = page > 0 ? (page - 1) * 10 : 0;
        List<Document> reports = new ArrayList<>();
        MongoCollection<Document>coll = username.contains("@Business")?mongoDriver.getCollection("BusinessActivity"):mongoDriver.getCollection("User");

        AggregateIterable<Document> aggregateResult = coll.aggregate(Arrays.asList(
                Aggregates.match(eq("username", username)),
                Aggregates.unwind("$reportList"),
                Aggregates.match((regex("reportList.title",searchText))),
                Aggregates.sort(sortFilter),
                Aggregates.skip(skip),
                Aggregates.limit(10)
        ));
        for (Document report : aggregateResult) {
            reports.add((Document) report.get("reportList"));

        }
        return reports;
    }
    public GenericResponse update(UpdateNotificationRequest updateRequest){
        ClientSession session = mongoDriver.getSession();
        MongoCollection<User> userColl = mongoDriver.getUserCollection();
        MongoCollection<BusinessActivity>businessColl = mongoDriver.getBusinessActivityCollection();
        boolean isTicket = updateRequest.getReferenceTicket() != null;
        Instant utcTime = isTicket?
                updateRequest.getReferenceTicket().getTicketDate().toInstant().atOffset(ZoneOffset.UTC).toInstant():
                updateRequest.getReferenceReport().getDateOfReport().toInstant().atOffset(ZoneOffset.UTC).toInstant();
        Bson initiatorFilter = isTicket ?
                and(eq("username",updateRequest.getUpdateInitiator()),eq("ticketList.ticketDate",utcTime),eq("ticketList.ticketId",updateRequest.getReferenceTicket().getTicketId())):
                and(eq("username",updateRequest.getUpdateInitiator()),eq("reportList.dateOfReport",utcTime),eq("reportList.reportId",updateRequest.getReferenceReport().getReportId()));
        Bson targetFilter = isTicket?
                and(eq("username", updateRequest.getReferenceTicket().getUsername()), eq("ticketList.ticketDate", utcTime),eq("ticketList.ticketId",updateRequest.getReferenceTicket().getTicketId())):
                and(eq("username",updateRequest.getReferenceReport().getBusinessActivityName()),eq("reportList.dateOfReport",utcTime),eq("reportList.reportId",updateRequest.getReferenceReport().getReportId()));

        GenericResponse response = new GenericResponse();
        TransactionBody<GenericResponse> transaction = () -> {
            User admin;
            MongoCollection<SupportRequest> openSupport = mongoDriver.getSupportCollection();
            //take initiator because it could be an admin but we should move this statement because if initiator is a business it will return null
            GenericResponse transactionResult =  new GenericResponse();
            switch (updateRequest.getCommand()) {
                case "DELETE": //initiator could be both user and business (no admin)

                    if(isTicket && updateRequest.getReferenceTicket().getStatus() == Costant.SupportStatus.OPEN){
                        openSupport.deleteOne(session,and(eq("_id",new ObjectId(updateRequest.getReferenceTicket().getTicketId())),eq("requestCategory",Costant.RequestCategory.TICKET)));
                        userColl.updateOne(session,eq("username",updateRequest.getUpdateInitiator()),
                                Updates.pull("ticketList",updateRequest.getReferenceTicket()));
                        transactionResult.setAnswer(String.format("Ticket n° %s has removed closed successfully", updateRequest.getReferenceTicket().getTicketId()));
                    }else if(!isTicket && updateRequest.getReferenceReport().getStatus() == Costant.SupportStatus.OPEN){
                        openSupport.deleteOne(eq("_id",new ObjectId(updateRequest.getReferenceReport().getReportId())));
                        businessColl.updateOne(session,eq("username",updateRequest.getUpdateInitiator()),
                                Updates.pull("reportList",updateRequest.getReferenceReport()));
                        transactionResult.setAnswer(String.format("Report n° %s has been removed successfully", updateRequest.getReferenceReport().getReportId()));
                   }else{
                        if(isTicket){
                            userColl.updateOne(session,eq("username",updateRequest.getUpdateInitiator()),
                                    Updates.pull("ticketList",updateRequest.getReferenceTicket()));
                            String updateAdmin = updateRequest.getReferenceTicket().getAdmin();
                            updateRequest.getReferenceTicket().setAdmin(null);
                            updateRequest.getReferenceTicket().setUsername(updateRequest.getUpdateInitiator());
                            userColl.updateOne(session,eq("username",updateAdmin),
                                    Updates.pull("ticketList",updateRequest.getReferenceTicket()));
                            transactionResult.setAnswer(String.format("Ticket n° %s has been removed successfully", updateRequest.getReferenceTicket().getTicketId()));
                        }else{
                            businessColl.updateOne(session,eq("username",updateRequest.getUpdateInitiator()),
                                    Updates.pull("reportList",updateRequest.getReferenceReport()));
                            String updateAdmin = updateRequest.getReferenceReport().getAdmin();
                            updateRequest.getReferenceReport().setAdmin(null);
                            updateRequest.getReferenceReport().setBusinessActivityName(updateRequest.getUpdateInitiator());
                            userColl.updateOne(session,eq("username",updateAdmin),
                                    Updates.pull("reportList",updateRequest.getReferenceReport()));
                            transactionResult.setAnswer(String.format("Report n° %s has been removed successfully", updateRequest.getReferenceReport().getReportId()));
                        }
                    }
                    break;
                case "CLOSE":
                   admin = userColl.find(initiatorFilter).first();
                    if(admin != null && admin.getIsAdmin()) {
                        Bson statusFieldToUpdate = isTicket ?
                              Updates.set("ticketList.$.status", Costant.SupportStatus.CLOSED) :
                              Updates.set("reportList.$.status", Costant.SupportStatus.CLOSED);
                        userColl.updateOne(session,initiatorFilter, statusFieldToUpdate); //initiator must be an admin
                        if(isTicket) {
                            userColl.updateOne(session,targetFilter, statusFieldToUpdate); //target is another user (ticket case)
                          transactionResult.setAnswer(String.format("Ticket n° %s has been closed successfully", updateRequest.getReferenceTicket().getTicketId()));
                        }else {
                            businessColl.updateOne(session,targetFilter, statusFieldToUpdate); //target is a business (report case)
                          transactionResult.setAnswer(String.format("Report n° %s has been closed successfully", updateRequest.getReferenceReport().getReportId()));
                         }
                     }else{
                      transactionResult.setAnswer("You aren't able to perform this update operation");
                     }

                    break;
                case "REPLY":
                    admin = (User) userColl.find(initiatorFilter).first();
                    if(admin != null && admin.getIsAdmin()) {
                        Bson responseFieldToUpdate = isTicket ?
                                Updates.set("ticketList.$.response", updateRequest.getResponse()) :
                                Updates.set("reportList.$.response", updateRequest.getResponse());
                        if (isTicket) {
                            userColl.updateOne(session,initiatorFilter, responseFieldToUpdate);
                            userColl.updateOne(session,targetFilter, responseFieldToUpdate);
                            transactionResult.setAnswer(String.format("ticket n° %s has been replayed", updateRequest.getReferenceTicket().getTicketId()));
                        } else {
                            userColl.updateOne(session,initiatorFilter, responseFieldToUpdate);
                            businessColl.updateOne(session,targetFilter, responseFieldToUpdate);
                            transactionResult.setAnswer(String.format("Report n° %s has been replayed", updateRequest.getReferenceReport().getReportId()));
                        }
                    }else{
                        transactionResult.setAnswer("You aren't able to perform this update operation");
                    }
                    break;

                case "UPDATE":
                    Field[] field = updateRequest.getClass().getDeclaredFields();
                    ArrayList<Bson> updates = new ArrayList<>();
                    for(Iterator<Field> iterator = Arrays.stream(field).iterator(); iterator.hasNext();){
                        Field element = iterator.next();
                        switch (element.getName()){
                            case "title":
                                if(updateRequest.getTitle() != null){
                                    Bson updateTitle = isTicket ?
                                            Updates.set("ticketList.$.title",updateRequest.getTitle()):
                                            Updates.set("reportList.$.title",updateRequest.getTitle());
                                    updates.add(updateTitle);
                                }
                                break;
                            case "description":
                                if(updateRequest.getDescription()!=null){
                                    Bson updateDescription = isTicket ?
                                            Updates.set("ticketList.$.description",updateRequest.getDescription()):
                                            Updates.set("reportList.$.description",updateRequest.getDescription());
                                    updates.add(updateDescription);
                                }
                                break;
                            case "category":
                                if(updateRequest.getCategory()!=null && isTicket){
                                    Bson updateCategory = Updates.set("ticketList.$.category",updateRequest.getCategory());
                                    updates.add(updateCategory);
                                }
                                break;
                        }
                    }
                    List<Bson> openUpdate = new ArrayList<>();
                    updates.forEach(element->{
                        BsonDocument doc = element.toBsonDocument();
                        doc.entrySet().forEach(entry -> {
                            String key = entry.getKey();
                            entry.getValue().asDocument().entrySet().forEach(entryset->{
                                        String keySet= entryset.getKey();
                                        BsonString value = entryset.getValue().asString();
                                openUpdate.add(Updates.set(keySet.contains("ticketList")?keySet.replace("ticketList.$","ticket"):keySet.replace("reportList.$","report"),value.getValue()));
                                    });


                        });

                    });
                    Bson openFilter = isTicket ?
                            eq ("_id",new ObjectId(updateRequest.getReferenceTicket().getTicketId())):
                            eq ("_id",new ObjectId(updateRequest.getReferenceReport().getReportId()));
                   if(isTicket && !updates.isEmpty()){
                        if(updateRequest.getReferenceTicket().getStatus().equals(Costant.SupportStatus.OPEN)){
                            openSupport.updateOne(session,openFilter,Updates.combine(openUpdate));
                            userColl.updateOne(session, initiatorFilter, Updates.combine(updates));
                        }else {
                            Bson adminFilter = and(eq("username", updateRequest.getReferenceTicket().getAdmin()), eq("ticketList.ticketDate", utcTime), eq("ticketList.ticketId", updateRequest.getReferenceTicket().getTicketId()));
                            userColl.updateOne(session, initiatorFilter, Updates.combine(updates));
                            userColl.updateOne(session, adminFilter, Updates.combine(updates));
                        }
                        transactionResult.setAnswer(String.format("Ticket n° %s has been updated",updateRequest.getReferenceTicket().getTicketId()));
                   }else if(!isTicket && !updates.isEmpty()){
                       if(updateRequest.getReferenceReport().getStatus().equals(Costant.SupportStatus.OPEN)){
                           openSupport.updateOne(session,openFilter,Updates.combine(openUpdate));
                           businessColl.updateOne(session, initiatorFilter, Updates.combine(updates));
                       }else {
                           Bson adminFilter = and(eq("username", updateRequest.getReferenceReport().getAdmin()), eq("ticketList.dateOfReport", utcTime), eq("ticketList.ticketId", updateRequest.getReferenceReport().getReportId()));
                           businessColl.updateOne(session, initiatorFilter, Updates.combine(updates));
                           userColl.updateOne(session, adminFilter, Updates.combine(updates));
                           transactionResult.setAnswer(String.format("Report n° %s has been updated", updateRequest.getReferenceReport().getReportId()));
                       }
                   }

            }
            return transactionResult;
        };
        try{
            response = session.withTransaction(transaction);
        }catch(RuntimeException e){

            logger.error(e.toString());
        }
        finally {
            session.close();
        }
    return response;
    }

}
