package manuelvicnt.com.rxjava_android_structure.networking.mock;


import android.content.Context;
import android.preference.PreferenceActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit.client.Client;
import retrofit.client.Header;
import retrofit.client.Request;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class MockHttpClient implements Client {

    private Context context;
    private List<Header> headerList;

    public MockHttpClient(Context context) {

        this.context = context;
        headerList = new ArrayList<>();
    }

    @Override
    public Response execute(Request request) throws IOException {

        try {

            String url = request.getUrl();

            MockResponse mockResponse = null;

            if (url.contains("registration")) {
                mockResponse = getResponse("registrationResponse.txt");
            } else if (url.contains("login")) {
                mockResponse = getResponse("loginResponse.txt");
            } else if (url.contains("account")) {
                mockResponse = getResponse("accountResponse.txt");
            } else if (url.contains("games")) {
                mockResponse = getResponse("gamesResponse.txt");
            }

            TypedByteArray responseBody = new TypedByteArray("application/json", mockResponse.getResponseBody().getBytes());

            Thread.sleep(2000);

            return new Response(request.getUrl(), mockResponse.getResponseCode(), mockResponse.getResponseMessage(), headerList, responseBody);

        } catch (Exception e) {

            TypedByteArray responseBody = new TypedByteArray("application/json", "".getBytes());

            return new Response(request.getUrl(), 500, "File not found", Collections.EMPTY_LIST, responseBody);
        }
    }

    public MockResponse getResponse(String path) throws Exception {

        InputStream inputStream = context.getAssets().open(path);
        String result = convertStreamToString(inputStream);

        Log.d("TEST", result);
        String responseBody = getResponseBody(result);

        Log.d("TEST", responseBody);
        JSONObject responseHeader = getResponseHeader(result);
        processResponseHeader(result);
        String responseCode = getResponseCode(responseHeader);
        String responseMessage = getResponseMessage(responseHeader);

        return new MockResponse(responseCode, responseMessage, responseBody);
    }

    private String getResponseBody(String responseJson) throws JSONException {

        JSONObject topLevelJsonObject = new JSONObject(responseJson);
        return topLevelJsonObject.getJSONObject("Body").toString();
    }

    private String getResponseCode(JSONObject responseHeaderJson) throws JSONException {

        return responseHeaderJson.getString("Code");
    }

    private String getResponseMessage(JSONObject responseHeaderJson) throws JSONException {

        return responseHeaderJson.getString("Message");
    }

    private JSONObject getResponseHeader(String responseJson) throws JSONException {

        JSONObject topLevelObject = new JSONObject(responseJson);
        return topLevelObject.getJSONObject("Header");
    }

    private void processResponseHeader(String responseHeader) throws JSONException {

        headerList.clear();
        JSONObject topLevelObject = new JSONObject(responseHeader);

        JSONObject responseHeaderJson = topLevelObject.getJSONObject("Header");
        Iterator<?> keys = responseHeaderJson.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            Header header = new Header(key, responseHeaderJson.getString(key));
            headerList.add(header);
        }
    }

    private static String convertStreamToString(InputStream inputStream)
            throws IOException {
        Writer writer = new StringWriter();
        char[] buffer = new char[2048];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            inputStream.close();
        }
        String text = writer.toString();
        return text;
    }


    public static class MockResponse {

        private String responseBody;
        private String responseCodeString;
        private String responseMessage;

        public MockResponse(String responseCode, String responseMessage, String responseBody) {

            this.responseCodeString = responseCode;
            this.responseMessage = responseMessage;
            this.responseBody = responseBody;
        }

        public int getResponseCode() {

            return Integer.valueOf(responseCodeString);
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public String getResponseBody() {
            return responseBody;
        }
    }
}
