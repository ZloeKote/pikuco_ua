package com.pikuco.dbgateway.mapper;

import com.pikuco.dbgateway.entity.User;
import com.pikuco.sharedComps.userService.dto.UserDto;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getNickname(),
                user.getEmail(),
                "ROLE_USER",
                user.getAvatar(),
                user.getDescription(),
                user.getBirthday(),
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
                .birthday(userDto.birthday())
                .creationDate(userDto.creationDate())
                .build();
    }
}
