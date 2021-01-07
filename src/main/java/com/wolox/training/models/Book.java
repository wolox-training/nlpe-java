package com.wolox.training.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

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
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private int id;

    @Column
    @Getter
    @Setter
    private String genre;

    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String author;

    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String image;

    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String title;

    @Column(name = "sub_title", nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String subTitle;

    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String publisher;

    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String year;

    @Column(nullable = false)
    @NotNull
    @Getter
    private int pages;

    /**
     * This mean "International Standard Book Number" code
     */
    @Column(nullable = false)
    @NotNull
    @NonNull
    @Getter
    @Setter
    private String isbn;

    @ManyToMany(mappedBy = "books")
    @JsonIgnore
    private List<User> users;

    public Book(String genre, @NotNull String author, @NotNull String image, @NotNull String title, @NotNull String subTitle, @NotNull String publisher, @NotNull String year, @NotNull int pages, @NotNull String isbn) {
        this.genre = genre;
        this.author = author;
        this.image = image;
        this.title = title;
        this.subTitle = subTitle;
        this.publisher = publisher;
        this.year = year;
        this.pages = pages;
        this.isbn = isbn;
    }

    public void setPages(int pages) {
        Preconditions.checkArgument(pages > 0, "Pages must be not null");
        this.pages = pages;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id + "," +
                "\"genre\":\"" + genre + "\"," +
                "\"author\":\"" + author + "\"," +
                "\"image\":\"" + image + "\"," +
                "\"title\":\"" + title + "\"," +
                "\"subTitle\":\"" + subTitle + "\"," +
                "\"publisher\":\"" + publisher + "\"," +
                "\"year\":\"" + year + "\"," +
                "\"pages\":" + pages + "," +
                "\"isbn\":\"" + isbn + "\"" +
                "}";
    }
}
