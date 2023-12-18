package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Type {
    TOURNAMENT_VIDEO("Tournament - Video"),
    TOURNAMENT_PICTURE("Tournament - Picture");

    private final String name;
}
