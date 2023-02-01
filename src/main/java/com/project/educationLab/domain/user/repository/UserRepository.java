package com.project.educationLab.domain.user.repository;

import com.project.educationLab.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Transactional
    @Modifying
    @Query("UPDATE User u set u.refreshToken=:refreshToken WHERE u.username=:username")
    void updateRefreshTokenByUsername(@Param("username") String username, @Param("refreshToken") String refreshToken);
}
