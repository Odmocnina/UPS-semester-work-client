package upsSP.GUI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import upsSP.Nastroje.Constants;
import upsSP.Server.Connection;

/************************************************************
 * Instance tridy AfterTurnWindow predstavuje panel, ktery se zobzi, kdyz hrac zvoli tah
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class AfterTurnWindow extends JPanel implements Connection.IListenerAfterTurn {

    /****
     * Konstruktor vytvari panel AfterTurnWindow, nastavuje jeho rozlozeni a pridava posluchace na udalosti po tahu.
     *
     *
     * @param window Hlavni okno aplikace.
     */
    public AfterTurnWindow(Window window) {
        GridBagLayout grid = new GridBagLayout();
        setLayout(grid);
        setBackground(Constants.BACKGROUND_COLOR);

        GridBagConstraints gridBorders = new GridBagConstraints();
        gridBorders.insets = new Insets(7, 7, 7, 7);
        gridBorders.fill = GridBagConstraints.CENTER;
        gridBorders.anchor = GridBagConstraints.CENTER; // Ukotví nápis ve středu

        gridBorders.weighty = 1.0;
        gridBorders.weightx = 1.0;
        gridBorders.gridwidth = GridBagConstraints.REMAINDER;

        // Vytvoření nápisu
        JLabel text = new JLabel("Čekání na protihráče...");
        text.setForeground(Color.WHITE); // Nastavení barvy textu
        text.setFont(new Font("Arial", Font.BOLD, 24)); // Nastavení většího a tučného písma

        gridBorders.gridx = 0;
        gridBorders.gridy = 0;

        add(text, gridBorders);

        try {
            Connection.getInstance().addListnerAfterTurn(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /****
     * Metoda onMessage se vola pri prijeti zpravy po tahu.
     * Zpracovava ruzne typy zprav a aktualizuje stav aplikace.
     *
     *
     * @param message Prijata zprava od serveru.
     */
    @Override
    public void onMessage(String message) {
        if (message.startsWith("Mess:gameResult:")) {
            GameEvaluationScreen.setGameState(message);
            GameEvaluationScreen.aktualizujLably();
        }
        if (message.startsWith("Mess:bothPlayerTurn:")) {
            Window window = (Window) SwingUtilities.getWindowAncestor(this);
            window.zobrazHru("gameJudgement");
        }
        if (message.startsWith("Mess:bothPlayersReady:")) {
            Window window = (Window) SwingUtilities.getWindowAncestor(this);
            window.zobrazHru("game");
        }
    }
}

