package upsSP.GUI;

import upsSP.Nastroje.Constants;
import upsSP.Server.Connection;
import upsSP.Nastroje.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/************************************************************
 * Instance tridy GameEvaluationScreen predstavuje panel, ktery se zobrzi na vyhdonoceni deni hry
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class GameEvaluationScreen extends JPanel implements Connection.IListenerInJudgement {

    /**Popisek pro zobrazeni vlastniho tahu.**/
    static JLabel turnLabel;
    /**Popisek pro zobrazeni tahu protivnika.**/
    static JLabel opponentTurnLabel;
    /**Popisek pro zobrazeni vysledku kola.**/
    static JLabel resultRoundLabel;
    /**Popisek pro zobrazeni vysledku hry.**/
    static JLabel gameResultLabel;

    /**Hodnota tahu protivnika.**/
    static int opponentTurnValue = -1;
    /**Textovy retezec obsahujici vysledek kola.**/
    static String roundResult = "";
    /**Hodnota vlastniho tahu.**/
    static int valueTurn = -1;

    /**Tlacitko pro zahajeni dalsiho kola nebo navrat do fronty.**/
    static JButton nextRoundButton;

    /****
     * Konstruktor vytvari panel GameEvaluationScreen, nastavuje jeho rozlozeni a pridava komponenty pro zobrazeni stavu hry.
     *
     *
     * @param window Hlavni okno aplikace.
     */
    public GameEvaluationScreen(Window window) {
        GridBagLayout mriz = new GridBagLayout();
        setLayout(mriz);
        setBackground(Constants.BACKGROUND_COLOR);

        GridBagConstraints gridBorders = new GridBagConstraints();
        gridBorders.insets = new Insets(7, 7, 7, 7);
        gridBorders.fill = GridBagConstraints.BOTH;

        // Přidání popisku pro jméno
        turnLabel = new JLabel("Zvolil jste: ");
        turnLabel.setFont(new Font("Arial", Font.BOLD, Constants.SIZE_OF_LETTER_IN_EVALUATION));
        gridBorders.gridx = 0;
        gridBorders.gridy = 0;
        add(turnLabel, gridBorders);

        opponentTurnLabel = new JLabel("Protivník zvolil: ");
        opponentTurnLabel.setFont(new Font("Arial", Font.BOLD, Constants.SIZE_OF_LETTER_IN_EVALUATION));
        gridBorders.gridx = 0;
        gridBorders.gridy = 1;
        add(opponentTurnLabel, gridBorders);

        resultRoundLabel = new JLabel("Výsledek kola: ");
        resultRoundLabel.setFont(new Font("Arial", Font.BOLD, Constants.SIZE_OF_LETTER_IN_EVALUATION));
        gridBorders.gridx = 0;
        gridBorders.gridy = 2;
        add(resultRoundLabel, gridBorders);

        gameResultLabel = new JLabel("Výsledek hry: ");
        gameResultLabel.setFont(new Font("Arial", Font.BOLD, Constants.SIZE_OF_LETTER_IN_EVALUATION));
        gridBorders.gridx = 0;
        gridBorders.gridy = 3;
        add(gameResultLabel, gridBorders);

        // Pridani tlacitka pro dalsi kolo
        nextRoundButton = new JButton("Další kolo");
        // zvetseni tlacitka
        nextRoundButton.setFont(new Font("Arial", Font.BOLD, Constants.SIZE_OF_LETTER_IN_EVALUATION));
        gridBorders.gridx = 0;
        gridBorders.gridy = 4;
        add(nextRoundButton, gridBorders);
        nextRoundButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.aktializujLably();
                if (GameState.getInstance().gameInProgress) {
                    window.zobrazHru("afterPlay");
                    try {
                        Connection.getInstance().sendMessage("Mess:readyForNextRound:" + Connection.getInstance().clientId + ":");
                        Connection.getInstance().setNextRoundSend(true);
                    } catch (IOException eQ) {
                        throw new RuntimeException(eQ);
                    }
                } else {
                    window.zobrazHru("wait");
                    try {
                        Connection.getInstance().sendMessage("Mess:game:" + Connection.getInstance().clientId + ":");
                        Connection.getInstance().setGameSend(true);
                        //GameState.getInstance().setScores(0, 0, 0);
                    } catch (IOException eQ) {
                        throw new RuntimeException(eQ);
                    }
                }
            }
        });
    }

    /****
     * Vraci nazev odpovidajici hodnote tahu.
     *
     * @param hodnota Hodnota tahu.
     * @return Nazev tahu.
     */
    public static String ziskejNazevZHodnoty(int hodnota) {
        switch (hodnota) {
            case Constants.STONE_VALUE:
                return "Kámen";
            case Constants.SCISSORS_VALUE:
                return "Nůžky";
            case Constants.PAPER_VALUE:
                return "Papír";
            case Constants.LIZZARD_VALUE:
                return "Tapír";
            case Constants.SPOCK_VALUE:
                return "Spock";
            default:
                return "Neznámá hodnota";
        }
    }

    /**
     * na updatovani labelu na novou hodnotu
     *
     *
     **/
    public static void aktualizujLably() {
        //if (opponentTurnLabel != null) {
        turnLabel.setText("Zvolil jste: " + ziskejNazevZHodnoty(valueTurn));
        opponentTurnLabel.setText("Protivník zvolil: " + ziskejNazevZHodnoty(opponentTurnValue));
        resultRoundLabel.setText("Výsledek kola: " + roundResult);
        if (GameState.getInstance().numberOfWonRounds == Constants.NUMBER_OF_ROUNDS) {
            gameResultLabel.setText("Výsledek hry: Vyhrál si");
            nextRoundButton.setText("Zpátky do fronty");
            GameState.getInstance().gameInProgress = false;
            GameState.getInstance().setScores(0, 0, 0);
        } else if (GameState.getInstance().numberOfLostRounds == Constants.NUMBER_OF_ROUNDS) {
            gameResultLabel.setText("Výsledek hry: Prohrál si");
            //dalsiKoloButton.setText("Zpátky na login");
            nextRoundButton.setText("Zpátky do fronty");
            GameState.getInstance().gameInProgress = false;
            GameState.getInstance().setScores(0, 0, 0);
        } else {
            gameResultLabel.setText("Výsledek hry: V průběhu hry");
        }
        //}
    }

    /****
     * Nastavuje stav hry na zaklade prijate zpravy.
     *
     *
     * @param message Zprava od serveru s informacemi o stavu hry.
     */
    public static void setGameState(String message) {
        String[] messageFragments = message.split(":");
        try {
            opponentTurnValue = Integer.parseInt(messageFragments[3]);
            if (messageFragments[2].equals("w")) {
                roundResult = "Výhra";
            } else if (messageFragments[2].equals("l")) {
                roundResult = "Prohra";
            } else if (messageFragments[2].equals("s")) {
                roundResult = "Remíza";
            } else {
                roundResult = "Chyba";
            }
            //System.out.println("parametry: " + parametry[4] + " " + parametry[5]);
            GameState.getInstance().setScores(Integer.parseInt(messageFragments[4]), Integer.parseInt(messageFragments[5]), Integer.parseInt(messageFragments[6]));
        } catch (NumberFormatException e) {
            System.out.println("Na miste cisla jinaci znaky");
        }
    }

    /****
     * Nastavuje vysledek kola.
     *
     *
     * @param result Vysledek kola.
     */
    public static void setRoundResultLabel(String result) {
        roundResult = result;
    }

    /****
     * Nastavuje tah protivnika.
     *
     *
     * @param turn Tah protivnika.
     */
    public static void setOpponentTurn(int turn) {
        opponentTurnValue = turn;
    }

    /****
     * Metoda pro vykreslovani komponenty panelu.
     *
     *
     * @param g Graficky objekt pro vykresleni.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    /****
     * Metoda pro zpracovani zprav prijatych od serveru.
     *
     *
     * @param message Prijata zprava od serveru.
     */
    @Override
    public void onMessage(String message) {

    }
}
