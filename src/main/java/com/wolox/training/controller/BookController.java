package com.wolox.training.controller;

import com.wolox.training.exception.BookIdMismatchException;
import com.wolox.training.exception.BookNotFoundException;
import com.wolox.training.models.Book;
import com.wolox.training.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/book")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    /**
     * This method return a collection of {@link Book}
     *
     * @return Collection of {@link Book}
     */
    @GetMapping
    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    /**
     * This method creates a {@link Book} with the following param:
     *
     * @param book: Data with structure like a Book to create
     * @return Created {@link Book} with attributes passed in the param
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Book create(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    /**
     * This method update a {@link Book} if exist with the following params:
     *
     * @param book: Data with structure like a Book to update
     * @param id: Id of book to update
     * @return Updated {@link Book}
     * @throws BookIdMismatchException: When book id is not equals with id param
     * @throws BookNotFoundException: When the book not found with id param passed
     */
    @PutMapping("{id}")
    public Book update(@RequestBody Book book, @PathVariable(name = "id") int id) throws BookIdMismatchException, BookNotFoundException {
        if (book.getId() != id) {
            throw new BookIdMismatchException("Id doesn't match");
        }

        bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book Not found"));
        return bookRepository.save(book);
    }

    /**
     * This method delete a {@link Book} if exist with the following params:
     *
     * @param id: Id of the book to delete
     * @throws BookNotFoundException: When the book not found with id param passed
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable(name = "id") int id) throws BookNotFoundException {
        bookRepository.findById(id).orElseThrow(() -> new BookNotFoundException("Book Not found"));
        bookRepository.deleteById(id);
    }

}
