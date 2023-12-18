package com.pikuco.quizservice.entity;

public enum SortQuizResultsType {
    SCORE_ASC,
    SCORE_DESC,
    TITLE_ASC,
    TITLE_DESC,
    PLACE_ASC,
    PLACE_DESC,
    NEWEST,
    OLDEST;

    public static SortQuizResultsType checkType(String type){
        for(SortQuizResultsType t :SortQuizResultsType.values()){
            if(t.name().equalsIgnoreCase(type)){
                return t;
            }
        }
        return null;
    }
}
