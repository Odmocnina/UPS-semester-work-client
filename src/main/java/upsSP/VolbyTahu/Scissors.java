package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy scissors reprezentuje th nuzek
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Scissors implements ITurn {

    /****
     * konstruktor tridy scissors
     *
     **/
    public Scissors() {

    }

    /****
     * vraci hodnotu tahu nuzky
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Nůžky";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem nuzky
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() {
        return "Nuzky.png";
    }

    /****
     * funkce vracejici hodnotu tahu nuzky
     *
     *
     * @return hodnota tahu z constants pro nuzky
     **/
    public int getValue() {
        return Constants.SCISSORS_VALUE;
    }
}
