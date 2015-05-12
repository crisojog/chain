package com.farapile.android.chain;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import java.io.InputStream;


public class MainActivity extends ActionBarActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerListItems;
    private LinearLayout mDrawerListLayout;


    private static final String TAG = "MAIN";
    private static final int PROFILE_PIC_SIZE = 80;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigationDrawer();

        //getProfileInformation();

        if (savedInstanceState == null) {
            startTaskListFragment(null);
        }
    }

    public void startFriendListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FriendListFragment())
                .commit();
    }

    public void startTaskListFragment(String id) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new TaskListFragment())
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    private void getProfileInformation() {
        Log.d(TAG, "getProfileInformation()");
        try {
            if(!GoogleApi.mGoogleApiClient.isConnected()) {
                GoogleApi.mGoogleApiClient.connect(); //dumbest hack ever
            }
            if (Plus.PeopleApi.getCurrentPerson(GoogleApi.mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(GoogleApi.mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(GoogleApi.mGoogleApiClient);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl);

                TextView txtName = (TextView) findViewById(R.id.text_username);
                TextView txtEmail = (TextView) findViewById(R.id.text_email);
                ImageView imgProfilePic = (ImageView) findViewById(R.id.img_user);

                txtName.setText(personName);
                txtEmail.setText(email);

                // by default the profile url gives 50x50 px image only
                // we can replace the value with whatever dimension we want by
                // replacing sz=X
                personPhotoUrl = personPhotoUrl.substring(0,
                        personPhotoUrl.length() - 2)
                        + PROFILE_PIC_SIZE;

                new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private void initNavigationDrawer() {

        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer);
        mDrawerList = (ListView)findViewById(R.id.list_view_drawer);
        mDrawerListLayout = (LinearLayout)findViewById(R.id.drawer_layout);
        mDrawerListItems = getResources().getStringArray(R.array.drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mDrawerListItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int editedPosition = position+1;
                //Toast.makeText(MainActivity.this, "You selected item " + editedPosition, Toast.LENGTH_SHORT).show();
                if (position == 0) {
                    startTaskListFragment(null);
                } else {
                    startFriendListFragment();
                }
                mDrawerLayout.closeDrawer(mDrawerListLayout);
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                null,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close){
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
                invalidateOptionsMenu();
                syncState();
            }
            public void onDrawerOpened(View v){
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
                syncState();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerToggle.syncState();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                if (mDrawerLayout.isDrawerOpen(mDrawerListLayout)) {
                    mDrawerLayout.closeDrawer(mDrawerListLayout);
                } else {
                    mDrawerLayout.openDrawer(mDrawerListLayout);
                }
                return true;
            }
            case R.id.action_example: {
                Toast.makeText(this, "Example action.", Toast.LENGTH_SHORT).show();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items

        /*
         * active those code if you want to hide options menu when drawer is
         * opened.
         */
        final boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLayout);

        /*
        menu.findItem(R.id.action_best).setVisible(!drawerOpen);
        menu.findItem(R.id.action_liked).setVisible(!drawerOpen);
        menu.findItem(R.id.action_rate).setVisible(!drawerOpen);
*/
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onRestart() {
        Log.d(TAG, "onRestart()");
        super.onRestart();
        //getProfileInformation();
    }
}
