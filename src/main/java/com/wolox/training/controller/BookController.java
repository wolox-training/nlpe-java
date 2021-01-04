package com.wolox.training.controller;

import com.wolox.training.dto.BookDTO;
import com.wolox.training.exception.BookIdMismatchException;
import com.wolox.training.exception.BookNotFoundException;
import com.wolox.training.models.Book;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.service.OpenLibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "api/book")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private OpenLibraryService openLibraryService;

    /**
     * This method return a list of {@link Book} following the next Optional params:
     *
     * @param id
     * @param author
     * @param genre
     * @param image
     * @param isbn
     * @param pages
     * @param publisher
     * @param subtitle
     * @param title
     * @param year
     *
     * @return The List of {@link Book} filtered with Optional parameters passed
     */
    @GetMapping
    public Iterable<Book> findAll(
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String image,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String year
    ) {
        return bookRepository.findAll(
                id,
                author,
                genre,
                image,
                isbn,
                (Objects.nonNull(pages) && pages > 0) ? pages : 0,
                publisher,
                subtitle,
                title,
                year
        );
    }

    /**
     * This method find a {@link Book} by isbn code passed as param and follow the next performance:
     * - If Book exist in database, retrieves the book
     * - If book don't exist in database, search in external api, create and retrieves the book
     *
     * @param isbn: The code of Book
     * @return The {@link Book} with Isbn passed of param
     * @throws IOException:           When has errors with the external api
     * @throws BookNotFoundException: When book not found neither database nor external api
     */
    @GetMapping("{isbn}")
    public ResponseEntity<BookDTO> findBookByIsbn(@PathVariable(name = "isbn") String isbn) throws IOException, BookNotFoundException {
        Optional<Book> optionalBook = bookRepository.findByIsbn(isbn);
        if (optionalBook.isPresent()) {
            return ResponseEntity.ok(new BookDTO(optionalBook.get()));
        }

        BookDTO dto = openLibraryService.bookInfo(isbn);

        Book book = new Book();
        book.setIsbn(dto.getIsbn());
        book.setYear(dto.getPublishDate());
        book.setTitle(dto.getTitle());
        book.setSubTitle(dto.getSubtitle());
        book.setAuthor(dto.getAuthors().get(0));
        book.setPublisher(dto.getPublishers().get(0));
        book.setPages(dto.getNumberOfPages());
        book.setImage(dto.getImageUrl());

        bookRepository.save(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
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
     * @param id:   Id of book to update
     * @return Updated {@link Book}
     * @throws BookIdMismatchException: When book id is not equals with id param
     * @throws BookNotFoundException:   When the book not found with id param passed
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

    /**
     * This method retrieves a List of {@link Book} following the next params.
     *
     * @param publisher: The publisher of Book for filter list
     * @param genre:     The genre of Book for filter list
     * @param year:      The year of Book for filter list
     * @return List of {@link Book} filtered with the params passed
     */
    @GetMapping("search")
    public List<Book> findBooks(
            @RequestParam(name = "publisher", required = false) String publisher,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "year", required = false) String year
    ) {
        return bookRepository.findAllByPublisherAndGenreAndYear(publisher, genre, year);
    }

}
