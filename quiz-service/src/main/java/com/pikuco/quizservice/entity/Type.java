package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Type {
    TOURNAMENT_VIDEO("Tournament - Video", "TOURNAMENT_VIDEO"),
    TOURNAMENT_PICTURE("Tournament - Picture", "TOURNAMENT_PICTURE");

    private final String name;
    private final String value;
}
