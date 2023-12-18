package com.pikuco.quizservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Creator {
    int creator_id;
    String nickname;
    String avatar;
}
