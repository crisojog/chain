package com.farapile.android.chain;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
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

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.concurrent.Callable;


public class MainActivity extends BaseActivity implements
        ResultCallback<People.LoadPeopleResult> {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String[] mDrawerListItems;
    private LinearLayout mDrawerListLayout;
    private ContentProvider mContentProvider;

    private static final String TAG = "MAIN";
    private static final int PROFILE_PIC_SIZE = 80;

    private String gPlusId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initNavigationDrawer();
        mContentProvider = ContentProvider.getInstance();
    }

    public void startFriendListFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new FriendListFragment())
                .commit();
    }

    public void startTaskListFragment(String gPlusId, boolean isFriend) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("gPlusId", gPlusId);
        bundle.putBoolean("isFriend", isFriend);
        TaskListFragment tlf = new TaskListFragment();
        tlf.setArguments(bundle);
        fragmentManager.beginTransaction()
                .replace(R.id.container, tlf)
                .commit();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {
        super.onConnected(bundle);
        getProfileInformation();
        Plus.PeopleApi.loadVisible(mGoogleApiClient, null)
                .setResultCallback(this);
        startTaskListFragment(gPlusId, false);
    }

    private void getProfileInformation() {
        Log.d(TAG, "getProfileInformation()");

        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);

                String personName = currentPerson.getDisplayName();
                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                gPlusId = currentPerson.getId();
                mContentProvider.logUser(gPlusId, personName);
                Log.e(TAG, "Id:" + currentPerson.getId()
                        + ", Name: " + personName
                        + ", plusProfile: " + personGooglePlusProfile
                        + ", email: " + email
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

                new ContentProvider.LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

            } else {
                Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                if (position == 0) {
                    startTaskListFragment(gPlusId, false);
                } else {
                    if (mContentProvider.getFriendList() == null)
                        return;
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
    
    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            ArrayList<String> mCirclesList = new ArrayList<>();
            Hashtable<String, String > mImageMap = new Hashtable<>();
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                int count = personBuffer.getCount();
                //personBuffer.get(0).getImage();
                for (int i = 0; i < count; i++) {
                    Person p = personBuffer.get(i);
                    String url = p.getImage().getUrl();

                    Log.d(TAG, p.getId() + " " + p.getDisplayName() + " " + url);
                    mCirclesList.add(p.getId());
                    if (url != null)
                        mImageMap.put(p.getId(), url);
                }
            } finally {
                personBuffer.close();
                mContentProvider.setCirclesList(mCirclesList, new Callable() {
                    @Override
                    public Object call() throws Exception {
                        return null;
                    }
                });
                mContentProvider.setImageMap(mImageMap);
            }

            //mCirclesAdapter.notifyDataSetChanged();
        } else {
            Log.e(TAG, "Error requesting visible circles: " + peopleData.getStatus());
        }
    }

}
