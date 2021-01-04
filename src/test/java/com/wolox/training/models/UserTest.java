package com.wolox.training.models;

import com.wolox.training.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
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

    @AfterEach
    public void after() {
        entityManager.clear();
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
    public void givenDateRangeAndCharSequence_whenSearch_thenReturnFilteredList() {
        LocalDate begin = LocalDate.of(1960, 1, 1);
        LocalDate end = LocalDate.of(1992, 12, 31);
        String charSequence = "pEr";
        String username = "username";
        String password = "123456";

        User one = new User();
        one.setBirthDate(LocalDate.of(1993, 6, 11));
        one.setName("nestorperez");
        one.setUsername(username);
        one.setPassword(password);

        User two = new User();
        two.setBirthDate(LocalDate.of(1960, 2, 10));
        two.setName("hidalgoperez");
        two.setUsername(username);
        two.setPassword(password);

        User three = new User();
        three.setBirthDate(LocalDate.of(1990, 6, 24));
        three.setName("isabelperez");
        three.setUsername(username);
        three.setPassword(password);

        User four = new User();
        four.setBirthDate(LocalDate.of(1964, 3, 30));
        four.setName("isbethespina");
        four.setUsername(username);
        four.setPassword(password);

        User five = new User();
        five.setBirthDate(LocalDate.of(1991, 10, 11));
        five.setName("zaidabriceno");
        five.setUsername(username);
        five.setPassword(password);

        entityManager.persist(one);
        entityManager.persist(two);
        entityManager.persist(three);
        entityManager.persist(four);
        entityManager.persist(five);
        entityManager.flush();

        List<User> expectedList = Arrays.asList(two, three);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(begin, end, charSequence)).isEqualTo(expectedList);
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
