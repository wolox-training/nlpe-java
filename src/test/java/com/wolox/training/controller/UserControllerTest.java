package com.wolox.training.controller;


import com.wolox.training.models.Book;
import com.wolox.training.models.User;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookRepository bookRepository;

    private final String PATH = "/api/user";
    private User user;
    private Book book;
    private final int id = 1;
    private final int bookId = 1;
    private final String body = "{" +
            "        \"id\": " + id + "," +
            "        \"username\": \"nlperez\"," +
            "        \"name\": \"nestor\"," +
            "        \"birthDate\": \"1193-06-11\"," +
            "        \"books\": []" +
            "    }";

    @BeforeEach
    public void init() {
        user = new User();
        user.setName("name1");
        user.setUsername("username1");
        user.setBirthDate(LocalDate.now());

        book = new Book();
        book.setImage("http://image.com");
        book.setGenre("genre");
        book.setPages(15);
        book.setPublisher("publisher");
        book.setTitle("title");
        book.setSubTitle("subtitle");
        book.setYear("2020");
        book.setIsbn("000111223366");
        book.setAuthor("author");
    }

    @Test
    public void givenUsers_whenGetAll_thenReturnUserArray() throws Exception {

        List<User> users = Arrays.asList(user);
        given(userRepository.findAll()).willReturn(users);

        mvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(usersToJsonArrayString(users), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenUser_whenCreateUser_thenReturnUser() throws Exception {
        given(userRepository.save(any())).willReturn(user);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(user.toString()))
                .andExpect(status().isCreated())
                .andExpect(res -> assertEquals(user.toString(), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenUser_whenCreateUser_thenReturnBodyException() throws Exception {
        given(userRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(user.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserAndId_whenUpdateUser_thenReturnUser() throws Exception {

        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(userRepository.save(any())).willReturn(user);

        mvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(user.toString(), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenUserAndId_whenUpdateUser_thenReturnIdMissMatch() throws Exception {

        mvc.perform(put(PATH + "/" + 1000)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserAndId_whenUpdateUser_thenReturnUNotFound() throws Exception {

        given(userRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserId_whenDeleteUser_thenDeleteSuccess() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        doNothing().when(bookRepository).deleteById(any());

        mvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void givenUserId_whenDeleteUser_thenReturnUserNotFound() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserIdAndBookId_whenAddBookToUser_thenReturnUserWithBook() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(userRepository.save(user)).willReturn(user);

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(user.toString(), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenUserIdAndBookId_whenAddBookToUser_thenReturnBookAlreadyOwned() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(userRepository.save(user)).willReturn(user);
        user.addBook(book);

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenUserIdAndBookId_whenAddBookToUser_thenReturnUserNotFound() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserIdAndBookId_whenAddBookToUser_thenReturnBookNotFound() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserIdAndBookId_whenRemoveBookToUser_thenReturnUserWithoutBook() throws Exception {
        user.addBook(book);
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.of(book));
        given(userRepository.save(user)).willReturn(user);

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(user.toString(), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenUserIdAndBookId_whenRemoveBookToUser_thenReturnUserNotFound() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        given(bookRepository.findById(bookId)).willReturn(Optional.empty());

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenUserIdAndBookId_whenRemoveBookToUser_thenReturnBookNotFound() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(patch(PATH + "/" + id + "/book/" + bookId + "/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    private String usersToJsonArrayString(List<User> users) {
        return "[" + users.stream().map(User::toString).collect(Collectors.joining()) + "]";
    }

}
