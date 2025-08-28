package com.abs.repository;

import com.abs.domain.User;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> findByEmailWithDsl(String email);
}
