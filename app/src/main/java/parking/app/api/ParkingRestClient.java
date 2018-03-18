package parking.app.api;

/**
 * Created by xngok on 1/3/2017.
 */

import com.loopj.android.http.*;

public class ParkingRestClient {
//    private final String BASE_URL = "http://174.67.219.206:8080/api/v1/";
    private final String BASE_URL = "https://online-parking-senior-design.appspot-preview.com/api/v1/";
    private AsyncHttpClient client = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setEnableRedirects(true);
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
//        client.setEnableRedirects(true);
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public void delete(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.delete(getAbsoluteUrl(url), params, responseHandler);
    }

    public void put(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.put(getAbsoluteUrl(url), params, responseHandler);
    }

    public void addHeader (String header, String value) {
        client.addHeader(header, value);
    }

    public String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
