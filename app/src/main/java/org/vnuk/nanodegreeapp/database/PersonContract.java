package org.vnuk.nanodegreeapp.database;

import android.net.Uri;
import android.provider.BaseColumns;

public final class PersonContract {

    public static final String AUTHORITY = "org.vnuk.nanodegreeapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_PERSONS = "persons";

    public static final class PersonEntry implements BaseColumns {
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSONS).build();
        static final String TABLE_NAME_PERSONS = "persons";

        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_GENDER = "gender";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_STREET = "street";
        public static final String COLUMN_CITY = "city";
        public static final String COLUMN_STATE = "state";
        public static final String COLUMN_POSTCODE = "postcode";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        static final String COLUMN_TIMESTAMP = "timestamp";

        public static Uri buildPersonUriWithID(int id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(id))
                    .build();
        }
    }
}
