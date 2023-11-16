package com.pikuco.sharedComps.quizService.entity;

import com.pikuco.sharedComps.quizService.entity.QuestionResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizResult {
    List<QuestionResult> questions;
    int participant_id;
}
