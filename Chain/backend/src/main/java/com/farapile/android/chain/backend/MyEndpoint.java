/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.farapile.android.chain.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Named;

import static com.farapile.android.chain.backend.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(name = "myApi", version = "v1", namespace = @ApiNamespace(ownerDomain = "backend.chain.android.farapile.com", ownerName = "backend.chain.android.farapile.com", packagePath = ""))
public class MyEndpoint {
    /**
     * A simple endpoint method that takes a name and says Hi back
     */
    @ApiMethod(name = "sayHi")
    public UserBean sayHi(@Named("name") String name) {
        UserBean response = new UserBean();
        response.setName("Hi, " + name);

        return response;
    }

    /* USER PART */

    @ApiMethod(name = "logUser")
    public UserBean logUser(@Named("gplusID") String gplusID, @Named("name") String name) throws ConflictException {
        UserBean user = findUser(gplusID);
        if (user == null) return createUser(gplusID, name);
        return user;
    }

    private UserBean findUser(String id) {
        return ofy().load().type(UserBean.class).id(id).now();
    }

    private UserBean createUser(String gplusID, String name) throws ConflictException {
        UserBean newUser = new UserBean(gplusID, name);
        ofy().save().entity(newUser).now();
        return newUser;
    }

    /* TASK PART */

    @ApiMethod(name = "createTask")
    public TaskBean createTask(@Named("Id") int Id,
                               @Named("userGplusID") String userGplusID,
                               @Named("type") int type,
                               @Nullable @Named("description") String description,
                               @Nullable @Named("startDate") String startDate, // TODO: after debugging, change back to Date and remove Nullable
                               @Named("duration") int duration ) throws ParseException {
        if(description == null) description = "";
        if(startDate == null) startDate = "29-Apr-2010,13:00:14 PM";
        DateFormat formatter = new SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa");
        TaskBean newTask = new TaskBean(Id, userGplusID, type, description, formatter.parse(startDate), duration);
        ofy().save().entities(newTask).now();
        return newTask;
    }

    @ApiMethod(name = "getTaskList")
    public ArrayList<TaskBean> getTaskList(@Named("userGplusID") String userGplusID) {
        ArrayList<TaskBean> rez = new ArrayList<TaskBean>();

        QueryResultIterator<TaskBean> iterator = ofy().load().type(TaskBean.class).filter("userGplusID =", userGplusID).iterator();
        while (iterator.hasNext()) {
            rez.add(iterator.next());
        }
        System.out.println(rez.size());

        return rez;
    }

    @ApiMethod(name = "updateCurrentDay")
    public TaskBean updateCurrentDay(@Named("Id") int id) throws NotFoundException {
        TaskBean task = findTask(id);
        if (task == null) {
            throw new NotFoundException("Task does not exist");
        }
        task.current++;
        ofy().save().entity(task).now();
        return task;
    }

    private TaskBean findTask(int id) {
        return ofy().load().type(TaskBean.class).id(id).now();
    }

    /* ENDORSEMENTS PART */
}
