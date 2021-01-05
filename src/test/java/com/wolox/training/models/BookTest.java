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
import java.util.Collections;
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
        Book book4 = new Book("Genre4", "author4", "image4", "title4", "subtitle4", commonPublisher, commonYear, 400, "004");
        Book book5 = new Book(commonGenre, "author5", "image5", "title5", "subtitle5", commonPublisher, commonYear, 500, "005");
        Book book6 = new Book(commonGenre, "author6", "image6", "title6", "subtitle6", "publish6", commonYear, 600, "006");
        Book book7 = new Book(commonGenre, "author6", "image6", "title6", "subtitle6", commonPublisher, "2013", 600, "006");

        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.persist(book4);
        entityManager.persist(book5);
        entityManager.persist(book6);
        entityManager.persist(book7);
        entityManager.flush();

        List<Book> expectedList = Arrays.asList(book1, book3, book5);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(commonPublisher, commonGenre, commonYear)).isEqualTo(expectedList);

        List<Book> withoutPublisher = Arrays.asList(book1, book3, book5, book6);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(null, commonGenre, commonYear)).isEqualTo(withoutPublisher);

        List<Book> withoutGenre = Arrays.asList(book1, book3, book4, book5);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(commonPublisher, null, commonYear)).isEqualTo(withoutGenre);

        List<Book> withoutYear = Arrays.asList(book1, book3, book5, book7);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(commonPublisher, commonGenre, null)).isEqualTo(withoutYear);

        List<Book> all = Arrays.asList(book1, book2, book3, book4, book5, book6, book7);
        assertThat(bookRepository.findAllByPublisherAndGenreAndYear(null, null, null)).isEqualTo(all);
    }

    @Test
    public void givenBookList_whenGetAllWithFilter_thenReturnFilteredList() {
        Book b = new Book("terror", "author1", "image1", "title1", "subtitle3", "publisher2", "2011", 150, "0001112");
        Book b2 = new Book("drama", "author2", "image3", "title1", "subtitle3", "publisher2", "2011", 250, "0001113");
        Book b3 = new Book("science", "author3", "image2", "title2", "subtitle3", "publisher2", "2020", 350, "0001114");
        Book b4 = new Book("kids", "author1", "image1", "title2", "subtitle3", "publisher2", "2020", 250, "0001114");
        Book b5 = new Book("drama", "author2", "image2", "title1", "subtitle1", "publisher3", "2015", 100, "0001113");
        Book b6 = new Book("terror", "author3", "image1", "title3", "subtitle2", "publisher3", "2016", 10, "0001112");
        Book b7 = new Book("science", "author3", "image3", "title1", "subtitle2", "publisher3", "2016", 1500, "0001111");

        entityManager.persist(b);
        entityManager.persist(b2);
        entityManager.persist(b3);
        entityManager.persist(b4);
        entityManager.persist(b5);
        entityManager.persist(b6);  
        entityManager.persist(b7);
        entityManager.flush();

        List<Book> id = Collections.singletonList(b);
        List<Book> result = bookRepository.findAll(b.getId(), null, null, null, null, 0, null, null, null, null);
        assertThat(result).isEqualTo(id);

        List<Book> genre = Arrays.asList(b, b6);
        List<Book> result2 = bookRepository.findAll(null, null, b.getGenre(), null, null, 0, null, null, null, null);
        assertThat(result2).isEqualTo(genre);

        List<Book> author = Arrays.asList(b3, b6, b7);
        List<Book> result3 = bookRepository.findAll(null, b3.getAuthor(), null, null, null, 0, null, null, null, null);
        assertThat(result3).isEqualTo(author);

        List<Book> image = Arrays.asList(b3, b5);
        List<Book> result4 = bookRepository.findAll(null, null, null, b5.getImage(), null, 0, null, null, null, null);
        assertThat(result4).isEqualTo(image);

        List<Book> isbn = Arrays.asList(b3, b4);
        List<Book> result5 = bookRepository.findAll(null, null, null, null, b4.getIsbn(), 0, null, null, null, null);
        assertThat(result5).isEqualTo(isbn);

        List<Book> pages = Collections.singletonList(b7);
        List<Book> result6 = bookRepository.findAll(null, null, null, null, null, b7.getPages(), null, null, null, null);
        assertThat(result6).isEqualTo(pages);

        List<Book> publisher = Arrays.asList(b5, b6, b7);
        List<Book> result7 = bookRepository.findAll(null, null, null, null, null, 0, b7.getPublisher(), null, null, null);
        assertThat(result7).isEqualTo(publisher);

        List<Book> subtitle = Arrays.asList(b, b2, b3, b4);
        List<Book> result8 = bookRepository.findAll(null, null, null, null, null, 0, null, b3.getSubTitle(), null, null);
        assertThat(result8).isEqualTo(subtitle);

        List<Book> title = Arrays.asList(b, b2, b5, b7);
        List<Book> result9 = bookRepository.findAll(null, null, null, null, null, 0, null, null, b.getTitle(), null);
        assertThat(result9).isEqualTo(title);

        List<Book> year = Arrays.asList(b, b2);
        List<Book> result10 = bookRepository.findAll(null, null, null, null, null, 0, null, null, null, b2.getYear());
        assertThat(result10).isEqualTo(year);

        List<Book> all = Arrays.asList(b, b2, b3, b4, b5, b6, b7);
        List<Book> result11 = bookRepository.findAll(null, null, null, null, null, 0, null, null, null, null);
        assertThat(result11).isEqualTo(all);

        List<Book> result12 = bookRepository.findAll();
        assertThat(result12).isEqualTo(all);
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
