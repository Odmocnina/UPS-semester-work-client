package upsSP.GUI;

import upsSP.Nastroje.Constants;
import upsSP.Server.Connection;
import upsSP.VolbyTahu.*;
import upsSP.Nastroje.GameState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;


/************************************************************
 * Instance tridy GameWindow predstavuje panel, ktery se zobrzi jako herni spiel
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class GameWindow extends JPanel implements Connection.IListenerInGame {

    /**hodnota tahu hrace**/
    int turnValue = -10;

    /**pole obsahujici mozne tahy**/
    ITurn[] turns = new ITurn[Constants.NUMBER_OF_TURNS + 1];

    /**popisek pro zobrazeni aktualniho kola**/
    static JLabel roundLabel;
    /**popisek pro zobrazeni stavu hry**/
    static JLabel stavLabel;

    /****
     * konstruktor inicializuje herni okno, pridava tlacitka a nastavuje layout
     *
     *
     * @param window Hlavni okno aplikace.
     */
    public GameWindow(Window window) {
        ITurn stone = new Stone();
        turns[0] = stone;
        ITurn scissors = new Scissors();
        turns[1] = scissors;
        ITurn paper = new Paper();
        turns[2] = paper;
        ITurn lizard = new Lizard();
        turns[3] = lizard;
        ITurn spock = new Spock();
        turns[4] = spock;
        ITurn mandom = new Mandom();
        turns[5] = mandom;
        setBackground(Constants.BACKGROUND_COLOR);

        GridBagLayout grid = new GridBagLayout();
        setLayout(grid);

        GridBagConstraints gridBorders = new GridBagConstraints();
        gridBorders.insets = new Insets(7, 7, 7, 7);
        gridBorders.fill = GridBagConstraints.BOTH;  // Roztažení na obě strany
        gridBorders.anchor = GridBagConstraints.CENTER;  // Zarovnání na střed
        gridBorders.weightx = 1;  // Roztahování na šířku
        gridBorders.weighty = 1;  // Roztahování na výšku

        // Přidání nadpisu pro kolo číslo
        roundLabel = new JLabel("Kolo číslo: " + GameState.getInstance().numberOfPlayedRounds, SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gridBorders.gridx = 0;
        gridBorders.gridy = 0;
        gridBorders.gridwidth = 1;  // Nápis přes všechny tři sloupce
        add(roundLabel, gridBorders);

        // Přidání nadpisu pro stav hry
        stavLabel = new JLabel("<html>Výher: " + GameState.getInstance().numberOfWonRounds + "<br>"
                + "Proher: " + GameState.getInstance().numberOfLostRounds + "<br>" +
                "Remíz:" + GameState.getInstance().numberOfSM + "</html>", SwingConstants.CENTER);
        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gridBorders.gridx = 1;
        gridBorders.gridy = 0;
        gridBorders.gridwidth = 1;  // Nápis přes všechny tři sloupce
        add(stavLabel, gridBorders);

        JLabel pravidlaLabel = new JLabel("<html>Kámen > Nůžky, Tapír" +
                "<br>Papír > Kámen, Tapír" +
                "<br>Nůžky > Papír, Spock" +
                "<br>Tapír > Nůžky, Spock" +
                "<br>Spock > Kámen, Papír</html>"
                , SwingConstants.CENTER);

        roundLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gridBorders.gridx = 2;
        gridBorders.gridy = 0;
        gridBorders.gridwidth = 1;  // Tento label bude přes všechny tři sloupce
        add(pravidlaLabel, gridBorders);

        addButton(gridBorders, 0, 1, stone, window);
        addButton(gridBorders, 1, 1, scissors, window);
        addButton(gridBorders, 2, 1, paper, window);
        addButton(gridBorders, 0, 2, lizard, window);
        addButton(gridBorders, 1, 2, spock, window);
        addButton(gridBorders, 2, 2, mandom, window);
    }

    /****
     * pridava tlacitko pro dany tah do herniho okna.
     *
     * @param grid Nastaveni umisteni tlacitka.
     * @param x Pozice v radku.
     * @param y Pozice ve sloupci.
     * @param turn Tah odpovidajici tlacitku.
     * @param window Hlavni okno aplikace.
     */
    private void addButton(GridBagConstraints grid, int x, int y, ITurn turn, Window window) {
        // První řádek tlačítek (gridx = 0, 1, 2 pro tři sloupce)
        JButton button = new JButton(turn.getNameOfTurn());

        ImageIcon image = new ImageIcon(Constants.PATH_TO_DATA + turn.getNameOfPictureFile());
        // Změna velikosti ikony
        Image img = image.getImage();
        Image newimg = img.getScaledInstance((int)(Constants.SOIOT),
                (int)(Constants.SOIOT), java.awt.Image.SCALE_SMOOTH); // Změň velikost podle potřeby
        ImageIcon scaledImage = new ImageIcon(newimg);
        button.setBackground(Color.WHITE);
        button.setIcon(scaledImage);

        grid.gridx = x;  // První sloupec
        grid.gridy = y;  // První řádek
        add(button, grid);

        try {
            Connection.getInstance().addListnerInGame(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                turnValue = turn.getValue();
                try {
                    Connection connection = Connection.getInstance();
                    connection.sendMessage("Mess:turn:" + turnValue + ":");
                    GameEvaluationScreen.valueTurn = turnValue;
                    connection.setTurnSend(true);
                } catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
                GameState.getInstance().turnValue = turnValue;
                window.zobrazHru("afterPlay");
            }
        });
    }

    /*public void pridejKolo() {
        this.pocetOdehranychKol = this.pocetOdehranychKol + 1;
    }*/

    /****
     * updatuje popisky herniho okna s aktualnim stavem hry.
     */
    public static void updateLabes() {
        System.out.println("v akturalizaci labelu");
        roundLabel.setText("Kolo číslo: " + GameState.getInstance().numberOfPlayedRounds);
        //stavLabel.setText("stavHry: " + pocetOdehranychKol);
        //if (stavLabel != null) {
        stavLabel.setText("<html>Výher: " + GameState.getInstance().numberOfWonRounds + "<br>" + "Proher:"
                + GameState.getInstance().numberOfLostRounds
                + "<br>" + "Remíz:" + GameState.getInstance().numberOfSM + "</html>");
    }

    /**
     * zpracovava prijatou zpravu od serveru a meni stav hry podle obsahu zpravy
     *
     *
     * @param message Prijata zprava od serveru.
     */
    @Override
    public void onMessage(String message) {
        if (message.startsWith("Mess:turn:")) {
            System.out.println("Zprava identifikovana jako tah");
            Window window = (Window) SwingUtilities.getWindowAncestor(this);
            window.zobrazHru("gameJudgement");
        }
    }
}
