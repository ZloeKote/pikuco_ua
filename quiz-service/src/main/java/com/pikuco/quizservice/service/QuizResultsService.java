package com.pikuco.quizservice.service;

import com.mongodb.DBRef;
import com.pikuco.quizservice.api.UserAPIClient;
import com.pikuco.quizservice.dto.*;
import com.pikuco.quizservice.entity.*;
import com.pikuco.quizservice.exception.NonAuthorizedException;
import com.pikuco.quizservice.repository.QuizResultsRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators.Switch.CaseOperator;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizResultsService {
    private final MongoTemplate mongoTemplate;
    private final QuizResultsRepository quizResultsRepository;
    private final UserAPIClient userAPIClient;
    @Getter
    private final QuizService quizService;

    public QuizResultsDto getQuizResultsById(int pseudoId, SortQuizResultsType sortType) {
        Quiz quiz = quizService.getQuizByPseudoId(pseudoId);
        int amountQuestions = quiz.getQuestions().size();
        int rounds = ((int) Math.round(Math.pow(amountQuestions, 0.5))) + 1; // + 1 because last round has 2nd and 1st place
        // calculate points for each place using fibonacci sequence
        int[] points = new int[rounds];
        points[0] = 0;
        points[1] = 1;
        points[2] = 2;
        for (int i = 3; i < rounds; i++) {
            points[i] = points[i - 1] + points[i - 2];
        }
        QuizResults quizResults = quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();

        // find results by quiz id
        MatchOperation matchOperation = Aggregation.match(Criteria.where("quiz.$id").is(quiz.getId()));
        // unwind results
        UnwindOperation unwindResults = Aggregation.unwind("results");
        // unwind questions
        UnwindOperation unwindQuestions = Aggregation.unwind("results.questions");

        // create cases for switch operator to calculate points for each question
        List<CaseOperator> cases = new ArrayList<>();
        for (int i = 1; i <= rounds; i++) {
            int roundAmountPlaces = (int) Math.round(amountQuestions / Math.pow(2, i));
            // тут можливо можна підрахувати кількість питань просто помноживши кількість місць на 2 (чи навпаки)
            int roundAmountQuestions = (int) Math.round(amountQuestions / Math.pow(2, i - 1));
            int minPlace = roundAmountQuestions - roundAmountPlaces;

            CaseOperator caseOperator = CaseOperator
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
                .first("question.place").as("place")
                .sum("score").as("totalScore");

        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "totalScore");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindResults, unwindQuestions, projectionOperation, groupOperation, sortOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "quizResults", Document.class);

        List<QuestionResultDto> questionResultList = new LinkedList<>();
        int index = 0;
        if (quiz.getTranslations() != null) {
            for (Document doc : results.getMappedResults()) {
                List<QuestionTranslationDto> questionTranslationDtoList = new ArrayList<>();
                for (QuizTranslation quizTranslation : quiz.getTranslations()) {
                    Question translatedQuestion = quizTranslation.getQuestions().stream()
                            .filter((tr) -> tr.getUrl().equals(doc.getString("_id"))).findFirst()
                            .orElse(new Question(doc.getString("title"),
                                    doc.getString("description"),
                                    doc.getString("_id")));
                    QuestionTranslationDto questionTranslationDto = new QuestionTranslationDto(translatedQuestion.getTitle(),
                            translatedQuestion.getDescription(), quizTranslation.getLanguage());
                    questionTranslationDtoList.add(questionTranslationDto);
                }
                QuestionResultDto questionResult = new QuestionResultDto(doc.getString("title"),
                        doc.getString("description"),
                        doc.getString("_id"),
                        doc.getInteger("totalScore"),
                        ++index,
                        quiz.getLanguage(),
                        questionTranslationDtoList);
                questionResultList.add(questionResult);
            }
        } else {
            for (Document doc : results.getMappedResults()) {
                QuestionResultDto questionResult = new QuestionResultDto(doc.getString("title"),
                        doc.getString("description"),
                        doc.getString("_id"),
                        doc.getInteger("totalScore"),
                        ++index,
                        quiz.getLanguage(),
                        new ArrayList<>());
                questionResultList.add(questionResult);
            }
        }

        if (sortType == SortQuizResultsType.SCORE_ASC) {
            questionResultList = questionResultList.stream()
                    .sorted(Comparator.comparingInt(QuestionResultDto::place).reversed())
                    .collect(Collectors.toList());
        } else if (sortType == SortQuizResultsType.TITLE_ASC) {
            questionResultList = questionResultList.stream()
                    .sorted(Comparator.comparing(QuestionResultDto::title))
                    .collect(Collectors.toList());
        } else if (sortType == SortQuizResultsType.TITLE_DESC) {
            questionResultList = questionResultList.stream()
                    .sorted(Comparator.comparing(QuestionResultDto::title).reversed())
                    .collect(Collectors.toList());
        }
        quizResults.getResults().clear();

        return new QuizResultsDto(new QuizResultDto(questionResultList,
                0L, LocalDateTime.now()));
    }

    public QuizResults getIndividualQuizResults(String authHeader, int pseudoId, SortQuizResultsType sortType) {
        ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(403))
            throw new NonAuthorizedException("Ви не авторизовані");
        Long userId = Objects.requireNonNull(responseEntity.getBody()).id();

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
                .and("results.passedAt").as("passedAt")
                .andExclude("_id");
        // group by participant id
        GroupOperation groupOperation = Aggregation.group("participant_id")
                .first("participant_id").as("participant_id")
                .first("passedAt").as("passedAt")
                .push("questions").as("questions");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindResults,
                matchOperationUser, unwindQuestions,
                sortOperation, projectionOperation, groupOperation);

        QuizResult resultsDoc = mongoTemplate.aggregate(aggregation, "quizResults", QuizResult.class).getUniqueMappedResult();
        QuizResults quizResults = quizResultsRepository.findFirstByQuiz_Id(quiz.getId()).orElseThrow();
        quizResults.getResults().clear();
        quizResults.getResults().add(resultsDoc);
        return quizResults;
    }

    public void deleteQuizResultsByQuizId(ObjectId quizId) {
        quizResultsRepository.deleteQuizResultsByQuiz_Id(quizId);
    }

    public void addNewQuizResult(String authHeader, QuizResult quizResult, int quizPseudoId) {
        ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(403))
            throw new NonAuthorizedException("Ви не авторизовані");
        quizResult.setParticipant_id(Objects.requireNonNull(responseEntity.getBody()).id());

        Quiz quiz = quizService.getQuizByPseudoId(quizPseudoId);
        QuizResults quizResults = new QuizResults();

        try {
            quizResults = quizResultsRepository.findByQuiz_Id(quiz.getId()).orElseThrow();
            Query query = new Query(Criteria.where("_id").is(new ObjectId(quizResults.getId()))
                    .and("results").elemMatch(Criteria.where("participant_id").is(quizResult.getParticipant_id())));
            Update update = new Update();
            // if participant already has results, update his questions
            boolean isParticipateExists = quizResults.getResults()
                    .stream().anyMatch((result) -> Objects.equals(quizResult.getParticipant_id(), result.getParticipant_id()));
            if (isParticipateExists) {
                update.set("results.$.questions", quizResult.getQuestions())
                        .set("results.$.passedAt", LocalDateTime.now());
                mongoTemplate.updateFirst(query, update, "quizResults");
                return;
            }

            // add results to quiz results that exists
            Document resultsDoc = new Document("questions", quizResult.getQuestions())
                    .append("participant_id", quizResult.getParticipant_id())
                    .append("passedAt", LocalDateTime.now());
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

    public List<ObjectId> getQuizzesIdByParticipantId(Long participantId, SortQuizResultsType sortType, int pageNo, int pageSize) {
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

    public int getNumQuizzesByParticipantId(long participantId) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("results").elemMatch(Criteria.where("participant_id").is(participantId)));
        UnwindOperation unwindOperation = Aggregation.unwind("results");
        MatchOperation matchOperation1 = Aggregation.match(Criteria.where("results.participant_id").is(participantId));
        CountOperation countOperation = Aggregation.count().as("quantity");

        Aggregation aggregation = Aggregation.newAggregation(matchOperation, unwindOperation, matchOperation1, countOperation);
        HashMap<String, Integer> resultsCountMap = mongoTemplate.aggregate(aggregation, "quizResults", HashMap.class)
                .getUniqueMappedResult();

        assert resultsCountMap != null;
        return resultsCountMap.get("quantity");
    }

    public List<ObjectId> getPopularQuizzes(int pageNo, int pageSize) {
        ProjectionOperation countResults = Aggregation.project().and("results").size().as("count")
                .and("quiz").as("quiz");
        SortOperation sortByCount = Aggregation.sort(Sort.Direction.DESC, "count");
        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        Aggregation aggregation = Aggregation.newAggregation(countResults, sortByCount, skipOperation, limitOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "quizResults", Document.class);
        List<ObjectId> quizzesIds = new LinkedList<>();
        for(Document quizResult : results.getMappedResults()) {
            quizzesIds.add((ObjectId) quizResult.get("quiz", DBRef.class).getId());
        }
        return quizzesIds;
    }
}
