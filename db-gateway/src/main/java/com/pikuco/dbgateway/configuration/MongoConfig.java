package com.pikuco.dbgateway.configuration;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    @Primary
    @Bean(name="quiz-service-properties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.quiz-service")
    public MongoProperties quizServiceMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name="wishlist-service-properties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.wishlist-service")
    public MongoProperties wishlistServiceMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name="evaluation-service-properties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.evaluation-service")
    public MongoProperties evaluationServiceMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "quiz-service-client")
    public MongoClientFactoryBean quizServiceMongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        ConnectionString connectionString = new ConnectionString(quizServiceMongoProperties().getUri());
        factoryBean.setConnectionString(connectionString);
        return factoryBean;
    }

    @Bean(name="wishlist-service-client")
    public MongoClientFactoryBean wishlistServiceMongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        ConnectionString connectionString = new ConnectionString(wishlistServiceMongoProperties().getUri());
        factoryBean.setConnectionString(connectionString);
        return factoryBean;
    }

    @Bean(name="evaluation-service-client")
    public MongoClientFactoryBean evaluationServiceMongoClientFactoryBean() {
        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
        ConnectionString connectionString = new ConnectionString(evaluationServiceMongoProperties().getUri());
        factoryBean.setConnectionString(connectionString);
        return factoryBean;
    }

    @Primary
    @Bean(name="mongoTemplate")
    public MongoTemplate quizServiceMongoTemplate(@Qualifier("quiz-service-client") MongoClient quizServiceMongoClient,
                                                  @Qualifier("quiz-service-properties") MongoProperties mongoProperties) {
        return new MongoTemplate(quizServiceMongoClient, mongoProperties.getDatabase());
    }

    @Bean(name="wishlist-service-template")
    public MongoTemplate wishlistServiceMongoTemplate(@Qualifier("wishlist-service-client") MongoClient wishlistServiceMongoClient,
                                                      @Qualifier("wishlist-service-properties") MongoProperties mongoProperties) {
        return new MongoTemplate(wishlistServiceMongoClient, mongoProperties.getDatabase());
    }

    @Bean(name="evaluation-service-template")
    public MongoTemplate evaluationServiceMongoTemplate(@Qualifier("evaluation-service-client") MongoClient evaluationServiceMongoClient,
                                                      @Qualifier("evaluation-service-properties") MongoProperties mongoProperties) {
        return new MongoTemplate(evaluationServiceMongoClient, mongoProperties.getDatabase());
    }

//    @Primary
//    @Bean
//    public MongoClientFactoryBean mongoClientFactoryBean() {
//        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://zloekote:vgeA8GOop9JB0i9R@pikucoua.eucx5ju.mongodb.net/?retryWrites=true&w=majority&authSource=admin");
//        factoryBean.setConnectionString(connectionString);
//        return factoryBean;
//    }
//
//    @Bean
//    public MongoClientFactoryBean wishlistServiceMongoClientFactoryBean() {
//        MongoClientFactoryBean factoryBean = new MongoClientFactoryBean();
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://zloekote:vgeA8GOop9JB0i9R@pikucoua.eucx5ju.mongodb.net/?retryWrites=true&w=majority&authSource=admin");
//        factoryBean.setConnectionString(connectionString);
//        return factoryBean;
//    }
//
//    @Bean(name = "mongoTemplate")
//    public MongoTemplate quizServiceMongoTemplate() throws Exception {
//        return new MongoTemplate(mongoClientFactoryBean().getObject(), "quizDB");
//    }
//
//    @Bean(name="wishlist-service-template")
//    public MongoTemplate wishlistServiceMongoTemplate() throws Exception {
//        return new MongoTemplate(wishlistServiceMongoClientFactoryBean().getObject(), "wishlistDB");
//    }

}
