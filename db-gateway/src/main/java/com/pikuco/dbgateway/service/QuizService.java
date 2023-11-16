package com.pikuco.dbgateway.service;

import com.pikuco.dbgateway.entity.Quiz;
import com.pikuco.dbgateway.entity.QuizResults;
import com.pikuco.dbgateway.repository.QuizRepository;
import com.pikuco.sharedComps.quizService.dto.QuizResultsDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    public int addQuiz(Quiz quiz) {
        int greatestId = quizRepository.findFirstByOrderByQuizIdDesc().orElseThrow().getQuizId();
        quiz.setQuizId(greatestId + 1);
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        Quiz insertedQuiz = mongoTemplate.insert(quiz, "quiz");
        return insertedQuiz.getQuizId();
    }

    public Quiz getQuizById(int id) {
        return quizRepository.findQuizByQuizId(id).orElseThrow();
    }

}
