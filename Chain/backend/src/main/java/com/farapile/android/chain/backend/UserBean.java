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

    @Id String gplusID;
    String name;
}