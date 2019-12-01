package org.vnuk.nanodegreeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ShareCompat;

public class PersonDetailsActivity extends AppCompatActivity {

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
}