# Recommendation Service

## Overview

The Recommendation Service is a Spring Boot microservice that provides personalized quiz recommendations using fuzzy logic inference. It's part of the Pikuco quiz platform and implements a sophisticated recommendation algorithm based on user behavior analysis and content similarity.

## Architecture

### Technology Stack

- **Framework**: Spring Boot 3.1.5
- **Language**: Java 21
- **Database**: MongoDB (for quiz statistics)
- **Caching**: Spring Cache (in-memory)
- **Service Discovery**: Netflix Eureka
- **Inter-service Communication**: OpenFeign
- **Build Tool**: Maven

### Core Components

1. **Fuzzy Logic Engine**
   - Membership Functions (Triangular & Trapezoidal)
   - Fuzzy Variables (6 inputs, 1 output)
   - Fuzzy Rules (30 rules)
   - Inference Engine (Mamdani method with centroid defuzzification)

2. **Metric Calculators**
   - TagSimilarityCalculator: Weighted Jaccard similarity
   - PopularityCalculator: Quiz completion counts
   - LikeRatioCalculator: Bayesian average with confidence weighting
   - TemporalRelevanceCalculator: Exponential decay for newness
   - CreatorAffinityCalculator: User-creator interaction tracking
   - LengthMatchCalculator: Ratio-based length preference matching

3. **Services**
   - UserPreferenceService: Builds user preference profiles (cached 15 min)
   - QuizStatisticsService: Updates quiz statistics hourly
   - RecommendationService: Core recommendation logic

## Fuzzy Logic Model

### Input Variables (Normalized 0-1)

1. **TagSimilarity**: How well quiz tags match user preferences
   - NO_MATCH, WEAK_MATCH, MODERATE_MATCH, STRONG_MATCH

2. **QuizPopularity**: Number of completions (raw: 0-200+)
   - UNPOPULAR, MODERATE, POPULAR, VIRAL

3. **LikeRatio**: Percentage of likes using Bayesian averaging (raw: 0-100%)
   - DISLIKED, NEUTRAL, LIKED, LOVED

4. **TemporalRelevance**: Quiz age with exponential decay
   - NEW, RECENT, OLD, OUTDATED

5. **CreatorAffinity**: User's interaction history with creator (raw: 0-100%)
   - NO_HISTORY, SOME_INTERACTION, REGULAR_USER, FAN

6. **LengthMatch**: Quiz length vs user preference (raw: -1 to +1 ratio)
   - TOO_SHORT, PERFECT_MATCH, TOO_LONG

### Output Variable

**RecommendationScore** (0-100): Final recommendation score
- VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH

### Example Rules

```
Rule 1: IF TagSimilarity=STRONG_MATCH AND LikeRatio=LOVED 
        THEN RecommendationScore=VERY_HIGH (weight: 1.0)

Rule 2: IF TagSimilarity=MODERATE_MATCH AND QuizPopularity=POPULAR AND TemporalRelevance=NEW
        THEN RecommendationScore=HIGH (weight: 0.85)

Rule 3: IF CreatorAffinity=FAN AND TagSimilarity>=WEAK_MATCH
        THEN RecommendationScore=HIGH (weight: 0.8)
```

## API Endpoints

### GET /api/v1/recommendations/for-you

Get personalized quiz recommendations for authenticated user.

**Headers:**
- `X-User-Id` (required): User ID for authentication

**Query Parameters:**
- `limit` (optional, default: 10): Number of recommendations (1-100)

**Response:**
```json
[
  {
    "quiz": {
      "pseudoId": 123,
      "title": "Quiz Title",
      "description": "Description",
      "type": "TOURNAMENT_VIDEO",
      "tags": ["tag1", "tag2"],
      ...
    },
    "score": 85.5
  }
]
```

### GET /api/v1/recommendations/similar/{quizPseudoId}

Get quizzes similar to the specified quiz.

**Path Parameters:**
- `quizPseudoId` (required): Quiz pseudo ID

**Query Parameters:**
- `limit` (optional, default: 6): Number of similar quizzes (1-50)

**Response:** Same format as above

## Configuration

### application.properties

```properties
# Service Configuration
spring.application.name=RECOMMENDATION-SERVICE
server.port=8090

# MongoDB
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=recommendation_db

# Caching
spring.cache.type=simple
spring.cache.cache-names=userPreferences,recommendations,quizStatistics

# Bayesian Priors (tunable)
recommendation.prior.likes=5.0
recommendation.prior.dislikes=5.0

# Feign Clients
feign.client.config.default.connectTimeout=5000
feign.client.config.default.readTimeout=5000
```

