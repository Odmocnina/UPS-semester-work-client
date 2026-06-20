package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy spock reprezentuje th spock
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Spock implements ITurn {

    /****
     * konstruktor tridy spock
     *
     **/
    public Spock() {

    }

    /****
     * vraci hodnotu tahu spock
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Spock";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem spock
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() {
        return "Spock.png";
    }

    /****
     * funkce vracejici hodnotu tahu spock
     *
     *
     * @return hodnota tahu z constants pro spock
     **/
    public int getValue() {
        return Constants.SPOCK_VALUE;
    }
}
