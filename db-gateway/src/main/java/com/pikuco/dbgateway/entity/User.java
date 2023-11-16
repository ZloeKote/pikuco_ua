package com.pikuco.dbgateway.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int id;
    @Column(name = "nickname", nullable = false, unique = true)
    private String nickname;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name="email", nullable = false, unique = true)
    private String email;
    @Column(name="role_id", nullable = false)
    private int roleId;
    @Column(name="avatar", nullable = false)
    private String avatar;
    @Column(name="user_description")
    private String description;
    @Column(name="birthday")
    private Date birthday;
    @Column(name="creation_date")
    private LocalDateTime creationDate;

}
