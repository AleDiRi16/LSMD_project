package it.unipi.lsmsd.service;

import com.mongodb.client.*;
import it.unipi.lsmsd.DTO.SupportRequest;
import it.unipi.lsmsd.entity.BusinessActivity;
import it.unipi.lsmsd.entity.User;
import org.bson.Document;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

//Service Class it will be instanced with spring-boot application, and  it is treats as Singleton Class
@Service
public class MongoDriver {
    Logger logger = LoggerFactory.getLogger(MongoDriver.class);
    private MongoClient client;
    private MongoDatabase database;

    public MongoDriver(@Value("${mongodb.connection}") String mongoConnectionUrl, @Value("${mongodb.database}") String mongoDatabase) {
        try {

            CodecProvider pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build();
            CodecRegistry pojoCodecRegistry = fromRegistries(getDefaultCodecRegistry(),
                    fromProviders(pojoCodecProvider));
            this.client = MongoClients.create(mongoConnectionUrl);
            this.database = client.getDatabase(mongoDatabase).withCodecRegistry(pojoCodecRegistry);
        } catch (IllegalArgumentException e) {
            logger.error("Error in Mongo Connection String Hint: check url format");

        }
    }
    public MongoDatabase getDatabase (){
        return this.database;
    }
    public void close(){
        client.close();

    }
    public ClientSession getSession() {
        return  client.startSession();
    }

    public MongoCollection<User> getUserCollection(){
        return this.database.getCollection("User",User.class);
    }
    public MongoCollection<BusinessActivity> getBusinessActivityCollection(){
        return this.database.getCollection("BusinessActivity",BusinessActivity.class);

    }


    public MongoCollection<SupportRequest> getSupportCollection(){
        return this.database.getCollection("open_notification", SupportRequest.class);
    }
    public MongoCollection<Document> getCollection(String collection) {
        return this.database.getCollection(collection);
    }

}
