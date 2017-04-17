package nl.yoerinijs.notebuddy.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nl.yoerinijs.notebuddy.R;
import nl.yoerinijs.notebuddy.credits.CreditsBuilder;

/**
 * A simple activity to display credits
 */
public class CreditsActivity extends AppCompatActivity {

    private static final String PACKAGE_NAME = "nl.yoerinijs.notebuddy.activities";
    private static final String NOTES_ACTIVITY = "NotesActivity";
    private static final String LOG_TAG = "Credits Activity";

    TextView mTextView;
    Button mButton;
    Context mContext;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        // Set context
        mContext = this;

        // Due the fact all activities will be closed for security reasons,
        // the password will be set to pass it back later to the NotesActivity.
        mPassword = getIntent().getStringExtra("PASSWORD");

        // Log credit activity
        Log.d(LOG_TAG, "Display credits");

        // Display credits
        mTextView = (TextView) findViewById(R.id.creditsView);
        CreditsBuilder cb = new CreditsBuilder();
        mTextView.setText(cb.getCredits(mContext));

        // Back button
        mButton = (Button) findViewById(R.id.backButton);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                toNotesActivity();
            }
        });
    }

    /**
     * Go to Notes Activity and close current activity.
     */
    private void toNotesActivity() {
        Intent intent = new Intent();
        intent.putExtra("PASSWORD", mPassword);
        intent.setClassName(mContext, PACKAGE_NAME + "." + NOTES_ACTIVITY);
        startActivity(intent);
        finish();
    }
}
