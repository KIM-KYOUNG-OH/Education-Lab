package com.project.educationLab.domain.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Builder
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRole UserRole;

    @Enumerated(EnumType.STRING)
    private AuthProvider authProvider;

    private String refreshToken;
    private Timestamp createDate;
    private Timestamp updateDate;

    public User() {}

    public User(Long id, String username, String password, String email, UserRole UserRole, AuthProvider authProvider, String refreshToken, Timestamp createDate, Timestamp updateDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.UserRole = UserRole;
        this.authProvider = authProvider;
        this.refreshToken = refreshToken;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }
}
