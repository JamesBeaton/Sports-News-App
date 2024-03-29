package com.example.jb.newsapp;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dobry on 04.07.17.
 */

public class Utils {
    /**
     * Tag for the log messages
     */
    private static final String LOG_TAG = Utils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link Utils} object.
     */
    private Utils() {

    }

    /**
     * Return an {@link News} object by parsing out information
     * about the first news from the input newsJSON string.
     */
    private static List<News> extractNewsFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding sports news to
        List<News> sportsNews = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            // Extract JSONObject from baseJesonResponse by using key: "response"
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of newses.
            JSONArray sportsNewsArray = response.getJSONArray("results");

            // For each news in the newsesArray, create an {@link News} object
            for (int i = 0; i < sportsNewsArray.length(); i++) {

                // Get a single news at position i within the list of items (newses)
                JSONObject currentNews = sportsNewsArray.getJSONObject(i);

                // For a given news, extract the JSONObject associated with the
                // key called "fields", which represents an object with THUMBNAIL IMG URL and
                // HEADLINE of article
                JSONObject fields = currentNews.getJSONObject("fields");

                // # Extract the value for the key called "webTitle" as Author
                String authorArticle;

                // For a given news, extract the JSONArray associated with the
                // key called "tags", which represents a ArrayList of author, etc.
                // Check if JSONArray exist
                if (currentNews.has("tags")) {
                    JSONArray tagsArray = currentNews.getJSONArray("tags");

                    // Check JSONArray Returns true if this object has no mapping for name or
                    // if it has a mapping whose value is NULL and if tagsArray contains
                    // at list one element
                    if (!currentNews.isNull("tags") && tagsArray.length() > 0) {
                        // Get 1st element - object
                        JSONObject tagObject = (JSONObject) tagsArray.get(0);
                        // Get authors name by using a "webTitle" key
                        authorArticle = tagObject.getString("webTitle");
                    } else {
                        // assign info about missing info about author
                        authorArticle = "*** unknown author ***";
                    }
                } else {
                    // assign info about missing info about author
                    authorArticle = "*** missing info of author ***";
                }
                // # Extract the value for the key called "webTitle"
                String titleArticle = currentNews.getString("webTitle");

                // # Extract the value for the key called "sectionName"
                String sectionArticle = "#" + currentNews.getString("sectionName");

                // # Extract String URL of specific cover for the key "thumbnail"
                String imageArticle;
                if (fields.has("thumbnail")) {
                    imageArticle = fields.getString("thumbnail");
                } else {
                    continue; // back to begin of this loop without adding an object without thumbnail img to List
                }
                // # Extract the value for the key called "webPublicationDate"
                String dateArticle = currentNews.getString("webPublicationDate");

                // Switch format date
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateArticle);
                String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                dateArticle = formattedDate;

                // # Extract the value for the key called "webUrl"
                String urlArticle = currentNews.getString("webUrl");

                // Create a new {@link News} object with the title, author, coverImageUrl, price, currency and language
                // and url from the JSON response.
                News newsItem = new News(titleArticle, sectionArticle, authorArticle, imageArticle, dateArticle, urlArticle);

                // Add the new {@link News} to the list of sports news.
                sportsNews.add(newsItem);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the list of sports news
        return sportsNews;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {

        // To avoid "magic numbers" in code, all numeric values mustn't been used directly in a code
        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;
        final int CORRECT_RESPONSE_CODE = 200;

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT /* milliseconds */);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == CORRECT_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (IOException e) {
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
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    static List<News> fetchNewsData(String requestUrl) {

        final int SLEEP_TIME_MILLIS = 2000;

        // This action with sleeping is required for displaying circle progress bar
        try {
            Thread.sleep(SLEEP_TIME_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> listSportsNews = extractNewsFromJson(jsonResponse);

        // Return the list of {@link News}s
        return listSportsNews;
    }

}