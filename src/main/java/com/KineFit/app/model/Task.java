package com.KineFit.app.model;

import com.KineFit.app.model.enums.TaskStatus;

import java.sql.Date;

/**
 * Created by Thomas on 28/04/16.
 */
public class Task {

    private int id;
    private String message;
    private Date create_date;
    private TaskStatus status;


    public Task(){
        super();
    }

    public Task(int id, String message, Date create_date, TaskStatus status) {
        this.id = id;
        this.message = message;
        this.create_date = create_date;
        this.status = status;
    }

    @Override
    public String toString() {
        return this.id + ". " + this.message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
