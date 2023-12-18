package com.pikuco.quizservice.service;

import com.pikuco.quizservice.QuizServiceApplication;
import com.pikuco.quizservice.entity.*;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.exception.ObjectNotValidException;
import com.pikuco.quizservice.repository.QuizRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuizService {
    private QuizRepository quizRepository;
    private MongoTemplate mongoTemplate;
    private ApplicationContext context;

    public List<Quiz> getQuizzes(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return quizRepository.findAll(pageable).getContent();
    }

    public List<Quiz> getFilterSortQuizzes(String title,
                                           String type,
                                           int numberQuestions,
                                           int creatorId,
                                           SortType sort,
                                           int pageNo,
                                           int pageSize) {
        List<Criteria> criteriaList = new LinkedList<>();
        if (title != null && !title.isBlank()) {
            criteriaList.add(Criteria.where("title").regex(title, "i"));
        }
        if (type != null && !type.isBlank()) {
            criteriaList.add(Criteria.where("type").is(Type.valueOf(type)));
        }
        if (numberQuestions != 0) {
            double checkAmountQuestions = Math.log(numberQuestions) / Math.log(2);
            if (checkAmountQuestions % 1 == 0) {
                criteriaList.add(Criteria.where("questions").size(numberQuestions));
            }
        }

        if (creatorId != 0) {
            criteriaList.add(Criteria.where("creator.creator_id").is(creatorId));
        }

        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        SortOperation sortOperation = null;
        Aggregation aggregation;
        switch (sort) {
            case HIGHEST_RATED -> sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, ""));
            case NEWEST -> sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        MatchOperation matchOperation = null;
        if (!criteriaList.isEmpty()) {
            matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));
            aggregation = Aggregation.newAggregation(matchOperation, sortOperation, skipOperation, limitOperation);
        } else {
            aggregation = Aggregation.newAggregation(sortOperation, skipOperation, limitOperation);
        }
        AggregationResults<Quiz> results = mongoTemplate.aggregate(aggregation, "quiz", Quiz.class);

        return results.getMappedResults();
    }

    public List<Quiz> getQuizzesByParticipantId(int participantId, SortQuizResultsType sort, int PageNo, int pageSize) {
        QuizResultsService quizResultsService = context.getBean(QuizResultsService.class);
        List<ObjectId> quizIds = quizResultsService.getQuizzesIdByParticipantId(participantId, sort, PageNo, pageSize);
        List<Quiz> unsortedQuizzes = quizRepository.findAllByIdIn(quizIds);
        // Створіть Map для ефективного відображення id на Quiz
        Map<ObjectId, Quiz> quizMap = unsortedQuizzes.stream().collect(Collectors.toMap(Quiz::getId, Function.identity()));

        // Створіть список, в якому порядок відображається згідно із вхідним списком id
        return quizIds.stream()
                .map(quizMap::get)
                .toList();
    }

    public int addQuiz(Quiz quiz) {
        if (quiz.getPseudoId() != 0)
            throw new ObjectNotValidException(new HashSet<String>(List.of("Ви не можете додати вікторину, яка вже була створена!")));

        int greatestId = quizRepository.findFirstByOrderByPseudoIdDesc().orElseThrow().getPseudoId();
        quiz.setPseudoId(++greatestId);
        quiz.setCreatedAt(LocalDateTime.now());
        if (!quiz.isRoughDraft())
            quiz.setUpdatedAt(quiz.getCreatedAt());
        validateQuiz(quiz); // validate quiz
        greatestId = quizRepository.insert(quiz).getPseudoId();
        if (greatestId == 0) throw new NullPointerException("Quiz was not added");
        return greatestId;
    }

    public void changeQuiz(Quiz quiz) {
        Quiz quizToChange = quizRepository.findQuizByPseudoId(quiz.getPseudoId())
                .orElseThrow(() -> new ObjectNotFoundException(new HashSet<String>(List.of("Турнір не знайдено"))));
        quiz.setId(quizToChange.getId());

        if (!quizToChange.isRoughDraft() && !quiz.isRoughDraft()) {
            quiz.setUpdatedAt(LocalDateTime.now());
        } else if (quizToChange.isRoughDraft() && !quiz.isRoughDraft()) {
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(quiz.getCreatedAt());
        } else if (!quizToChange.isRoughDraft() && quiz.isRoughDraft()) {
            throw new ObjectNotValidException(new HashSet<String>(List.of("Ви не можете замінити вже опублікований турнір на чернетку")));
        }

        validateQuiz(quiz);

        Query query = new Query().addCriteria(Criteria.where("_id").is(quiz.getId()));
        mongoTemplate.findAndReplace(query, quiz, "quiz");
    }

    public void deleteQuiz(int pseudoId, int userId) {
        Quiz quizToDelete = quizRepository.findQuizByPseudoId(pseudoId).orElseThrow();
        if (quizToDelete.getCreator().getCreator_id() == userId) {
            quizRepository.deleteById(quizToDelete.getId());
            return;
        }
        throw new ObjectNotValidException(new HashSet<String>(List.of("Ви не маєте права видалити цей турнір")));
    }

    public Quiz getQuizByPseudoId(int quizId) {
        return quizRepository.findQuizByPseudoId(quizId)
                .orElseThrow(() -> new ObjectNotFoundException(new HashSet<String>(List.of("Турнір не знайдено"))));
    }

    private void validateQuiz(Quiz quiz) {
        // validate creator's credentials
        if (quiz.getCreator().getCreator_id() == 0 || (quiz.getCreator().getNickname() == null || quiz.getCreator().getNickname().isBlank()))
            throw new ObjectNotValidException(new HashSet<String>(List.of("Ви не авторизовані")));
        if (quiz.getCreator().getAvatar() == null || quiz.getCreator().getAvatar().isBlank())
            quiz.getCreator().setAvatar("default avatar");

        // validate title
        if (quiz.getTitle() == null || quiz.getTitle().isBlank())
            throw new ObjectNotValidException(new HashSet<String>(List.of("Введіть назву турніру")));
        else {
            if (quiz.getTitle().length() < 3 || quiz.getTitle().length() > 30)
                throw new ObjectNotValidException(new HashSet<String>(List.of("Назва турніру повинна містити від 3 до 30 символів")));
        }

        // validate description if exists
        if (quiz.getDescription() != null && !quiz.getDescription().isBlank()) {
            if (quiz.getDescription().length() < 5 || quiz.getDescription().length() > 80) {
                throw new ObjectNotValidException(new HashSet<String>(List.of("Опис турніру повинен містити від 5 до 80 символів")));
            }
        } else if (!quiz.isRoughDraft()) { // if isn't rough draft, description is required
            throw new ObjectNotValidException(new HashSet<String>(List.of("Введіть опис турніру")));
        }

        // validate questions
        String regex;
        if (quiz.getType() == Type.TOURNAMENT_VIDEO)
            regex = "((https?:)?//)?((www|m)\\.)?(youtube\\.com|youtu\\.be)(/([\\w\\-]+[?]v=|embed/|v/)?)([_\\w\\-]{11})([?|&]\\S+)?"; // youtube url
        else
            regex = "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)"; // any url

        Pattern pattern = Pattern.compile(regex);
        // if isn't rough draft, check amount of questions
        if (!quiz.isRoughDraft()) {
            double checkAmountQuestions = Math.log(quiz.getQuestions().size()) / Math.log(2);
            if (checkAmountQuestions % 1 != 0) {
                throw new ObjectNotValidException(new HashSet<String>(List.of("Невірна кількість питань. Кількість питань повинна дорівнювати числу в степені 2")));
            }
        }
        for (Question q : quiz.getQuestions()) {
            if (q.getTitle() != null && !q.getTitle().isBlank()) {
                if (q.getTitle().length() < 3 || q.getTitle().length() > 30)
                    throw new ObjectNotValidException(new HashSet<String>(List.of("Назва питання повинно містити від 3 до 80 символів")));
            } else
                throw new ObjectNotValidException(new HashSet<String>(List.of("Введіть назву питання")));

            if (q.getDescription() != null && !q.getDescription().isBlank()) {
                if (q.getDescription().length() < 5 || q.getDescription().length() > 80)
                    throw new ObjectNotValidException(new HashSet<String>(List.of("Опис питання повинно містити від 5 до 80 символів")));
            }

            if (q.getUrl() != null && !q.getUrl().isBlank()) {
                if (!pattern.matcher(q.getUrl()).matches())
                    throw new ObjectNotValidException(new HashSet<String>(List.of("Невірний формат посилання")));
            } else
                throw new ObjectNotValidException(new HashSet<String>(List.of("Введіть посилання на відео")));
        }
        //return true;
    }
}
