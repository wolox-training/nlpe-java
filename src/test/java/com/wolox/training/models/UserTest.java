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
import org.springframework.data.domain.PageRequest;
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
        PageRequest page = PageRequest.of(0, 10);
        LocalDate begin = LocalDate.of(1963, 1, 1);
        LocalDate end = LocalDate.of(1992, 12, 31);
        String charSequence = "pEr";
        String username = "username";
        String password = "123456";

        User nestor = new User();
        nestor.setBirthDate(LocalDate.of(1993, 6, 11));
        nestor.setName("nestorperez");
        nestor.setUsername(username);
        nestor.setPassword(password);

        User hidalgo = new User();
        hidalgo.setBirthDate(LocalDate.of(1960, 2, 10));
        hidalgo.setName("hidalgoperez");
        hidalgo.setUsername(username);
        hidalgo.setPassword(password);

        User isabel = new User();
        isabel.setBirthDate(LocalDate.of(1990, 6, 24));
        isabel.setName("isabelperez");
        isabel.setUsername(username);
        isabel.setPassword(password);

        User isbeth = new User();
        isbeth.setBirthDate(LocalDate.of(1964, 3, 30));
        isbeth.setName("isbethespina");
        isbeth.setUsername(username);
        isbeth.setPassword(password);

        User zaida = new User();
        zaida.setBirthDate(LocalDate.of(1991, 10, 11));
        zaida.setName("zaidabriceno");
        zaida.setUsername(username);
        zaida.setPassword(password);

        User cristian = new User();
        cristian.setBirthDate(LocalDate.of(1970, 6, 30));
        cristian.setName("cristianperez");
        cristian.setUsername(username);
        cristian.setPassword(password);

        entityManager.persist(nestor);
        entityManager.persist(hidalgo);
        entityManager.persist(isabel);
        entityManager.persist(isbeth);
        entityManager.persist(zaida);
        entityManager.persist(cristian);
        entityManager.flush();

        List<User> expectedList = Arrays.asList(isabel, cristian);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(begin, end, charSequence, page).getContent()).isEqualTo(expectedList);

        List<User> withoutBegin = Arrays.asList(hidalgo, isabel, cristian);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(null, end, charSequence, page).getContent()).isEqualTo(withoutBegin);

        List<User> withoutEnd = Arrays.asList(nestor, isabel, cristian);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(begin, null, charSequence, page).getContent()).isEqualTo(withoutEnd);

        List<User> withoutSequence = Arrays.asList(isabel, isbeth, zaida, cristian);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(begin, end, "", page).getContent()).isEqualTo(withoutSequence);

        List<User> all = Arrays.asList(nestor, hidalgo, isabel, isbeth, zaida, cristian);
        assertThat(userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(null, null, "", page).getContent()).isEqualTo(all);
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
