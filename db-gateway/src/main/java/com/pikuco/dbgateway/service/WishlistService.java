package com.pikuco.dbgateway.service;

import com.pikuco.dbgateway.entity.Wishlist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WishlistService {
    private final MongoTemplate mongoTemplate;

    public WishlistService(@Qualifier("wishlist-service-template") MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Wishlist> getAllWishlists() {
        return mongoTemplate.findAll(Wishlist.class, "wishlist");

    }
}
