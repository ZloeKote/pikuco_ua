package com.pikuco.wishlistservice.repository;

import com.pikuco.wishlistservice.entity.Wishlist;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends MongoRepository<Wishlist, ObjectId> {
}
