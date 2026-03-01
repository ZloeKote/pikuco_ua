package com.pikuco.recommendationservice.repository;

import com.pikuco.recommendationservice.entity.QuizStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizStatisticsRepository extends MongoRepository<QuizStatistics, String> {
    
    Optional<QuizStatistics> findByQuizPseudoId(int quizPseudoId);
    
    void deleteByQuizPseudoId(int quizPseudoId);
}


