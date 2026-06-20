package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy stone reprezentuje th sutr
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Stone implements ITurn {

    /****
     * konstruktor tridy stone
     *
     **/
    public Stone() {

    }

    /****
     * vraci hodnotu tahu kamen
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Kámen";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem kamen
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() { return "Kamen.png"; }

    /****
     * funkce vracejici hodnotu tahu kamen
     *
     *
     * @return hodnota tahu z constants pro kamen
     **/
    public int getValue() {
        return Constants.STONE_VALUE;
    }
}
