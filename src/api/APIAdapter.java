package api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;

class APIAdapter implements APIInterface {

    private static final String FORMAT_STRING = "http%s://www.openlibrary.org/%s.json%s";

    public URL getUrl(RequestType _requestType, String _arg) throws IOException {
        return new URL(String.format(FORMAT_STRING, supportsHttps() ? "s" : "", _requestType + "/" + _arg, ""));
    }

    public URL getURL(@NotNull String _firstArg, String _secondArg) throws IOException {
        return new URL(String.format(FORMAT_STRING, supportsHttps() ? "s" : "", (_firstArg.startsWith("/") ? _firstArg.substring(1) : _firstArg), _secondArg));
    }

    public URL getUrlSearch(RequestType _nullableRequestType, String _arg) throws IOException {
        return new URL(String.format(FORMAT_STRING, supportsHttps() ? "s" : "", "search" +
                        (_nullableRequestType != null ? "/" + _nullableRequestType : ""),
                "?q=" + URLEncoder.encode(_arg, StandardCharsets.UTF_8)));
    }

    public HttpURLConnection getConnection(@NotNull URL _url) throws IOException {
        return (HttpURLConnection) _url.openConnection();
    }
}