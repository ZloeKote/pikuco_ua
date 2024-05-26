package com.pikuco.evaluationservice.service;

import com.pikuco.evaluationservice.api.QuizAPIClient;
import com.pikuco.evaluationservice.api.UserAPIClient;
import com.pikuco.evaluationservice.dto.EvaluationBestDto;
import com.pikuco.evaluationservice.dto.EvaluationDto;
import com.pikuco.evaluationservice.dto.QuizzesResponse;
import com.pikuco.evaluationservice.entity.Evaluation;
import com.pikuco.evaluationservice.exception.NonAuthorizedException;
import com.pikuco.evaluationservice.repository.EvaluationRepository;
import feign.FeignException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
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
import java.util.*;

@Service
@RequiredArgsConstructor
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final MongoTemplate mongoTemplate;
    private final QuizAPIClient quizAPI;
    private final UserAPIClient userAPI;

    public EvaluationDto getEvaluationByQuizId(String authHeader, int pseudoId) {
        String quizId = quizAPI.showQuizIdByPseudoId(pseudoId).getBody();

        if (quizId == null) {
            throw new NoSuchElementException("Вікторини з таким id не знайдено");
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

    public Map<String, Object> getBestQuizzesIds(@NonNull List<String> quizzesIds, int pageNo, int pageSize) {
        List<Switch.CaseOperator> cases = new ArrayList<>();
        Switch.CaseOperator caseOperatorTrue = Switch.CaseOperator
                .when(ComparisonOperators.valueOf("isLiked").equalToValue(true)).then(1);
        Switch.CaseOperator caseOperatorFalse = Switch.CaseOperator
                .when(ComparisonOperators.valueOf("isLiked").equalToValue(false)).then(-1);
        cases.add(caseOperatorTrue);
        cases.add(caseOperatorFalse);

        ProjectionOperation projectionOperation = Aggregation.project()
                .and(ConditionalOperators.switchCases(cases).defaultTo(0)).as("isLiked")
                .and("evaluation_object_id").as("evaluation_object_id");

        GroupOperation groupOperation = Aggregation.group("evaluation_object_id")
                .sum("isLiked").as("evaluation");

        CountOperation countOperation = Aggregation.count().as("quantity");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "evaluation");
        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        Aggregation aggregation;
        if (quizzesIds != null && !quizzesIds.isEmpty()) {
            MatchOperation matchOperation = Aggregation.match(Criteria.where("evaluation_object_id").in(quizzesIds));
            aggregation = Aggregation.newAggregation(
                    matchOperation,
                    projectionOperation,
                    groupOperation,
                    sortOperation,
                    skipOperation,
                    limitOperation);
        } else {
            aggregation = Aggregation
                    .newAggregation(projectionOperation, groupOperation, sortOperation, skipOperation, limitOperation);
        }
        List<Document> results = mongoTemplate.aggregate(
                aggregation, "evaluation", Document.class).getMappedResults();

        Set<EvaluationBestDto> uniqueBestQuizzesids = new HashSet<>(results.size() + quizzesIds.size());
        for (Document doc : results) {
            EvaluationBestDto evaluationBestDto =
                    new EvaluationBestDto(doc.getString("_id"), doc.getInteger("evaluation"));
            uniqueBestQuizzesids.add(evaluationBestDto);
        }
        for (String quizId : quizzesIds)
            uniqueBestQuizzesids.add(new EvaluationBestDto(quizId, 0));

        List<EvaluationBestDto> evaluationBestDtos = new ArrayList<>(uniqueBestQuizzesids);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("numPages", (int) Math.ceil((evaluationBestDtos.size() / (double) pageSize)));
        evaluationBestDtos.sort(Comparator.comparingInt(EvaluationBestDto::getEvaluation).reversed());

        List<String> bestQuizzesIds = new LinkedList<>();
        for (int i = (pageNo - 1) * pageSize; i < pageNo * pageSize; i++) {
            if (i >= evaluationBestDtos.size()) break;
            bestQuizzesIds.add(evaluationBestDtos.get(i).getQuizId());
        }
        resultMap.put("quizzesIds", bestQuizzesIds);
        return resultMap;
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
