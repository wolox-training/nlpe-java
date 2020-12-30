package com.wolox.training.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wolox.training.models.Book;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BookDTO {

    private String isbn;
    private String title;
    private String subtitle;
    private List<String> publishers;
    private String publishDate;
    private int numberOfPages;
    private List<String> authors;

    @JsonIgnore
    private String imageUrl;

    public BookDTO(String isbn, JSONObject json) {
        this.isbn = isbn;
        this.title = json.getString("title");
        this.subtitle = json.getString("subtitle");
        this.publishers = parseArray("name", json.getJSONArray("publishers"));
        this.publishDate = json.getString("publish_date");
        this.numberOfPages = json.getInt("number_of_pages");
        this.authors = parseArray("name", json.getJSONArray("authors"));
        this.imageUrl = json.getString("url");
    }

    public BookDTO(Book book) {
        this.isbn = book.getIsbn();
        this.title = book.getTitle();
        this.subtitle = book.getSubTitle();
        this.publishers = Collections.singletonList(book.getPublisher());
        this.publishDate = book.getYear();
        this.numberOfPages = book.getPages();
        this.authors = Collections.singletonList(book.getAuthor());
        this.imageUrl = book.getImage();
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<String> getPublishers() {
        return publishers;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    private List<String> parseArray(String key, JSONArray array) {
        return StreamSupport
                .stream(array.spliterator(), false)
                .map(o -> new JSONObject(o.toString()).getString(key))
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "{" +
                "\"isbn\":\"" + isbn + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"subtitle\":\"" + subtitle + "\"," +
                "\"publishers\":[" + publishers.stream().map(p -> "\"" + p + "\"").collect(Collectors.joining()) + "]," +
                "\"publishDate\":\"" + publishDate + "\"," +
                "\"numberOfPages\":" + numberOfPages + "," +
                "\"authors\":[" + authors.stream().map(a -> "\"" + a + "\"").collect(Collectors.joining()) + "]" +
                "}";
    }
}
