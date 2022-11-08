package connection;

import api.*;
import org.json.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class Connection {
    public static void main(String[] args) {
        SpringApplication.run(Connection.class, args);
    }

    @GetMapping("/getBook")
    public String getBook(@RequestParam(value = "key", defaultValue = "") String _key,
                          @RequestParam(value = "isbn", defaultValue = "") String _isbn,
                          @RequestParam(value = "title", defaultValue = "") String _title,
                          @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (!_key.isEmpty()) return Utilities.format("books", Book.getBookByKey(_key, _limit).toJsonObject());

        if (!_title.isEmpty()) return Utilities.format("books", Book.getBooksByTitleAsJSONArray(_title, _limit));

        if (!_isbn.isEmpty()) try {
            return Utilities.format("books", Book.getBookByIsbn(_isbn).toJsonObject());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return Utilities.format("books", new JSONArray());
    }

    @GetMapping("/getAuthor")
    public String getAuthor(@RequestParam(value = "key", defaultValue = "") String _key,
                            @RequestParam(value = "name", defaultValue = "") String _name,
                            @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (!_key.isEmpty())
            return Utilities.format("authors", Author.getAuthorById(_key).toJsonObject());

        if (!_name.isEmpty())
            return Utilities.format("authors", Utilities.toJsonList(Author.getAuthorsByName(_name, _limit)));

        else return Utilities.format("authors", new JSONArray());
    }
}
