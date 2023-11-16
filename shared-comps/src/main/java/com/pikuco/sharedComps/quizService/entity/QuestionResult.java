package com.pikuco.sharedComps.quizService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResult {
    private String title;
    private String description;
    private String url;
    private int place;
}
