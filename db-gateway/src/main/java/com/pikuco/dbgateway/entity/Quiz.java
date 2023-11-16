package com.pikuco.dbgateway.entity;

import com.pikuco.sharedComps.quizService.dto.Creator;
import com.pikuco.sharedComps.quizService.entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "quiz")
public class Quiz {
    @Id
    @Field(name = "_id")
    private String id;
    @Field(name = "title")
    private String title;
    @Field(name = "description")
    private String description;
    @Field(name = "type")
    private String type;
    @Field(name = "createdAt")
    private LocalDateTime createdAt;
    @Field(name = "updatedAt")
    private LocalDateTime updatedAt;
    @Field(name = "creator")
    private Creator creator;
    @Field(name = "participants")
    private List<Question> questions;
}
