package org.vnuk.nanodegreeapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import org.vnuk.nanodegreeapp.utils.PersonJsonUtils;
import org.vnuk.nanodegreeapp.utils.PersonNetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements PersonAdapter.ItemClickListener {
    private static final LatLng BGD = new LatLng(44.81791, 20.45683);
    private static final int NUM_PERSONS = 17;

    private PersonAdapter personsAdapter;
    private RecyclerView personsRecyclerView;

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
        loadPersonData();
    }

    private void loadPersonData() {
        tvError.setVisibility(View.INVISIBLE);
        personsRecyclerView.setVisibility(View.VISIBLE);
        String query = String.valueOf(NUM_PERSONS);
        new FetchPersonTask().execute(query);
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
            personsAdapter.setPersonsData(null);
            loadPersonData();
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

    public class FetchPersonTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbLoader.setVisibility(View.VISIBLE);
            if (tvError.getVisibility()==View.VISIBLE)
                tvError.setVisibility(View.INVISIBLE);
        }

        @Override
        protected String[] doInBackground(String... strings) {
            if (strings.length == 0) {
                return null;
            }

            String location = strings[0];
            URL personRequestUrl = PersonNetworkUtils.buildUrl(location);

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

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] personData) {
            if (personData != null) {
                personsRecyclerView.setVisibility(View.VISIBLE);
                personsAdapter.setPersonsData(personData);
            } else {
                tvError.setVisibility(View.VISIBLE);
            }
            pbLoader.setVisibility(View.INVISIBLE);
        }
    }
}
