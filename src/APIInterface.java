import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public interface APIInterface {
    URL getURL(RequestType _requestType, String... _args) throws IOException;

    HttpURLConnection getConnection(URL _url) throws IOException;

    default String getJsonAsString(RequestType _requestType, String... _args) throws IOException {
        return new String(getConnection(getURL(_requestType, _args))
                .getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    @Contract(" -> new")
    static @NotNull APIInterface getInstance(){
        return new APIAdapter();
    }
}
