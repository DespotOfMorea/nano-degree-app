package org.vnuk.nanodegreeapp.utils;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.net.HttpURLConnection;

public class PersonJsonUtils {

    public static String[] getSimplePersonStringsFromJson(Context context, String personJsonStr)
            throws JSONException {

        final String PERSON_LIST = "results";

        final String PERSON_NAME = "name";
        final String PERSON_LOCATION = "location";
        final String PERSON_COORDINATES = "coordinates";
        final String PERSON_DOB = "dob";
        final String PERSON_PICTURE = "picture";

        final String PERSON_GENDER = "gender";
        final String PERSON_TITLE = "title";
        final String PERSON_FIRST = "first";
        final String PERSON_LAST = "last";
        final String PERSON_STREET = "street";
        final String PERSON_CITY = "city";
        final String PERSON_STATE = "state";
        final String PERSON_POSTCODE = "postcode";
        final String PERSON_LATITUDE = "latitude";
        final String PERSON_LONGITUDE = "longitude";
        final String PERSON_AGE = "age";
        final String PERSON_NAT = "nat";
        final String PERSON_THUMBNAIL = "thumbnail";

        final String MESSAGE_CODE = "cod";

        String[] parsedPersonData = null;

        JSONObject personJson = new JSONObject(personJsonStr);

        if (personJson.has(MESSAGE_CODE)) {
            int errorCode = personJson.getInt(MESSAGE_CODE);

            switch (errorCode) {
                case HttpURLConnection.HTTP_OK:
                    break;
                case HttpURLConnection.HTTP_NOT_FOUND:
                    /* Location invalid */
                    return null;
                default:
                    /* Server probably down */
                    return null;
            }
        }

        JSONArray personArray = personJson.getJSONArray(PERSON_LIST);

        parsedPersonData = new String[personArray.length()];

        for (int i = 0; i < personArray.length(); i++) {
            String gender;
            String title;
            String first;
            String last;
            String street;
            String city;
            String state;
            String postcode;
            String latitude;
            String longitude;
            String age;
            String nat;
            String thumbnail;

            JSONObject person = personArray.getJSONObject(i);

            gender = person.getString(PERSON_GENDER);

            JSONObject nameObject = person.getJSONObject(PERSON_NAME);
            title = nameObject.getString(PERSON_TITLE);
            first = nameObject.getString(PERSON_FIRST);
            last = nameObject.getString(PERSON_LAST);

            JSONObject locationObject = person.getJSONObject(PERSON_LOCATION);
            street = locationObject.getString(PERSON_STREET);
            city = locationObject.getString(PERSON_CITY);
            state = locationObject.getString(PERSON_STATE);
            postcode = locationObject.getString(PERSON_POSTCODE);
            JSONObject coordinatesObject = locationObject.getJSONObject(PERSON_COORDINATES);
            latitude = coordinatesObject.getString(PERSON_LATITUDE);
            longitude = coordinatesObject.getString(PERSON_LONGITUDE);


            parsedPersonData[i] = title + " " + first + " " + last;
        }

        return parsedPersonData;
    }
}