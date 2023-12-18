package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "quizResults")
public class QuizResults {
    @Id
    @Field(name = "_id")
    private String id;
    @Field(name = "results")
    private List<QuizResult> results;
    @Field(name = "quiz")
    @DBRef
    private Quiz quiz;
}
