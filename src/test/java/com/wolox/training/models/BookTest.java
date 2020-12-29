package com.wolox.training.models;

import com.wolox.training.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class BookTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    public void init() {
        book = new Book();
        book.setAuthor("nestor");
        book.setImage("http://image.com");
        book.setIsbn("000111225588");
        book.setYear("2017");
        book.setTitle("title");
        book.setSubTitle("subtitle");
        book.setPublisher("publisher");
        book.setGenre("terror");
        book.setPages(100);
    }

    @Test
    public void givenBook_whenCreateEntity_thenPersistSuccess() {
        entityManager.persist(book);
        entityManager.flush();

        Optional<Book> opt = bookRepository.findTopByAuthor("nestor");
        assertThat(opt).isNotEmpty();
        assertThat(opt.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    public void givenBookWithGenreNull_whenCreateEntity_thenPersistSuccess() {
        book.setGenre(null);

        entityManager.persist(book);
        entityManager.flush();

        Optional<Book> opt = bookRepository.findTopByAuthor("nestor");
        assertThat(opt).isNotEmpty();
        assertThat(opt.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    public void givenBookWithAuthorNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setAuthor(null));
    }

    @Test
    public void givenBookWithImageNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setImage(null));
    }

    @Test
    public void givenBookWithIsbnNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setIsbn(null));
    }

    @Test
    public void givenBookWithPagesZero_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> book.setPages(0));
    }

    @Test
    public void givenBookWithPublisherNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setPublisher(null));
    }

    @Test
    public void givenBookWithTitleNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setTitle(null));
    }

    @Test
    public void givenBookWithSubtitleNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setSubTitle(null));
    }

    @Test
    public void givenBookWithYearNull_whenCreateEntity_thenConstraintError() {
        Assertions.assertThrows(NullPointerException.class, () -> book.setYear(null));
    }
}
