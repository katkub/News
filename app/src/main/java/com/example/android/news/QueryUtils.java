package com.example.android.news;

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

/**
 * Utility class with methods to help perform the HTTP request and
 * parse the response.
 */
public class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    static final int ReadTimeout = 10000;
    static final int ConnectTimeout = 15000;

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the guardianapis dataset and return a list of {@link TechNews} objects.
     */
    public static List<TechNews> fetchNewsData(String quardianRequestUrl) {
        // Create URL object
        URL url = createUrl(quardianRequestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link TechNews}

        // Return the {@link Event}
        return extractFeatureFromJson(jsonResponse);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
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
            urlConnection.setReadTimeout(ReadTimeout /* milliseconds */);
            urlConnection.setConnectTimeout(ConnectTimeout /* milliseconds */);
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
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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
     * Return a list of {@link TechNews} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<TechNews> extractFeatureFromJson(String technologyNewsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(technologyNewsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news to
        List<TechNews> newsList = new ArrayList<>();

        // TTry to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(technologyNewsJSON);
            JSONObject response = baseJsonResponse.getJSONObject("response");
            //Create JSON Array
            JSONArray resultsArray = response.getJSONArray("results");

            //For each news create JSON Object
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentResults = resultsArray.getJSONObject(i);

                //Create String for section, title, date and url of news
                // Extract the value for the key called "sectionName"
                String section = currentResults.getString("sectionName");
                // Extract the value for the key called "webTitle"
                String title = currentResults.getString("webTitle");
                // Extract the value for the key called "webPublicationDate"
                String date = currentResults.getString("webPublicationDate");
                // Extract the author value
                JSONArray tagsAuthor = currentResults.getJSONArray("tags");
                String author="";
                if (tagsAuthor.length()!= 0) {
                    JSONObject currentTagsAuthor = tagsAuthor.getJSONObject(0);
                    author = currentTagsAuthor.getString("webTitle");
                }else{
                    author = "Author: unknown";
                }
                // Extract the value for the key called "webUrl"
                String url = currentResults.getString("webUrl");

                //Create new news and add it to arrayList
                TechNews news = new TechNews(section, title, date, author, url);
                newsList.add(news);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newsList;
    }
}