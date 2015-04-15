package com.farapile.android.chain.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


import java.util.Date;

@Entity
public class TaskBean {

    public TaskBean() {}

    public TaskBean(long Id, String userGplusID, int type, String description, Date startDate, int duration) {
        this.Id = Id;
        this.userGplusID = userGplusID;
        this.type = type;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.current = 0;
    }


    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public String getUserGplusID() {
        return userGplusID;
    }

    public void setUserGplusID(String userGplusID) {
        this.userGplusID = userGplusID;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }


    @Id long Id;
    @Index String userGplusID;
    int type;
    String description;
    Date startDate;
    int duration;
    int current;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskBean taskBean = (TaskBean) o;

        if (Id != taskBean.Id) return false;
        if (current != taskBean.current) return false;
        if (duration != taskBean.duration) return false;
        if (type != taskBean.type) return false;
        if (description != null ? !description.equals(taskBean.description) : taskBean.description != null)
            return false;
        if (!startDate.equals(taskBean.startDate)) return false;
        if (!userGplusID.equals(taskBean.userGplusID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = userGplusID.hashCode();
        result = 31 * result + (int) (Id ^ (Id >>> 32));
        result = 31 * result + type;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + startDate.hashCode();
        result = 31 * result + duration;
        result = 31 * result + current;
        return result;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "userGplusID='" + userGplusID + '\'' +
                ", Id=" + Id +
                ", type=" + type +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", current=" + current +
                '}';
    }
}