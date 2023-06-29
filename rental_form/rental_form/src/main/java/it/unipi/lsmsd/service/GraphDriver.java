package it.unipi.lsmsd.service;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Service
public class GraphDriver implements AutoCloseable {
    private Driver driver;
    private final String uri = "neo4j://10.1.1.11:7687";
    private final String user = "neo4j";
    private final String pass = "12345678";
    boolean result=true;

    public GraphDriver() {
        Config config = Config.builder()
                .withMaxConnectionLifetime(30, TimeUnit.MINUTES)
                .withMaxConnectionPoolSize(50)
                .withConnectionAcquisitionTimeout(2, TimeUnit.MINUTES)
                .build();
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, pass), config);
    }

    public Driver getGraphDriver() {
            return this.driver;
    }

    public void close() {
            this.driver.close();
    }


}