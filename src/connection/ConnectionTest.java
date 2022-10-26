package connection;

import api.Book;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
public class ConnectionTest {
    public static void main(String[] args) {
        SpringApplication.run(ConnectionTest.class, args);
    }

    @GetMapping("/hello")
    public String hello(@RequestParam(value = "isbn", defaultValue = "9780788789830") String _name) {
        Map<String,String> map = new HashMap<>();
        Book book = Book.getBook(_name);
        map.put("isbn", _name);
        map.put("title", book.getTitle());
        map.put("authorIds", new JSONArray(book.getAuthorIds()).toString());
        map.put("authorNames", new JSONArray(book.getAuthorNames()).toString());
        JSONObject json = new JSONObject(map);
        return String.format(json.toString());
    }
}
