package com.farapile.android.chain;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Created by crisojog on 12/05/15.
 */
public class BaseActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        GoogleApiClient.ServerAuthCodeCallbacks {

    protected static final int STATE_DEFAULT = 0;
    protected static final int STATE_SIGN_IN = 1;
    protected static final int STATE_IN_PROGRESS = 2;

    protected static final int RC_SIGN_IN = 0;

    protected static final String TAG = "BASE";
    protected static final String SAVED_PROGRESS = "sign_in_progress";
    protected static final String WEB_CLIENT_ID = "WEB_CLIENT_ID";
    protected static final String SERVER_BASE_URL = "SERVER_BASE_URL";
    protected static final String EXCHANGE_TOKEN_URL = SERVER_BASE_URL + "/exchangetoken";
    protected static final String SELECT_SCOPES_URL = SERVER_BASE_URL + "/selectscopes";

    protected int mSignInProgress;
    protected PendingIntent mSignInIntent;
    protected int mSignInError;
    protected boolean mServerHasToken = true;
    protected GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mGoogleApiClient = buildGoogleApiClient();
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN);

        return builder.build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }


    @Override
    public CheckResult onCheckServerAuthorization(String s, Set<Scope> scopes) {
        Log.i(TAG, "Checking if server is authorized.");
        Log.i(TAG, "Mocking server has refresh token: " + String.valueOf(mServerHasToken));

        if (!mServerHasToken) {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(SELECT_SCOPES_URL);
            HashSet<Scope> serverScopeSet = new HashSet<Scope>();

            try {
                HttpResponse httpResponse = httpClient.execute(httpGet);
                int responseCode = httpResponse.getStatusLine().getStatusCode();
                String responseBody = EntityUtils.toString(httpResponse.getEntity());

                if (responseCode == 200) {
                    String[] scopeStrings = responseBody.split(" ");
                    for (String scope : scopeStrings) {
                        Log.i(TAG, "Server Scope: " + scope);
                        serverScopeSet.add(new Scope(scope));
                    }
                } else {
                    Log.e(TAG, "Error in getting server scopes: " + responseCode);
                }

            } catch (ClientProtocolException e) {
                Log.e(TAG, "Error in getting server scopes.", e);
            } catch (IOException e) {
                Log.e(TAG, "Error in getting server scopes.", e);
            }

            return CheckResult.newAuthRequiredResult(serverScopeSet);
        } else {
            return CheckResult.newAuthNotRequiredResult();
        }
    }

    @Override
    public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(EXCHANGE_TOKEN_URL);

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("serverAuthCode", serverAuthCode));
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            HttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            final String responseBody = EntityUtils.toString(response.getEntity());
            Log.i(TAG, "Code: " + statusCode);
            Log.i(TAG, "Resp: " + responseBody);

            // Show Toast on UI Thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(LoginActivity.this, responseBody, Toast.LENGTH_LONG).show();
                }
            });
            return (statusCode == 200);
        } catch (ClientProtocolException e) {
            Log.e(TAG, "Error in auth code exchange.", e);
            return false;
        } catch (IOException e) {
            Log.e(TAG, "Error in auth code exchange.", e);
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
/*
        // Retrieve some profile information to personalize our app for the user.
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
*/
        // Indicate that the sign in process is complete.
        mSignInProgress = STATE_DEFAULT;


    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            Log.w(TAG, "API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                resolveSignInError();
            }
        }

        onSignedOut();
    }

    protected void onSignedOut() {
        Log.d(TAG, "onSignedOut");
    }

    private void resolveSignInError() {
        if (mSignInIntent != null) {
            try {
                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {
            createErrorDialog().show();
        }
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    this,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e(TAG, "Google Play services resolution cancelled");
                            mSignInProgress = STATE_DEFAULT;
                        }
                    });
        } else {
            return new AlertDialog.Builder(this)
                    .setMessage(R.string.play_services_error)
                    .setPositiveButton(R.string.close,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "Google Play services error could not be "
                                            + "resolved: " + mSignInError);
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            }).create();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    mSignInProgress = STATE_DEFAULT;
                }

                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }


}
