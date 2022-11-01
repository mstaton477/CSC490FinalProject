package connection;

import api.Author;
import api.Book;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

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
        } else return getBooksByTitle(_title);
    }

    //TODO
    private String getBooksByTitle(String _title) {
        
        Map<String,JSONArray> map = new HashMap<>();
        map.put("books", new JSONArray(Book.getBooksByTitle(_title)));
        return new JSONObject(map).toString();
    }

    @GetMapping("/getAuthor")
    public String getAuthor(@RequestParam(value = "name", defaultValue = "J.R.R. Tolkien") String _name,
                            @RequestParam(value = "author", defaultValue = "") String _author) {

        if(_author.isEmpty()){
            _author = _name;
        }

        HashMap<String, JSONArray> map = new HashMap<>();
        LinkedList<JSONObject> jsons = new LinkedList<>();

        Author.getAuthorsByName(_author).forEach(e -> jsons.add(e.toJsonObject()));
        map.put("authors", new JSONArray(jsons));

        return new JSONObject(map).toString();
    }
}
