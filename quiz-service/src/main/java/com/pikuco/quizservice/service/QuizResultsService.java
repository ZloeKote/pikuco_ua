package com.pikuco.quizservice.service;

import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.pikuco.quizservice.entity.*;
import com.pikuco.quizservice.repository.QuizResultsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.UpdateDefinition;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
public class QuizResultsService {
    private final MongoTemplate mongoTemplate;
    private final QuizResultsRepository quizResultsRepository;
    @Getter
    private final QuizService quizService;

    public QuizResults getQuizResultsById(int pseudoId, SortQuizResultsType sortType) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);
        int amountQuestions = quiz.getQuestions().size();
        int rounds = (int) Math.pow(amountQuestions, 0.5) + 1; // + 1 because last round has 2nd and 1st place

        // find results by quiz id
        MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(Criteria.where("quiz.$id").is(quiz.getId())));
        // unwind results
        UnwindOperation unwindResults = Aggregation.unwind("results");
        // unwind questions
        UnwindOperation unwindQuestions = Aggregation.unwind("results.questions");

        // calculate points for each place using fibonacci sequence
        int[] points = new int[rounds];
        points[0] = 1;
        points[1] = 2;
        for (int i = 2; i < rounds; i++) {
            points[i] = points[i - 1] + points[i - 2];
        }

        // create cases for switch operator to calculate points for each question
        List<ConditionalOperators.Switch.CaseOperator> cases = new ArrayList<>();
        for (int i = 1; i <= rounds; i++) {
            int roundAmountPlaces = (int) Math.round(amountQuestions / Math.pow(2, i));
            int roundAmountQuestions = (int) Math.round(amountQuestions / Math.pow(2, i - 1));
            int minPlace = roundAmountQuestions - roundAmountPlaces;

            ConditionalOperators.Switch.CaseOperator caseOperator = ConditionalOperators.Switch.CaseOperator
                    .when(BooleanOperators.And.and(ComparisonOperators.valueOf("$results.questions.place").lessThanEqualToValue(roundAmountQuestions),
                            ComparisonOperators.valueOf("$results.questions.place").greaterThanValue(minPlace))).then(points[i - 1]);
            cases.add(caseOperator);
        }

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("results.participant_id").as("participantId")
                .and("results.questions").as("question")
                .and(ConditionalOperators.switchCases(cases).defaultTo(1)).as("score")
                .andExclude("_id");

        GroupOperation groupOperation = Aggregation.group("question.url")
                .first("question.title").as("title")
                .first("question.description").as("description")
                .sum("score").as("totalScore");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "totalScore");
        if (sortType == SortQuizResultsType.SCORE_ASC) {
            sortOperation = Aggregation.sort(Sort.Direction.ASC, "totalScore");
        } else if (sortType == SortQuizResultsType.TITLE_ASC) {
            sortOperation = Aggregation.sort(Sort.Direction.ASC, "title");
        } else if (sortType == SortQuizResultsType.TITLE_DESC) {
            sortOperation = Aggregation.sort(Sort.Direction.DESC, "title");
        }

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindResults, unwindQuestions, projectionOperation, groupOperation, sortOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "quizResults", Document.class);

        QuizResults quizResults = quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();
        List<QuestionResult> questionResultList = new LinkedList<>();
        for (Document doc : results.getMappedResults()) {
            QuestionResult questionResult = new QuestionResult();
            questionResult.setTitle(doc.getString("title"));
            questionResult.setDescription(doc.getString("description"));
            questionResult.setScore(doc.getInteger("totalScore"));
            questionResult.setUrl(doc.getString("_id")); // url
            questionResultList.add(questionResult);
        }
        quizResults.getResults().clear();
        quizResults.getResults().add(new QuizResult(questionResultList, 0, LocalDateTime.now()));
        return quizResults;
    }

    public QuizResults getIndividualQuizResults(int pseudoId, int userId, SortQuizResultsType sortType) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);

        // find results by quiz id
        MatchOperation matchOperation = Aggregation.match(Criteria.where("quiz.$id").is(quiz.getId()));
        // unwind results
        UnwindOperation unwindResults = Aggregation.unwind("results");
        // find results only one user by user id
        MatchOperation matchOperationUser = Aggregation.match(Criteria.where("results.participant_id").is(userId));
        // unwind questions
        UnwindOperation unwindQuestions = Aggregation.unwind("results.questions");
        // sort
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.ASC, "results.questions.place");
        if (sortType == SortQuizResultsType.TITLE_ASC) {
            sortOperation = Aggregation.sort(Sort.Direction.ASC, "results.questions.title");
        } else if (sortType == SortQuizResultsType.TITLE_DESC) {
            sortOperation = Aggregation.sort(Sort.Direction.DESC, "results.questions.title");
        } else if (sortType == SortQuizResultsType.PLACE_DESC) {
            sortOperation = Aggregation.sort(Sort.Direction.DESC, "results.questions.place");
        }
        // project only needed fields
        ProjectionOperation projectionOperation = Aggregation.project()
                .and("results.participant_id").as("participant_id")
                .and("results.questions").as("questions")
                .andExclude("_id");
        // group by participant id
        GroupOperation groupOperation = Aggregation.group("participant_id")
                .first("participant_id").as("participant_id")
                .push("questions").as("questions");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindResults,
                matchOperationUser, unwindQuestions,
                sortOperation, projectionOperation, groupOperation);

        QuizResult resultsDoc = mongoTemplate.aggregate(aggregation, "quizResults", QuizResult.class).getMappedResults().get(0);
        QuizResults quizResults = quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();
        quizResults.getResults().clear();
        quizResults.getResults().add(resultsDoc);
        return quizResults;
    }

    public void deleteQuizResultsByQuizId(int pseudoId) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);
        quizResultsRepository.deleteQuizResultsByQuiz_Id(quiz.getId());
    }

    public void addNewQuizResult(QuizResult quizResult, int quizPseudoId) {
        Quiz quiz = quizService.getQuizByPseudoId(quizPseudoId);
        QuizResults quizResults = new QuizResults();

        try {
            quizResults = quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();

            Query query = new Query(Criteria.where("_id").is(new ObjectId(quizResults.getId()))
                    .and("results").elemMatch(Criteria.where("participant_id").is(quizResult.getParticipant_id())));
            Update update = new Update();
            // if participant already has results, update his questions
            boolean isParticipateExists = quizResults.getResults()
                    .stream().anyMatch((result) -> quizResult.getParticipant_id() == result.getParticipant_id());
            if (isParticipateExists) {
                update.set("results.$.questions", quizResult.getQuestions())
                        .set("results.$.passedAt", quizResult.getPassedAt());
                mongoTemplate.updateFirst(query, update, "quizResults");
                return;
            }

            // add results to quiz results that exists
            Document resultsDoc = new Document("questions", quizResult.getQuestions())
                    .append("participant_id", quizResult.getParticipant_id())
                    .append("passedAt", quizResult.getPassedAt());
            query = new Query(Criteria.where("_id").is(new ObjectId(quizResults.getId())));
            update = new Update();
            update.push("results", resultsDoc);
            mongoTemplate.updateFirst(query, update, "quizResults");
        } catch (NoSuchElementException e) { // if results doesn't exist yet
            quizResults.setQuiz(quiz);
            quizResult.setPassedAt(LocalDateTime.now());
            quizResults.setResults(List.of(quizResult));
            quizResultsRepository.save(quizResults);
        }
    }

    public List<ObjectId> getQuizzesIdByParticipantId(int participantId, SortQuizResultsType sortType, int pageNo, int pageSize) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("results").elemMatch(Criteria.where("participant_id").is(participantId)));
        UnwindOperation unwindOperation = Aggregation.unwind("results");
        MatchOperation matchOperation1 = Aggregation.match(Criteria.where("results.participant_id").is(participantId));

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "results.passedAt");
        if (sortType == SortQuizResultsType.OLDEST) {
            sortOperation = Aggregation.sort(Sort.Direction.ASC, "results.passedAt");
        }
        ProjectionOperation projectionOperation = Aggregation.project().and("quiz").as("quiz");
        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindOperation, matchOperation1, sortOperation, projectionOperation, skipOperation, limitOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "quizResults", Document.class);
        List<ObjectId> quizzesId = new ArrayList<>();
        for (Document doc : results.getMappedResults()) {
            quizzesId.add(new ObjectId(doc.get("quiz", DBRef.class).getId().toString()));
        }
        System.out.println(quizzesId);
        return quizzesId;
    }
}
