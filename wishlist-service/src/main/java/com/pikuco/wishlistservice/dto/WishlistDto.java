package com.pikuco.wishlistservice.dto;

import java.time.LocalDateTime;

public record WishlistDto(
    String type,
    long userId,
    String wishlistedId,
    LocalDateTime wishlistedAt
) {}
