package upsSP.Nastroje;


/************************************************************
 * Jedinacek pouzity na udrezeni informaci o stavu hry
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class GameState {

    /**pocet odehranych kol.**/
    public int numberOfPlayedRounds = 0;

    /**pocet vyhranych kol.**/
    public int numberOfWonRounds = 0;
    /**pocet prohranych kol.**/
    public int numberOfLostRounds = 0;
    /**pocet remiz.**/
    public int numberOfSM = 0;
    /**indikator, zda hra probiha.**/
    public boolean gameInProgress = true;

    /**singletonova instance tridy GameState.**/
    static GameState instance;
    /**stav hry.**/
    public States stateOfGame;
    /**hodnota tahu hrace.**/
    public int turnValue;
    /****
     * soukromy konstruktor pro singletonovou implementaci.
     */
    private GameState() {

    }

    /****
     * vraci instanci tridy GameState.
     *
     * @return instance singletonu GameState.
     */
    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    /****
     * nastavi skore hry na zaklade poctu kol, remiz a vyher.
     *
     * @param numberOfRounds celkovy pocet odehranych kol.
     * @param numberOfStalemates pocet remiz.
     * @param numberOfWins pocet vyhranych kol.
     */
    public void setScores(int numberOfRounds, int numberOfStalemates, int numberOfWins) {
        //System.out.println("v metode");
        numberOfPlayedRounds = numberOfRounds;
        numberOfSM = numberOfStalemates;
        numberOfWonRounds = numberOfWins;
        numberOfLostRounds = numberOfRounds - numberOfWins - numberOfStalemates;
        //System.out.println("nastavene hodnoty " + numberOfPlayedRounds + " " + numberOfWonRounds + " "
        //        + numberOfLostRounds);
    }

    /****
     * prida vyhru k celkovemu poctu vyher
     *
     */
    public void addWin() {
        numberOfWonRounds = numberOfWonRounds + 1;
    }
    /****
     * prida prohru k celkovemu poctu proher
     *
     */
    public void addLoss() {
        numberOfLostRounds = numberOfLostRounds + 1;
    }

    /****
     * prida remizu k celkovemu poctu remiz
     */
    public void addStaleMate() {
        numberOfSM = numberOfSM + 1;
    }

    /****
     * nastavi aktualni stav hry
     *
     *
     * @param state novy stav hry.
     */
    public void setState(States state) {
        if (state != null) {
            this.stateOfGame = state;
        }
    }

}
