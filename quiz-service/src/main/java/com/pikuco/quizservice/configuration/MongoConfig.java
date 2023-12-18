package com.pikuco.quizservice.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    @Primary
    @Bean
    public MongoProperties mongoProperties() {
        return new MongoProperties();
    }

    @Bean
    public MongoClientFactoryBean mongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        ConnectionString connectionString = new ConnectionString(mongoProperties().getUri());
        factoryBean.setConnectionString(connectionString);
        return factoryBean;
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MongoProperties mongoProperties) {
        return new MongoTemplate(mongoClient, mongoProperties().getDatabase());
    }
}
