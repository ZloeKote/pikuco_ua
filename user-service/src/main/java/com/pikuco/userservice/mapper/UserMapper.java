package com.pikuco.userservice.mapper;


import com.pikuco.userservice.dto.UserDto;
import com.pikuco.userservice.entity.User;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                "ROLE_USER",
                user.getAvatar(),
                user.getDescription(),
                user.getBirthdate(),
                user.getCreationDate()
        );
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
