package com.abs.repository;

import com.abs.domain.QUser;
import com.abs.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final JPAQueryFactory query;

    @Override
    public Optional<User> findByEmailWithDsl(String email) {
        QUser u = QUser.user;
        User result = query
                .selectFrom(u)
                .where(u.userEmail.eq(email))
                .fetchOne();
        return Optional.ofNullable(result);
    }
}
