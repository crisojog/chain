package com.farapile.android.chain;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.farapile.android.chain.backend.myApi.MyApi;
import com.farapile.android.chain.backend.myApi.model.TaskBean;
import com.farapile.android.chain.backend.myApi.model.UserBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by crisojog on 13/04/15.
 */
public class ContentProvider {
    private static MyApi myApiService = null;
    private static ContentProvider instance = null;
    private ArrayList<String> mCirclesList = new ArrayList<>();
    private Hashtable<String, String> mImageMap;
    private ArrayList<UserBean> friendList = null;
    private ArrayList<TaskBean> taskList = new ArrayList<TaskBean>();
    private UserBean myUser = null;

    public UserBean getMyUser() {
        return myUser;
    }

    public void setMyUser(UserBean myUser) {
        this.myUser = myUser;
    }

    public ArrayList<TaskBean> getTaskList() {
        return taskList;
    }

    public void setTaskList(ArrayList<TaskBean> taskList) {
        this.taskList = taskList;
    }

    public ArrayList<UserBean> getFriendList() {
        return friendList;
    }

    public void setFriendList(ArrayList<UserBean> friendList) {
        this.friendList = friendList;
    }

    public ArrayList<String> getCirclesList() {
        return mCirclesList;
    }

    public Hashtable<String, String> getImageMap() {
        return mImageMap;
    }

    public void setCirclesList(ArrayList<String> circlesList, Callable c) {
        mCirclesList = circlesList;
        if (friendList != null) friendList.clear();
        filterFriends(mCirclesList, c);
    }

    public void setImageMap(Hashtable<String,String> imageMap) {
        mImageMap = imageMap;
    }

    public ContentProvider() {
        init();
    }

    public static synchronized ContentProvider getInstance() {
        if (instance == null) {
            instance = new ContentProvider();
        }
        return instance;
    }

    /**
     * Background Async task to load user profile picture from url
     * */
    public static class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
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

    public static void init() {

        if (myApiService == null) {  // Only do this once
            /*  //uncomment for local server development
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null)
                    // options for running against local devappserver
                    // - 10.0.2.2 is localhost's IP address in Android emulator
                    // - turn off compression when running against local devappserver
                    .setRootUrl("http://10.0.2.2/_ah/api/")
                    .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                        @Override
                        public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                            abstractGoogleClientRequest.setDisableGZipContent(true);
                        }
                    });
            // end options for devappserver
            */
            MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                    .setRootUrl("https://dontbreakthechain-89920.appspot.com/_ah/api/");
            myApiService = builder.build();
        }

    }

    public void getTasks(Pair<String, Callable> p) {
        new EndpointsGetTasksAsyncTask().execute(p);
    }

    public void addTask(UUID uuid, String gPlusId, int type, String name, String description, long date, int numDays, Context c) {
        new EndpointsAddTaskAsyncTask().execute(new Pair<Task, Context>(
                new Task(
                        uuid.toString(),
                        gPlusId,
                        name,
                        description,
                        date,
                        type,
                        numDays),
                c));
    }

    public void updateCurrent(String id, Context c) {
        new EndpointsUpdateCurrentAsyncTask().execute(new Pair<String, Context>(
                id,
                c
        ));
    }

    public void logUser(String gPlusId, String name) {
        new EndpointsLogUserAsyncTask().execute(new Pair<String, String>(
                gPlusId,
                name
        ));
    }


    public void filterFriends(ArrayList<String> circlesList, Callable c) {
        for(String friend : circlesList) {
            new EndpointsFilterFriendsAsyncTask().execute(friend);
        }
    }

    private class EndpointsGetTasksAsyncTask extends AsyncTask<Pair<String, Callable>, Void, List<TaskBean>> {

        private Callable c;
        private String user;

        @Override
        protected List<TaskBean> doInBackground(Pair<String, Callable>... params) {
            init();

            c = params[0].second;
            user = params[0].first;
            try {
                return myApiService.getTaskList(user).execute().getItems();
            } catch (IOException e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(List<TaskBean> result) {
            //Log.d("ContentProvider", result);
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            if (result != null) {
                taskList = (ArrayList<TaskBean>) result;
                for (TaskBean tb : result) {
                    Log.d("ContentProvider", tb.getUserGplusID());
                }
                try {
                    c.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class EndpointsAddTaskAsyncTask extends AsyncTask<Pair<Task, Context>, Void, Integer> {

        private Context context;
        private Task task;

        @Override
        protected Integer doInBackground(Pair<Task, Context>... params) {
            init();

            context = params[0].second;
            task = params[0].first;
            try {
                myApiService.createTask(
                        task.id,
                        task.gPlusId,
                        task.type,
                        task.name,
                        task.description,
                        task.date,
                        task.numDays
                        ).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }


        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
                Toast.makeText(context, "New chain created", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Could not create chain", Toast.LENGTH_SHORT).show();
        }
    }

    private class EndpointsUpdateCurrentAsyncTask extends AsyncTask<Pair<String, Context>, Void, Integer> {

        private Context context;
        private String id;

        @Override
        protected Integer doInBackground(Pair<String, Context>... params) {
            init();

            context = params[0].second;
            id = params[0].first;
            try {
                myApiService.updateCurrentDay(
                        id
                ).execute();
            } catch (IOException e) {
                e.printStackTrace();
                return 1;
            }
            return 0;
        }


        @Override
        protected void onPostExecute(Integer result) {
            if (result == 0)
                Toast.makeText(context, "Chain updated for today", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, "Could not update chain", Toast.LENGTH_SHORT).show();
        }
    }

    private class EndpointsLogUserAsyncTask extends AsyncTask<Pair<String, String>, Void, UserBean> {

        private String gPlusId, name;

        @Override
        protected UserBean doInBackground(Pair<String, String>... params) {
            init();

            gPlusId = params[0].first;
            name = params[0].second;

            try {
                return myApiService.logUser(gPlusId, name).execute();
            } catch (IOException e) {
                return null;
            }
        }


        @Override
        protected void onPostExecute(UserBean result) {
            myUser = result;
        }
    }

    private class EndpointsFilterFriendsAsyncTask extends AsyncTask<String, Void, UserBean> {

        private String friendsList;

        @Override
        protected UserBean doInBackground(String... params) {
            init();

            friendsList = params[0];

            try {
                return myApiService.findUser(friendsList).execute();
            } catch (IOException e) {
                e.printStackTrace();
                /*
                UserBean u = new UserBean();
                u.setGplusID("asdf");
                u.setName("asdf");
                u.setNumTasks(0);
                ArrayList<UserBean> rez = new ArrayList<>();
                rez.add(u);
                return rez;
                */
                return null;
            }
        }


        @Override
        protected void onPostExecute(UserBean result) {
            if (result != null) {
                ContentProvider mContentProvider = ContentProvider.getInstance();
                ArrayList<UserBean> friendList = mContentProvider.getFriendList();
                if (friendList == null) friendList = new ArrayList<>();
                friendList.add(result);
                mContentProvider.setFriendList(friendList);
                Log.d("asdf", "" + friendList.size());
            }
        }
    }

    class Task {
        public String id, gPlusId, description, name;
        public int type, numDays;
        public long date;

        public Task(String id, String gPlusId, String name, String description, long date, int type, int numDays) {
            this.id = id;
            this.gPlusId = gPlusId;
            this.name = name;
            this.description = description;
            this.date = date;
            this.type = type;
            this.numDays = numDays;
        }
    }
}
