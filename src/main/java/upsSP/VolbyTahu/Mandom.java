package upsSP.VolbyTahu;

import upsSP.Nastroje.Constants;
import java.util.Random;

/************************************************************
 * Instance tridy mdom reprezentuje th nhody
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class Mandom implements ITurn {

    /****
     * konstruktor tridy mandom
     *
     **/
    public Mandom() {

    }

    /****
     * vraci hodnotu tahu nahoda
     *
     *
     * @return hodnota tahu jako cele cislo
     **/
    public String getNameOfTurn() {
        return "Náhoda";
    }

    /****
     * funkce vracejici jmeno souboru s obrazkem nahod
     *
     *
     * @return nazev souboru s obrazkem
     **/
    public String getNameOfPictureFile() {
        return "Nahoda.png";
    }

    /****
     * funkce vracejici hodnotu tahu nahoda, za pouziti tridy Random
     *
     *
     * @return hodnota tahu z constants pro nahodu
     **/
    public int getValue() {
        //java.util.Random nahoda = new java.util.Random();
        Random random = new Random();
        return random.nextInt(Constants.NUMBER_OF_TURNS) + 1;
    }
}
