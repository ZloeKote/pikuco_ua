package com.pikuco.dbgateway.service;

import com.pikuco.dbgateway.entity.Quiz;
import com.pikuco.dbgateway.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuizService {
    private final MongoTemplate mongoTemplate;
    private final QuizRepository quizRepository;

    public QuizService(@Qualifier("mongoTemplate") MongoTemplate mongoTemplate, QuizRepository quizRepository) {
        this.mongoTemplate = mongoTemplate;
        this.quizRepository = quizRepository;
    }

    public List<Quiz> getQuizzes() {
        return mongoTemplate.findAll(Quiz.class, "quiz");
    }
}
