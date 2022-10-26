package api;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface APIInterface {
    URL getUrl(RequestType _requestType, String _arg) throws IOException;

    URL getUrlSearch(RequestType _requestType, String _arg) throws IOException;

    HttpURLConnection getConnection(URL _url) throws IOException;

    //TODO fix
    default String getJsonAsString(RequestType _requestType, String _arg) throws IOException {
        return new String(this.getConnection(this.getUrl(_requestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    default String getJsonSearchAsString(RequestType _nullableRequestType, String _arg) throws IOException {
        return new String(this.getConnection(this.getUrlSearch(_nullableRequestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    default boolean supportsHttps(){
        return false;
    }


    static APIInterface getInstance() {
        return new APIAdapter();
    }
}
