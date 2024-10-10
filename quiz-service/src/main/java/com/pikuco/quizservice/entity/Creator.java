package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
public class Creator {
    @Field(name = "creator_id")
    Long creatorId;
    String nickname;
    String avatar;
}
