package com.pikuco.quizservice.service;

import com.pikuco.quizservice.api.EvaluationAPIClient;
import com.pikuco.quizservice.api.UserAPIClient;
import com.pikuco.quizservice.api.WishlistAPIClient;
import com.pikuco.quizservice.dto.UserDto;
import com.pikuco.quizservice.dto.quiz.QuizCardDto;
import com.pikuco.quizservice.dto.quiz.QuizListDto;
import com.pikuco.quizservice.entity.*;
import com.pikuco.quizservice.exception.NonAuthorizedException;
import com.pikuco.quizservice.exception.ObjectNotFoundException;
import com.pikuco.quizservice.exception.ObjectNotValidException;
import com.pikuco.quizservice.mapper.QuizMapper;
import com.pikuco.quizservice.repository.QuizRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepository;
    private final MongoTemplate mongoTemplate;
    private final ApplicationContext context;
    private final EvaluationAPIClient evaluationAPIClient;
    private final UserAPIClient userAPIClient;
    private final WishlistAPIClient wishlistAPIClient;


    public List<Quiz> getQuizzes(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return quizRepository.findAll(pageable).getContent();
    }

    public QuizListDto getFilterSortQuizzes(String title,
                                            String type,
                                            String showRoughDraft,
                                            int numberQuestions,
                                            String creatorNickname,
                                            SortType sort,
                                            String lang,
                                            int pageNo,
                                            int pageSize) {
        List<Criteria> criteriaList = new LinkedList<>();
        if ("false".equals(showRoughDraft))
            criteriaList.add(Criteria.where("isRoughDraft").is(false));
        if (title != null && !title.isBlank()) {
            criteriaList.add(new Criteria().orOperator(Criteria.where("title").regex(title, "i"),
                    Criteria.where("translations.title").regex(title, "i")));
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
        if (creatorNickname != null) {
            criteriaList.add(Criteria.where("creator.nickname").regex(creatorNickname, "i"));
        }

        SkipOperation skipOperation = Aggregation.skip((long) (pageNo - 1) * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        SortOperation sortOperation = null;
        switch (sort) {
            case HIGHEST_RATED -> sortOperation = Aggregation.sort(Sort.by(Sort.Direction.ASC, ""));
            case NEWEST -> sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        MatchOperation matchOperation;
        CountOperation countOperation = Aggregation.count().as("quantity");
        Aggregation aggregation;
        Aggregation aggregationCount;
        if (!criteriaList.isEmpty()) {
            matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList));
            aggregation = Aggregation.newAggregation(matchOperation, sortOperation, skipOperation, limitOperation);
            aggregationCount = Aggregation.newAggregation(matchOperation, countOperation);
        } else {
            aggregation = Aggregation.newAggregation(sortOperation, skipOperation, limitOperation);
            aggregationCount = Aggregation.newAggregation(countOperation);
        }
        AggregationResults<Quiz> results = mongoTemplate.aggregate(aggregation, "quiz", Quiz.class);
        HashMap<String, Integer> resultsCountMap = mongoTemplate.aggregate(aggregationCount, "quiz", HashMap.class)
                .getUniqueMappedResult();

        List<QuizCardDto> quizzes = mapQuizListToQuizCardDtoList(lang, "uk", results.getMappedResults());
        int numPages;
        try {
            numPages = (int) Math.ceil((resultsCountMap.get("quantity") / (double) pageSize));
        } catch (NullPointerException ex) {
            numPages = 0;
        }

        return new QuizListDto(quizzes, numPages);
    }

    public QuizListDto getPopularQuizzes(String lang, int pageNo, int pageSize) {
        QuizResultsService quizResultsService = context.getBean(QuizResultsService.class);
        List<ObjectId> quizzesIds = quizResultsService.getPopularQuizzes(pageNo, pageSize);
        List<Quiz> unsortedQuizzes = quizRepository.findAllByIdIn(quizzesIds);
        Map<ObjectId, Quiz> quizMap = unsortedQuizzes.stream().collect(Collectors.toMap(Quiz::getId, Function.identity()));

        List<Quiz> quizzes = quizzesIds.stream().map(quizMap::get).toList();
        List<QuizCardDto> quizCards = mapQuizListToQuizCardDtoList(lang, "uk", quizzes);
        return new QuizListDto(quizCards, 1);
    }

    public QuizListDto getQuizzesByParticipantId(String authHeader, SortQuizResultsType sort, String lang, int PageNo, int pageSize) {
        try {
            ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
            Long participantId = Objects.requireNonNull(responseEntity.getBody()).id();
            QuizResultsService quizResultsService = context.getBean(QuizResultsService.class);
            List<ObjectId> quizIds = quizResultsService.getQuizzesIdByParticipantId(participantId, sort, PageNo, pageSize);
            List<Quiz> unsortedQuizzes = quizRepository.findAllByIdIn(quizIds);
            Map<ObjectId, Quiz> quizMap = unsortedQuizzes.stream().collect(Collectors.toMap(Quiz::getId, Function.identity()));

            List<Quiz> quizzes = quizIds.stream().map(quizMap::get).toList();
            List<QuizCardDto> quizCards = mapQuizListToQuizCardDtoList(lang, "uk", quizzes);
            int numQuizzes = quizResultsService.getNumQuizzesByParticipantId(participantId);
            int numPages;
            try {
                numPages = (int) Math.ceil((numQuizzes / (double) pageSize));
            } catch (NullPointerException ex) {
                numPages = 0;
            }

            return new QuizListDto(quizCards, numPages);
        } catch (FeignException e) {
            throw new NonAuthorizedException("Ви не авторизовані");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public QuizListDto getWishlistedQuizzesByUserId(String authHeader, String lang, int pageNo, int pageSize) {
        ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
        Long userId = Objects.requireNonNull(responseEntity.getBody()).id();

        ResponseEntity<List<String>> responseEntityQuizzes =
                wishlistAPIClient.getWishlistedQuizzesIdsByUserId(userId, pageNo, pageSize);
        List<String> quizzesIds = responseEntityQuizzes.getBody();

        assert quizzesIds != null;
        List<ObjectId> quizzesObjectIds = new ArrayList<>(quizzesIds.size());
        for (String quizId : quizzesIds)
            quizzesObjectIds.add(new ObjectId(quizId));

        List<Quiz> unsortedQuizzes = quizRepository.findAllByIdIn(quizzesObjectIds);
        Map<ObjectId, Quiz> quizMap = unsortedQuizzes.stream().collect(Collectors.toMap(Quiz::getId, Function.identity()));
        List<Quiz> quizzes = quizzesObjectIds.stream().map(quizMap::get).toList();

        List<QuizCardDto> quizCards = mapQuizListToQuizCardDtoList(lang, "uk", quizzes);
        ResponseEntity<Integer> responseEntityNumQuizzes = wishlistAPIClient.getNumWishlistedQuizzesByUserId(userId);
        int numPages;
        try {
            int numQuizzes = Objects.requireNonNull(responseEntityNumQuizzes.getBody());
            numPages = (int) Math.ceil((numQuizzes / (double) pageSize));
        } catch (NullPointerException ex) {
            numPages = 0;
        }

        return new QuizListDto(quizCards, numPages);
    }

    public int addQuiz(String authHeader, Quiz quiz) {
        ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(403))
            throw new NonAuthorizedException("Ви не авторизовані");
        UserDto user = Objects.requireNonNull(responseEntity.getBody());

//        if (quiz.getPseudoId() != 0)
//            throw new ObjectNotValidException(new HashSet<>(List.of("Ви не можете додати вікторину, яка вже була створена!")));

        // if quiz hasn't been created yet, then find pseudo id for it.
        // On other way, set quiz id to update information
        if (quiz.getPseudoId() == 0) {
            int greatestId = quizRepository.findFirstByOrderByPseudoIdDesc().orElseThrow().getPseudoId();
            quiz.setPseudoId(++greatestId);
        } else {
            quiz.setId(getQuizByPseudoId(quiz.getPseudoId()).getId());
        }
        if (!quiz.isRoughDraft()) {
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(quiz.getCreatedAt());
        }
        quiz.setCreator(new Creator(user.id(), user.nickname(), user.avatar()));
        validateQuiz(quiz); // validate quiz
        int pseudoId = quizRepository.save(quiz).getPseudoId();
        if (pseudoId == 0) throw new NullPointerException("Quiz was not added");
        return pseudoId;
    }

    public void changeQuiz(String authHeader, Quiz quiz) {
        Long creatorId = getUser(authHeader).id();

        Quiz quizToChange = quizRepository.findQuizByPseudoId(quiz.getPseudoId())
                .orElseThrow(() -> new ObjectNotFoundException(new HashSet<String>(List.of("Турнір не знайдено"))));

        if (!Objects.equals(quizToChange.getCreator().getCreator_id(), creatorId))
            throw new NonAuthorizedException("Ви не є творцем вікторини, тому не маєте права змінювати її");

        quiz.setId(quizToChange.getId());
        quiz.setCreator(quizToChange.getCreator());
        quiz.setCreatedAt(quizToChange.getCreatedAt());
        if (!quizToChange.isRoughDraft() && !quiz.isRoughDraft()) { // залишився не чернеткою
            quiz.setUpdatedAt(LocalDateTime.now());
        } else if (quizToChange.isRoughDraft() && !quiz.isRoughDraft()) { // був чернеткою -> став не чернеткою
            quiz.setCreatedAt(LocalDateTime.now());
            quiz.setUpdatedAt(quiz.getCreatedAt());
        } else if (!quizToChange.isRoughDraft()) { // був не чернеткою -> став чернеткою
            throw new ObjectNotValidException(new HashSet<>(List.of("Ви не можете замінити вже опублікований турнір на чернетку")));
        }

        validateQuiz(quiz);

        Query query = new Query().addCriteria(Criteria.where("_id").is(quiz.getId()));
        mongoTemplate.findAndReplace(query, quiz, "quiz");
    }

    public void deleteQuiz(String authHeader, int pseudoId) {
        Long creatorId = getUser(authHeader).id();

        Quiz quizToDelete = quizRepository.findQuizByPseudoId(pseudoId).orElseThrow(() ->
                new ObjectNotFoundException(new HashSet<>(List.of("Турнір не знайдено"))));
        if (Objects.equals(quizToDelete.getCreator().getCreator_id(), creatorId)) {
            // delete all evaluations of quiz
            evaluationAPIClient.deleteAllEvaluationsByQuizId(quizToDelete.getId().toString());
            // delete quiz from all wishlists
            wishlistAPIClient.deleteQuizWishlistsByQuizId(quizToDelete.getId().toString());
            // delete quiz results
            QuizResultsService quizResultsService = context.getBean(QuizResultsService.class);
            quizResultsService.deleteQuizResultsByQuizId(quizToDelete.getId());
            // delete quiz
            quizRepository.deleteById(quizToDelete.getId());
        }
        else throw new NonAuthorizedException("Ви не є творцем вікторини, тому не маєте права видаляти її");
    }

    public void addQuizTranslation(String authHeader, int pseudoId, QuizTranslation quizTranslation) {
        Long creatorId = getUser(authHeader).id();

        Quiz quiz = quizRepository.findQuizByPseudoId(pseudoId).orElseThrow(() ->
                new ObjectNotFoundException(new HashSet<>(List.of("Турнір не знайдено"))));

        // validate quiz translation
        validateQuizTranslation(quiz, quizTranslation, null);

        if (Objects.equals(quiz.getCreator().getCreator_id(), creatorId)) {
            if (quiz.getTranslations() == null) {
                List<QuizTranslation> quizTranslations = new ArrayList<>();
                quizTranslations.add(quizTranslation);
                quiz.setTranslations(quizTranslations);
            } else {
                quiz.getTranslations().add(quizTranslation);
            }
        } else {
            throw new NonAuthorizedException("Ви не є творцем вікторини, тому не маєте права видаляти її");
        }
        quizRepository.save(quiz);
    }

    public void editQuizTranslation(String authHeader, int pseudoId, QuizTranslation quizTranslation, String language) {
        Long creatorId = getUser(authHeader).id();

        Quiz quiz = quizRepository.findQuizByPseudoId(pseudoId).orElseThrow(() ->
                new ObjectNotFoundException(new HashSet<>(List.of("Турнір не знайдено"))));

        if (Objects.equals(quiz.getCreator().getCreator_id(), creatorId)) {
            if (quiz.getTranslations() != null) {
                for (int i = 0; i < quiz.getTranslations().size(); i++) {
                    if (quiz.getTranslations().get(i).getLanguage().equals(language)) {
                        validateQuizTranslation(quiz, quizTranslation, language);
                        quiz.getTranslations().set(i, quizTranslation);
                        quizRepository.save(quiz);
                        return;
                    }
                }
            } else {
                throw new ObjectNotFoundException(new HashSet<>(List.of("Такого перекладу не існує")));
            }
        } else {
            throw new NonAuthorizedException("Ви не є творцем вікторини, тому не маєте права видаляти її");
        }

        throw new ObjectNotFoundException(new HashSet<>(List.of("Перекладу на мову" + language + " не знайдено!")));
    }

    public Quiz getQuizByPseudoId(int quizId) {
        return quizRepository.findQuizByPseudoId(quizId)
                .orElseThrow(() -> new ObjectNotFoundException(new HashSet<>(List.of("Турнір не знайдено"))));
    }

    private void validateQuiz(Quiz quiz) {
        // validate creator's credentials
        if (quiz.getCreator().getCreator_id() == 0 || (quiz.getCreator().getNickname() == null || quiz.getCreator().getNickname().isBlank()))
            throw new ObjectNotValidException(new HashSet<>(List.of("Ви не авторизовані")));
        if (quiz.getCreator().getAvatar() == null || quiz.getCreator().getAvatar().isBlank())
            quiz.getCreator().setAvatar("default avatar");

        // validate title
        if (quiz.getTitle() == null || quiz.getTitle().isBlank())
            throw new ObjectNotValidException(new HashSet<>(List.of("Введіть назву турніру")));
        else {
            if (quiz.getTitle().length() < 3 || quiz.getTitle().length() > 30)
                throw new ObjectNotValidException(new HashSet<>(List.of("Назва турніру повинна містити від 3 до 30 символів")));
        }

        // validate description if exists
        if (quiz.getDescription() != null && !quiz.getDescription().isBlank()) {
            if (quiz.getDescription().length() < 5 || quiz.getDescription().length() > 80) {
                throw new ObjectNotValidException(new HashSet<>(List.of("Опис турніру повинен містити від 5 до 80 символів")));
            }
        } else if (!quiz.isRoughDraft()) { // if isn't rough draft, description is required
            throw new ObjectNotValidException(new HashSet<>(List.of("Введіть опис турніру")));
        }

        // validate questions
        if (!quiz.isRoughDraft() && quiz.getNumQuestions() != quiz.getQuestions().size())
            throw new ObjectNotValidException(new HashSet<>(List.of("Кількість питань не відповідає заявленій")));
        validateQuestions(quiz.getQuestions(), quiz.isRoughDraft(), quiz.getType(), quiz.getQuestions().size());
    }

    private void validateQuizTranslation(Quiz quiz, QuizTranslation quizTranslation, String language) {
        // check if there is quiz translation on this language
        // if attribute 'language' exists, then it means that existing quiz translation is validating
        if (quiz.getTranslations() != null && language == null) {
            if (quiz.getTranslations().stream().anyMatch((tr) -> tr.getLanguage().equals(quizTranslation.getLanguage()))) {
                throw new ObjectNotValidException(new HashSet<>(List.of(
                        "Переклад вікторини на мову " + quizTranslation.getLanguage() + " вже існує")));
            }
        } else if (language != null) {
            if (!quizTranslation.getLanguage().equals(language)) {
                throw new ObjectNotValidException(new HashSet<>(List.of(
                        "Перекладу вікторини на мову " + quizTranslation.getLanguage() + " не існує")));
            }
        }
        // validate title
        if (quizTranslation.getTitle() == null || quizTranslation.getTitle().isBlank())
            throw new ObjectNotValidException(new HashSet<>(List.of("Введіть назву турніру")));
        else {
            if (quizTranslation.getTitle().length() < 3 || quiz.getTitle().length() > 30)
                throw new ObjectNotValidException(new HashSet<>(List.of("Назва турніру повинна містити від 3 до 30 символів")));
        }

        // validate description if exists
        if (quizTranslation.getDescription() != null && !quizTranslation.getDescription().isBlank()) {
            if (quizTranslation.getDescription().length() < 5 || quizTranslation.getDescription().length() > 80) {
                throw new ObjectNotValidException(new HashSet<>(List.of("Опис турніру повинен містити від 5 до 80 символів")));
            }
        } else {
            throw new ObjectNotValidException(new HashSet<>(List.of("Введіть опис турніру")));
        }

        // validate questions
        if (quiz.getNumQuestions() != quizTranslation.getQuestions().size()) {
            throw new ObjectNotValidException(new HashSet<>(List.of("Кількість питань перекладу не відповідає кількості питань вікторини!")));
        }
        validateQuestions(quizTranslation.getQuestions(), false, quiz.getType(), quizTranslation.getQuestions().size());
    }

    private void validateQuestions(List<Question> questions, boolean isRoughDraft, Type type, int numQuestions) {
        String regex;
        if (type == Type.TOURNAMENT_VIDEO)
            regex = ".*(youtu\\.be\\/|v\\/|u\\/\\w\\/|embed\\/|watch\\?v=|\\&v=)([^#\\&\\?]*).*"; //"((https?:)?//)?((www|m)\\.)?(youtube\\.com|youtu\\.be)(/([\\w\\-]+[?]v=|embed/|v/)?)([_\\w\\-]{11})([?|&]\\S+)?"; // youtube url
        else
            regex = "[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_+.~#?&//=]*)"; // any url

        Pattern pattern = Pattern.compile(regex);

        // if isn't rough draft, check amount of questions
        if (!isRoughDraft) {
            double checkAmountQuestions = Math.round(Math.log(numQuestions / Math.log(2)));
            if (checkAmountQuestions % 1 != 0) {
                throw new ObjectNotValidException(new HashSet<>(List.of("Невірна кількість питань. Кількість питань повинна дорівнювати числу в степені 2")));
            }
        }

        for (Question q : questions) {
            if (q.getTitle() != null && !q.getTitle().isBlank()) {
                if (q.getTitle().length() < 3 || q.getTitle().length() > 30)
                    throw new ObjectNotValidException(new HashSet<>(List.of("Назва питання повинно містити від 3 до 30 символів")));
            } else
                throw new ObjectNotValidException(new HashSet<>(List.of("Введіть назву питання")));

            if (q.getDescription() != null && !q.getDescription().isBlank()) {
                if (q.getDescription().length() < 5 || q.getDescription().length() > 80)
                    throw new ObjectNotValidException(new HashSet<>(List.of("Опис питання повинно містити від 5 до 80 символів")));
            }

            if (q.getUrl() != null && !q.getUrl().isBlank()) {
                if (!pattern.matcher(q.getUrl()).matches())
                    throw new ObjectNotValidException(new HashSet<>(List.of("Невірний формат посилання")));
            } else
                throw new ObjectNotValidException(new HashSet<>(List.of("Введіть посилання на відео")));
        }
    }

    private @NotNull List<QuizCardDto> mapQuizListToQuizCardDtoList(String lang, String defaultLang, List<Quiz> results) {
        List<QuizCardDto> quizzes = new ArrayList<>(results.size());
        for (Quiz quiz : results) {
            if (quiz == null) continue;
            QuizCardDto quizCard;
            QuizCardDto quizCardOriginal = QuizMapper.mapToQuizCardDto(quiz);
            if (quiz.getTranslations() == null || quiz.getLanguage().equals(lang)) quizCard = quizCardOriginal;
                // Якщо є переклад на мову, вказану в параметрах - брати її
            else if (quiz.getTranslations().stream().anyMatch((tr) -> tr.getLanguage().equals(lang)))
                quizCard = findQuizCardByLang(quiz, lang, quizCardOriginal);
                // Якщо немає, то шукати переклад на мову за замовчуванням
            else if (quiz.getTranslations().stream().anyMatch((tr) -> tr.getLanguage().equals(defaultLang)))
                quizCard = findQuizCardByLang(quiz, defaultLang, quizCardOriginal); //!"uk".equals(lang) &&
                // Якщо немає перекладу і на мову за замовчуванням - брати мову оригіналу
            else quizCard = quizCardOriginal;

            quizzes.add(quizCard);
        }
        return quizzes;
    }

    private QuizCardDto findQuizCardByLang(Quiz quiz, String lang, QuizCardDto original) {
        QuizTranslation quizTranslation = quiz.getTranslations().stream()
                .filter((tr) -> tr.getLanguage().equals(lang))
                .findFirst()
                .orElse(QuizTranslation.builder()
                        .title(original.title())
                        .description(original.description())
                        .language(original.language())
                        .build());
        return new QuizCardDto(quizTranslation.getTitle(),
                quizTranslation.getDescription(),
                original.type(),
                original.creator(),
                original.pseudoId(),
                quizTranslation.getLanguage(),
                original.languages(),
                original.isRoughDraft());
    }

    public static String[] getLanguages(Quiz quiz) {
        String[] languages;
        if (quiz.getTranslations() != null) {
            languages = new String[quiz.getTranslations().size() + 1];
            languages[0] = quiz.getLanguage();
            for (int i = 0; i < quiz.getTranslations().size(); i++) {
                languages[i + 1] = quiz.getTranslations().get(i).getLanguage();
            }
        } else languages = new String[]{quiz.getLanguage()};
        return languages;
    }

    private UserDto getUser(String authHeader) {
        ResponseEntity<UserDto> responseEntity = userAPIClient.showUserByToken(authHeader);
        if (responseEntity.getStatusCode() == HttpStatusCode.valueOf(403))
            throw new NonAuthorizedException("Ви не авторизовані");
        return Objects.requireNonNull(responseEntity.getBody());
    }
}
