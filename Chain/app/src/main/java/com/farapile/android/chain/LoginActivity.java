package com.farapile.android.chain;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.plus.Plus;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "BASE";
    private SignInButton mSignInButton;
    private Button mSignOutButton;
 //   private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSignInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mSignOutButton = (Button) findViewById(R.id.sign_out_button);

        // Button listeners
        mSignInButton.setOnClickListener(this);
        mSignOutButton.setOnClickListener(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {
            switch (v.getId()) {
                case R.id.sign_in_button:
                    mSignInProgress = STATE_SIGN_IN;
                    mGoogleApiClient.connect();
                    break;
                case R.id.sign_out_button:
                    if (mGoogleApiClient.isConnected()) {
                        Log.d(TAG, "sign out called");
                        Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                        mGoogleApiClient.disconnect();
                    }
                    onSignedOut();
                    break;
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        Log.i(TAG, "onConnected");
        // Update the user interface to reflect that the user is signed in.
        mSignInButton.setEnabled(false);
        mSignOutButton.setEnabled(true);
/*
        // Retrieve some profile information to personalize our app for the user.
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
*/
        // Indicate that the sign in process is complete.
        startMainActivity();
        Log.d(TAG, "Finishing");
        finish();
    }

    private void startMainActivity() {
        //GoogleApi.mGoogleApiClient = mGoogleApiClient;
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onSignedOut() {
        super.onSignedOut();
        Log.d(TAG, "onSignedOut");
        mSignInButton.setEnabled(true);
        mSignOutButton.setEnabled(false);
    }



}
