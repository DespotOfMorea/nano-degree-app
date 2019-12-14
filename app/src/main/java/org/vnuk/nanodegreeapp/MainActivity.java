package org.vnuk.nanodegreeapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.vnuk.nanodegreeapp.database.PersonContract;
import org.vnuk.nanodegreeapp.model.FakePerson;
import org.vnuk.nanodegreeapp.settings.SettingsActivity;
import org.vnuk.nanodegreeapp.utils.PersonJsonUtils;

public class MainActivity extends AppCompatActivity implements PersonAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {
    private static final LatLng BGD = new LatLng(44.81791, 20.45683);

    private static final int NUM_PERSONS = 17;
    private static final String PERSONS_JSON_FILE = "persons.json";
    private int queryNum;

    private PersonAdapter personsAdapter;
    private RecyclerView personsRecyclerView;
    private int position = RecyclerView.NO_POSITION;

    public static final String[] MAIN_PERSON_PROJECTION = {
            PersonContract.PersonEntry.COLUMN_TITLE,
            PersonContract.PersonEntry.COLUMN_FIRST_NAME,
            PersonContract.PersonEntry.COLUMN_LAST_NAME,
    };

    public static final int INDEX_PERSON_TITLE = 0;
    public static final int INDEX_PERSON_FIRST_NAME = 1;
    public static final int INDEX_PERSON_LAST_NAME = 2;

    private static final int PERSON_LOADER_ID = 45;

    private TextView tvError;
    private ProgressBar pbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupPreferences();

        personsRecyclerView = findViewById(R.id.rv_persons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        personsRecyclerView.setLayoutManager(layoutManager);
        personsRecyclerView.setHasFixedSize(true);

        personsAdapter = new PersonAdapter(this,this);
        personsRecyclerView.setAdapter(personsAdapter);

        pbLoader = findViewById(R.id.pb_loader);
        tvError = findViewById(R.id.tv_error_message);

        LoaderManager.LoaderCallbacks<Cursor> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(PERSON_LOADER_ID, null, callback);
    /* This line should be for generating FakePerson data in Database. */
        //generateFakePersonData();
    }

    private void setupPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String valStr = preferences.getString(getString(R.string.pref_key_query_persons), String.valueOf(NUM_PERSONS));
        queryNum = Integer.parseInt(valStr);

        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            invalidatePersonData();
            getSupportLoaderManager().restartLoader(PERSON_LOADER_ID, null, this);
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(int id) {
        Intent detailsIntent = new Intent(this,PersonDetailsActivity.class);
        detailsIntent.putExtra(Intent.EXTRA_TEXT,id);
        startActivity(detailsIntent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_query_persons))) {
            String valStr = sharedPreferences.getString(key, String.valueOf(NUM_PERSONS));
            queryNum = Integer.parseInt(valStr);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {
        switch (id) {
            case PERSON_LOADER_ID:
                Uri personQueryUri = PersonContract.PersonEntry.CONTENT_URI;
                String sortOrder = null;
                String selection = null;

                return new CursorLoader(this,personQueryUri,MAIN_PERSON_PROJECTION,selection,null,sortOrder);
                default:
                    throw new RuntimeException("Loader not implemented: " + id);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor personData) {
        if (personData != null) {
            personsRecyclerView.setVisibility(View.VISIBLE);
            personsAdapter.swapCursor(personData);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
        pbLoader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        personsAdapter.swapCursor(null);
    }

    private void invalidatePersonData() {
        personsAdapter.swapCursor(null);
    }

    private void generateFakePersonData() {
        FakePerson [] personsInArray = new FakePerson[0];
        try {
            String jsonPersonResponse = PersonJsonUtils
                    .loadJSONFromAsset(this,PERSONS_JSON_FILE);

            personsInArray = PersonJsonUtils
                    .getSimplePersonStringsFromJson(MainActivity.this, jsonPersonResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (personsInArray.length!=0) {
            for (FakePerson fakePerson : personsInArray) {
                insertFakePersonToDB(fakePerson);
            }
        }
    }

    private void insertFakePersonToDB(FakePerson person) {
        ContentValues values = new ContentValues();
        values.put(PersonContract.PersonEntry.COLUMN_TITLE,person.getTitle());
        values.put(PersonContract.PersonEntry.COLUMN_FIRST_NAME,person.getFirstName());
        values.put(PersonContract.PersonEntry.COLUMN_LAST_NAME,person.getLastName());
        values.put(PersonContract.PersonEntry.COLUMN_GENDER,person.getGender());
        values.put(PersonContract.PersonEntry.COLUMN_AGE,person.getAge());
        values.put(PersonContract.PersonEntry.COLUMN_STREET,person.getAddress().getStreet());
        values.put(PersonContract.PersonEntry.COLUMN_CITY,person.getAddress().getCity());
        values.put(PersonContract.PersonEntry.COLUMN_STATE,person.getAddress().getState());
        values.put(PersonContract.PersonEntry.COLUMN_POSTCODE,person.getAddress().getPostcode());
        values.put(PersonContract.PersonEntry.COLUMN_LATITUDE,person.getAddress().getCoordinates().latitude);
        values.put(PersonContract.PersonEntry.COLUMN_LONGITUDE,person.getAddress().getCoordinates().longitude);

        getContentResolver().insert(PersonContract.PersonEntry.CONTENT_URI, values);
    }
}
