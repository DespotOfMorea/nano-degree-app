package org.vnuk.nanodegreeapp.database;

import android.provider.BaseColumns;

final class PersonContract {
    private PersonContract() {}

    static class PersonEntry implements BaseColumns {
        static final String TABLE_NAME_PERSONS = "persons";

        static final String COLUMN_TITLE = "title";
        static final String COLUMN_FIRST_NAME = "first_name";
        static final String COLUMN_LAST_NAME = "last_name";
        static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
