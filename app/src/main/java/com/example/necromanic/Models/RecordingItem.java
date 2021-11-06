package com.example.necromanic.Models;

import java.io.Serializable;

public class RecordingItem implements Serializable {

    private String name,path;
    private long time_added, length;
    private int id;

    public RecordingItem(String name, String path, long length, long time_added) {
        this.name = name;
        this.path = path;
        this.time_added = time_added;
        this.length = length;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTime_added(Long time_added) {
        this.time_added = time_added;
    }

    public void setLength(Long length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getTime_added() {
        return time_added;
    }

    public long getLength() {
        return length;
    }

    public void setID(int id){this.id = id;}

    public int getId(){return  id;}



}
