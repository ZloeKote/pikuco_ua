package com.pikuco.evaluationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class QuizzesRequest {
    List<String> quizzesIds;
}
