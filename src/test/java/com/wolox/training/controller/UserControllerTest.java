package com.wolox.training.controller;


import com.google.gson.Gson;
import com.wolox.training.models.User;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    private String PATH = "/api/user";
    private User user;
    private Gson json = new Gson();
    private int id = 1;

    @BeforeEach
    public void init() {
        user = new User();
        user.setName("name1");
        user.setUsername("username1");
        user.setBirthDate(LocalDate.now());
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
    public void givenUserAndId_whenUpdateUser_thenReturnUser() throws Exception {
        String body = "{" +
                "        \"id\": " + id + "," +
                "        \"username\": \"nlperez\"," +
                "        \"name\": \"nestor\"," +
                "        \"birthDate\": \"1193-06-11\"," +
                "        \"books\": []" +
                "    }";

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
    public void givenUserId_whenDeleteUser_thenDeleteSuccess() throws Exception {
        given(userRepository.findById(id)).willReturn(Optional.of(user));
        doNothing().when(bookRepository).deleteById(any());

        mvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private String usersToJsonArrayString(List<User> users) {
        return "[" + users.stream().map(User::toString).collect(Collectors.joining()) + "]";
    }

}
