package com.pikuco.sharedComps.wishlistService.dto;

import java.time.LocalDateTime;

public record WishlistDto(
    String id,
    String type,
    int userId,
    String wishlistedId,
    LocalDateTime wishlistedAt
) {}
