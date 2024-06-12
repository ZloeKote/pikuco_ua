package com.pikuco.wishlistservice.mapper;


import com.pikuco.wishlistservice.dto.WishlistDto;
import com.pikuco.wishlistservice.entity.Wishlist;

public class WishlistMapper {
    public static WishlistDto mapToWishlistDto(Wishlist wishlist) {
        return new WishlistDto(
                wishlist.getType(),
                wishlist.getUserId(),
                wishlist.getWishlistedId(),
                wishlist.getWishlistedAt()
        );
    }

    public static Wishlist mapToWishlist(WishlistDto wishlistDto) {
        return new Wishlist(null,
                wishlistDto.type(),
                wishlistDto.userId(),
                wishlistDto.wishlistedId(),
                wishlistDto.wishlistedAt()
        );
    }
}
