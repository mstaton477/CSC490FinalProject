package data_Base.src;

public class Book {
    private String isbn, author, title, genre;

    Book(String _isbn, String _author, String _title, String _genre){
        this.isbn = _isbn;
        this.author = _author;
        this.title = _title;
        this.genre = _genre;
    }

    Book(String _isbn, String _author, String _title){
        this(_isbn, _author, _title, null);
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }
}
