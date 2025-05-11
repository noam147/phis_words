package API;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class API_Handler {
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
