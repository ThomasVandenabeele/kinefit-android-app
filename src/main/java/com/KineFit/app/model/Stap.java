package com.KineFit.app.model;

import org.joda.time.DateTime;

import java.sql.Date;

/**
 * Modelklasse voor Stap
 *
 * Created by Thomas on 18/05/16.
 * @author Thomas Vandenabeele
 */
public class Stap {

    //region DATAMEMBERS

    /** de id van de stap */
    private int id;

    /** het aantal stappen */
    private int aantalStappen;

    /** de starttijd */
    private Date startTijd;

    /** de eindtijd */
    private Date eindTijd;

    //endregion

    /**
     * Constructor voor Stap
     * @param id de id
     * @param aantalStappen het totaal aantal stappen
     * @param startTijd de start tijd
     * @param eindTijd de eind tijd
     */
    public Stap(int id, int aantalStappen, Date startTijd, Date eindTijd) {
        this.id = id;
        this.aantalStappen = aantalStappen;
        this.startTijd = startTijd;
        this.eindTijd = eindTijd;
    }

    /**
     * Geeft de gemiddelde tijd tussen begin en eind tijd
     * @return de gemiddelde tijd als Date
     */
    public java.util.Date getGemDateTime(){
        long totaallSeconden = (startTijd.getTime()+eindTijd.getTime())/1000L;
        long gemSeconden = totaallSeconden / 2;
        return new Date(gemSeconden * 1000L);
    }

    /**
     * Geet het totaal aantal stappen terug
     * @return totaal aantal stappen als int
     */
    public int getAantalStappen(){
        return aantalStappen;
    }

}

