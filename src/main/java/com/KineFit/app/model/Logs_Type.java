package com.KineFit.app.model;

/**
 * Modelklasse voor Logs_Type
 *
 * Created by Thomas on 29/04/16.
 * @author Thomas Vandenabeele
 */
public class Logs_Type {

    //region DATAMEMBER

    /** naam van het type */
    private String naam;

    /** id van het type */
    private int id;

    /** eenheid van het type */
    private String eenheid;

    //endregion

    /**
     * Constructor voor Logs_Type
     * @param id de id
     * @param naam  de naam van het type
     * @param eenheid de eenheid bij het type
     */
    public Logs_Type(int id, String naam, String eenheid) {
        this.id=id;
        this.naam = naam;
        this.eenheid = eenheid;
    }

    /**
     * Overriding toString() methode.
     * @return naam
     */
    @Override
    public String toString() {
        return getNaam();
    }

    /**
     * Geeft de naam terug
     * @return naam
     */
    public String getNaam() {
        return naam;
    }

    /**
     * Geeft de id terug
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Geeft de eenheid terug
     * @return eenheid
     */
    public String getEenheid() {
        return eenheid;
    }
}
