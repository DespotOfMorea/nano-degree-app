package org.vnuk.nanodegreeapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;
import androidx.preference.PreferenceManager;

import org.vnuk.nanodegreeapp.settings.SettingsActivity;

public class PersonDetailsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private TextView tvTitle;
    private TextView tvFirstName;
    private TextView tvLastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_details);

        tvTitle = findViewById(R.id.tv_title);
        tvFirstName = findViewById(R.id.tv_first_name);
        tvLastName = findViewById(R.id.tv_last_name);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(Intent.EXTRA_TEXT)) {
                String message = intent.getStringExtra(Intent.EXTRA_TEXT);
                setValues(message);
            }
        }
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

    private void setValues(String message) {
        if (null!=message) {
            String[] details = message.split(" ");
            if (details.length == 3) {
                tvTitle.setText(details[0]);
                tvFirstName.setText(details[1]);
                tvLastName.setText(details[2]);
            }
        }
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
        String shareTitle = "Person Details";
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
}