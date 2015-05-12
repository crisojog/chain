package com.farapile.android.chain.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * The object model for the data we are sending through endpoints
 */

@Entity
public class UserBean {

    public UserBean() {}

    public UserBean(String gplusID, String name) {
        this.gplusID = gplusID;
        this.name = name;
        this.numTasks = 0;
    }

    public String getGplusID() {
        return gplusID;
    }

    public void setGplusID(String gplusID) {
        this.gplusID = gplusID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNumTasks() {
        return numTasks;
    }

    public void setNumTasks(Integer numTasks) {
        this.numTasks = numTasks;
    }

    public void incNumTasks() {
        numTasks++;
    }

    public void decNumTasks() {
        numTasks--;
        if (numTasks < 0) numTasks = 0;
    }

    @Id String gplusID;
    String name;
    Integer numTasks;
}