package com.farapile.android.chain.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class TaskBean {

    public TaskBean() {}

    public TaskBean(String Id, String userGplusID, int type, String name, String description, long startDate, int duration) {
        this.Id = Id;
        this.userGplusID = userGplusID;
        this.type = type;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.duration = duration;
        this.current = 0;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
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

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Id String Id;
    @Index String userGplusID;
    int type;
    String name;
    String description;
    long startDate;
    int duration;
    int current;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TaskBean taskBean = (TaskBean) o;

        if (type != taskBean.type) return false;
        if (startDate != taskBean.startDate) return false;
        if (duration != taskBean.duration) return false;
        if (current != taskBean.current) return false;
        if (Id != null ? !Id.equals(taskBean.Id) : taskBean.Id != null) return false;
        if (userGplusID != null ? !userGplusID.equals(taskBean.userGplusID) : taskBean.userGplusID != null)
            return false;
        if (name != null ? !name.equals(taskBean.name) : taskBean.name != null) return false;
        return !(description != null ? !description.equals(taskBean.description) : taskBean.description != null);

    }

    @Override
    public int hashCode() {
        int result = Id != null ? Id.hashCode() : 0;
        result = 31 * result + (userGplusID != null ? userGplusID.hashCode() : 0);
        result = 31 * result + type;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (int) (startDate ^ (startDate >>> 32));
        result = 31 * result + duration;
        result = 31 * result + current;
        return result;
    }

    @Override
    public String toString() {
        return "TaskBean{" +
                "Id='" + Id + '\'' +
                ", userGplusID='" + userGplusID + '\'' +
                ", type=" + type +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", duration=" + duration +
                ", current=" + current +
                '}';
    }
}