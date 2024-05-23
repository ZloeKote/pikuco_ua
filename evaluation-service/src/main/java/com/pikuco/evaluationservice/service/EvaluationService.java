package com.pikuco.evaluationservice.service;

import com.pikuco.evaluationservice.api.QuizAPIClient;
import com.pikuco.evaluationservice.api.UserAPIClient;
import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.entity.Evaluation;
import com.pikuco.evaluationservice.exception.NonAuthorizedException;
import com.pikuco.evaluationservice.repository.EvaluationRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final MongoTemplate mongoTemplate;
    private final QuizAPIClient quizAPI;
    private final UserAPIClient userAPI;

    public List<Evaluation> getEvaluations() {
        return evaluationRepository.findAll();
    }

    public EvaluationDto getEvaluationByQuizId(String authHeader, int pseudoId) {
        String quizId = quizAPI.showQuizIdByPseudoId(pseudoId).getBody();

        if (quizId == null) {
            throw new NonAuthorizedException("Ви не авторизовані");
        }


        MatchOperation matchOperation = Aggregation.match(Criteria.where("type").is("quiz")
                .and("evaluation_object_id").is(quizId));

        List<Switch.CaseOperator> cases = new ArrayList<>();
        Switch.CaseOperator caseOperatorTrue = Switch.CaseOperator
                .when(ComparisonOperators.valueOf("isLiked").equalToValue(true)).then(1);
        Switch.CaseOperator caseOperatorFalse = Switch.CaseOperator
                .when(ComparisonOperators.valueOf("isLiked").equalToValue(false)).then(-1);
        cases.add(caseOperatorTrue);
        cases.add(caseOperatorFalse);

        ProjectionOperation projectionOperation = Aggregation.project()
                .and(ConditionalOperators.switchCases(cases).defaultTo(0)).as("isLiked");

        GroupOperation groupOperation = Aggregation.group("evaluation_object_id")
                .sum("isLiked").as("evaluation");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectionOperation, groupOperation);
        Document evaluationDoc = mongoTemplate.aggregate(aggregation, "evaluation", Document.class)
                .getUniqueMappedResult();
        EvaluationDto evaluation = new EvaluationDto();

        if (evaluationDoc == null) {
            evaluation.setEvaluation(0);
        }

        try {
            evaluation.setEvaluation(Objects.requireNonNull(evaluationDoc).getInteger("evaluation"));
        } catch (NullPointerException e) {
            evaluation.setEvaluation(0);
        }

        // if user is not authorized, then return evaluation
        // if user is authorized, then find his evaluation
        ResponseEntity<Long> userResponse;
        try {
            userResponse = userAPI.showUserIdByToken(authHeader);
        } catch (FeignException e) {
            return evaluation;
        }

        MatchOperation matchQuizWithUser = Aggregation.match(Criteria.where("type").is("quiz")
                .and("evaluation_object_id").is(quizId)
                .and("user_id").is(userResponse.getBody()));

        Aggregation aggregationQuizWithUser = Aggregation.newAggregation(matchQuizWithUser);
        Document evaluationWithUserDoc = mongoTemplate.aggregate(aggregationQuizWithUser,
                "evaluation", Document.class).getUniqueMappedResult();

        // if authorized user have not evaluate quiz yet, then return evaluation
        Boolean isLiked;
        try {
            isLiked = Objects.requireNonNull(evaluationWithUserDoc).getBoolean("isLiked");
        } catch (NullPointerException e) {
            return evaluation;
        }

        if (isLiked) {
            evaluation.setLiked(true);
        } else {
            evaluation.setDisliked(true);
        }

        return evaluation;
    }

    public void addEvaluation(String authHeader, int pseudoId, Boolean isLiked) {
        Pair<Long, String> quizAndUserId = getUserAndQuizId(authHeader, pseudoId);

        Evaluation evaluation = new Evaluation();
        evaluation.setUserId(quizAndUserId.getFirst());
        evaluation.setEvaluationObjectId(quizAndUserId.getSecond());
        evaluation.setType("quiz");
        evaluation.setLiked(isLiked);
        evaluation.setEvaluatedAt(LocalDateTime.now());

        Query query = new Query(Criteria.where("user_id").is(quizAndUserId.getFirst())
                .and("evaluation_object_id").is(quizAndUserId.getSecond())
                .and("type").is("quiz"));
        mongoTemplate.findAndReplace(query, evaluation, FindAndReplaceOptions.options().upsert());
    }

    public void deleteEvaluation(String authHeader, int pseudoId) {
        Pair<Long, String> quizAndUserId = getUserAndQuizId(authHeader, pseudoId);
        Query query = new Query(Criteria.where("user_id").is(quizAndUserId.getFirst())
                .and("evaluation_object_id").is(quizAndUserId.getSecond())
                .and("type").is("quiz"));
        mongoTemplate.remove(query, "evaluation");
    }

    public void deleteQuizEvaluationsByQuizId(String quizId) {
        Query matchQuery = new Query(Criteria.where("type").is("quiz")
                .and("evaluation_object_id").is(quizId));
        mongoTemplate.findAllAndRemove(matchQuery, "evaluation");
    }

    private Pair<Long, String> getUserAndQuizId(String authHeader, int pseudoId) {
        String quizId = quizAPI.showQuizIdByPseudoId(pseudoId).getBody();

        if (quizId == null) {
            throw new NoSuchElementException("Такої вікторини не існує");
        }

        ResponseEntity<Long> userResponse;
        try {
            userResponse = userAPI.showUserIdByToken(authHeader);
            return Pair.of(Objects.requireNonNull(userResponse.getBody()), quizId);
        } catch (FeignException | NullPointerException e) {
            throw new NonAuthorizedException("Ви не авторизовані");
        }
    }
}
