package upsSP.VolbyTahu;


/************************************************************
 * Rozhrani definujici jake maji tridy reprezentujici herni thy implemtovt
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public interface ITurn {

    /****
     * vraci hodnotu tahu
     *
     *
     * @return hodnota tahu jako cele cislo
     */
    public int getValue();


    /****
     * vraci nazev tahu
     *
     *
     * @return nazev tahu jako retezec
     */
    public String getNameOfTurn();

    /****
     * vraci nazev souboru s obrazkem pro tah
     *
     * @return nazev obrazkoveho souboru jako retezec
     */
    public String getNameOfPictureFile();
}
