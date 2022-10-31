package connection;

import api.Author;
import api.Book;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;

@SpringBootApplication
@RestController
public class Connection {
    public static void main(String[] args) {
        SpringApplication.run(Connection.class, args);
    }

    @GetMapping("/getBook")
    public String getBook(@RequestParam(value = "isbn", defaultValue = "9780788789830") String _isbn,
                          @RequestParam(value = "title", defaultValue = "") String _title) {

        if (_title.isEmpty()) {
            return Book.getBook(_isbn).toJsonObject().toString();
        } else return getBookByTitle(_title);
    }

    //TODO
    private String getBookByTitle(String _title) {

        Book book = Book.getBookByTitle(_title);
        return book != null ? book.toJsonObject().toString() : null;
    }

    @GetMapping("/getAuthor")
    public String author(@RequestParam(value = "author", defaultValue = "J.R.R. Tolkien") String _author) {

        HashMap<String, JSONArray> map = new HashMap<>();
        LinkedList<JSONObject> jsons = new LinkedList<>();

        Author.getAuthorsByName(_author).forEach(e -> jsons.add(e.toJsonObject()));
        map.put("authors", new JSONArray(jsons));

        return new JSONObject(map).toString();
    }
}
