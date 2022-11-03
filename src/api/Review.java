package api;

import java.io.IOException;

public class Review {
    private String username, review;
    private Book book;

    public Review(Book _book, String _username, String _review) {

        Utilities.throwExceptionIfNull(_book, _username);

        this.book = _book;
        this.username = _username;
        this.review = _review;
    }

    public Review(Book _book, String _username) throws IOException {
        this(_book, _username, null);
    }

    public Book getBook() {
        return this.book;
    }

    public String getUsername() {
        return this.username;
    }

    public String getReview() {
        return this.review;
    }

    public void setBook(Book _book) {
        this.book = _book;
    }

    public void setUsername(String _username) {
        this.username = _username;
    }

    public void setReview(String _review) {
        this.review = _review;
    }
}
