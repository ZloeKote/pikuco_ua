package com.pikuco.dbgateway.service;

import com.pikuco.dbgateway.entity.Quiz;
import com.pikuco.dbgateway.entity.QuizResults;
import com.pikuco.dbgateway.repository.QuizRepository;
import com.pikuco.dbgateway.repository.QuizResultsRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class QuizResultsService {
    private final MongoTemplate mongoTemplate;
    private final QuizResultsRepository quizResultsRepository;
    private final QuizService quizService;

    public QuizResultsService(@Qualifier("mongoTemplate") MongoTemplate mongoTemplate,
                              QuizResultsRepository quizResultsRepository,
                              QuizService quizService) {
        this.mongoTemplate = mongoTemplate;
        this.quizResultsRepository = quizResultsRepository;
        this.quizService = quizService;
    }

    public QuizResults getQuizResultsById(int id) {
        Quiz quiz = quizService.getQuizById(id);
        return quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();
    }
}
