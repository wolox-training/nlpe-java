package com.wolox.training.models;

import com.google.common.base.Preconditions;
import com.wolox.training.exception.BookAlreadyOwnedException;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class represent the User entity
 */
@Entity(name = "users")
@ApiModel(description = "Users of application")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @NotNull
    @ApiModelProperty(notes = "Username: Is the identifier of the user")
    private String username;

    @Column(nullable = false)
    @NotNull
    @ApiModelProperty(notes = "Name: Is the first name of the user")
    private String name;

    @Column(nullable = false)
    @NotNull
    @ApiModelProperty(notes = "BirthDate: Is the born date of te user")
    private LocalDate birthDate;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_book",
            joinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "book_id"}))
    @ApiModelProperty(notes = "Books: Is the collection of favorite books of the user")
    private List<Book> books;

    public User() {
        this.books = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Preconditions.checkNotNull(username, "Username must be not null");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Preconditions.checkNotNull(name, "Name must be not null");
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = Preconditions.checkNotNull(birthDate, "BirthDate must be not null");
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    /**
     * This method allow add only a single {@link Book} to collection if not is already assigned to same user
     *
     * @param book: Book to add in list
     * @throws BookAlreadyOwnedException: When attempt add a book witch is already assigned to user
     */
    public void addBook(@NotNull Book book) throws BookAlreadyOwnedException {
        if (this.books.contains(book)) {
            throw new BookAlreadyOwnedException("The book " + book.getTitle() + " is already assigned to user " + this.id);
        }
        this.books.add(book);
    }


    /**
     * This method remove The {@link Book} allocated in the position received in the index param
     *
     * @param index: Index or position to remove book in the list
     */
    public void removeBook(@NotNull int index) {
        if (Objects.nonNull(this.books) && this.books.size() > index) {
            this.books.remove(index);
        }
    }

    /**
     * This method remove The {@link Book} equals to received from param
     *
     * @param book: Book to add in list
     */
    public void removeBook(@NotNull Book book) {
        this.books.remove(book);
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\":" + id +
                ",\"username\":\"" + username + "\"" +
                ",\"name\":\"" + name + "\"" +
                ",\"birthDate\":\"" + birthDate.toString() + "\"" +
                ",\"books\":" + books  +
                "}";
    }
}
