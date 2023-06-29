package it.unipi.lsmsd.component;

import com.mongodb.MongoException;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import it.unipi.lsmsd.DTO.GenericPageDTO;
import it.unipi.lsmsd.entity.User;
import it.unipi.lsmsd.service.GraphDriver;
import it.unipi.lsmsd.service.MongoDriver;
import it.unipi.lsmsd.util.TransactionUtil;
import org.bson.conversions.Bson;
import org.neo4j.driver.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.mongodb.client.model.Filters.eq;

@Component
public class AdminComponentLogic {
    @Autowired
    MongoDriver mongoDriver;
    @Autowired
    TransactionUtil transactionUtil;
    @Autowired
    GraphDriver graphDriver;
    Logger logger = LoggerFactory.getLogger(AdminComponentLogic.class);

    public boolean removeUser(String username){
        boolean response = false;
        try(ClientSession mongoSession = mongoDriver.getSession();
            Session graphSession = graphDriver.getGraphDriver().session()){
            response = transactionUtil.removeUserTransaction(mongoSession,graphSession,username);
        }
        return response;
    }

    public boolean removeBusiness(String name){
        boolean response = false;
        try(ClientSession mongoSession = mongoDriver.getSession();
            Session graphSession = graphDriver.getGraphDriver().session()){
            response = transactionUtil.removeBusinessTransaction(mongoSession,graphSession,name);
        }
        return response;
    }


}
