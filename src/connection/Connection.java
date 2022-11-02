package connection;

import api.*;
import org.json.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class Connection {
    public static void main(String[] args) {
        SpringApplication.run(Connection.class, args);
    }

    @GetMapping("/getBook")
    public String getBook(@RequestParam(value = "isbn", defaultValue = "") String _isbn,
                          @RequestParam(value = "title", defaultValue = "") String _title,
                          @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (!_title.isEmpty()) {
            return Connection.getBooksByTitle(_title, _limit);

        } else if (!_isbn.isEmpty()) try {
            return Book.getBookByIsbn(_isbn).toJsonObject().toString();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "{}";
    }

    private static String getBooksByTitle(String _title, String _limit) {

        Map<String, JSONArray> map = new HashMap<>();
        map.put("books", new JSONArray(Book.getBooksByTitleAsJSONArray(_title, _limit)));
        return new JSONObject(map).toString();
    }

    @GetMapping("/getAuthor")
    public String getAuthor(@RequestParam(value = "name", defaultValue = "") String _name,
                            @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (_name.isEmpty()) return "{}";

        HashMap<String, JSONArray> map = new HashMap<>();
        LinkedList<JSONObject> jsons = new LinkedList<>();

        Author.getAuthorsByName(_name, _limit).forEach(e -> jsons.add(e.toJsonObject()));
        map.put("authors", new JSONArray(jsons));

        return new JSONObject(map).toString();
    }
}
