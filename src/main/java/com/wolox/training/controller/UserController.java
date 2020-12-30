package com.wolox.training.controller;

import com.wolox.training.exception.BookAlreadyOwnedException;
import com.wolox.training.exception.BookNotFoundException;
import com.wolox.training.exception.ErrorHandler;
import com.wolox.training.exception.UserIdMismatchException;
import com.wolox.training.exception.UserNotFoundException;
import com.wolox.training.models.Book;
import com.wolox.training.models.User;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.repository.UserRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping(value = "api/user", produces = MediaType.APPLICATION_JSON_VALUE)
@Api
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder encoder;

    /**
     * This method return a collection of {@link User}
     *
     * @return Collection of {@link User}
     */
    @GetMapping
    @ApiOperation(value = "Return all users", response = User.class, responseContainer = "List")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully retrieves a users list")
    })
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * This method return current {@link User} authenticated
     *
     * @return The data of {@link User} authenticated
     */
    @GetMapping("session")
    @ResponseBody
    public User getAuthenticatedUser(Authentication authentication) {
        User u = new User();
        u.setUsername(authentication.getName());
        return u;
    }

    /**
     * This method creates a {@link User} with the following param:
     *
     * @param user: Data with structure like a User to create
     * @return Created {@link User} with attributes passed in the param
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a User", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 201, message = "Successfully retrieves then recently created user"),
            @ApiResponse(code = 400, message = "The Body received not has all required values", response = ErrorHandler.Response.class)
    })
    public User create(@ApiParam(value = "User to create", required = true) @RequestBody User user) {
        if (Objects.isNull(user.getPassword())) {
            throw new DataIntegrityViolationException("Password must be not null");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * This method update a {@link User} if exist with the following params:
     *
     * @param user: Data with structure like a User to update
     * @param id:   Id of user to update
     * @return Updated {@link User}
     * @throws UserIdMismatchException: When user id is not equals with id param
     * @throws UserNotFoundException:   When the user not found with id param passed
     */
    @PutMapping("{id}")
    @ApiOperation(value = "Giving an Id, update User", response = User.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully update user"),
            @ApiResponse(code = 404, message = "User Not found by id passed", response = ErrorHandler.Response.class),
            @ApiResponse(code = 400, message = "The Body received not has all required values", response = ErrorHandler.Response.class)
    })
    public User update(
            @ApiParam(value = "User to update", required = true) @RequestBody User user,
            @ApiParam(value = "Id of the user", required = true) @PathVariable(name = "id") Integer id
    ) throws UserIdMismatchException, UserNotFoundException {
        if (user.getId() != id) {
            throw new UserIdMismatchException("Id doesn't not match");
        }

        User u = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setPassword(u.getPassword());
        return userRepository.save(user);
    }

    /**
     * This method delete a {@link User} if exist with the following params:
     *
     * @param id: Id of the user to delete
     * @throws UserNotFoundException: When the user not found with id param passed
     */
    @DeleteMapping("{id}")
    @ApiOperation(value = "Giving an Id, delete user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully delete user"),
            @ApiResponse(code = 404, message = "User Not found by id passed", response = ErrorHandler.Response.class)
    })
    public void delete(@ApiParam(value = "Id of the user", required = true) @PathVariable(name = "id") Integer id) throws UserNotFoundException {

        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteById(id);
    }

    /**
     * This method add a {@link Book} in the books collection of the {@link User}
     *
     * @param userId: Id of the user to add the book
     * @param bookId: Id of book to add of the user
     * @return The {@link User} with the books collection updated with the new {@link Book}
     * @throws UserNotFoundException:     When the user not found with id param passed
     * @throws BookNotFoundException:     When the book not found with id param passed
     * @throws BookAlreadyOwnedException: When the book to be added already exists in the user's book list
     */
    @PatchMapping("{user_id}/book/{book_id}/add")
    @ApiOperation(value = "Giving an Id of user and Id of book, add a book to user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully add book to user"),
            @ApiResponse(code = 404, message = "User Not found by id passed", response = ErrorHandler.Response.class),
            @ApiResponse(code = 404, message = "Book Not found by id passed", response = ErrorHandler.Response.class)
    })
    public User addBookToUser(@ApiParam(value = "Id of the user", required = true) @PathVariable(name = "user_id") Integer userId,
                              @ApiParam(value = "Id of the user", required = true) @PathVariable(name = "book_id") Integer bookId) throws UserNotFoundException, BookNotFoundException, BookAlreadyOwnedException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));

        user.addBook(book);
        return userRepository.save(user);

    }

    /**
     * This method remove a {@link Book} in the books collection of the {@link User}
     *
     * @param userId: Id of the user to remove the book
     * @param bookId: Id of book to remove of the user
     * @return The {@link User} with the books collection updated without the {@link Book} removed
     * @throws UserNotFoundException: When the user not found with id param passed
     * @throws BookNotFoundException: When the book not found with id param passed
     */
    @PatchMapping("{user_id}/book/{book_id}/remove")
    @ApiOperation(value = "Giving an Id of user and Id of book, remove a book to user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully remove book to user"),
            @ApiResponse(code = 404, message = "User Not found by id passed", response = ErrorHandler.Response.class),
            @ApiResponse(code = 404, message = "Book Not found by id passed", response = ErrorHandler.Response.class)
    })
    public User removeBookToUser(@ApiParam(value = "Id of the user", required = true) @PathVariable(name = "user_id") Integer userId,
                                 @ApiParam(value = "Id of the user", required = true) @PathVariable(name = "book_id") Integer bookId) throws UserNotFoundException, BookNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));

        user.removeBook(book);
        return userRepository.save(user);
    }

    /**
     * This method retrieves a List of {@link User} following the next params
     *
     * @param begin:    Begin date as first date in the range search
     * @param end:      end date as last date in the range search
     * @param sequence: Characters sequence must contain the name of the user
     * @return List of {@link User} filtered with params passed
     */
    @GetMapping("search")
    public List<User> findUsers(
            @RequestParam(name = "begin") String begin,
            @RequestParam(name = "end") String end,
            @RequestParam(name = "sequence") String sequence
    ) {
        return userRepository.findAllByBirthDateBetweenAndNameIsContainingIgnoreCase(LocalDate.parse(begin), LocalDate.parse(end), sequence);
    }

}
