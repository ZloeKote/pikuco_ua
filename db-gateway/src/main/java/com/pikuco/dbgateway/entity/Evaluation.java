package com.pikuco.dbgateway.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "evaluation")
public class Evaluation {
    @Id
    @Field(name = "_id")
    private String id;
    @Field(name = "type")
    private String type;
    @Field(name = "user_id")
    private int userId;
    @Field(name = "evaluation_object_id")
    private String evaluationObjectId;
    @Field(name = "isLiked")
    private boolean isLiked;
    @Field(name = "evaluatedAt")
    private LocalDateTime evaluatedAt;

}
