package com.farapile.android.chain;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.util.Pair;
import android.widget.Toast;
import android.util.Log;

import com.farapile.android.chain.backend.myApi.MyApi;
import com.farapile.android.chain.backend.myApi.model.TaskBean;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by crisojog on 13/04/15.
 */
public class ContentProvider {
    private static MyApi myApiService = null;
    private static ContentProvider instance = null;
    public static ArrayList<TaskBean> taskList = new ArrayList<TaskBean>();

    public ContentProvider() {
        init();
    }

    public static synchronized ContentProvider getInstance() {
        if (instance == null) {
            instance = new ContentProvider();
        }
        return instance;
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

    private class EndpointsAsyncTask extends AsyncTask<Pair<Context, String>, Void, String> {

        private Context context;

        @Override
        protected String doInBackground(Pair<Context, String>... params) {
            init();

            context = params[0].first;
            String name = params[0].second;
            try {
                return myApiService.sayHi(name).execute().getName();
            } catch (IOException e) {
                return e.getMessage();
            }
        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("ContentProvider", result);
            Toast.makeText(context, result, Toast.LENGTH_LONG).show();
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
}
