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

    Optional<Book> findTopByAuthor(String author);

    Optional<Book> findByIsbn(String isbn);

    @Query("select b from Book b where (:publisher is null or b.publisher = :publisher) " +
            "and (:year is null or b.year = :year ) " +
            "and (:genre is null or b.genre = :genre)")
    List<Book> findAllByPublisherAndGenreAndYear(
            @Param("publisher") String publisher,
            @Param("genre" )String genre,
            @Param("year" )String year);
}
