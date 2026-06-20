package upsSP.GUI;

import upsSP.Nastroje.Constants;
import upsSP.Server.Connection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/************************************************************
 * Instance tridy InBetweenWindow predstavuje panel, ktery se zobrzi kryz hrac cek az opponent take zvoli tah/bude
 * pripraven
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */

public class InBetweenWindow extends JPanel implements Connection.IListenerAfterLogin {

    /**
     * konstruktor vytvari panel InBetweenWindow, nastavuje jeho layout a zobrazuje cekaci zpravu.
     *
     * @param window hlavni okno aplikace.
     */
    public InBetweenWindow(Window window) {
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
        JLabel text = new JLabel("Chekuju");
        text.setForeground(Color.WHITE); // Nastavení barvy textu
        text.setFont(new Font("Arial", Font.BOLD, 24)); // Nastavení většího a tučného písma

        gridBorders.gridx = 0;
        gridBorders.gridy = 0;

        add(text, gridBorders);

        try {
            Connection.getInstance().addListnerAfterLogin(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * zpracovava prijate zpravy po prihlaseni a provadi odpovidajici akce na zaklade zpravy.
     *
     * @param message prijata zprava od serveru.
     */
    @Override
    public void onMessage(String message) {
        if (message.startsWith("Mess:login:")) {
            //System.out.println("Zprava identifikovana jako login v po loginu");
            String id = message.split(":")[2];
            try {
                if (Integer.parseInt(id) == -1) {
                    JOptionPane.showMessageDialog(null, "Jméno již použito!", "Chyba",
                            JOptionPane.ERROR_MESSAGE);
                    Connection.getInstance().closeConnection();
                    Window window = (Window) SwingUtilities.getWindowAncestor(this);
                    window.zobrazHru("login");
                } else {
                    Connection.getInstance().clientId = Integer.parseInt(id);
                    Window window = (Window) SwingUtilities.getWindowAncestor(this);
                    window.zobrazHru("wait");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException e) {
                System.out.println("Na miste cisla jinaci znaky");
            }
        }

    }
}
