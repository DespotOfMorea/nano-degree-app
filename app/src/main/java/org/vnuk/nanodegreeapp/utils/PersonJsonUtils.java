package org.vnuk.nanodegreeapp.utils;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.vnuk.nanodegreeapp.model.FakePerson;
import org.vnuk.nanodegreeapp.model.PersonAddress;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class PersonJsonUtils {

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static FakePerson[] getSimplePersonStringsFromJson(Context context, String personJsonStr)
            throws JSONException {

        final String PERSON_LIST = "results";

        final String PERSON_NAME = "name";
        final String PERSON_LOCATION = "location";
        final String PERSON_STREET = "street";
        final String PERSON_COORDINATES = "coordinates";
        final String PERSON_DOB = "dob";
        final String PERSON_PICTURE = "picture";

        final String PERSON_GENDER = "gender";
        final String PERSON_TITLE = "title";
        final String PERSON_FIRST = "first";
        final String PERSON_LAST = "last";
        final String PERSON_NUMBER = "number";
        final String PERSON_CITY = "city";
        final String PERSON_STATE = "state";
        final String PERSON_POSTCODE = "postcode";
        final String PERSON_LATITUDE = "latitude";
        final String PERSON_LONGITUDE = "longitude";
        final String PERSON_AGE = "age";
        final String PERSON_NAT = "nat";
        final String PERSON_THUMBNAIL = "thumbnail";

        final String MESSAGE_CODE = "cod";

        FakePerson[] parsedPersonData = null;

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

        parsedPersonData = new FakePerson[personArray.length()];

        for (int i = 0; i < personArray.length(); i++) {
            String gender;
            String title;
            String first;
            String last;
            String street;
            String city;
            String state;
            String postcode;
            double latitude;
            double longitude;
            int age;
            String nat;
            String thumbnail;

            JSONObject person = personArray.getJSONObject(i);

            gender = person.getString(PERSON_GENDER);

            JSONObject nameObject = person.getJSONObject(PERSON_NAME);
            title = nameObject.getString(PERSON_TITLE);
            first = nameObject.getString(PERSON_FIRST);
            last = nameObject.getString(PERSON_LAST);

            JSONObject locationObject = person.getJSONObject(PERSON_LOCATION);
            JSONObject streetObject = locationObject.getJSONObject(PERSON_STREET);
            street = streetObject.getString(PERSON_NAME)+" "+streetObject.getInt(PERSON_NUMBER);
            city = locationObject.getString(PERSON_CITY);
            state = locationObject.getString(PERSON_STATE);
            postcode = locationObject.getString(PERSON_POSTCODE);
            JSONObject coordinatesObject = locationObject.getJSONObject(PERSON_COORDINATES);
            latitude = coordinatesObject.getDouble(PERSON_LATITUDE);
            longitude = coordinatesObject.getDouble(PERSON_LONGITUDE);

            JSONObject dobObject = person.getJSONObject(PERSON_DOB);
            age = Integer.valueOf(dobObject.getString(PERSON_AGE));

            LatLng latLng = new LatLng(latitude,longitude);

            PersonAddress address = new PersonAddress(street,city,state,postcode,latLng);
            FakePerson retPerson = new FakePerson(title,first,last,gender,age,address);
            parsedPersonData[i] = retPerson;
        }

        return parsedPersonData;
    }
}