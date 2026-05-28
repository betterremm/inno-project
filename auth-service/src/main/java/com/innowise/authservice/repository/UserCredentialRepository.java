package com.innowise.authservice.repository;

import com.innowise.authservice.entity.UserCredentialEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredentialEntity, Long> {

    Optional<UserCredentialEntity> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByUserId(Long userId);
}
