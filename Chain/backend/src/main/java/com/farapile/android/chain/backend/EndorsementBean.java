package com.farapile.android.chain.backend;

import com.googlecode.objectify.annotation.Id;

/**
 * Created by Cristi on 5/12/2015.
 */
public class EndorsementBean {

    public EndorsementBean() {}

    public EndorsementBean(String Id, String fromUserId, String toUserId, String taskID) {
        this.Id = Id;
        this.fromUserID = fromUserId;
        this.toUserID = toUserId;
        this.taskID = taskID;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getToUserID() {
        return toUserID;
    }

    public void setToUserID(String toUserID) {
        this.toUserID = toUserID;
    }

    public String getFromUserID() {
        return fromUserID;
    }

    public void setFromUserID(String fromUserID) {
        this.fromUserID = fromUserID;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EndorsementBean that = (EndorsementBean) o;

        if (Id != null ? !Id.equals(that.Id) : that.Id != null) return false;
        if (fromUserID != null ? !fromUserID.equals(that.fromUserID) : that.fromUserID != null)
            return false;
        if (taskID != null ? !taskID.equals(that.taskID) : that.taskID != null) return false;
        if (toUserID != null ? !toUserID.equals(that.toUserID) : that.toUserID != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (fromUserID != null ? fromUserID.hashCode() : 0);
        result = 31 * result + (toUserID != null ? toUserID.hashCode() : 0);
        result = 31 * result + (taskID != null ? taskID.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EndorsementBean{" +
                "Id='" + Id + '\'' +
                ", fromUserID='" + fromUserID + '\'' +
                ", toUserID='" + toUserID + '\'' +
                ", taskID='" + taskID + '\'' +
                '}';
    }

    @Id String Id;
    String fromUserID;
    String toUserID;
    String taskID;
}
