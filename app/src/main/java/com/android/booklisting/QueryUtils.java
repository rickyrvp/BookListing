package com.android.booklisting;

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


    private QueryUtils() {
    }


    public static List<Book> extractFeatureFromJSON(String bookJSON) {

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        List<Book> book = new ArrayList<>();

        try {

            JSONObject baseJSONResponse = new JSONObject(bookJSON);
            JSONArray bookArray = baseJSONResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {

                JSONObject currentBook = (JSONObject) bookArray.get(i);
                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                JSONObject saleInfo = currentBook.getJSONObject("saleInfo");

                //extract the value for the key called "title"
                String title = volumeInfo.getString("title");

                //extract the value for the key called "author"
                String author;

                //Check if JSONArray exists
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");

                    // Check JSONArray Returns true if this object has no mapping for name or if it has a mapping whose value is NULL
                    if (!volumeInfo.isNull("authors")) {
                        author = (String) authors.get(0);
                    } else {
                        // assign info about missing info about author
                        author = "**Unknown Author**";
                    }
                } else {
                    // assign info about missing info about author
                    author = "**Unknown Author**";

                }

                //extract the value for the key called "imageLinks"
                String imageUrl = imageLinks.getString("thumbnail");

                //extract the value for the key called "buyLink"
                String buyUrl = volumeInfo.getString("canonicalVolumeLink");

                Book books = new Book(title, author, imageUrl, buyUrl);
                book.add(books);

            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }



        return book;


    }

    public static List<Book> fetchBookData(String requestUrl) {

        URL url = createUrl(requestUrl);
        String jsonResponse = "";

        try {
            jsonResponse = makeHttpRequest(url);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making HTTP request", e);
        }

        List<Book> books = extractFeatureFromJSON(jsonResponse);

        return books;

    }

    private static String makeHttpRequest(URL url) throws IOException {

        // To avoid "magic numbers" in code, all numeric values mustn't been used directly in a code
        final int READ_TIMEOUT = 10000;
        final int CONNECT_TIMEOUT = 15000;
        final int CORRECT_RESPONSE_CODE = 200;

        String jsonResponse = "";

        //if url is null, then return null early
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIMEOUT);
            urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //if request was successful (RESPONSE CODE = 200),
            //then read the input stream and parse the response

            if (urlConnection.getResponseCode() == CORRECT_RESPONSE_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results", e);

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


    private static URL createUrl(String requestUrl) {
        URL url = null;

        try {
            url = new URL(requestUrl);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;


    }

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

}
