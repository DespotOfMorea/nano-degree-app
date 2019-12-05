package org.vnuk.nanodegreeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.vnuk.nanodegreeapp.utils.PersonJsonUtils;
import org.vnuk.nanodegreeapp.utils.PersonNetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements PersonAdapter.ItemClickListener, LoaderManager.LoaderCallbacks<String[]> {
    private static final LatLng BGD = new LatLng(44.81791, 20.45683);
    private static final int NUM_PERSONS = 17;

    private PersonAdapter personsAdapter;
    private RecyclerView personsRecyclerView;

    private static final int PERSON_LOADER_ID = 45;

    private TextView tvError;
    private ProgressBar pbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        personsRecyclerView = findViewById(R.id.rv_persons);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        personsRecyclerView.setLayoutManager(layoutManager);
        personsRecyclerView.setHasFixedSize(true);

        personsAdapter = new PersonAdapter(this);
        personsRecyclerView.setAdapter(personsAdapter);

        pbLoader = findViewById(R.id.pb_loader);
        tvError = findViewById(R.id.tv_error_message);

        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
        getSupportLoaderManager().initLoader(PERSON_LOADER_ID, null, callback);
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
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(String message) {
        Intent detailsIntent = new Intent(this,PersonDetailsActivity.class);
        detailsIntent.putExtra(Intent.EXTRA_TEXT,message);

        startActivity(detailsIntent);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<String[]>(this) {

            String[] personData = null;

            @Override
            protected void onStartLoading() {
                if (personData != null) {
                    deliverResult(personData);
                } else {
                    pbLoader.setVisibility(View.VISIBLE);
                    if (tvError.getVisibility()==View.VISIBLE)
                        tvError.setVisibility(View.INVISIBLE);
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String query = String.valueOf(NUM_PERSONS);
                URL personRequestUrl = PersonNetworkUtils.buildUrl(query);

                try {
                    String jsonPersonResponse = PersonNetworkUtils
                            .getResponseFromHttpUrl(personRequestUrl);

                    return PersonJsonUtils
                            .getSimplePersonStringsFromJson(MainActivity.this, jsonPersonResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                personData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] personData) {
        if (personData != null) {
            personsRecyclerView.setVisibility(View.VISIBLE);
            personsAdapter.setPersonsData(personData);
        } else {
            tvError.setVisibility(View.VISIBLE);
        }
        pbLoader.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {

    }

    private void invalidatePersonData() {
        personsAdapter.setPersonsData(null);
    }
}
