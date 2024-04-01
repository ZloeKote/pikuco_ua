package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizTranslation {
    @Field(name = "title")
    private String title;
    @Field(name = "description")
    private String description;
    @Field(name = "language")
    private String language;
    @Field(name = "questions")
    private List<Question> questions;
}
