package com.wolox.training.controller;

import com.wolox.training.exception.BookAlreadyOwnedException;
import com.wolox.training.exception.BookNotFoundException;
import com.wolox.training.exception.UserIdMismatchException;
import com.wolox.training.exception.UserNotFoundException;
import com.wolox.training.models.Book;
import com.wolox.training.models.User;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    /**
     * This method return a collection of {@link User}
     *
     * @return Collection of {@link User}
     */
    @GetMapping
    public Iterable<User> getAll() {
        return userRepository.findAll();
    }

    /**
     * This method creates a {@link User} with the following param:
     *
     * @param user: Data with structure like a User to create
     * @return Created {@link User} with attributes passed in the param
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody User user) {
        return userRepository.save(user);
    }

    /**
     * This method update a {@link User} if exist with the following params:
     *
     * @param user: Data with structure like a User to update
     * @param id: Id of user to update
     * @return Updated {@link User}
     * @throws UserIdMismatchException: When user id is not equals with id param
     * @throws UserNotFoundException: When the user not found with id param passed
     */
    @PutMapping("{id}")
    public User update(@RequestBody User user, @PathVariable(name = "id") Integer id) throws UserIdMismatchException, UserNotFoundException {
        if (user.getId() != id) {
            throw new UserIdMismatchException("Id doesn't not match");
        }

        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        return userRepository.save(user);
    }

    /**
     * This method delete a {@link User} if exist with the following params:
     *
     * @param id: Id of the user to delete
     * @throws UserNotFoundException: When the user not found with id param passed
     */
    @DeleteMapping("{id}")
    public void delete(@PathVariable(name = "id") Integer id) throws UserNotFoundException {

        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
        userRepository.deleteById(id);
    }

    /**
     * This method add a {@link Book} in the books collection of the {@link User}
     *
     * @param userId: Id of the user to add the book
     * @param bookId: Id of book to add of the user
     * @return The {@link User} with the books collection updated with the new {@link Book}
     * @throws UserNotFoundException: When the user not found with id param passed
     * @throws BookNotFoundException: When the book not found with id param passed
     * @throws BookAlreadyOwnedException: When the book to be added already exists in the user's book list
     */
    @PatchMapping("{user_id}/book/{book_id}/add")
    public User addBookToUser(@PathVariable(name = "user_id") Integer userId, @PathVariable(name = "book_id") Integer bookId) throws UserNotFoundException, BookNotFoundException, BookAlreadyOwnedException {
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
    public User removeBookToUser(@PathVariable(name = "user_id") Integer userId, @PathVariable(name = "book_id") Integer bookId) throws UserNotFoundException, BookNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User Not found"));
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new BookNotFoundException("Book Not Found"));

        user.removeBook(book);
        return userRepository.save(user);
    }

}
