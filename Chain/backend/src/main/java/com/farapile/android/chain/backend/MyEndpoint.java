/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.farapile.android.chain.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.QueryResultIterator;

import java.text.ParseException;
import java.util.ArrayList;

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

    @ApiMethod(name = "findUser")
    public UserBean findUser(@Named("id") String id) {
        return ofy().load().type(UserBean.class).id(id).now();
    }

    private UserBean createUser(String gplusID, String name) throws ConflictException {
        UserBean newUser = new UserBean(gplusID, name);
        ofy().save().entity(newUser).now();
        return newUser;
    }

    @ApiMethod(name = "filterFriends")
    public ArrayList<UserBean> filterFriends(@Named("friends") ArrayList<String> friends) {
        ArrayList<UserBean> friendsInDB = new ArrayList<>();
        for(String id : friends) {
            UserBean friend = findUser(id);
            if (friend != null) {
                friendsInDB.add(friend);
            }
        }
        return friendsInDB;
    }

    /* TASK PART */

    @ApiMethod(name = "createTask")
    public TaskBean createTask(@Named("Id") String Id,
                                 @Named("userGplusID") String userGplusID,
                                 @Named("type") int type,
                                 @Named("name") String name,
                                 @Named("description") String description,
                                 @Named("startDate") long startDate,
                                 @Named("duration") int duration ) throws ParseException {
        if(description == null) description = "";

        TaskBean newTask = new TaskBean(Id, userGplusID, type, name, description, startDate, duration);
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
    public TaskBean updateCurrentDay(@Named("Id") String id) throws NotFoundException {
        TaskBean task = findTask(id);
        if (task == null) {
            throw new NotFoundException("Task does not exist");
        }
        task.current++;
        ofy().save().entity(task).now();
        return task;
    }

    private TaskBean findTask(String id) {
        return ofy().load().type(TaskBean.class).id(id).now();
    }

    @ApiMethod(name = "createEndorsement")
    public EndorsementBean addEndorsement(@Named("Id") String Id,
                               @Named("fromUserID") String fromUserID,
                               @Named("toUserID") String toUserID,
                               @Named("taskID") String taskID) {
        EndorsementBean newEndorsement = new EndorsementBean(Id, fromUserID, toUserID, taskID);
        ofy().save().entities(newEndorsement).now();
        return newEndorsement;
    }
}
