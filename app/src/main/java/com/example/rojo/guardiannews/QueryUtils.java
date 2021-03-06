package com.example.rojo.guardiannews;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private static List<Guardian> extractFeatureFromJson(String guardianJSON) {

        if (TextUtils.isEmpty(guardianJSON)) {
            return null;
        }

        List<Guardian> guardians = new ArrayList<>();

        try {
            //Create a JSONObject from the JSON response string
            JSONObject baseJSONResponse = new JSONObject(guardianJSON);

            //Extract the JSONArray associated with the key called "results"
            //which represents a list of features or guardian stories.
            JSONObject guardianObject = baseJSONResponse.getJSONObject("response");

            JSONArray currentNewsArray = guardianObject.getJSONArray("results");

            for (int i = 0; i < currentNewsArray.length(); i++) {
                JSONObject newsArticle = currentNewsArray.getJSONObject(i);

                //Used Project 6 walkthrough to find author and peer comments
                JSONArray tagArray = newsArticle.getJSONArray("tags");
                String author = " ";
                if (tagArray.length() == 0) {
                    author ="N/A";
                }
                else {
                    for (int t = 0; t < tagArray.length(); t++) {
                        JSONObject authorName = tagArray.getJSONObject(t);
                        author = authorName.getString("webTitle");
                    }
                }
                String article = newsArticle.getString("webTitle");
                String section = newsArticle.getString("sectionName");
                String date = newsArticle.getString("webPublicationDate");
                String url = newsArticle.getString("webUrl");

                Guardian guardian = new Guardian(article, section, date, url, author);
                guardians.add(guardian);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the guardian JSON results", e);
        }
       return guardians;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the guardian JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Query the USGS dataset and return a list of {@link Guardian} objects.
     */
    public static List<Guardian> fetchGuardianData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Guardian}
        List<Guardian> guardians = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return guardians;
    }
}