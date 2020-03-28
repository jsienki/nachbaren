package ch.eltra.notkauf;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPHandler {
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String baseURL;
    private OkHttpClient client;
    private MyCookieJar cookies;

    HTTPHandler(String baseURL) {
        this.baseURL = baseURL;
        cookies = new MyCookieJar();
        createClient();
    }

    private void createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookies);
        client = builder.build();
    }

    int get(String ext) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("i", "100");
        String lUrl = urlBuilder.toString();

        Request request = new Request.Builder()
                .url(lUrl)
                .get()
                .build();

        Response response = client.newCall(request).execute();
        return response.code();
    }


    int post(String ext, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(baseURL + ext)
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.code();
        }
    }

    JSONArray getJSONArray(String ext) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + ext)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONArray(response.body().string());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    JSONArray getJSONArrayParams(String ext, String params, String mode) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("Country", "CH");
        if (params != null) {
            urlBuilder.addQueryParameter(mode, params);
        }
        String lUrl = urlBuilder.toString();

        Request request = new Request.Builder()
                .url(lUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONArray(response.body().string());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    JSONObject getJSON(String ext) throws IOException {
        Request request = new Request.Builder()
                .url(baseURL + ext)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONObject(response.body().string());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    JSONObject getJSONParams(String ext, String params) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("Uuid", params);
        String lUrl = urlBuilder.toString();

        Request request = new Request.Builder()
                .url(lUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.body() != null) {
                return new JSONObject(response.body().string());
            } else {
                return null;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}

