package connection;

import api.Book;
import org.json.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Connection {
    public static void main(String[] args) {
        SpringApplication.run(Connection.class, args);
    }

    @GetMapping("/test")
    public String test(@RequestParam(value = "isbn", defaultValue = "9780788789830") String _isbn) {
        return new JSONObject(Book.getBook(_isbn).getMap()).toString();
    }
}
