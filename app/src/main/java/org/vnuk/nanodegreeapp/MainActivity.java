package org.vnuk.nanodegreeapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import org.vnuk.nanodegreeapp.utils.PersonJsonUtils;
import org.vnuk.nanodegreeapp.utils.PersonNetworkUtils;

import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final LatLng BGD = new LatLng(44.81791, 20.45683);
    private static final int NUM_PERSONS = 17;
    private TextView tvPerson;
    private TextView tvError;
    private ProgressBar pbLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvPerson = findViewById(R.id.tv_person_data);
        pbLoader = findViewById(R.id.pb_loader);
        tvError = findViewById(R.id.tv_error_message);
        loadPersonData();
    }

    private void loadPersonData() {
        String query = String.valueOf(NUM_PERSONS);
        new FetchPersonTask().execute(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            tvPerson.setText("");
            loadPersonData();
            return true;
        }

        return super.onOptionsItemSelected(item);
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

                String[] simplePersonData = PersonJsonUtils
                        .getSimplePersonStringsFromJson(MainActivity.this, jsonPersonResponse);

                return simplePersonData;

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
                for (String personString : personData) {
                    tvPerson.append((personString) + "\n\n\n");
                }
            } else {
                tvError.setVisibility(View.VISIBLE);

            }
            pbLoader.setVisibility(View.INVISIBLE);
        }
    }
}
