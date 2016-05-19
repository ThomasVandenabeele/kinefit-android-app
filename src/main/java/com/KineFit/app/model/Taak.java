package com.KineFit.app.model;

import com.KineFit.app.model.enums.TaskStatus;

import java.sql.Date;

/**
 * Modelklasse voor Taak
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class Taak {

    //region DATAMEMBERS

    /** id van de taak */
    private int id;

    /** bericht van de taak */
    private String bericht;

    /** aanmaakdatum van taak */
    private Date aanmaakDatum;

    /** status van taak */
    private TaskStatus status;

    //endregion

    /**
     * Constructor voor Taak
     * @param id id
     * @param bericht   het bericht van de taak
     * @param aanmaakDatum de aanmaak datum
     * @param status de status
     */
    public Taak(int id, String bericht, Date aanmaakDatum, TaskStatus status) {
        this.id = id;
        this.bericht = bericht;
        this.aanmaakDatum = aanmaakDatum;
        this.status = status;
    }

    /**
     * Overriding toString() methode.
     * @return id + bericht
     */
    @Override
    public String toString() {
        return this.id + ". " + this.bericht;
    }

    /**
     * Geeft de id terug
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Geeft het bericht terug
     * @return bericht
     */
    public String getBericht() {
        return bericht;
    }

    /**
     * Geeft de aanmaak datum terug
     * @return aanmaak datum
     */
    public Date getAanmaakDatum() {
        return aanmaakDatum;
    }

    /**
     * Geeft de status terug
     * @return status
     */
    public TaskStatus getStatus() {
        return status;
    }
}
