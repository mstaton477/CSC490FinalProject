import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface APIInterface {
    URL getUrl(@NotNull RequestType _requestType, String _arg) throws IOException;

    URL getUrlSearch(RequestType _requestType, String _arg) throws IOException;

    HttpURLConnection getConnection(URL _url) throws IOException;

    //TODO fix
    default String getJsonAsString(RequestType _requestType, String _arg) throws IOException {
        return new String(getConnection(getUrl(_requestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    default String getJsonSearchAsString(RequestType _requestType, String _arg) throws IOException {
        return new String(getConnection(getUrlSearch(_requestType, _arg))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    default boolean supportsHttps(){
        return false;
    }

    @Contract(" -> new")
    static @NotNull APIInterface getInstance() {
        return new APIAdapter();
    }
}
