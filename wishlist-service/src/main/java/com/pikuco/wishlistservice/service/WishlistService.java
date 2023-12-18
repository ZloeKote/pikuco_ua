package com.pikuco.wishlistservice.service;

import com.pikuco.wishlistservice.entity.Wishlist;
import com.pikuco.wishlistservice.repository.WishlistRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WishlistService {
    private WishlistRepository wishlistRepository;

    public List<Wishlist> getAllWishlists() {
        return wishlistRepository.findAll();

    }
}
