package com.pikuco.wishlistservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "wishlist")
public class Wishlist {
    @Id
    @Field(name = "_id")
    private String id;
    @Field(name = "type")
    private String type;
    @Field(name = "user_id")
    private long userId;
    @Field(name = "wishlisted_id")
    private String wishlistedId;
    @Field(name = "wishlistedAt")
    private LocalDateTime wishlistedAt;
}
