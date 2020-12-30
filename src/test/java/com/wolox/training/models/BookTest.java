package com.wolox.training.models;

import com.wolox.training.repository.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
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

    @AfterEach
    public void afterEach() {
        entityManager.clear();
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
    public void givenYearAndGenreAndPublisher_whenFindBooks_thenReturnFilteredList() {
        String commonPublisher = "common publisher";
        String commonYear = "2017";
        String commonGenre = "Terror";

        Book book1 = new Book(commonGenre, "author1", "image1", "title1", "subtitle1", commonPublisher, commonYear, 100, "001");
        Book book2 = new Book(commonGenre, "author2", "image2", "title2", "subtitle2", "publish2", "2020", 200, "002");
        Book book3 = new Book(commonGenre, "author3", "image3", "title3", "subtitle3", commonPublisher, commonYear, 300, "003");
        Book book4 = new Book(commonGenre, "author4", "image4", "title4", "subtitle4", commonPublisher, "2012", 400, "004");
        Book book5 = new Book(commonGenre, "author5", "image5", "title5", "subtitle5", commonPublisher, commonYear, 500, "005");
        Book book6 = new Book(commonGenre, "author6", "image6", "title6", "subtitle6", "publish6", "2015", 600, "006");

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.persist(book4);
        entityManager.persist(book5);
        entityManager.persist(book6);
        entityManager.flush();

        List<Book> expectedList = Arrays.asList(book1, book3, book5);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(commonPublisher, commonGenre, commonYear)).isEqualTo(expectedList);
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
