package api;

public class Review {
    private String username, review;
    private Book book;

    public Review(String _isbn, String _username, String _review){
        Utilities.notNull(_isbn, _username);
        this.book = Book.getBook(_isbn);
        this.username = _username;
        this.review = _review;
    }

    public Review(String _isbn, String _username){
        this(_isbn, _username, null);
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

    public void setBook(String _isbn) {
        this.book = Book.getBook(_isbn);
    }

    public void setBook(Book _book){
        this.book = _book;
    }

    public void setUsername(String _username) {
        this.username = _username;
    }

    public void setReview(String _review) {
        this.review = _review;
    }
}
