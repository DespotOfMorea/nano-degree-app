package org.vnuk.nanodegreeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;

import org.vnuk.nanodegreeapp.database.PersonContract;
import org.vnuk.nanodegreeapp.model.FakePerson;
import org.vnuk.nanodegreeapp.settings.SettingsActivity;

public class PersonDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, LoaderManager.LoaderCallbacks<Cursor> {

    public static final String[] DETAIL_PERSON_PROJECTION = {
            PersonContract.PersonEntry.COLUMN_TITLE,
            PersonContract.PersonEntry.COLUMN_FIRST_NAME,
            PersonContract.PersonEntry.COLUMN_LAST_NAME,
            PersonContract.PersonEntry.COLUMN_GENDER,
            PersonContract.PersonEntry.COLUMN_AGE,
            PersonContract.PersonEntry.COLUMN_STREET,
            PersonContract.PersonEntry.COLUMN_CITY,
            PersonContract.PersonEntry.COLUMN_STATE,
            PersonContract.PersonEntry.COLUMN_POSTCODE,
            PersonContract.PersonEntry.COLUMN_LATITUDE,
            PersonContract.PersonEntry.COLUMN_LONGITUDE,
    };

    public static final int INDEX_PERSON_TITLE = 0;
    public static final int INDEX_PERSON_FIRST_NAME = 1;
    public static final int INDEX_PERSON_LAST_NAME = 2;
    public static final int INDEX_PERSON_GENDER = 3;
    public static final int INDEX_PERSON_AGE = 4;
    public static final int INDEX_PERSON_STREET = 5;
    public static final int INDEX_PERSON_CITY = 6;
    public static final int INDEX_PERSON_STATE = 7;
    public static final int INDEX_PERSON_POSTCODE = 8;

    private static final int ID_DETAIL_LOADER = 123;
    private Uri mUri;

    private TextView tvTitle;
    private TextView tvFirstName;
    private TextView tvLastName;
    private TextView tvGender;
    private TextView tvAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        tvTitle = findViewById(R.id.tv_title);
        tvFirstName = findViewById(R.id.tv_first_name);
        tvLastName = findViewById(R.id.tv_last_name);
        tvGender = findViewById(R.id.tv_gender);
        tvAge = findViewById(R.id.tv_age);

        Intent intent = getIntent();

        int personID = 0;

        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                personID = intent.getIntExtra(Intent.EXTRA_TEXT,0);
            }
        }

        mUri = PersonContract.PersonEntry.buildPersonUriWithID(personID);
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);

        setupPreferences();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String valStr = preferences.getString(getString(R.string.pref_key_details_person),getString(R.string.pref_details_value_1));
        setDetailsFromPref(valStr);

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_details_share) {
            Intent shareIntent = createSharePersonIntent();

            if (shareIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(shareIntent);
            }
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createSharePersonIntent() {
        String mimeType = "text/plain";
        String shareTitle = "FakePerson Details";
        String detailsText = "Title: " + tvTitle.getText()
                + ", First name: " + tvFirstName.getText()
                + ", Last name: " + tvLastName.getText();

        ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(this)
                .setType(mimeType)
                .setChooserTitle(shareTitle)
                .setText(detailsText);
        return builder.getIntent();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_details_person))) {
            String valStr = sharedPreferences.getString(key,getString(R.string.pref_details_value_1));
            setDetailsFromPref(valStr);
        }
    }

    private void setDetailsFromPref(String valStr) {
        if (valStr.equals(getString(R.string.pref_details_value_1))) {
            tvTitle.setVisibility(View.VISIBLE);
            tvFirstName.setVisibility(View.VISIBLE);
        } else if (valStr.equals(getString(R.string.pref_details_value_2))) {
            tvTitle.setVisibility(View.INVISIBLE);
            tvFirstName.setVisibility(View.VISIBLE);
        } else if (valStr.equals(getString(R.string.pref_details_value_3))) {
            tvTitle.setVisibility(View.VISIBLE);
            tvFirstName.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        switch (id) {
            case ID_DETAIL_LOADER:
                return new CursorLoader(this,
                        mUri,
                        DETAIL_PERSON_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            return;
        }

        String title = data.getString(INDEX_PERSON_TITLE);
        String firstName = data.getString(INDEX_PERSON_FIRST_NAME);
        String lastName = data.getString(INDEX_PERSON_LAST_NAME);
        String gender = data.getString(INDEX_PERSON_GENDER);
        int age = data.getInt(INDEX_PERSON_AGE);

        tvTitle.setText(title);
        tvFirstName.setText(firstName);
        tvLastName.setText(lastName);
        tvGender.setText(gender);
        tvAge.setText(String.valueOf(age));
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void setValues(FakePerson person) {
        if (person!=null){
            tvTitle.setText(person.getTitle());
            tvFirstName.setText(person.getFirstName());
            tvLastName.setText(person.getLastName());
            tvGender.setText(person.getGender());
            tvAge.setText(String.valueOf(person.getAge()));
        }
    }
}