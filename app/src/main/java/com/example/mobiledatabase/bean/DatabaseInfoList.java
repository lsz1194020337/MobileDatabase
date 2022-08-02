package com.example.mobiledatabase.bean;

import java.io.Serializable;
import java.util.List;

public class DatabaseInfoList implements Serializable {
    private List<Database> databases;

    public List<Database> getDatabases() {
        return databases;
    }

    public void setDatabases(List<Database> databases) {
        this.databases = databases;
    }
}
