import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class API implements APIInterface {

    private static final String FORMAT_STRING = "http://www.openlibrary.org/%s/%s.json%s";

    public URL getURL(@NotNull RequestType _requestType, @NotNull String... _args) throws IOException {
        return new URL(String.format(FORMAT_STRING, _requestType, Utilities.getIndexSafe(_args, 0),
                _requestType != RequestType.SEARCH ? ""
                        : "?" + URLEncoder.encode(Utilities.getIndexSafe(_args, 1), StandardCharsets.UTF_8)));
    }

    public HttpURLConnection getConnection(@NotNull URL _url) throws IOException {
        return (HttpURLConnection) _url.openConnection();
    }
}

