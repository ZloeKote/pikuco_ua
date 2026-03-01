package com.pikuco.recommendationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "quizStatistics")
public class QuizStatistics {
    
    @Id
    @Field(name = "_id")
    private String id;
    
    @Indexed(unique = true)
    @Field(name = "quiz_pseudo_id")
    private int quizPseudoId;
    
    @Field(name = "total_completions")
    private long totalCompletions;
    
    @Field(name = "total_likes")
    private long totalLikes;
    
    @Field(name = "total_dislikes")
    private long totalDislikes;
    
    @Field(name = "like_ratio")
    private double likeRatio;
    
    @Field(name = "popularity_score")
    private double popularityScore;
    
    @Field(name = "last_updated")
    private LocalDateTime lastUpdated;
}


