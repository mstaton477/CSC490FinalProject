package api;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public interface APIInterface {
    Pattern WHITESPACE_AND_PERIODS = Pattern.compile("\\s|\\."), AUTHOR_KEY = Pattern.compile("A$"),
            WORKS_KEY = Pattern.compile("W$"), BOOKS_KEY = Pattern.compile("M$");

    URL getUrl(RequestType _requestType, String _arg) throws IOException;

    URL getUrlSearch(RequestType _requestType, String _arg) throws IOException;

    HttpURLConnection getConnection(URL _url) throws IOException;

    URL getURL(String _firstArg, String _secondArg) throws IOException;

    default JSONObject getJson(String _firstArg, String _secondArg) throws IOException {
        return new JSONObject(new String(this.getConnection(this.getURL(_firstArg, _secondArg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    default JSONObject getJson(String _firstArg) throws IOException {
        return this.getJson(_firstArg, "");
    }

    default JSONObject getJson(RequestType _requestType, String _arg) throws IOException {
        return new JSONObject(new String(this.getConnection(this.getUrl(_requestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    default JSONObject getJsonSearch(RequestType _nullableRequestType, String _arg) throws IOException {
        return new JSONObject(new String(this.getConnection(this.getUrlSearch(_nullableRequestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8));
    }

    default boolean supportsHttps(){
        return false;
    }

    static APIInterface getInstance() {
        return new APIAdapter();
    }
}
