package com.wolox.training.repository;

import com.wolox.training.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findTopByUsername(String username);

}
