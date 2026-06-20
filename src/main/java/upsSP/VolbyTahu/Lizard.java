package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy lizard reprezentuje th tpir
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Lizard implements ITurn {

    /****
     * konstruktor tridy lizard
     *
     **/
    public Lizard() {

    }

    /****
     * vraci hodnotu tahu tapir
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Tapír";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem tpir
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() {
        return "Tapir.png";
    }

    /****
     * funkce vracejici hodnotu tahu tapir
     *
     *
     * @return hodnota tahu z constants pro tapira
     **/
    public int getValue() {
        return Constants.LIZZARD_VALUE;
    }
}
