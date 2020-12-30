package com.wolox.training.controller;

import com.google.gson.Gson;
import com.wolox.training.dto.BookDTO;
import com.wolox.training.exception.BookNotFoundException;
import com.wolox.training.models.Book;
import com.wolox.training.repository.BookRepository;
import com.wolox.training.service.AuthService;
import com.wolox.training.service.OpenLibraryService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private OpenLibraryService openLibraryService;

    @MockBean
    private AuthService authService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final Gson json = new Gson();
    private final String PATH = "/api/book";
    private final String SPRING_USER = "spring";
    private final int id = 1;
    private final String body = "{" +
            "\"id\":" + id + "," +
            "\"genre\":\"Genre 1\"," +
            "\"author\":\"Author 1\"," +
            "\"image\":\"http://image-1.com\"," +
            "\"title\":\"title 1\"," +
            "\"subTitle\":\"subtitle 1\"," +
            "\"publisher\":\"publisher 1\"," +
            "\"year\":\"2001\"," +
            "\"pages\":26," +
            "\"isbn\":\"001122331\"" +
            "}";

    @BeforeEach
    public void init() {
        String SPRING_PASSWORD = "123456";
        UserDetails userDetails = User.builder().username(SPRING_USER).password(SPRING_PASSWORD).authorities(new ArrayList<>()).build();
        given(authService.loadUserByUsername(SPRING_USER)).willReturn(userDetails);
        given(passwordEncoder.matches(SPRING_PASSWORD, SPRING_PASSWORD)).willReturn(true);
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBooks_whenGetAll_thenReturnArray() throws Exception {
        List<Book> books = this.mockBooks();
        given(bookRepository.findAll()).willReturn(books);

        mvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(json.toJson(books), res.getResponse().getContentAsString()));
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenIsbn_whenGetBookByIsbn_thenReturnBook() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.findByIsbn(b.getIsbn())).willReturn(Optional.of(b));

        mvc.perform(get(PATH + "/" + b.getIsbn())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(new BookDTO(b).toString(), res.getResponse().getContentAsString()));
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenIsbn_whenGetBookByIsbn_thenCreateAndReturnBook() throws Exception {
        Book b = this.mockBooks().get(0);
        BookDTO dto = new BookDTO(b);
        given(bookRepository.findByIsbn(b.getIsbn())).willReturn(Optional.empty());
        given(openLibraryService.bookInfo(b.getIsbn())).willReturn(dto);
        given(bookRepository.save(b)).willReturn(b);

        mvc.perform(get(PATH + "/" + b.getIsbn())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(res -> assertEquals(dto.toString(), res.getResponse().getContentAsString()));
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenIsbn_whenGetBookByIsbn_thenReturnBookNotFound() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.findByIsbn(b.getIsbn())).willReturn(Optional.empty());
        given(openLibraryService.bookInfo(b.getIsbn())).willThrow(BookNotFoundException.class);

        mvc.perform(get(PATH + "/" + b.getIsbn())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void givenBook_whenCreateBook_thenReturnBook() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.save(any())).willReturn(b);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(b.toString()))
                .andExpect(status().isCreated())
                .andExpect(res -> Assertions.assertEquals(b.toString(), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenBook_whenCreateBook_thenThrowBodyException() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(b.toString()))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBookAndId_whenUpdateBook_thenReturnBook() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.findById(id)).willReturn(Optional.of(b));
        given(bookRepository.save(any())).willReturn(b);

        mvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(res -> Assertions.assertEquals(b.toString(), res.getResponse().getContentAsString()));
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBookAndId_whenUpdateBook_thenReturnIdMissMatch() throws Exception {

        mvc.perform(put(PATH + "/" + 50)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBookAndId_whenUpdateBook_thenReturnBookNotFound() throws Exception {
        given(bookRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBookId_whenDeleteBook_thenSuccess() throws Exception {

        Book b = this.mockBooks().get(0);
        given(bookRepository.findById(id)).willReturn(Optional.of(b));
        doNothing().when(bookRepository).deleteById(any());

        mvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @WithMockUser(value = SPRING_USER)
    @Test
    public void givenBookId_whenDeleteBook_thenReturnBookNotFound() throws Exception {

        given(bookRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(delete(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private List<Book> mockBooks() {
        return IntStream.range(1, 11).mapToObj(i -> {
            Book b = new Book();
            b.setGenre("Genre " + i);
            b.setAuthor("Author " + i);
            b.setImage("http://image-" + i + ".com");
            b.setIsbn("00112233" + i);
            b.setTitle("title " + i);
            b.setSubTitle("subtitle " + i);
            b.setYear("200" + i);
            b.setPublisher("publisher " + i);
            b.setPages(25 + i);
            return b;
        }).collect(Collectors.toList());
    }

}
