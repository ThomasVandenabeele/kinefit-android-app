package com.KineFit.app.model;

import java.sql.Date;
import java.sql.Time;

/**
 * Modelklasse voor Logging
 *
 * Created by Thomas on 28/04/16.
 * @author Thomas Vandenabeele
 */
public class Logging {

    //region DATAMEMBERS

    /** de id van de logging */
    private int id;

    /** de beschrijving van de logging */
    private String beschrijving;

    /** de tijd van de logging */
    private Time tijd;

    /** de datum van de logging */
    private Date datum;

    /** de hoeveelheid van de logging */
    private int hoeveelheid;

    /** de eenheid van de logging */
    private String eenheid;

    /** de tevredenheid score van de logging */
    private int tScore;

    /** de pijn score van de logging */
    private int pScore;

    //endregion

    /**
     * Constructor voor Logging
     * @param id de id
     * @param beschrijving de beschrijving
     * @param tijd de tijd
     * @param datum de datum
     * @param hoeveelheid de hoeveelheid
     * @param eenheid de eenheid
     * @param tScore de tevredenheid score
     * @param pScore de pijn score
     */
    public Logging(int id, String beschrijving, Time tijd, Date datum, int hoeveelheid, String eenheid, int tScore, int pScore) {
        this.id = id;
        this.beschrijving = beschrijving;
        this.tijd = tijd;
        this.datum = datum;
        this.hoeveelheid = hoeveelheid;
        this.eenheid = eenheid;
        this.tScore = tScore;
        this.pScore = pScore;
    }


    /**
     * Overriding toString() methode
     * @return de id + de beschrijving
     */
    @Override
    public String toString() {
        return this.id + ". " + this.beschrijving;
    }

    /**
     * Geeft de tevredenheid score terug
     * @return tScore
     */
    public int gettScore() {
        return tScore;
    }

    /**
     * Geeft de pijn score terug
     * @return pScore
     */
    public int getpScore() {
        return pScore;
    }

    /**
     * Geeft de id terug
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Geeft de beschrijving terug
     * @return beschrijving
     */
    public String getBeschrijving() {
        return beschrijving;
    }

    /**
     * Geeft de tijd terug
     * @return tijd
     */
    public Time getTijd() {
        return tijd;
    }

    /**
     * Geeft de datum terug
     * @return datum
     */
    public Date getDatum() {
        return datum;
    }

    /**
     * Geeft de hoeveelheid terug
     * @return hoeveelheid
     */
    public int getHoeveelheid() {
        return hoeveelheid;
    }

    /**
     * Geeft de eenheid terug
     * @return eenheid
     */
    public String getEenheid() {
        return eenheid;
    }
}
