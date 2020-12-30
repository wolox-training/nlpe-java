package com.wolox.training.models;

import com.wolox.training.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    public void init() {
        user = new User();
        user.setUsername("username");
        user.setBirthDate(LocalDate.now());
        user.setName("nestor");
        user.setPassword("123456");
    }

    @Test
    public void givenUser_whenCreateEntity_thenPersistSuccess() {
        entityManager.persist(user);
        entityManager.flush();

        Optional<User> opt = userRepository.findTopByUsername("username");

        assertThat(opt).isNotEmpty();
        assertThat(opt.get().getUsername()).isEqualTo(user.getUsername());
    }

    @Test
    public void givenUserWithUsernameNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> user.setUsername(null));
    }

    @Test
    public void givenUserWithBirthDateNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> user.setBirthDate(null));
    }

    @Test
    public void givenUserWithNameNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> user.setName(null));
    }

    @Test
    public void givenUserWithPasswordNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> user.setPassword(null));
    }

}
