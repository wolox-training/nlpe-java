package com.wolox.training.repository;

import com.wolox.training.models.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {

    @Query("select b from Book b " +
            "where (:id is null or b.id = :id) " +
            "and (:author is null or b.author = :author) " +
            "and (:genre is null or b.genre = :genre) " +
            "and (:image is null or b.image = :image) " +
            "and (:isbn is null or b.isbn = :isbn) " +
            "and (:pages <= 0 or b.pages = :pages) " +
            "and (:publisher is null or b.publisher = :publisher) " +
            "and (:subtitle is null or b.subTitle = :subtitle) " +
            "and (:title is null or b.title = :title) " +
            "and (:year is null or b.year = :year)")
    List<Book> findAll(
            @Param("id") Integer id,
            @Param("author") String author,
            @Param("genre") String genre,
            @Param("image") String image,
            @Param("isbn") String isbn,
            @Param("pages") Integer pages,
            @Param("publisher") String publisher,
            @Param("subtitle") String subtitle,
            @Param("title") String title,
            @Param("year") String year
            );

    Optional<Book> findTopByAuthor(String author);

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where (:publisher is null or b.publisher = :publisher) " +
            "and (:year is null or b.year = :year ) " +
            "and (:genre is null or b.genre = :genre)")
    List<Book> findAllByPublisherAndGenreAndYear(
            @Param("publisher") String publisher,
            @Param("genre" ) String genre,
            @Param("year" ) String year
    );
}
