package com.pikuco.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
public class QuizzesResponse {
    List<String> quizzesIds;
    int numPages;
}
