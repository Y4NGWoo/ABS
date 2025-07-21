// src/main/java/com/abs/abs/domain/UserM.java
package com.abs.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_m")                       // 테이블명 user_m
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_NO")
    private Long userNo;                       // PK 컬럼 USER_NO

    @Column(name = "USER_EMAIL", nullable = false, unique = true)
    private String userEmail;                  // 로그인용 이메일

    @Column(name = "USER_PWD", nullable = false)
    private String userPwd;                    // 암호화된 비밀번호

    @Column(name = "USER_NAME", length = 50)
    private String userName;                   // 닉네임

    @Column(name = "REG_DTM", nullable = false, updatable = false)
    private LocalDateTime regDtm;              // 가입 일시
}
