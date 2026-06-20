package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy pper reprezentuje th ppir
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Paper implements ITurn {

    /****
     * konstruktor tridy paper
     *
     **/
    public Paper() {

    }
    /****
     * vraci hodnotu tahu papir
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Papír";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem papir
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() {
        return "Papir.png";
    }

    /****
     * funkce vracejici hodnotu tahu papir
     *
     *
     * @return hodnota tahu z constants pro papir
     **/
    public int getValue() {
        return Constants.PAPER_VALUE;
    }
}