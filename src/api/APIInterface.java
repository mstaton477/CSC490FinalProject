package api;

import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public interface APIInterface {
    Pattern ISBN13 = Pattern.compile("\\s*\\d{13}\\s*"), WHITESPACE_AND_PERIODS = Pattern.compile("\\s|\\."),
            WORKS_KEY = Pattern.compile("W$"), BOOKS_KEY = Pattern.compile("M$"),
            AUTHOR_KEY = Pattern.compile("A$");

    URL getUrl(RequestType _requestType, String _arg) throws IOException;

    URL getUrlSearch(RequestType _requestType, String _arg) throws IOException;

    HttpURLConnection getConnection(URL _url) throws IOException;

    public URL getURL(String _firstArg, String _secondArg) throws IOException;

    default JSONObject getJson(String _firstArg, String _secondArg) throws IOException {
        URL url = this.getURL(_firstArg, _secondArg);
        System.out.println("url path: " + url.getPath());
        String s = new String(this.getConnection(url)
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println("json: " + s);
        return new JSONObject(s);
    }

    default JSONObject getJson(String _firstArg) throws IOException {
        return getJson(_firstArg, "");
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
