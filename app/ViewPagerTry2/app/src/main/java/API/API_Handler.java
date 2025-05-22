
package API;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * This class provides a utility for making asynchronous HTTP GET requests.
 * It uses the OkHttp library to handle the network communication.
 */
public class API_Handler {

    /**
     * Sends an asynchronous HTTP GET request to the specified URL.
     * The result (success or failure) is communicated through the provided {@link ReqCallback}.
     * The request is executed on a separate thread to avoid blocking the main thread.
     *
     * @param url      The URL to send the GET request to.
     * @param callback The {@link ReqCallback} to handle the response or error.
     */
    public static void sendGetRequest(String url, ReqCallback callback) {
        Log.d("HttpSender", "URL: " + url);
        try {
            final OkHttpClient client = new OkHttpClient();
            final Request request = new Request.Builder().url(url).build();
            new Thread(() -> executeRequest(client, request, callback)).start();
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    /**
     * Executes the HTTP GET request using the provided OkHttpClient and Request.
     * It handles the response, checks if it's successful, and extracts the response body as a string.
     * The result is then passed to the appropriate method of the {@link ReqCallback}.
     * Any IOException during the request execution is caught and reported as a failure.
     * The response body is closed in the finally block to prevent resource leaks.
     *
     * @param client   The OkHttpClient instance to use for the request.
     * @param request  The Request object containing the URL and other request details.
     * @param callback The {@link ReqCallback} to handle the success or failure of the request.
     */
    private static void executeRequest(OkHttpClient client, Request request, ReqCallback callback) {
        Response response = null;
        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful() && response.body() != null) {
                String responseData = response.body().string();
                callback.onSuccess(responseData);
            } else {
                callback.onFailure(new IOException("Request failed with code: " + response.code()));
            }
        } catch (IOException e) {
            callback.onFailure(e);
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
