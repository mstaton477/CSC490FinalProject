package api;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class APIAdapter implements APIInterface {

    final protected API api = new API();

    @Override
    public URL getUrl(@NotNull RequestType _requestType, String _arg) throws IOException {
        return this.api.getUrl(_requestType, _arg);
    }

    @Override
    public URL getUrlSearch(RequestType _requestType, String _arg)throws IOException {
        return this.api.getUrlSearch(_requestType, _arg);
    }

    @Override
    public String getJsonAsString(RequestType _requestType, String _arg) throws IOException {
        return this.api.getJsonAsString(_requestType, _arg);
    }

    @Override
    public HttpURLConnection getConnection(URL _url) throws IOException {
        return this.api.getConnection(_url);
    }


}