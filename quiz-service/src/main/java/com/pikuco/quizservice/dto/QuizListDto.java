package com.pikuco.quizservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class QuizListDto {
    List<QuizDto> quizzes;
    int numPages;

    public QuizListDto(List<QuizDto> quizzes, int numPages) {
        if (!quizzes.isEmpty()) this.quizzes = quizzes;
        else this.quizzes = new ArrayList<>();
        this.numPages = numPages;
    }
}
