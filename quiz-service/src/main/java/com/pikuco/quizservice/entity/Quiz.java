package com.pikuco.quizservice.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "quiz")
public class Quiz {
    @Id
    @Field(name = "_id")
    private ObjectId id;
    @Field(name = "title")
    private String title;
    @Field(name = "description")
    private String description;
    @Field(name = "type")
    private Type type;
    @Field(name = "createdAt")
    private LocalDateTime createdAt;
    @Field(name = "updatedAt")
    private LocalDateTime updatedAt;
    @Field(name = "creator")
    private Creator creator;
    @Field(name = "questions")
    private List<Question> questions;
    @Field(name = "pseudoId")
    private int pseudoId;
    @Field(name = "isRoughDraft")
    private boolean isRoughDraft;
}
