import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

class APIAdapter implements APIInterface {

    final protected API api = new API();

    @Override
    public URL getURL(RequestType _requestType, String... _args) throws IOException {
        return this.api.getURL(_requestType, _args);
    }

    @Override
    public String getJsonAsString(RequestType _requestType, String... _args) throws IOException {
        return this.api.getJsonAsString(_requestType, _args);
    }

    @Override
    public HttpURLConnection getConnection(URL _url) throws IOException {
        return this.api.getConnection(_url);
    }


}