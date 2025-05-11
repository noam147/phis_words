package API;

public class DailyWord {
    private static String baseUrl = "http://13.51.79.222:15555/";
    public static String getDailyWord()
    {
        String currUrl = baseUrl+"daily_word";
        API_Handler.sendGetRequest("http://13.51.79.222:15555/daily+word", new ReqCallback() {
            @Override
            public void onSuccess(String response) {

            }

            @Override
            public void onFailure(Exception e) {

            }
        });
        //func will get the word with its translation
        return "";
    }
}
