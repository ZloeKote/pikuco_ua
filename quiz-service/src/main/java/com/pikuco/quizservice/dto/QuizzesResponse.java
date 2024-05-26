package com.pikuco.quizservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
public class QuizzesResponse {
    List<String> quizzesIds;
    int numPages;
}
