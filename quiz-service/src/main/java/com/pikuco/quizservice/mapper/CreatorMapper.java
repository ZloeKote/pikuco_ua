package com.pikuco.quizservice.mapper;

import com.pikuco.quizservice.dto.CreatorDto;
import com.pikuco.quizservice.entity.Creator;

public class CreatorMapper {
    public static CreatorDto mapToCreatorDto(Creator creator) {
        return new CreatorDto(creator.getNickname(), creator.getAvatar());
    }

    public static Creator mapToCreator(CreatorDto creator) {
        return new Creator(0L, creator.nickname(), creator.avatar());
    }
}
