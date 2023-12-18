package com.pikuco.quizservice.entity;

public enum SortType {
    HIGHEST_RATED,
    NEWEST;

    public static SortType checkType(String type){
        for(SortType t :SortType.values()){
            if(t.name().equalsIgnoreCase(type)){
                return t;
            }
        }
        return null;
    }
}
