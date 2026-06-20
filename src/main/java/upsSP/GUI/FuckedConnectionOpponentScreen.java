package upsSP.GUI;

import javax.swing.*;
import java.awt.*;

import upsSP.Nastroje.Constants;

/************************************************************
 * Instance tridy FuckedConnectionOpponentScreen predstavuje panel, ktery se zobrzi, kdyz se oppoentovi hrce pokzi
 * spojeni
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class FuckedConnectionOpponentScreen extends JPanel {

    /****
     * Konstruktor vytvari panel FuckedConnectionOpponentScreen, nastavuje jeho rozlozeni a obsah.
     *
     *
     * @param window Hlavni okno aplikace.
     */
    public FuckedConnectionOpponentScreen(Window window) {
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
        JLabel text = new JLabel("Tvůj opponent má problém s Ynternety");
        text.setForeground(Color.WHITE); // Nastavení barvy textu
        text.setFont(new Font("Arial", Font.BOLD, 24)); // Nastavení většího a tučného písma

        gridBorders.gridx = 0;
        gridBorders.gridy = 0;

        add(text, gridBorders);
    }
}
