package com.pikuco.wishlistservice.service;

import com.pikuco.sharedComps.wishlistService.dto.WishlistDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class WishlistService {
    private DBAPIClient apiClient;

    public List<WishlistDto> getWishlists() {
        return apiClient.showWishlists().getBody();
    }
}
