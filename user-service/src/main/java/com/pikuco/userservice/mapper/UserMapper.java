package com.pikuco.userservice.mapper;


import com.pikuco.userservice.dto.UserDto;
import com.pikuco.userservice.dto.UserPrivacyDto;
import com.pikuco.userservice.dto.UserProfileDto;
import com.pikuco.userservice.dto.UserPublicToUpdateDto;
import com.pikuco.userservice.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                user.getRole().name(),
                user.getAvatar(),
                user.getDescription(),
                user.getBirthdate(),
                user.getCreationDate()
        );
    }

    public static UserProfileDto mapToUserProfileDto(User user) {
        return new UserProfileDto(
                user.getNickname(),
                user.getRole().name(),
                user.getAvatar(),
                user.getDescription(),
                user.getBirthdate(),
                user.getCreationDate());
    }

    public static UserPrivacyDto mapToUserPrivacyDto(User user) {
        return new UserPrivacyDto(user.getEmail());
    }

    public static User mapToUser(UserPublicToUpdateDto userDto) {
        return User.builder()
                .nickname(userDto.nickname())
                .description(userDto.description())
                .build();
    }

    public static User mapToUser(UserDto userDto) {
        return User.builder()
                .id(userDto.id())
                .nickname(userDto.nickname())
                .email(userDto.email())
                .avatar(userDto.avatar())
                .description(userDto.description())
                .birthdate(userDto.birthdate())
                .creationDate(userDto.creationDate())
                .build();
    }
}
