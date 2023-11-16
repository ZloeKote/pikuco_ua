package com.pikuco.quizservice.service;

import com.pikuco.quizservice.repository.QuizRepository;
import com.pikuco.sharedComps.quizService.dto.QuizDto;
import com.pikuco.sharedComps.quizService.dto.QuizResultsDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class QuizService {
    private QuizRepository quizRepository;
    private DBAPIClient apiClient;

    public List<QuizDto> getQuizzes() {
        return apiClient.showQuizzes().getBody();
        //return quizRepository.findAll();
    }

    public int addQuiz(QuizDto quizDto) {
        Integer body = apiClient.addQuiz(quizDto).getBody();
        if (body == null) throw new NullPointerException("Quiz was not added");
        return body;
    }

    public QuizDto getQuizById(int quizId) {
        return apiClient.showQuizById(quizId).getBody();
    }

    public QuizResultsDto getQuizResultsByQuizId(int quizId) {
        return apiClient.showQuizResultsById(quizId).getBody();
    }
}
