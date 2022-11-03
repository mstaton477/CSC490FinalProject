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
    public String getBook(@RequestParam(value = "key", defaultValue = "") String _key,
                          @RequestParam(value = "isbn", defaultValue = "") String _isbn,
                          @RequestParam(value = "title", defaultValue = "") String _title,
                          @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (!_key.isEmpty()) return Connection.format("books", Book.getBookByKey(_key, _limit).toJsonObject());

        else if (!_title.isEmpty()) return Connection.format("books", Book.getBooksByTitleAsJSONArray(_title, _limit));

        else if (!_isbn.isEmpty()) try {
            return Connection.format("books", Book.getBookByIsbn(_isbn).toJsonObject());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "{}";
    }

    private static String format(String _type, JSONArray _arr){

        Map<String, JSONArray> map = new HashMap<>();
        map.put(_type, _arr);
        return new JSONObject(map).toString();
    }

    private static String format(String _type, JSONObject... _jsons){
        return format(_type, new JSONArray(_jsons));
    }

    @GetMapping("/getAuthor")
    public String getAuthor(@RequestParam(value = "name", defaultValue = "") String _name,
                            @RequestParam(value = "limit", defaultValue = "") String _limit) {

        if (_name.isEmpty()) return "{}";

        LinkedList<JSONObject> jsons = new LinkedList<>();

        Author.getAuthorsByName(_name, _limit).forEach(e -> jsons.add(e.toJsonObject()));

        return Connection.format("authors", new JSONArray(jsons));
    }
}