## Key Algorithms

### 1. Bayesian Average for Like Ratio

Prevents bias from small sample sizes:

```
finalScore = (confidence × actualRatio) + ((1 - confidence) × bayesianRatio)
where confidence = min(1.0, totalVotes / 50.0)
```

**Examples:**
- Quiz with 1 like, 0 dislikes = ~55% (pulled toward 50% prior)
- Quiz with 100 likes, 0 dislikes = ~98% (actual ratio dominates)

### 2. Exponential Decay for Temporal Relevance

Human perception of "newness" decays exponentially:

```
perceivedAge = 1.0 - exp(-daysOld / TAU)
where TAU = 30 days
```

### 3. Ratio-Based Length Matching

More intuitive than logarithmic:

```
ratio = (quizQuestions - userAvgQuestions) / userAvgQuestions
clamped to [-1, +1]
```

- -1 = half as long
- 0 = perfect match
- +1 = twice as long

## Scheduled Tasks

### Quiz Statistics Update

**Schedule**: Every hour (cron: `0 0 * * * *`)

**Process**:
1. Fetch all active quizzes
2. For each quiz:
   - Count completions from quiz-service
   - Count likes/dislikes from evaluation-service
   - Calculate metrics
3. Batch save to MongoDB

## Caching Strategy

1. **User Preferences** (15 minutes TTL)
   - Reduces load on evaluation, wishlist, and quiz services
   - Rebuilds automatically after TTL expires

2. **Recommendations** (5 minutes TTL)
   - Keyed by `userId_limit`
   - Provides fast response times

3. **Quiz Statistics** (persistent)
   - Updated hourly
   - Reduces real-time aggregation overhead

## Performance Considerations

- **Input Normalization**: All inputs normalized to [0,1] prevents scale domination
- **Membership Function Overlaps**: Increased overlaps (25-40%) ensure smooth transitions
- **Adaptive Defuzzification**: 200-step centroid calculation for precision
- **Fallback Rules**: Low-weight rules ensure stable output even with edge cases
- **Batch Operations**: Statistics updated in batch for efficiency

## Testing

### Manual Testing Steps

1. Start all required services:
   ```bash
   # Service Registry
   cd service-registry && mvn spring-boot:run
   
   # Config Server
   cd config-server && mvn spring-boot:run
   
   # Other services...
   cd quiz-service && mvn spring-boot:run
   cd user-service && mvn spring-boot:run
   cd evaluation-service && mvn spring-boot:run
   cd wishlist-service && mvn spring-boot:run
   
   # Recommendation Service
   cd recommendation-service && mvn spring-boot:run
   
   # API Gateway
   cd api-gateway && mvn spring-boot:run
   ```

2. Test via Postman:
   ```
   GET http://localhost:9091/api/v1/recommendations/for-you?limit=10
   Headers: X-User-Id: 1
   ```

3. Verify fuzzy logic:
   - Check logs for input/output values
   - Ensure scores are in reasonable ranges (0-100)
   - Confirm recommendations match user preferences

## Thesis Documentation

### Key Contributions

1. **From-Scratch Fuzzy Logic Implementation**: Complete implementation without external fuzzy libraries
2. **Mathematician-Approved Improvements**:
   - Input normalization layer
   - Bayesian averaging for like ratios
   - Exponential decay for temporal relevance
   - Increased membership function overlaps
3. **Microservices Integration**: Seamless integration with existing platform architecture
4. **Production-Ready**: Caching, error handling, scheduled tasks

### Metrics to Track

- Response time for recommendations
- Cache hit rates
- Recommendation quality (user engagement)
- System load impact
- Fuzzy inference time

## Future Enhancements

1. **Redis Caching**: For distributed caching in production
2. **A/B Testing**: Compare fuzzy logic vs collaborative filtering
3. **User Feedback Loop**: Incorporate explicit feedback on recommendations
4. **Advanced Features**:
   - Cold start handling for new users
   - Diversity in recommendations
   - Serendipity factor for discovery
   - Real-time updates instead of hourly batch

## License

Part of the Pikuco UA platform - Bachelor's/Master's thesis project.


