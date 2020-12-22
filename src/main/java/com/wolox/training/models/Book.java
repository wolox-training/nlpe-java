package com.wolox.training.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * This class represent the Book Entity
 */
@Entity
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String genre;

    @Column(nullable = false)
    @NotNull
    private String author;

    @Column(nullable = false)
    @NotNull
    private String image;

    @Column(nullable = false)
    @NotNull
    private String title;

    @Column(name = "sub_title", nullable = false)
    @NotNull
    private String subTitle;

    @Column(nullable = false)
    @NotNull
    private String publisher;

    @Column(nullable = false)
    @NotNull
    private String year;

    @Column(nullable = false)
    @NotNull
    private int pages;

    /**
     * This mean "International Standard Book Number" code
     */
    @Column(nullable = false)
    @NotNull
    private String isbn;

    @ManyToMany(mappedBy = "books")
    @JsonIgnore
    private List<User> users;

    public Book() {
    }

    public int getId() {
        return id;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

}
