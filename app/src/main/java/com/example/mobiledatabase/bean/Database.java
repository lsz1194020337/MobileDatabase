package com.example.mobiledatabase.bean;

import java.io.Serializable;
import java.util.List;

public class Database implements Serializable {
    private String databaseName;
    private List<String> sqlList;

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getSqlList() {
        return sqlList;
    }

    public void setSqlList(List<String> sqlList) {
        this.sqlList = sqlList;
    }

    @Override
    public String toString() {
        return "Database{" +
                "databaseName='" + databaseName + '\'' +
                ", sqlList=" + sqlList +
                '}';
    }
}
