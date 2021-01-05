package com.wolox.training.repository;

import com.wolox.training.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Page<User> findAll(Pageable pageable);

    Optional<User> findTopByUsername(String username);

    @Query("select u from users u " +
            "where (:sequence = '' or lower(u.name) like lower(concat('%', :sequence,'%'))) " +
            "and (cast(:begin as date) is null or u.birthDate >= :begin) and (cast(:end as date) is null or u.birthDate <= :end)")
    Page<User> findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(
            @Param("begin") LocalDate begin,
            @Param("end") LocalDate end,
            @Param("sequence") String sequence,
            Pageable pageable
    );

}
