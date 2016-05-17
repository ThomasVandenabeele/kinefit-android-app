package com.KineFit.app.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Created by Thomas on 28/04/16.
 */
public class Logging {

    private int id;
    private String description;
    private Time time;
    private Date date;
    private int amount;
    private String unit;
    private int sScore;
    private int pScore;


    public Logging(){
        super();
    }

    public Logging(int id, String description, Time time, Date date, int amount, String unit, int sScore, int pScore) {
        this.id = id;
        this.description = description;
        this.time = time;
        this.date = date;
        this.amount = amount;
        this.unit = unit;
        this.sScore = sScore;
        this.pScore = pScore;
    }


    @Override
    public String toString() {
        return this.id + ". " + this.description;
    }


    public int getsScore() {
        return sScore;
    }

    public int getpScore() {
        return pScore;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Time getTime() {
        return time;
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public String getUnit() {
        return unit;
    }
}
