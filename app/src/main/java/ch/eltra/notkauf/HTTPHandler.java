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

class HTTPHandler {
    private MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private String baseURL;
    private OkHttpClient client;
    private MyCookieJar cookies;

    HTTPHandler(String baseURL) {
        this.baseURL = baseURL;
        cookies = new MyCookieJar();
        createClient();
    }

    void clearCookies() {
        cookies = new MyCookieJar();
    }

    private void createClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.cookieJar(cookies);
        client = builder.build();
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

    JSONArray getJSONArrayParams(String ext, String country, String region, String city) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("Country", country);
        urlBuilder.addQueryParameter("Region", region);
        urlBuilder.addQueryParameter("City", city);
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

    JSONArray getJSONArrayParams2(String ext, String country, String pCode) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("Country", country);
        urlBuilder.addQueryParameter("PostalCode", pCode);
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

    JSONArray getJSONArrayParams3(String ext, String country, String langCode) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("CountryCode", country);
        urlBuilder.addQueryParameter("LangCode", langCode);
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

    JSONArray getJSONArrayParams4(String ext, String country, String regCode) throws IOException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseURL + ext)).newBuilder();
        urlBuilder.addQueryParameter("CountryCode", country);
        urlBuilder.addQueryParameter("RegionCode", regCode);
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

