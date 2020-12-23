package com.wolox.training.controller;

import com.google.gson.Gson;
import com.wolox.training.models.Book;
import com.wolox.training.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookRepository bookRepository;

    private final Gson json = new Gson();
    private final String PATH = "/api/book";
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

    @Test
    public void givenBooks_whenGetAll_thenReturnArray() throws Exception {
        List<Book> books = this.mockBooks();
        given(bookRepository.findAll()).willReturn(books);

        mvc.perform(get(PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(res -> assertEquals(json.toJson(books), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenBook_whenCreateBook_thenReturnBook() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.save(any())).willReturn(b);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json.toJson(b)))
                .andExpect(status().isCreated())
                .andExpect(res -> Assertions.assertEquals(new Gson().toJson(b), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenBook_whenCreateBook_thenThrowBodyException() throws Exception {
        Book b = this.mockBooks().get(0);
        given(bookRepository.save(any())).willThrow(DataIntegrityViolationException.class);

        mvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{" +
                        "        \"genre\": \"terror\"," +
                        "        \"subTitle\": \"some subTitle\"," +
                        "        \"publisher\": \"some publisher\"," +
                        "        \"year\": \"2020\"," +
                        "        \"pages\": 14," +
                        "        \"isbn\": \"ksidsndjs\"" +
                        "    }"))
                .andExpect(status().isBadRequest());
    }

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
                .andExpect(res -> Assertions.assertEquals(new Gson().toJson(b), res.getResponse().getContentAsString()));
    }

    @Test
    public void givenBookAndId_whenUpdateBook_thenReturnIdMissMatch() throws Exception {

        mvc.perform(put(PATH + "/" + 50)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenBookAndId_whenUpdateBook_thenReturnBookNotFound() throws Exception {
        given(bookRepository.findById(id)).willReturn(Optional.empty());

        mvc.perform(put(PATH + "/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isNotFound());
    }

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
