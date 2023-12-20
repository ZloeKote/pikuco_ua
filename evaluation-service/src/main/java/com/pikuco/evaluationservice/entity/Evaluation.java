package com.pikuco.evaluationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "evaluation")
public class Evaluation {
    @Id
    @Field(name = "_id")
    private ObjectId id;
    @Field(name = "type")
    private String type;
    @Field(name = "user_id")
    private Long userId;
    @Field(name = "evaluation_object_id")
    private String evaluationObjectId;
    @Field(name = "isLiked")
    private boolean isLiked;
    @Field(name = "evaluatedAt")
    private LocalDateTime evaluatedAt;

}
