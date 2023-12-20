package com.pikuco.quizservice.repository;

import com.pikuco.quizservice.entity.QuizResults;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuizResultsRepository extends MongoRepository<QuizResults, ObjectId> {
    @Query(value = "{ 'quiz.$id' : ?0 }")
    Optional<QuizResults> findFirstByQuiz_Id(ObjectId id);
    @Query(value = "{ 'quiz.$id' : ?0 }")
    Optional<QuizResults> findByQuiz_Id(ObjectId id);
    @Query(value = "{ 'quiz.$id' : ?0 }", delete = true)
    void deleteQuizResultsByQuiz_Id(ObjectId id);
}
