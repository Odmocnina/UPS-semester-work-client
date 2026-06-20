package upsSP;

import javax.swing.*;

import upsSP.GUI.Window;
import upsSP.Server.Connection;

/************************************************************
 * Trid s main funkci progrmu
 *
 *
 * @author  Michael Hladky
 * @version 1.0.0
 */
public class Main {

    /****
     * funkce programu, kter je min, spusti cely progrm
     *
     *
     * @param args argumenty prikazove radky, nepouzito
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Window window = new Window();

            window.setVisible(true);
        });
    }
}