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

    /**
     * The mapping for the GET request "/getBook" with multiple usages (see below for which inputs are prioritized)
     *
     * @param _key   Prioritized over other inputs; The OpenLibrary authors key for the specific author
     * @param _isbn  Third in priority; The ISBN to search by
     * @param _title Second in priority; The title to search by
     * @param _limit The maximum number of books request from the OpenLibrary API. If the number of matching books in
     *               the system is greater than {@code _limit}, it returns all of them
     * @return A json object as a string in the form {"books":[<0-or-more-json-objects>]}
     */
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

    /**
     *
     * @param _key
     * @param _name
     * @param _limit
     * @return
     */
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
