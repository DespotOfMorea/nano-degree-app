package org.vnuk.nanodegreeapp.utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class PersonNetworkUtils {
    private static final String TAG = PersonNetworkUtils.class.getSimpleName();
    private static final String STATIC_URL =
            "https://randomuser.me/api/";

    private static final String BASE_URL = STATIC_URL;

    /* The format we want our API to return */
    private static final String format = "json";
    private static final String nat = "gb,nz,ie";
    private static final String results = "17";

    final static String QUERY_PARAM = "results";
    final static String LAT_PARAM = "lat";
    final static String LON_PARAM = "lon";
    final static String FORMAT_PARAM = "format";
    final static String NAT_PARAM = "nat";
    final static String RESULTS_PARAM = "results";

    public static URL buildUrl(String resultsQuery) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_PARAM, resultsQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(NAT_PARAM, nat)
//                .appendQueryParameter(RESULTS_PARAM, results)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}