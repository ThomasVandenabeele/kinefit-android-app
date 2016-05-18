package com.KineFit.app.model;

import org.joda.time.DateTime;

import java.sql.Date;

/**
 * Created by Thomas on 18/05/16.
 */
public class Step {

    private int id;
    private int aantalStappen;
    private Date startTijd;
    private Date eindTijd;

    public Step(int id, int aantalStappen, Date startTijd, Date eindTijd) {
        this.id = id;
        this.aantalStappen = aantalStappen;
        this.startTijd = startTijd;
        this.eindTijd = eindTijd;
    }

    public java.util.Date getGemDateTime(){
        long totaallSeconden = (startTijd.getTime()+eindTijd.getTime())/1000L;
        long gemSeconden = totaallSeconden / 2;
        return new Date(gemSeconden * 1000L);
    }

    public int getAantalStappen(){
        return aantalStappen;
    }

}

