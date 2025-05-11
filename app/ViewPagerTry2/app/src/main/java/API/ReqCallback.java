package API;

public interface ReqCallback {
    void onSuccess(String response);
    void onFailure(Exception e);
}
