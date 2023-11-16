package com.pikuco.quizservice.service;

import com.pikuco.quizservice.repository.QuizRepository;
import com.pikuco.sharedComps.quizService.dto.QuizDto;
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
}
