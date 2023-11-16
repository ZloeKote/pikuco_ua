package com.pikuco.dbgateway.mapper;

import com.pikuco.dbgateway.entity.Wishlist;
import com.pikuco.sharedComps.wishlistService.dto.WishlistDto;

public class WishlistMapper {
    public static WishlistDto mapToWishlistDto(Wishlist wishlist) {
        return new WishlistDto(
                wishlist.getId(),
                wishlist.getType(),
                wishlist.getUserId(),
                wishlist.getWishlistedId(),
                wishlist.getWishlistedAt()
        );
    }

    public static Wishlist mapToWishlist(WishlistDto wishlistDto) {
        return new Wishlist(
                wishlistDto.id(),
                wishlistDto.type(),
                wishlistDto.userId(),
                wishlistDto.wishlistedId(),
                wishlistDto.wishlistedAt()
        );
    }
}
